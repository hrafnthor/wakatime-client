package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

public class GlobalStatsTests : DescribeSpec({

    val json = WakatimeJsonFactory.json

    val aggregation = Aggregation(
        name = "testing",
        verified = true,
        humanReadableCount = "large number of people",
        average = Amount(
            seconds = 1.0f,
            secondsHumanReadable = "One second"
        ),
        max = Amount(
            seconds = 2.0f,
            secondsHumanReadable = "Two seconds"
        ),
        median = Amount(
            seconds = 3.0f,
            secondsHumanReadable = "Three seconds"
        ),
        sum = Amount(
            seconds = 4.0f,
            secondsHumanReadable = "Four seconds"
        )
    )

    val globalStats = GlobalStats(
        total = aggregation.copy(name = "total"),
        dailyAverages = aggregation.copy(name = "daily average"),
        categories = listOf(aggregation.copy(name = "category")),
        editors = listOf(aggregation.copy(name = "editors")),
        languages = listOf(aggregation.copy(name = "language")),
        operatingSystems = listOf(aggregation.copy(name = "operating system")),
        range = Range(
            start = "starting time",
            end = "ending time"
        ),
        timeout = 15,
        writesOnly = true
    )

    describe("serialization") {
        describe("of Aggregation") {
            it("from local values") {
                val expectedJsonStructure = buildJsonObject {
                    put(Aggregation.NAME, aggregation.name)
                    put(Aggregation.VERIFIED, aggregation.verified)
                    put(Aggregation.HUMAN_READABLE_COUNT, aggregation.humanReadableCount)
                    put(Aggregation.AVERAGE, json.encodeToJsonElement(aggregation.average))
                    put(Aggregation.MAX, json.encodeToJsonElement(aggregation.max))
                    put(Aggregation.MEDIAN, json.encodeToJsonElement(aggregation.median))
                    put(Aggregation.SUM, json.encodeToJsonElement(aggregation.sum))
                }

                json.encodeToJsonElement(aggregation) shouldBe expectedJsonStructure
            }
        }

        describe("of GlobalStats") {
            it("from local values") {
                val expectedJsonStructure = buildJsonObject {
                    put(GlobalStats.TOTAL, json.encodeToJsonElement(globalStats.total))
                    put(GlobalStats.AVERAGES, json.encodeToJsonElement(globalStats.dailyAverages))
                    put(GlobalStats.CATEGORIES, json.encodeToJsonElement(globalStats.categories))
                    put(GlobalStats.EDITORS, json.encodeToJsonElement(globalStats.editors))
                    put(GlobalStats.LANGUAGES, json.encodeToJsonElement(globalStats.languages))
                    put(GlobalStats.OPERATING_SYSTEMS, json.encodeToJsonElement(globalStats.operatingSystems))
                    put(GlobalStats.RANGE, json.encodeToJsonElement(globalStats.range))
                    put(GlobalStats.TIMEOUT, globalStats.timeout)
                    put(GlobalStats.WRITES_ONLY, globalStats.writesOnly)
                }

                json.encodeToJsonElement(globalStats) shouldBe expectedJsonStructure
            }
        }
    }

    describe("deserialization") {
        describe("of Aggregation") {
            it("as received from server") {
                // tests the flattening of the "count" field from an object and into a string

                val received = buildJsonObject {
                    put(Aggregation.NAME, aggregation.name)
                    put(Aggregation.VERIFIED, aggregation.verified)
                    put("count", buildJsonObject {
                        put("text", aggregation.humanReadableCount)
                    })
                    put(Aggregation.AVERAGE, json.encodeToJsonElement(aggregation.average))
                    put(Aggregation.MAX, json.encodeToJsonElement(aggregation.max))
                    put(Aggregation.MEDIAN, json.encodeToJsonElement(aggregation.median))
                    put(Aggregation.SUM, json.encodeToJsonElement(aggregation.sum))
                }

                json.decodeFromJsonElement<Aggregation>(received) shouldBe aggregation
            }
            it("as locally serialized") {
                val serialized = json.encodeToJsonElement(aggregation)
                json.decodeFromJsonElement<Aggregation>(serialized) shouldBe aggregation
            }
        }

        describe("of GlobalStats") {
            it("as received from server") {
                // tests the hoisting of values inside "data" up into the root object
                // as a json transformation

                val received = buildJsonObject {
                    put("data", buildJsonObject {
                        put(GlobalStats.TOTAL, json.encodeToJsonElement(globalStats.total))
                        put(GlobalStats.AVERAGES, json.encodeToJsonElement(globalStats.dailyAverages))
                        put(GlobalStats.CATEGORIES, json.encodeToJsonElement(globalStats.categories))
                        put(GlobalStats.EDITORS, json.encodeToJsonElement(globalStats.editors))
                        put(GlobalStats.LANGUAGES, json.encodeToJsonElement(globalStats.languages))
                        put(GlobalStats.OPERATING_SYSTEMS, json.encodeToJsonElement(globalStats.operatingSystems))
                    })
                    put(GlobalStats.RANGE, json.encodeToJsonElement(globalStats.range))
                    put(GlobalStats.TIMEOUT, globalStats.timeout)
                    put(GlobalStats.WRITES_ONLY, globalStats.writesOnly)
                }

                json.decodeFromJsonElement<GlobalStats>(received) shouldBe globalStats
            }
        }
    }
})