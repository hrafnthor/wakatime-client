package `is`.hth.wakatimeclient.wakatime.data.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

public class ConstantTests : DescribeSpec({

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    describe("serialization") {
        it("of Visibility") {
            json.encodeToJsonElement(Visibility.Visible)
        }
        it("of GoalStatus") {
            json.encodeToJsonElement(GoalStatus.Pending)
        }
        it("of Delta") {
            json.encodeToJsonElement(Delta.Day)
        }
        it("of Frequency") {
            json.encodeToJsonElement(Frequency.Daily)
        }
        it("of HumanRange") {
            json.encodeToJsonElement(HumanRange.WEEK)
        }
        it("of InvitationStatus") {
            json.encodeToJsonElement(InvitationStatus.Accepted)
        }
        it("of Type") {
            json.encodeToJsonElement(Type.App)
        }
        it("of Category") {
            json.encodeToJsonElement(Category.TestsRunning)
        }
        it("of ExportStatus") {
            json.encodeToJsonElement(ExportStatus.Pending)
        }
        it("of ProcessingStatus") {
            json.encodeToJsonElement(ProcessingStatus.Done)
        }
    }

    // Contains deserialization test for each type of constant used
    describe("deserialization") {

        describe("of Visibility") {
            it("visible") {
                decodeAndMatch(json, "visible", Visibility.Visible)
            }
            it("hidden") {
                decodeAndMatch(json, "hidden", Visibility.Hidden)
            }
        }

        describe("of GoalStatus") {
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

        describe("of Delta") {
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

        describe("of Frequency") {
            it("daily") {
                decodeAndMatch(json, Frequency.Daily.toString(), Frequency.Daily)
            }
            it("every other day") {
                decodeAndMatch(json, Frequency.EveryOtherDay.toString(), Frequency.EveryOtherDay)
            }
            it("once per week") {
                decodeAndMatch(json, Frequency.OncePerWeek.toString(), Frequency.OncePerWeek)
            }
            it("none") {
                decodeAndMatch(json, Frequency.None.toString(), Frequency.None)
            }
        }

        describe("of HumanRange") {
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

        describe("of InvitationStatus") {
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

        describe("of Type") {
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

        describe("of Category") {
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

        describe("of ExportStatus") {
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

        describe("of ProcessingStatus") {
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
    public companion object {
        public inline fun <reified T> decodeAndMatch(json: Json, value: String, match: T) {
            json.decodeFromString<T>("\"$value\"") shouldBe match
        }
    }
}