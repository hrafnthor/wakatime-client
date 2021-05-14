package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

@Serializable
public data class Goal internal constructor(
    /**
     * Unique id of the goal
     */
    val id: String,
    /**
     * The goal amount in seconds
     */
    @SerialName("seconds")
    val goalInSeconds: Int,
    /**
     * percent goal should increase each delta
     */
    @SerialName("improve_by_percent")
    val improveByPercent: Float = 0f,
    /**
     * Indicates whether this goal is enabled or disabled
     */
    @SerialName("is_enabled")
    val isEnabled: Boolean,
    /**
     * Indicates whether this goal revolves around reducing the
     * time spent performing the measured action.
     */
    @SerialName("is_inverse")
    val isInverse: Boolean,
    /**
     * Indicates whether this goal has been snoozed. If so, the time of when the snooze
     * ends can be found in [snoozedUntil]
     */
    @SerialName("is_snoozed")
    val isSnoozed: Boolean,
    /**
     * Indicates if this goal is being automatically shared on Twitter by Wakatime.
     */
    @SerialName("is_tweeting")
    val isTweeting: Boolean,
    /**
     *
     */
    @SerialName("ignore_zero_days")
    val ignoreZeroDays: Boolean,
    /**
     *
     */
    @SerialName("is_current_user_owner")
    val isCurrentUserOwner: Boolean,
    /**
     *  Complete range of this goal for all delta periods in human-readable format
     */
    @SerialName("range_text")
    val rangeText: String = "",
    /**
     * If this goal has been snoozed, as indicated by [isSnoozed], then this value will
     * indicate when the snooze ends.
     */
    @SerialName("snooze_until")
    val snoozedUntil: String = "",
    /**
     * human readable title for this goal
     */
    val title: String = "",
    /**
     * type of goal
     */
    val type: Category,
    /**
     * Most recent day or week range status
     */
    val status: GoalStatus,
    /**
     * [GoalStatus.Failure] when there are more failure days or weeks than success, otherwise [GoalStatus.Success]
     */
    @SerialName("average_status")
    val averageStatus: GoalStatus,
    /**
     * [GoalStatus] over all delta periods, either [GoalStatus.Success], [GoalStatus.Failure] or [GoalStatus.Ignored]
     */
    @SerialName("cumulative_status")
    val cumulativeStatus: GoalStatus,
    /**
     * Goal step duration
     */
    val delta: Delta,
    /**
     * The owner of this goal
     */
    val owner: User,
    /**
     * goal status set to "ignored" instead of "failed" for these weekdsays, when delta is "day"
     */
    @SerialName("ignore_days")
    val ignoreDays: Set<String> = emptySet(),
    /**
     * The languages that this goal is focused on, if any
     */
    val languages: Set<String> = emptySet(),
    /**
     * The projects that this goal is focused on, if any
     */
    val projects: Set<String> = emptySet(),
    /**
     * The editors that this goal is focused on, if any
     */
    val editors: Set<String> = emptySet(),
    /**
     * A list of users that have subscribed to this goal.
     * Any subscriber which is not also the owner of the goal will also be present
     * in the list of invited users.
     */
    @Serializable(SubscriberListTransformer::class)
    val subscribers: List<Subscriber> = emptyList(),
    /**
     * A list of users that have been invited to observer this goal's progress.
     */
    @SerialName("shared_with")
    @Serializable(InvitedUserListTransformer::class)
    val invitedUsers: List<InvitedUser> = emptyList(),
    /**
     * A list of measurements taken at the correct time interval as per the
     * configured delta [Delta] of the goal.
     */
    @SerialName("chart_data")
    val dataPoints: List<DataPoint> = emptyList(),
    /**
     * The creation date of the goal in ISO 8601 format
     */
    @SerialName("created_at")
    val createdAt: String = "",
    /**
     * The latest modification date of the goal, if any, in ISO 8601 format
     */
    @SerialName("modified_at")
    val modifiedAt: String = ""
)

/**
 * A data point measurement over a range matching the configured delta of the goal.
 */
