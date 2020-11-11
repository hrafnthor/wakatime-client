package `is`.hth.wakatimeclient.wakatime.model

/**
 * Predefined chronological range constants
 */
sealed class Range(val description: String) {

    /**
     * The last week
     */
    object Week : Range("last_7_days")

    /**
     * The last 30 days from today
     */
    object Month : Range("last_30_days")

    /**
     * The last 6 months since today
     */
    object HalfYear : Range("last_6_months")

    /**
     * The last year from today
     */
    object Year : Range("last_year")
}