package com.example.wakatimeclient.sample

import `is`.hth.wakatimeclient.WakatimeClient
import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.Scope
import `is`.hth.wakatimeclient.wakatime.data.model.CurrentUser
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wakatimeclient.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.openid.appauth.browser.BrowserMatcher
import net.openid.appauth.browser.BrowserSelector
import net.openid.appauth.browser.Browsers
import okhttp3.logging.HttpLoggingInterceptor
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

    companion object {

        private const val AUTHENTICATION_REQUEST_CODE = 100
    }

    private lateinit var binding: ActivityMainBinding

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
            when (authenticated) {
                is Results.Success.Value -> {
                    if (authenticated.value) {
                        onRefresh()
                    } else {
                        Toast.makeText(this, "Not authenticated!", Toast.LENGTH_LONG).show()
                    }
                }
                is Results.Failure -> {
                    Toast.makeText(this, "Not authenticated!", Toast.LENGTH_LONG).show()
                }
                else -> Toast.makeText(this, "Not authenticated!", Toast.LENGTH_LONG).show()
            }
        })

        model.error.observe(this, Observer {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        })
    }

    override fun onActivityResult(request: Int, result: Int, data: Intent?) {
        super.onActivityResult(request, result, data)
        if (request == AUTHENTICATION_REQUEST_CODE && data != null) {
            model.onAuthenticationResult(data)
        } else {
            longToast(getString(R.string.authentication_failed))
        }
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
            val scopes = listOf(
                Scope.Email,
                Scope.ReadPrivateLeaderboards,
                Scope.ReadLoggedTime,
                Scope.ReadStats,
                Scope.WriteLoggedTime
            )
            val intent = model.getAuthenticationRequest(scopes)
            startActivityForResult(intent, AUTHENTICATION_REQUEST_CODE)
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
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SampleViewModel(client) as T
    }
}

@Suppress("unused")
object Injector {

    private fun getApiClient(context: Context): WakatimeClient {
        return WakatimeClient.Builder(base64EncodedApiKey = "<Your base64 encoded api key here")
            .authenticator {

            }.network {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                getOKHttpBuilder().addInterceptor(interceptor)
            }.build(context)
    }

    private fun getOauthClient(context: Context): WakatimeClient {
        return WakatimeClient.Builder(
            secret = "Your client secret as generated by Wakatime",
            clientId = "You client id as generated by Wakatime",
            redirectUri = Uri.parse("The redirect schema you define in Wakatime.")
        ).authenticator {

        }.network {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            getOKHttpBuilder().addInterceptor(interceptor)
        }.build(context)
    }

    fun providesViewModelFactory(context: Context): SampleViewModelFactory {
        return SampleViewModelFactory(getOauthClient(context))
    }
}
