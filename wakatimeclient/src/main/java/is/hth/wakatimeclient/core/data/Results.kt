package `is`.hth.wakatimeclient.core.data

/**
 * A discriminating wrapper that encapsulates operational results
 */
public sealed class Results<out T>

/**
 * The operation was deemed successful
 */
public class Success<T>(public val value: T) : Results<T>()

/**
 * A complete failure happened due to the indicated [Error]
 */
public class Failure(
    /**
     * A description of what failed
     */
    public val error: Error
) : Results<Nothing>()