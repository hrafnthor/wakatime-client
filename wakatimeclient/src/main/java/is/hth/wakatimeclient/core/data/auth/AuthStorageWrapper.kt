package `is`.hth.wakatimeclient.core.data.auth

import net.openid.appauth.AuthState
import timber.log.Timber

public interface AuthStorage {

    /**
     * Retrieves the currently stored authentication state, or null if none is found
     */
    public fun getState(): String?

    /**
     * Stores the supplied authentication state as the current state
     */
    public fun setState(state: String)

    /**
     * Stores the authentication method type being used
     */
    public fun setMethod(method: String)

    /**
     * Retrieves the stored authentication method being used, or null if none is found
     */
    public fun getMethod(): String?

    /**
     * Stores the api key being used
     */
    public fun setKey(key: String)

    /**
     * Retrieves the stored API key if configured to use one, or null if none is
     * found or not configured to use one
     */
    public fun getKey(): String?

    /**
     * Clears all stored authentication information
     */
    public fun clear()
}

/**
 * Handles the safe storage of the resulting [AuthState] from the OAuth flow
 */
internal class AuthStorageWrapper(private val storage: AuthStorage) {

    /**
     * Retrieves the current [AuthState], returning a new instance if none exists
     */
    fun getState(): AuthState {
        val state: String = storage.getState() ?: ""
        return if (state.isNotEmpty()) {
            state.runCatching {
                AuthState.jsonDeserialize(this)
            }.getOrElse {
                Timber.e("AuthState deserialization failed!")
                Timber.i("Resetting stored AuthState")
                setState(AuthState())
            }
        } else setState(AuthState())
    }

    /**
     * Stores the supplied [AuthState] as the current state
     */
    private fun setState(state: AuthState): AuthState {
        return state.also {
            storage.setState(state.jsonSerializeString())
        }
    }

    /**
     * Updates the current [AuthState]
     */
    fun update(func: (AuthState) -> Unit): AuthState = getState().apply(func).let(this::setState)

    /**
     * The authentication method being used
     */
    fun getMethod(): Method? {
        return storage.getMethod()?.let { Method.convert(it) }
    }

    /**
     * Configures the storage for a supplied [method]. Calling this method clears the storage
     */
    fun setMethod(method: Method) {
        clear()
        storage.setMethod(method.toString())
    }

    /**
     * Stores the api key being used for [Method.ApiKey] authentication
     */
    fun setKey(key: String): Unit = storage.setKey(key)

    /**
     * Retrieves the stored API key if configured to use [Method.ApiKey] and one is stored
     */
    fun getKey(): String = storage.getKey() ?: ""

    /**
     * Resets the any stored OAuth information
     */
    fun clear(): Unit = storage.clear()
}