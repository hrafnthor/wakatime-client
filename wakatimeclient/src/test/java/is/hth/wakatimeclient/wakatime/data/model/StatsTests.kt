package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

public class StatsTests : DescribeSpec({

    val json = WakatimeJsonFactory.json

    val day = Day(
        id = "123",
        date = "date of day",
        humanReadableTime = "a human readable value",
        secondsTotal = 5000.0,
        createdAt = "creation date for this object",
        modifiedAt = "modification date for this object"
    )

    val measurement = Measurement(
        hours = 12,
        minutes = 10,
        percent = 10.0,
        totalSeconds = 11.0,
        total24Hour = "total 24 hour",
        name = "a entity name",
        humanReadableTotalTime = "total 24 hour in readable format"
    )

    val machine = Machine(
        id = "123",
        ip = "1.1.1.1",
        name = "localhost",
        lastSeenAt = "at a date",
        createdAt = " created at date"
    )

    val machineMeasurement = MachineMeasurement(
        machine = machine,
        measurement = measurement
    )

    val stats = Stats(
        data = StatsData(
            id = "123",
            userId = "Tester",
            username = "Tester Testerson",
            dailyAverage = 12,
            dailyAverageTotal = 10,
            daysWithHolidays = 24,
            daysWithoutHolidays = 36,
            holidays = 4,
            timeout = 15,
            totalSeconds = 1000.0,
            totalSecondsAllLanguages = 1500.0,
            humanReadableDailyAverage = "Human readable daily average",
            humanReadableTotal = "Human readable total",
            createdAt = "created at",
            modifiedAt = "modified at",
            start = "start date",
            end = "end date",
            project = "wakatime-client",
            range = HumanRange.YEAR,
            timezone = "timezone",
            codingActivityPubliclyVisible = true,
            includesToday = true,
            otherActivityPubliclyVisible = true,
            writesOnly = true,
            bestDay = day,
            machines = listOf(machineMeasurement),
            categories = emptyList(),
            dependencies = listOf(measurement),
            editors = emptyList(),
            languages = listOf(measurement),
            operatingSystems = emptyList(),
            projects = listOf(measurement)
        ),
        status = Status(
            isStuck = true,
            isAlreadyUpdating = true,
            status = ProcessingStatus.Done,
            percentCalculated = 100,
            isUpToDate = false
        )
    )


    describe("serialization") {

        describe("of Stats") {
            val expectedSerialized = buildJsonObject {
                put(Stats.DATA, buildJsonObject {
                    put("id", stats.data.id)
                    put("user_id", stats.data.userId)
                    put("username", stats.data.username)
                    put("daily_average", stats.data.dailyAverage)
                    put("daily_average_including_other_language", stats.data.dailyAverageTotal)
                    put("days_including_holidays", stats.data.daysWithHolidays)
                    put("days_minus_holidays", stats.data.daysWithoutHolidays)
                    put("holidays", stats.data.holidays)
                    put("timeout", stats.data.timeout)
                    put("total_seconds", stats.data.totalSeconds)
                    put(
                        "total_seconds_including_other_language",
                        stats.data.totalSecondsAllLanguages
                    )
                    put("human_readable_daily_average", stats.data.humanReadableDailyAverage)
                    put("human_readable_total", stats.data.humanReadableTotal)
                    put("created_at", stats.data.createdAt)
                    put("modified_at", stats.data.modifiedAt)
                    put("start", stats.data.start)
                    put("end", stats.data.end)
                    put("project", stats.data.project)
                    put("range", json.encodeToJsonElement(stats.data.range))
                    put("timezone", stats.data.timezone)
                    put("is_coding_activity_visible", stats.data.codingActivityPubliclyVisible)
                    put("is_including_today", stats.data.includesToday)
                    put("is_other_usage_visible", stats.data.otherActivityPubliclyVisible)
                    put("writes_only", stats.data.writesOnly)
                    put("best_day", json.encodeToJsonElement(day))
                    put("machines", json.encodeToJsonElement(stats.data.machines))
                    put("categories", json.encodeToJsonElement(stats.data.categories))
                    put("dependencies", json.encodeToJsonElement(stats.data.dependencies))
                    put("editors", json.encodeToJsonElement(stats.data.editors))
                    put("languages", json.encodeToJsonElement(stats.data.languages))
                    put("operating_systems", json.encodeToJsonElement(stats.data.operatingSystems))
                    put("projects", json.encodeToJsonElement(stats.data.projects))
                })
                put(Stats.STATUS, buildJsonObject {
                    put(Status.IS_ALREADY_UPDATING, stats.status.isAlreadyUpdating)
                    put(Status.IS_STUCK, stats.status.isStuck)
                    put(Status.IS_UP_TO_DATE, stats.status.isUpToDate)
                    put(Status.PERCENTAGE_CALCULATED, stats.status.percentCalculated)
                    put(Status.STATUS, json.encodeToJsonElement(stats.status.status))
                })
            }

            val serialized = json.encodeToJsonElement(stats)

            it("matches expected serialization") {
                serialized shouldBe expectedSerialized
            }
        }
    }

    describe("deserialization") {
        describe("of MachineMeasurement") {
            // MachineMeasurement is only ever received in a list from the service
            // and has a custom list serialization mechanism as a Stats field
            describe("as populated list from service") {
                // Tests the payload as received from the service
                val payload = buildJsonArray {
                    add(buildJsonObject {
                        put("digital", machineMeasurement.measurement.total24Hour)
                        put("hours", machineMeasurement.measurement.hours)
                        put("machine", json.encodeToJsonElement(machineMeasurement.machine))
                        put("minutes", machineMeasurement.measurement.minutes)
                        put("name", machineMeasurement.measurement.name)
                        put("percent", machineMeasurement.measurement.percent)
                        put("text", machineMeasurement.measurement.humanReadableTotalTime)
                        put("total_seconds", machineMeasurement.measurement.totalSeconds)
                    })
                }

                val deserialized = json.decodeFromJsonElement(
                    deserializer = MachineMeasurementListTransformer,
                    element = payload
                )

                it("matches expected deserialization") {
                    deserialized shouldBe listOf(machineMeasurement)
                }
            }

            describe("as populated list from local") {
                // Tests the local value serialization and deserialization chain
                val localList = listOf(machineMeasurement)
                val payload = json.encodeToJsonElement(localList)

                val deserialized = json.decodeFromJsonElement(
                    deserializer = MachineMeasurementListTransformer,
                    element = payload
                )

                it("matches expected deserialization") {
                    deserialized shouldBe localList
                }
            }

            describe("as empty list from service") {
                val payload = JsonArray(emptyList())

                val deserialized = json.decodeFromJsonElement(
                    deserializer = MachineMeasurementListTransformer,
                    element = payload
                )

                it("matches expected deserialization") {
                    deserialized shouldBe emptyList()
                }
            }

            describe("as null value from service") {
                // Tests the handling of null values from the service
                val deserialized = json.decodeFromJsonElement(
                    deserializer = MachineMeasurementListTransformer,
                    element = JsonNull
                )

                it("matches expected deserialization") {
                    deserialized shouldBe emptyList()
                }
            }
        }

        describe("of Stats") {
            describe("as pending response") {
                // Reduced payload is received during a pending processing step
                val pending = Stats(
                    data = StatsData(
                        codingActivityPubliclyVisible = true,
                        includesToday = false,
                        otherActivityPubliclyVisible = true,
                        username = "user"
                    ),
                    status = Status(
                        percentCalculated = 0,
                        status = ProcessingStatus.Pending,
                    )
                )

                // Payload is hand built to make sure it is representative of
                // how the payload is received before going through any custom
                // serialization mechanisms
                val pendingPayload: String = buildJsonObject {
                    put("is_coding_activity_visible", pending.data.codingActivityPubliclyVisible)
                    put("is_including_today", pending.data.includesToday)
                    put("is_other_usage_visible", pending.data.otherActivityPubliclyVisible)
                    put("percent_calculated", pending.status.percentCalculated)
                    put("status", "pending_update")
                    put("username", pending.data.username)
                }.toString()

                val deserialized = json.decodeFromString<Stats>(pendingPayload)

                it("matches expected deserialization") {
                    deserialized shouldBe pending
                }
            }

            describe("as processing response") {
                // Reduced payload is received during an ongoing processing step

                val processing = Stats(
                    data = StatsData(
                        createdAt = "date",
                        end = "date",
                        id = "123456789",
                        codingActivityPubliclyVisible = true,
                        includesToday = false,
                        otherActivityPubliclyVisible = true,
                        range = HumanRange.HALF_YEAR,
                        timeout = 15,
                        project = "wakatime-client",
                        timezone = "zone",
                        userId = "123456789",
                        username = "user",
                        writesOnly = true
                    ),
                    status = Status(
                        isAlreadyUpdating = true,
                        isStuck = false,
                        isUpToDate = false,
                        percentCalculated = 13,
                        status = ProcessingStatus.Processing,
                    )
                )

                // Payload is hand built to make sure it is representative of
                // how the payload is received before going through any custom
                // serialization mechanisms
                val processingPayload = buildJsonObject {
                    put("created_at", processing.data.createdAt)
                    put("end", processing.data.end)
                    put("id", processing.data.id)
                    put("is_already_updating", processing.status.isAlreadyUpdating)
                    put("is_coding_activity_visible", processing.data.codingActivityPubliclyVisible)
                    put("is_including_today", processing.data.includesToday)
                    put("is_other_usage_visible", processing.data.otherActivityPubliclyVisible)
                    put("is_stuck", processing.status.isStuck)
                    put("is_up_to_date", processing.status.isUpToDate)
                    put("modified_at", processing.data.modifiedAt)
                    put("project", processing.data.project)
                    put("percent_calculated", processing.status.percentCalculated)
                    put("project", processing.data.project)
                    put("range", json.encodeToJsonElement(processing.data.range))
                    put("start", processing.data.start)
                    put("status", "updating")
                    put("timeout", processing.data.timeout)
                    put("timezone", processing.data.timezone)
                    put("user_id", processing.data.userId)
                    put("username", processing.data.username)
                    put("writes_only", processing.data.writesOnly)
                }

                val deserialized = json.decodeFromJsonElement<Stats>(processingPayload)

                it("matches expected deserialization") {
                    deserialized shouldBe processing
                }
            }

            describe("as normal response") {
                // Payload is hand built to make sure it is representative of
                // how the payload is received before going through any custom
                // serialization mechanisms
                val payload = buildJsonObject {
                    put("id", stats.data.id)
                    put("user_id", stats.data.userId)
                    put("username", stats.data.username)
                    put("daily_average", stats.data.dailyAverage)
                    put("daily_average_including_other_language", stats.data.dailyAverageTotal)
                    put("days_including_holidays", stats.data.daysWithHolidays)
                    put("days_minus_holidays", stats.data.daysWithoutHolidays)
                    put("holidays", stats.data.holidays)
                    put("timeout", stats.data.timeout)
                    put("total_seconds", stats.data.totalSeconds)
                    put(
                        "total_seconds_including_other_language",
                        stats.data.totalSecondsAllLanguages
                    )
                    put("human_readable_daily_average", stats.data.humanReadableDailyAverage)
                    put("human_readable_total", stats.data.humanReadableTotal)
                    put("created_at", stats.data.createdAt)
                    put("modified_at", stats.data.modifiedAt)
                    put("start", stats.data.start)
                    put("end", stats.data.end)
                    put("project", stats.data.project)
                    put("range", json.encodeToJsonElement(stats.data.range))
                    put("timezone", stats.data.timezone)
                    put("is_coding_activity_visible", stats.data.codingActivityPubliclyVisible)
                    put("is_including_today", stats.data.includesToday)
                    put("is_other_usage_visible", stats.data.otherActivityPubliclyVisible)
                    put("writes_only", stats.data.writesOnly)
                    put("best_day", json.encodeToJsonElement(day))
                    put("machines", json.encodeToJsonElement(stats.data.machines))
                    put("categories", json.encodeToJsonElement(stats.data.categories))
                    put("dependencies", json.encodeToJsonElement(stats.data.dependencies))
                    put("editors", json.encodeToJsonElement(stats.data.editors))
                    put("languages", json.encodeToJsonElement(stats.data.languages))
                    put("operating_systems", json.encodeToJsonElement(stats.data.operatingSystems))
                    put("projects", json.encodeToJsonElement(stats.data.projects))
                    put("is_already_updating", stats.status.isAlreadyUpdating)
                    put("is_stuck", stats.status.isStuck)
                    put("is_up_to_date", stats.status.isUpToDate)
                    put("percent_calculated", stats.status.percentCalculated)
                    put("status", json.encodeToJsonElement(stats.status.status))
                }

                val deserialized = json.decodeFromJsonElement<Stats>(payload)

                it("matches expected deserialization") {
                    deserialized shouldBe stats
                }
            }

            describe("deserialization of local serialization") {
                // Verifies that the custom deserializer works on
                // locally serialized values

                val serialized = json.encodeToString(stats)
                val deserialized = json.decodeFromString<Stats>(serialized)

                it("matches expected deserialization") {
                    deserialized shouldBe stats
                }
            }
        }
    }

    describe("construction") {
        describe("of Status") {

            it("range outside 0..100 should throw exception") {
                shouldThrow<IllegalArgumentException> {
                    Status(percentCalculated = -1)
                }
                shouldThrow<IllegalArgumentException> {
                    Status(percentCalculated = 101)
                }
            }
            it("range inside 0..100 should not throw exception") {
                shouldNotThrow<IllegalArgumentException> {
                    Status(percentCalculated = 0)
                }
                shouldNotThrow<IllegalArgumentException> {
                    Status(percentCalculated = 100)
                }
            }
        }
    }
})