package `is`.hth.wakatimeclient.core.data.net

/**
 * An annotation for identifying the payload that we want to extract from an API response wrapped in
 * an envelope object.
 *
 * Based on Google's EnvelopPayload class from the Plaid sample application
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class EnvelopePayload(val value: String = "")