package `is`.hth.wakatimeclient.auth

import `is`.hth.wakatimeclient.modify
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
interface AuthStateStorage {

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
     * Clears stored [AuthState] information
     */
    fun clear()
}

/**
 * Library's default implementation of [AuthStateStorage]
 */
internal class DefaultAuthStateStorage internal constructor(
    private val preferences: SharedPreferences
) : AuthStateStorage {

    companion object {
        private const val KEY_AUTH_STATE = "auth_state"

        fun construct(context: Context): AuthStateStorage {
            return DefaultAuthStateStorage(
                getSharedPreferences(
                    context,
                    "${context.packageName}_auth_prefs"
                )
            )
        }

        private fun getSharedPreferences(context: Context, name: String): SharedPreferences {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> EncryptedSharedPreferences.create(
                    name,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                else -> {
                    Timber.e("API level is < 23. Using plain SharedPreferences for authentication storage!")
                    context.getSharedPreferences(
                        name,
                        Context.MODE_PRIVATE
                    )
                }
            }
        }
    }

    override fun getState(): AuthState = preferences.getString(KEY_AUTH_STATE, null)?.let {
        try {
            AuthState.jsonDeserialize(it)
        } catch (e: JSONException) {
            Timber.e(e.localizedMessage, "AuthState deserialization failed. Resetting auth storage")
            null
        }
    } ?: run {
        AuthState().also {
            setState(it)
        }
    }

    override fun setState(state: AuthState): AuthState {
        return preferences.modify {
            putString(KEY_AUTH_STATE, state.jsonSerializeString())
        }.let { state }
    }

    override fun update(func: (AuthState) -> Unit): AuthState {
        return getState().apply(func).let(this::setState)
    }

    override fun clear() {
        preferences.modify { clear() }
    }
}