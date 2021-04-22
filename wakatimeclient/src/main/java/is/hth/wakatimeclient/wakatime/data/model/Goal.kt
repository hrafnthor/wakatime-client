package `is`.hth.wakatimeclient.wakatime.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.*

@Serializable
data class Goal internal constructor(
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
data class DataPoint internal constructor(
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
     * An explanation for why this delta period passed or failed
     */
    @SerialName("range_status_reason")
    val rangeStatusReason: String = "",
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
data class InvitedUser internal constructor(
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
                    if (innerElement is JsonObject) {
                        // The inner element is of the correct type and contains as many
                        // keys as would be expected for the transformation to take place
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
@Serializable(with = SubscribedUserSerializer::class)
data class Subscriber internal constructor(
    /**
     * The user detail of this subscriber
     */
    val user: User,
    /**
     *  How often this subscriber receives emails about this goal
     */
    val frequency: Frequency
)

/**
 * This serializer manually decodes the structure of the subscriber list creating a custom
 * object out of it, [Subscriber].
 */
internal object SubscribedUserSerializer : KSerializer<Subscriber> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SubscribedUser") {
        element<String>(elementName = "display_name", isOptional = true)
        element<String>(elementName = "email", isOptional = true)
        element<Frequency>(elementName = "email_frequency")
        element<String>(elementName = "full_name", isOptional = true)
        element<String>(elementName = "user_id")
        element<String>(elementName = "username", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Subscriber) {
        throw NotImplementedError("Serialization method has not been implemented for 'Subscriber'")
    }

    override fun deserialize(decoder: Decoder): Subscriber {
        return decoder.decodeStructure(descriptor) {
            var displayName = ""
            var email = ""
            var frequency = Frequency.Daily
            var fullName = ""
            var id = ""
            var username = ""
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> displayName = decodeStringElement(descriptor, index)
                    1 -> email = decodeStringElement(descriptor, index)
                    2 -> frequency =
                        decodeSerializableElement(descriptor, index, Frequency.serializer())
                    3 -> fullName = decodeStringElement(descriptor, index)
                    4 -> id = decodeStringElement(descriptor, index)
                    5 -> username = decodeStringElement(descriptor, index)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            Subscriber(
                user = User(
                    displayName = displayName,
                    email = email,
                    fullName = fullName,
                    id = id,
                    username = username
                ),
                frequency = frequency
            )
        }
    }
}