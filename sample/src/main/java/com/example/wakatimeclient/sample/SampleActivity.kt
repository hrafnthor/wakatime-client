package com.example.wakatimeclient.sample

import `is`.hth.wakatimeclient.WakatimeClient
import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.AuthStorage
import `is`.hth.wakatimeclient.core.data.auth.Scope
import `is`.hth.wakatimeclient.wakatime.data.model.CurrentUser
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wakatimeclient.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.openid.appauth.browser.BrowserSelector
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

interface SampleActivityDataSource {

    fun onAuthentication(): LiveData<Boolean>

    fun onUser(): LiveData<CurrentUser>
}

interface SampleActivityActionHandler {

    fun onLogin(view: View)

    fun onRevoke(view: View)

    fun loadCurrentUser()
}

class SampleActivity : AppCompatActivity(),
    SampleActivityActionHandler,
    SampleActivityDataSource,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private val authenticationRequest = registerForActivityResult(StartActivityForResult()) {
        val data = it.data
        if (data != null) {
            model.onAuthenticationResult(data)
        } else {
            longToast(getString(R.string.authentication_failed))
        }
    }

    private val model: SampleViewModel by viewModels {
        Injector.providesViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).let {
            it.lifecycleOwner = this
            it.handler = this
            it.source = this
            it.refreshLayout.setOnRefreshListener(this)
            binding = it
        }

        model.authentication.observe(this, Observer { authenticated ->
            if (authenticated is Results.Success.Value && authenticated.value) {
                onRefresh()
            } else {
                Toast.makeText(this, "Not authenticated!", Toast.LENGTH_LONG).show()
            }
        })

        model.error.observe(this, Observer {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        })
    }

    //
    //              SwipeRefreshLayout implementation
    //////////////////////////////////////////////////////////////////////////

    override fun onRefresh() {
        binding.refreshLayout.isRefreshing = false
        loadCurrentUser()
    }

    //
    //              SampleActivityHandler implementation
    //////////////////////////////////////////////////////////////////////////

    override fun onLogin(view: View) {
        if (BrowserSelector.getAllBrowsers(view.context).isEmpty()) {
            longToast("No browsers found")
        } else {
            val intent = model.getAuthenticationRequest(Scope.values().toList())
            authenticationRequest.launch(intent)
        }
    }

    override fun onRevoke(view: View): Unit = model.logout()

    override fun loadCurrentUser(): Unit = model.loadCurrentUser()

    //
    //              SampleActivityDataSource implementation
    //////////////////////////////////////////////////////////////////////////

    override fun onAuthentication(): LiveData<Boolean> = Transformations.map(model.authentication) {
        when (it) {
            is Results.Success.Value -> it.value
            else -> false
        }
    }

    override fun onUser(): LiveData<CurrentUser> = model.currentUser

    //
    //                      Private API implementation
    //////////////////////////////////////////////////////////////////////////

    private fun longToast(msg: String): Unit = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

class SampleViewModel(
    private val client: WakatimeClient
) : ViewModel(), CoroutineScope {

    private var job = Job()

    private val _authenticated: MutableLiveData<Results<Boolean>> = MutableLiveData()
    val authentication: LiveData<Results<Boolean>> = _authenticated

    private val _currentUser: MutableLiveData<CurrentUser> = MutableLiveData()
    val currentUser: LiveData<CurrentUser> = _currentUser

    private val _error: MutableLiveData<Error> = MutableLiveData()
    val error: LiveData<Error> = _error

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    init {
        _authenticated.postValue(Results.Success.Value(client.session().isAuthorized()))
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getAuthenticationRequest(scopes: List<Scope>): Intent {
        return client.createAuthenticationIntent(scopes)
    }

    fun onAuthenticationResult(result: Intent) {
        launch(context = coroutineContext) {
            _authenticated.postValue(client.onAuthenticationResult(result))
        }
    }

    fun logout() {
        launch(context = coroutineContext) {
            when (val results = client.logout(false)) {
                is Results.Success -> _authenticated.postValue(Results.Success.Value(false))
                is Results.Failure -> _error.postValue(results.error)
            }
        }
    }

    fun loadCurrentUser() {
        launch(context = coroutineContext) {
            when (val results = client.getCurrentUser()) {
                is Results.Success.Value -> _currentUser.postValue(results.value)
                is Results.Failure -> _error.postValue(results.error)
            }
        }
    }
}

class SampleViewModelFactory(
    private val client: WakatimeClient
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SampleViewModel(client) as T
    }
}

@Suppress("unused", "EXPERIMENTAL_API_USAGE")
object Injector {

    fun providesViewModelFactory(context: Context): SampleViewModelFactory {
        return SampleViewModelFactory(getOauthClient(context))
    }

    private fun getApiClient(context: Context): WakatimeClient {
        return WakatimeClient.Builder(base64EncodedApiKey = "Your base64 encoded api key here")
            .build(context, EncryptedAuthStorage(getSharedPreferences(context)))
    }

    private fun getOauthClient(context: Context): WakatimeClient {
        return WakatimeClient.Builder(
            clientSecret = BuildConfig.SECRET,
            clientId = BuildConfig.APPID,
            redirectUri = Uri.parse(BuildConfig.REDIRECT_URI)
        ).network {
            val interceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }

            getOKHttpBuilder().apply {
                addInterceptor(interceptor)
                callTimeout(15, TimeUnit.SECONDS)
            }

            enableCache(context.cacheDir, 30)
        }.build(context, EncryptedAuthStorage(getSharedPreferences(context)))
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return MasterKey
            .Builder(context, "wakatime_sample_master_key")
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build().let {
                EncryptedSharedPreferences.create(
                    context,
                    "${context.packageName}_auth_prefs",
                    it,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
    }
}

private class EncryptedAuthStorage(val preferences: SharedPreferences) : AuthStorage {

    private var state: String? = null
    private var key: String? = null
    private var method: String? = null

    override fun getState(): String? = state ?: getString(KEY_AUTH_STATE)

    override fun setState(state: String): Unit = edit(KEY_AUTH_STATE, state).also { this.state = state }

    override fun getMethod(): String? = method ?: getString(KEY_METHOD)

    override fun setMethod(method: String): Unit = edit(KEY_METHOD, method).also { this.method = method }

    override fun getKey(): String? = key ?: getString(KEY_API_KEY)

    override fun setKey(key: String): Unit = edit(KEY_API_KEY, key).also { this.key = key }

    override fun clear() {
        state = null
        key = null
        method = null
        preferences.edit {
            this.clear()
        }
    }

    private fun edit(key: String, value: String) {
        preferences.edit {
            putString(key, value)
        }
    }

    private fun getString(key: String): String? = preferences.getString(key, null)

    private companion object {
        private const val KEY_AUTH_STATE = "auth_state"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_METHOD = "method"
    }
}