@Serializable
public data class DataPoint internal constructor(
    /**
     * number of seconds coded during this delta period
     */
    @SerialName("actual_seconds")
    val actualSeconds: Float,
    /**
     * human readable time coded during this delta period
     */
    @SerialName("actual_seconds_text")
    val actualSecondsText: String,
    /**
     *  number of seconds required to meet goal for this delta period
     */
    @SerialName("goal_seconds")
    val goalSeconds: Float,
    /**
     * human readable coding time required to meet goal for this delta period
     */
    @SerialName("goal_seconds_text")
    val goalSecondsText: String,
    /**
     * The current status for this delta period
     */
    @SerialName("range_status")
    val rangeStatus: GoalStatus,
    /**
     * An explanation for why this delta period has the status it has
     */
    @SerialName("range_status_reason")
    val rangeStatusReason: String = "",
    /**
     * A shorter explanation for why this delta period has the status it has
     */
    @SerialName("range_status_reason_short")
    val rangeStatusReasonShort: String = "",
    /**
     * The chronological range over which this data point covers
     */
    val range: Range
)

/**
 * A user who was invited to observe the progress of the goal.
 */
@Serializable
public data class InvitedUser internal constructor(
    @SerialName(ID)
    val id: String,
    /**
     * The user detail of this subscriber
     */
    @SerialName(USER)
    val user: User,
    /**
     * The status of the invitation
     */
    @SerialName(STATUS)
    val status: InvitationStatus
) {
    internal companion object {
        const val ID = "id"
        const val USER = "user"
        const val STATUS = "status"
    }
}

/**
 * Transforms the incoming JSON payload to simplify the resulting structure and reuse
 * [User] objects for portion of the payload
 */
internal object InvitedUserListTransformer : JsonTransformingSerializer<List<InvitedUser>>(
    ListSerializer(InvitedUser.serializer())
) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonArray) {
            return buildJsonArray {
                element.map { innerElement ->
                    if (innerElement is JsonObject && innerElement.size != 3) {
                        // The inner element is of the correct type and does not seem to
                        // be already processed payload
                        buildJsonObject {
                            innerElement[InvitedUser.ID]?.let { value ->
                                put(InvitedUser.ID, value)
                            }

                            innerElement[InvitedUser.STATUS]?.let { value ->
                                put(InvitedUser.STATUS, value)
                            }

                            put(InvitedUser.USER, buildJsonObject {
                                innerElement
                                    .filterKeys { it != InvitedUser.ID && it != InvitedUser.STATUS }
                                    .forEach {
                                        put(it.key, it.value)
                                    }
                            })
                        }
                    } else innerElement
                }.forEach(this::add)
            }
        }
        throw IllegalArgumentException("Incorrect JsonElement type received for InvitedUser deserialization!")
    }
}

/**
 * A user who has an active email subscription for the goals progress
 */
@Serializable
public data class Subscriber internal constructor(
    /**
     * The user detail of this subscriber
     */
    @SerialName(USER)
    val user: User,
    /**
     *  How often this subscriber receives emails about this goal
     */
    @SerialName(FREQUENCY)
    val frequency: Frequency
) {
    internal companion object {
        const val USER = "user"
        const val FREQUENCY = "email_frequency"
    }
}

/**
 * Transforms the incoming JSON payload to simplify the resulting structure and reuse
 * [User] objects for portion of the payload
 */
internal object SubscriberListTransformer : JsonTransformingSerializer<List<Subscriber>>(
    ListSerializer(Subscriber.serializer())
) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonArray) {
            return buildJsonArray {
                element.map { innerElement ->
                    if (innerElement is JsonObject && innerElement.size != 2) {
                        // The inner element is of the correct type and doesn't seem to
                        // be already processed
                        buildJsonObject {
                            innerElement[Subscriber.FREQUENCY]?.let { value ->
                                put(Subscriber.FREQUENCY, value)
                            }

                            put(Subscriber.USER, buildJsonObject {
                                innerElement
                                    .filterKeys { it != Subscriber.FREQUENCY }
                                    .forEach {
                                        if(it.key == "user_id"){
                                            // One of the many instances where different field names
                                            // are used in the response from API. Replace with the
                                            // standard field name
                                            put("id", it.value)
                                        } else {
                                            put(it.key, it.value)
                                        }
                                    }
                            })
                        }
                    } else innerElement
                }.forEach(this::add)
            }
        }
        throw IllegalArgumentException("Incorrect JsonElement type received for Subscriber deserialization!")
    }
}