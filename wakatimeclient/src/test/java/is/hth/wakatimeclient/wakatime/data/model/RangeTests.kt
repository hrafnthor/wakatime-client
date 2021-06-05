package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

public class RangeTests : DescribeSpec({

    val json = WakatimeJsonFactory.makeJson()

    describe("serialization") {
        it("of Range") {
            json.encodeToJsonElement(
                Range(
                    start = "2014-05-01T00:00:00Z",
                    end = "2014-05-04T23:59:59Z",
                    timezone = "timezone"
                )
            ) shouldBe buildJsonObject {
                put(Range.START, "2014-05-01T00:00:00Z")
                put(Range.END, "2014-05-04T23:59:59Z")
                put(Range.TIMEZONE, "timezone")
            }
        }
    }
    describe("deserialization") {
        describe("of Range") {
            it("of restructured api response") {
                // Tests extraction of Range fields from the root level of a larger
                // payload object
                json.decodeFromJsonElement<Range>(buildJsonObject {
                    put(Range.START, "2014-05-01T00:00:00Z")
                    put(Range.END, "2014-05-04T23:59:59Z")
                    put("random_data", "test data")
                }) shouldBe Range(
                    start = "2014-05-01T00:00:00Z",
                    end = "2014-05-04T23:59:59Z",
                    timezone = ""
                )
            }

            it("of date only api response") {
                // Tests modifications to date fields to add time when missing
                json.decodeFromJsonElement<Range>(buildJsonObject {
                    put(Range.START_DATE, "2014-05-01")
                    put(Range.END_DATE, "2014-05-04")
                    put(Range.TIMEZONE, "timezone field")
                }) shouldBe Range(
                    start = "2014-05-01T00:00:00Z",
                    end = "2014-05-04T23:59:59Z",
                    timezone = "timezone field"
                )
            }
        }
    }
})