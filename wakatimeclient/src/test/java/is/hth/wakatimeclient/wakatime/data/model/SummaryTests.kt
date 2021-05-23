package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.*

private class SummaryTests : DescribeSpec({

    //#region Setup
    val json = WakatimeJsonFactory.makeJson()

    val summary = Summary(
        hours = 1,
        minutes = 2,
        seconds = 3,
        percent = 4.0,
        totalSeconds = 5.0,
        digitalClockFormat = "some digital amount",
        name = "testing",
        humanReadableTotalTime = "some length of time"
    )

    val machineSummary = MachineSummary(
        summary = summary,
        machineNameId = "some ip number"
    )

    // The machine summary json structure as it is expected to be after local serialization
    val machineSummaryJsonLocal = buildJsonObject {
        put(MachineSummary.FIELD_SUMMARY, buildJsonObject {
            put(Summary.FIELD_HOURS, machineSummary.summary.hours)
            put(Summary.FIELD_MINUTES, machineSummary.summary.minutes)
            put(Summary.FIELD_SECONDS, machineSummary.summary.seconds)
            put(Summary.FIELD_PERCENT, machineSummary.summary.percent)
            put(Summary.FIELD_TOTAL_SECONDS, machineSummary.summary.totalSeconds)
            put(Summary.FIELD_DIGITAL_CLOCK, machineSummary.summary.digitalClockFormat)
            put(Summary.FIELD_NAME, machineSummary.summary.name)
            put(
                Summary.FIELD_HUMAN_READABLE_TOTAL_TIME,
                machineSummary.summary.humanReadableTotalTime
            )
        })
        put(MachineSummary.FIELD_MACHINE_NAME_ID, machineSummary.machineNameId)
    }

    val dailySummary = DailySummary(
        grandTotal = GrandTotal(
            hours = 1,
            minutes = 2,
            totalSeconds = 3.0,
            digital = "some text",
            text = "some human text"
        ),
        categories = listOf(summary, summary),
        dependencies = listOf(summary),
        editors = listOf(summary, summary, summary),
        languages = listOf(summary),
        machines = listOf(
            machineSummary, machineSummary
        ),
        operatingSystems = listOf(summary, summary, summary),
        projects = listOf(summary),
        branches = listOf(summary, summary),
        entities = listOf(summary, summary, summary, summary),
        range = Range(
            start = "some start date",
            end = "some end date",
            timezone = "some timezone"
        )
    )

    // Timezone data is not received in root element, but is transported
    // through JSON transform up from the first daily summary object
    // which does have timezone values given
    val summaries = Summaries(
        availableBranches = listOf("develop", "master"),
        selectedBranches = listOf("feature"),
        summaries = listOf(
            dailySummary
        ),
        range = Range(
            start = "some start date",
            end = "some end date",
            timezone = dailySummary.range.timezone
        )
    )

    // The summaries json structure as it is expected to be when received from server
    val summariesJsonRemote: JsonObject = buildJsonObject {
        put(Summaries.AVAILABLE_BRANCHES, buildJsonArray {
            summaries.availableBranches.forEach {
                add(JsonPrimitive((it)))
            }
        })
        put(Summaries.SELECTED_BRANCHES, buildJsonArray {
            summaries.selectedBranches.forEach {
                add(JsonPrimitive((it)))
            }
        })
        put(Summaries.SUMMARIES, json.encodeToJsonElement(summaries.summaries))
        put(Summaries.START, summaries.range.start)
        put(Summaries.END, summaries.range.end)
    }

    // The summaries json structure as it is expected to be after local serialization
    val summariesJsonLocal: JsonObject = buildJsonObject {
        put(Summaries.AVAILABLE_BRANCHES, buildJsonArray {
            summaries.availableBranches.forEach {
                add(JsonPrimitive((it)))
            }
        })
        put(Summaries.SELECTED_BRANCHES, buildJsonArray {
            summaries.selectedBranches.forEach {
                add(JsonPrimitive((it)))
            }
        })
        put(Summaries.SUMMARIES, json.encodeToJsonElement(summaries.summaries))
        put(Summaries.RANGE, json.encodeToJsonElement(summaries.range))
    }
    //#endregion

    describe("serialization") {
        describe("of MachineSummary") {
            it("from local values") {
                json.encodeToJsonElement(machineSummary) shouldBe machineSummaryJsonLocal
            }
        }
        describe("of Summaries") {
            it("from local values") {
                json.encodeToJsonElement(summaries) shouldBe summariesJsonLocal
            }
        }
    }

    describe("deserialization") {
        describe("of MachineSummary") {
            it("as received from server") {
                val received = buildJsonObject {
                    put("hours", machineSummary.summary.hours)
                    put("minutes", machineSummary.summary.minutes)
                    put("seconds", machineSummary.summary.seconds)
                    put("percent", machineSummary.summary.percent)
                    put("total_seconds", machineSummary.summary.totalSeconds)
                    put("digital", machineSummary.summary.digitalClockFormat)
                    put("name", machineSummary.summary.name)
                    put("text", machineSummary.summary.humanReadableTotalTime)
                    put("machine_name_id", machineSummary.machineNameId)
                }

                json.decodeFromJsonElement<MachineSummary>(received) shouldBe machineSummary
            }

            it("as locally serialized") {
                json.decodeFromJsonElement<MachineSummary>(
                    machineSummaryJsonLocal
                ) shouldBe machineSummary
            }
        }

        describe("of Summaries") {
            it("timezone extraction") {
                SummariesJsonTransformer.extractTimezone(
                    summariesJsonRemote
                ) shouldBe summaries.range.timezone
            }

            it("as received from server") {
                json.decodeFromJsonElement<Summaries>(summariesJsonRemote) shouldBe summaries
            }

            it("as locally serialized") {
                json.decodeFromJsonElement<Summaries>(summariesJsonLocal) shouldBe summaries
            }
        }
    }
})