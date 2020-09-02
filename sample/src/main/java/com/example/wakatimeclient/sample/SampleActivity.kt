package com.example.wakatimeclient.sample

import `is`.hth.wakatimeclient.WakatimeClient
import `is`.hth.wakatimeclient.core.data.Error
import `is`.hth.wakatimeclient.core.data.Results
import `is`.hth.wakatimeclient.core.data.auth.Scope
import `is`.hth.wakatimeclient.wakatime.model.CurrentUser
import android.app.Activity
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
import net.openid.appauth.browser.BrowserSelector
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
            if (authenticated) {
                loadCurrentUser()
            } else {
                Toast.makeText(this, "Not authenticated!", Toast.LENGTH_LONG).show()
            }
        })

        model.error.observe(this, Observer {

        })
    }

    override fun onActivityResult(request: Int, result: Int, data: Intent?) {
        super.onActivityResult(request, result, data)
        if (request == AUTHENTICATION_REQUEST_CODE && result == Activity.RESULT_OK && data != null) {
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
            val scopes = listOf(Scope.Email, Scope.ReadPrivateLeaderboards)
            val intent = model.getAuthenticationRequest(scopes)
            startActivityForResult(intent, AUTHENTICATION_REQUEST_CODE)
        }
    }

    override fun onRevoke(view: View): Unit = model.logout()

    override fun loadCurrentUser(): Unit = model.loadCurrentUser()

    //
    //              SampleActivityDataSource implementation
    //////////////////////////////////////////////////////////////////////////

    override fun onAuthentication(): LiveData<Boolean> = model.authentication

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

    private val _authenticated: MutableLiveData<Boolean> =
        MutableLiveData(client.session().isAuthorized())
    val authentication: LiveData<Boolean> = _authenticated

    private val _currentUser: MutableLiveData<CurrentUser> = MutableLiveData()
    val currentUser: LiveData<CurrentUser> = _currentUser

    private val _error: MutableLiveData<Error> = MutableLiveData()
    val error: LiveData<Error> = _error

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

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
            when(val results = client.logout(false)) {
                is Results.Success -> _authenticated.postValue(false)
                is Results.Failure -> _error.postValue(results.error)
            }
        }
    }

    fun loadCurrentUser() {
        launch(context = coroutineContext) {
            when (val results = client.users.getCurrentUser()) {
                is Results.Failure -> {

                }
                is Results.Success.Values -> {
                    _currentUser.postValue(results.data)
                }
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

object Injector {

    private fun getApiClient(context: Context): WakatimeClient {
        return WakatimeClient.Builder(base64EncodedApiKey = "")
            .authenticator {

            }.network {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                getOKHttpBuilder().addInterceptor(interceptor)
            }.build(context)
    }

    private fun getOauthClient(context: Context): WakatimeClient {
        return WakatimeClient.Builder(
            secret = "sec_qyQkp5sDduZubb4WCZQk3pelLYDHUDHILSZbdiivii0Ri9Tfz9OHjSDcZLnUnwjUBzvaDKI2BSEHYaza",
            clientId = "ExpoVG2FuEHvIH6regUiasgX",
            redirectUri = Uri.parse("vakta://grant-callback")
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
