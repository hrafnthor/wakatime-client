package `is`.hth.wakatimeclient.core.data.auth

import `is`.hth.wakatimeclient.core.data.modify
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import net.openid.appauth.AuthState
import org.json.JSONException
import timber.log.Timber

/**
 * Handles the safe storage of the resulting [AuthState] from the OAuth flow
 */
interface AuthStorage {

    /**
     * Retrieves the current [AuthState], returning a new instance if none exists
     */
    fun getState(): AuthState

    /**
     * Stores the supplied [AuthState] as the current state
     */
    fun setState(state: AuthState): AuthState

    /**
     * Updates the current [AuthState]
     */
    fun update(func: (AuthState) -> Unit): AuthState

    /**
     * Configures the storage for a supplied [method]. Calling this method clears the storage
     */
    fun setMethod(method: Method)

    /**
     * The authentication method being used
     */
    fun getMethod(): Method

    /**
     * Stores the api key being used for [Method.ApiKey] authentication
     */
    fun setKey(key: String)

    /**
     * Retrieves the stored API key if configured to use [Method.ApiKey] and one is stored
     */
    fun getKey(): String?

    /**
     * Resets the any stored OAuth information
     */
    fun resetAuthState()
}

/**
 * Library's default implementation of [AuthStorage]
 */
internal class DefaultAuthStorage private constructor(
    private val preferences: SharedPreferences
) : AuthStorage {

    companion object {
        private const val KEY_AUTH_STATE = "auth_state"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_METHOD = "method"

        fun construct(context: Context): AuthStorage {
            return DefaultAuthStorage(
                getSharedPreferences(
                    context,
                    "${context.packageName}_auth_prefs"
                )
            )
        }

        private fun getSharedPreferences(context: Context, name: String): SharedPreferences =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> EncryptedSharedPreferences.create(
                    name,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                else -> {
                    Timber.e("API level is < 23. Using plain SharedPreferences for authentication storage!")
                    context.getSharedPreferences(name, Context.MODE_PRIVATE)
                }
            }
    }

    override fun getState(): AuthState {
        return preferences.getString(KEY_AUTH_STATE, null)?.let {
            try {
                AuthState.jsonDeserialize(it)
            } catch (e: JSONException) {
                Timber.e(
                    e.localizedMessage,
                    "AuthState deserialization failed. Resetting auth storage"
                )
                null
            }
        } ?: run {
            AuthState().also {
                setState(it)
            }
        }
    }

    override fun setState(state: AuthState): AuthState {
        return state.also {
            preferences.modify {
                putString(KEY_AUTH_STATE, it.jsonSerializeString())
            }
        }
    }

    override fun update(func: (AuthState) -> Unit): AuthState =
        getState().apply(func).let(this::setState)

    override fun getMethod(): Method {
        val value = preferences.getString(KEY_METHOD, "") ?: ""
        return if (value.isNotEmpty()) {
            Method.convert(value)
        } else throw IllegalStateException("No authentication method set!")
    }

    override fun setMethod(method: Method) {
        preferences.modify {
            clear()
            putString(KEY_METHOD, method.name)
        }
    }

    override fun setKey(key: String) {
        preferences.modify {
            putString(KEY_API_KEY, key)
        }
    }

    override fun getKey(): String? = preferences.getString(KEY_API_KEY, null)

    override fun resetAuthState() {
        setState(AuthState())
    }
}