package `is`.hth.wakatimeclient.core.data.auth

import net.openid.appauth.AuthState
import org.json.JSONException
import timber.log.Timber

interface AuthStorage {

    /**
     * Retrieves the currently stored authentication state, or null if none is found
     */
    fun getState(): String?

    /**
     * Stores the supplied authentication state as the current state
     */
    fun setState(state: String)

    /**
     * Stores the authentication method type being used
     */
    fun setMethod(method: String)

    /**
     * Retrieves the stored authentication method being used, or null if none is found
     */
    fun getMethod(): String?

    /**
     * Stores the api key being used
     */
    fun setKey(key: String)

    /**
     * Retrieves the stored API key if configured to use one, or null if none is
     * found or not configured to use one
     */
    fun getKey(): String?

    /**
     * Clears all stored authentication information
     */
    fun clear()
}

/**
 * Handles the safe storage of the resulting [AuthState] from the OAuth flow
 */
internal class AuthStorageWrapper(private val storage: AuthStorage) {

    /**
     * Retrieves the current [AuthState], returning a new instance if none exists
     */
    fun getState(): AuthState {
        return storage.getState()?.let {
            try {
                AuthState.jsonDeserialize(it)
            } catch (e: JSONException) {
                Timber.i("AuthState deserialization failed. Resetting auth storage")
                null
            }
        } ?: run {
            AuthState().also {
                setState(it)
            }
        }
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
    fun getMethod(): Method {
        val method = storage.getMethod() ?: ""
        return if (method.isEmpty()) {
            Method.None
        } else Method.convert(method)
    }

    /**
     * Configures the storage for a supplied [method]. Calling this method clears the storage
     */
    fun setMethod(method: Method) {
        clear()
        storage.setMethod(method.key)
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
    fun clear() {
        storage.clear()
    }
}