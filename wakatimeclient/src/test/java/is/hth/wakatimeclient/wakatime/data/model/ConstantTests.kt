package `is`.hth.wakatimeclient.wakatime.data.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class ConstantTests : DescribeSpec({

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    describe("serialization") {
        describe("visibility") {
            json.encodeToJsonElement(Visibility.Visible)
        }
        describe("goal status") {
            json.encodeToJsonElement(GoalStatus.Pending)
        }
        describe("delta") {
            json.encodeToJsonElement(Delta.Day)
        }
        describe("frequency") {
            json.encodeToJsonElement(Frequency.Daily)
        }
        describe("human range") {
            json.encodeToJsonElement(HumanRange.WEEK)
        }
        describe("invitation status") {
            json.encodeToJsonElement(InvitationStatus.Accepted)
        }
        describe("type") {
            json.encodeToJsonElement(Type.App)
        }
        describe("category") {
            json.encodeToJsonElement(Category.TestsRunning)
        }
        describe("export status") {
            json.encodeToJsonElement(ExportStatus.Pending)
        }
        describe("processing status") {
            json.encodeToJsonElement(ProcessingStatus.Done)
        }
    }

    // Contains deserialization test for each type of constant used
    describe("deserialization") {

        describe("visibility") {
            it("visible") {
                decodeAndMatch(json, "visible", Visibility.Visible)
            }
            it("hidden") {
                decodeAndMatch(json, "hidden", Visibility.Hidden)
            }
        }

        describe("goal status") {
            it("success") {
                decodeAndMatch(json, "success", GoalStatus.Success)
            }
            it("failure") {
                decodeAndMatch(json, "fail", GoalStatus.Failure)
            }
            it("pending") {
                decodeAndMatch(json, "pending", GoalStatus.Pending)
            }
            it("ignored") {
                decodeAndMatch(json, "ignored", GoalStatus.Ignored)
            }
        }

        describe("delta") {
            it("day") {
                decodeAndMatch(json, "day", Delta.Day)
            }
            it("week") {
                decodeAndMatch(json, "week", Delta.Week)
            }
            it("all time") {
                decodeAndMatch(json, "all time", Delta.AllTime)
            }
        }

        describe("frequency") {
            it("daily") {
                decodeAndMatch(json, "Daily", Frequency.Daily)
            }
            it("every other day") {
                decodeAndMatch(json, "Every other day", Frequency.EveryOtherDay)
            }
            it("once per week") {
                decodeAndMatch(json, "Once per week", Frequency.OncePerWeek)
            }
            it("none") {
                decodeAndMatch(json, "", Frequency.None)
            }
        }

        describe("human range") {
            it("week") {
                decodeAndMatch(json, "last_7_days", HumanRange.WEEK)
            }
            it("month") {
                decodeAndMatch(json, "last_30_days", HumanRange.MONTH)
            }
            it("half year") {
                decodeAndMatch(json, "last_6_months", HumanRange.HALF_YEAR)
            }
            it("year") {
                decodeAndMatch(json, "last_year", HumanRange.YEAR)
            }
            it("all time") {
                decodeAndMatch(json, "all_time", HumanRange.All)
            }
        }

        describe("invitation status") {
            it("accepted") {
                decodeAndMatch(json, "Accepted", InvitationStatus.Accepted)
            }
            it("invited") {
                decodeAndMatch(json, "Invitation sent", InvitationStatus.Invited)
            }
            it("declined") {
                decodeAndMatch(json, "Declined", InvitationStatus.Declined)
            }
        }

        describe("type") {
            it("file") {
                decodeAndMatch(json, "file", Type.File)
            }
            it("app") {
                decodeAndMatch(json, "app", Type.App)
            }
            it("domain") {
                decodeAndMatch(json, "domain", Type.Domain)
            }
        }

        describe("category") {
            it("coding") {
                decodeAndMatch(json, "coding", Category.Coding)
            }
            it("building") {
                decodeAndMatch(json, "building", Category.Building)
            }
            it("indexing") {
                decodeAndMatch(json, "indexing", Category.Indexing)
            }
            it("debugging") {
                decodeAndMatch(json, "debugging", Category.Debugging)
            }
            it("browsing") {
                decodeAndMatch(json, "browsing", Category.Browsing)
            }
            it("documentation") {
                decodeAndMatch(json, "writing docs", Category.Documentation)
            }
            it("code review") {
                decodeAndMatch(json, "code reviewing", Category.CodeReview)
            }
            it("researching") {
                decodeAndMatch(json, "researching", Category.Researching)
            }
            it("learning") {
                decodeAndMatch(json, "learning", Category.Learning)
            }
            it("designing") {
                decodeAndMatch(json, "designing", Category.Designing)
            }
            it("test running") {
                decodeAndMatch(json, "running tests", Category.TestsRunning)
            }
            it("test writing") {
                decodeAndMatch(json, "writing tests", Category.TestsWriting)
            }
            it("test manual") {
                decodeAndMatch(json, "manual testing", Category.TestsManual)
            }
        }

        describe("export status") {
            it("pending") {
                decodeAndMatch(json, "Pending…", ExportStatus.Pending)
            }
            it("processing") {
                decodeAndMatch(json, "Processing coding activity…", ExportStatus.Processing)
            }
            it("uploading") {
                decodeAndMatch(json, "Uploading…", ExportStatus.Uploading)
            }
            it("done") {
                decodeAndMatch(json, "Completed", ExportStatus.Completed)
            }
        }

        describe("processing status") {
            it("pending") {
                decodeAndMatch(json, "pending_update", ProcessingStatus.Pending)
            }
            it("updating") {
                decodeAndMatch(json, "updating", ProcessingStatus.Processing)
            }
            it("done") {
                decodeAndMatch(json, "ok", ProcessingStatus.Done)
            }
        }
    }
}) {
    companion object {
        inline fun <reified T> decodeAndMatch(json: Json, value: String, match: T) {
            json.decodeFromString<T>("\"$value\"") shouldBe match
        }
    }
}