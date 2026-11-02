package generated

import ai.koog.agents.core.planner.AIAgentPlannerStrategy
import ai.koog.agents.planner.goap
import kotlin.String

public fun createBashScriptPlanner(): AIAgentPlannerStrategy<String, String> = goap<String, String, BashScriptState>("BashScript", ::BashScriptState) {
  action(
    name = "ReadTicket",
    description = "Anforderungen aus dem Jira-Ticket lesen",
    precondition = { it.requirementsKnown == false && it.ticketTried == false },
    belief = { it.copy(ticketTried = true, requirementsKnown = true) },
    cost = { 1.0 },
    execute = { _, state ->
        val out = try {
            createResearchAgentAgent().run("Anfrage des Nutzers:\n${state.request}\n\nLies das in der Anfrage genannte Jira-Ticket mit getIssue. Antworte im vorgegebenen Format.")
        } catch (e: Exception) {
            if (e is kotlin.coroutines.cancellation.CancellationException) throw e
            null
        }
        if (out == null) state.copy(ticketTried = true)
        else if (out.contains("REQUIREMENTS_OK")) state.copy(requirements = out, ticketTried = true, requirementsKnown = true)
        else state.copy(requirements = out, ticketTried = true)
    },
  )
  action(
    name = "WriteScript",
    description = "Skript schreiben und testen",
    precondition = { it.requirementsKnown == true && it.scriptWritten == false && it.writeAttempts < 2 },
    belief = { it.copy(scriptWritten = true) },
    cost = { 3.0 },
    execute = { _, state ->
        val out = try {
            createDeveloperAgentAgent().run("Anforderungen aus dem Ticket:\n${state.requirements}\n\nSetze die Anforderungen als Bash-Skript um. Gehe genau so vor:\n1. Schreibe das Skript mit executeCommand: cat > <Dateiname> << 'EOF' (Skriptinhalt) EOF. Die Anfuehrungszeichen um 'EOF' sind Pflicht, sonst werden die \$-Variablen ersetzt.\n2. executeCommand: chmod +x <Dateiname>\n3. Lege die Testdaten an, falls die Anforderungen welche nennen.\n4. Fuehre jeden Test aus den Akzeptanzkriterien mit executeCommand aus.\nSobald alle Tests die erwartete Ausgabe liefern, rufe kein Werkzeug mehr auf. Antworte erst danach mit dem Dateinamen und je Test dem Befehl mit seiner wortwoertlichen Ausgabe.")
        } catch (e: Exception) {
            if (e is kotlin.coroutines.cancellation.CancellationException) throw e
            null
        }
        if (out == null) state.copy(writeAttempts = state.writeAttempts + 1)
        else state.copy(devReport = out, scriptWritten = true)
    },
  )
  action(
    name = "ReviewScript",
    description = "Skript selbst testen und pruefen",
    precondition = { it.scriptWritten == true && it.reviewDone == false },
    belief = { it.copy(reviewDone = true, reviewPassed = true) },
    cost = { 2.0 },
    execute = { _, state ->
        val out = try {
            createReviewAgentAgent().run("Anforderungen aus dem Ticket:\n${state.requirements}\n\nBericht des Entwicklers:\n${state.devReport}\n\nPruefe das Skript selbst und verlasse dich nicht auf den Bericht:\n1. executeCommand: cat <Dateiname>\n2. Fuehre jeden Test aus den Akzeptanzkriterien mit executeCommand aus. Lege fehlende Testdaten wie in den Anforderungen beschrieben an.\nAntworte genau in diesem Format. Die erste Zeile ist nur das Wort APPROVED, wenn jeder Test genau die erwartete Ausgabe liefert, sonst nur das Wort REJECTED. Danach je Test eine Zeile: Befehl | Ausgabe | ok oder falsch. Beispiel:\nAPPROVED\n./skript.sh a | erwartete Ausgabe | ok")
        } catch (e: Exception) {
            if (e is kotlin.coroutines.cancellation.CancellationException) throw e
            null
        }
        if (out == null) state.copy(reviewDone = true, reviewAttempts = state.reviewAttempts + 1)
        else if (out.contains("APPROVED")) state.copy(reviewFindings = out, reviewDone = true, reviewPassed = true)
        else state.copy(reviewFindings = out, reviewDone = true, reviewAttempts = state.reviewAttempts + 1)
    },
  )
  action(
    name = "FixScript",
    description = "Skript anhand der Review-Befunde korrigieren",
    precondition = { it.reviewDone == true && it.reviewPassed == false && it.reviewAttempts < 3 },
    belief = { it.copy(reviewDone = false) },
    cost = { 3.0 },
    execute = { _, state ->
        val out = try {
            createDeveloperAgentAgent().run("Anforderungen aus dem Ticket:\n${state.requirements}\n\nBefunde aus dem Review:\n${state.reviewFindings}\n\nBehebe die Befunde im Skript. Gehe genau so vor:\n1. executeCommand: cat <Dateiname>\n2. Schreibe die vollstaendige korrigierte Fassung mit executeCommand: cat > <Dateiname> << 'EOF' (Skriptinhalt) EOF. Die Anfuehrungszeichen um 'EOF' sind Pflicht, sonst werden die \$-Variablen ersetzt.\n3. Fuehre jeden Test aus den Akzeptanzkriterien mit executeCommand aus.\nSobald alle Tests die erwartete Ausgabe liefern, rufe kein Werkzeug mehr auf. Antworte erst danach mit dem Dateinamen und je Test dem Befehl mit seiner wortwoertlichen Ausgabe.")
        } catch (e: Exception) {
            if (e is kotlin.coroutines.cancellation.CancellationException) throw e
            null
        }
        if (out == null) state.copy(reviewDone = false)
        else state.copy(devReport = out, reviewDone = false)
    },
  )
  action(
    name = "SaveReport",
    description = "Bericht speichern",
    precondition = { it.reviewPassed == true && it.reportSaved == false && it.saveAttempts < 2 },
    belief = { it.copy(reportSaved = true) },
    cost = { 1.0 },
    execute = { _, state ->
        val out = try {
            createReviewAgentAgent().run("Anfrage des Nutzers:\n${state.request}\n\nSpeichere den Bericht: Rufe __write_file__ auf mit dem Pfad /workspace/sandbox/<TICKET-KEY>-report.md (Ticket-Key aus der Anfrage) und genau diesem Inhalt:\n\n# Bericht\n\n## Umsetzung\n\n${state.devReport}\n\n## Review\n\n${state.reviewFindings}\n\nAntworte erst nach dem erfolgreichen Werkzeugaufruf nur mit: SAVED <Pfad>")
        } catch (e: Exception) {
            if (e is kotlin.coroutines.cancellation.CancellationException) throw e
            null
        }
        if (out == null) state.copy(saveAttempts = state.saveAttempts + 1)
        else if (out.contains("SAVED")) state.copy(report = out, reportSaved = true)
        else state.copy(report = out, saveAttempts = state.saveAttempts + 1)
    },
  )
  goal(name = "ReportSaved", value = { cost -> cost / 2.0 }, condition = { it.reportSaved == true })
}
