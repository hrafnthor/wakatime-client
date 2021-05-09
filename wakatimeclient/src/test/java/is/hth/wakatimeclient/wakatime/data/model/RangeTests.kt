package `is`.hth.wakatimeclient.wakatime.data.model

import `is`.hth.wakatimeclient.core.data.net.WakatimeJsonFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put

class RangeTests : DescribeSpec({

    val json = WakatimeJsonFactory.makeJson()

    describe("serialization") {

    }
    describe("deserialization") {
        it("of restructured api response") {
            // Tests extraction of Range fields from the root level of a larger
            // payload object
            val range = Range(
                startDate = "2014-05-01T00:00:00Z",
                endDate = "2014-05-04T23:59:59Z",
                timezone = ""
            )

            val payload = buildJsonObject {
                put("start", "2014-05-01T00:00:00Z")
                put("end", "2014-05-04T23:59:59Z")
                put("random_data", "test data")
            }

            json.decodeFromJsonElement<Range>(payload) shouldBe range
        }

        it("of date only api response") {
            // Tests modifications to date fields to add time when missing
            val range = Range(
                startDate = "2014-05-01T00:00:00Z",
                endDate = "2014-05-04T23:59:59Z",
                timezone = "timezone field"
            )
            val payload = buildJsonObject {
                put("start_date", "2014-05-01")
                put("end_date", "2014-05-04")
                put("timezone", "timezone field")
            }

            json.decodeFromJsonElement<Range>(payload) shouldBe range
        }
    }
})