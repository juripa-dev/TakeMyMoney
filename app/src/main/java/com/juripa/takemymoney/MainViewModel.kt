package com.juripa.takemymoney

import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.juripa.takemymoney.contract.HealthToken
import com.juripa.takemymoney.contract.LockEth
import com.juripa.takemymoney.gas.CallGasProvider
import com.juripa.takemymoney.gas.SendGasProvider
import com.juripa.takemymoney.gas.WeiConverter
import com.juripa.takemymoney.location.LatLongDistance
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.security.Security

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private val tokenContractAddress = BuildConfig.TOKEN_CONTRACT
        private val lockEthContractAddress = BuildConfig.LOCK_ETH_CONTRACT
        private const val MINT_AMOUNT = 1_000_000_000_000_000_000L
    }

    private val geocoder by lazy { Geocoder(App.context) }

    private val web3 = Web3j.build(HttpService(BuildConfig.INFURA_URL))

    // Must be unique and don't forgot password.
    private val password = BuildConfig.PASSWARD

    // value
    private val _tokenBalance = MutableStateFlow(BigDecimal.valueOf(0))
    val tokenBalance = _tokenBalance.asStateFlow()

    private val _lockEthBalance = MutableStateFlow(BigDecimal.valueOf(0))
    val lockEthBalance = _lockEthBalance.asStateFlow()

    private val _ethBalance = MutableStateFlow(BigDecimal.valueOf(0))
    val ethBalance = _ethBalance.asStateFlow()

    private val _chainAddress = MutableStateFlow("")
    val chainAddress = _chainAddress.asStateFlow()

    private val _realAddress = MutableStateFlow("")
    val realAddress = _realAddress.asStateFlow()

    private val _alreadyStarted = MutableStateFlow(true)
    val alreadyStarted = _alreadyStarted.asStateFlow()

    // event
    private val _showToast = MutableSharedFlow<String>()
    val showToast = _showToast.asSharedFlow()

    private val _loadEtherScan = MutableSharedFlow<String>()
    val loadUrl = _loadEtherScan.asSharedFlow()

    private val _showProgress = MutableSharedFlow<Unit>()
    val showProgress = _showProgress.asSharedFlow()

    private val _hideProgress = MutableSharedFlow<Unit>()
    val hideProgress = _hideProgress.asSharedFlow()

    private val _requestLocation = MutableSharedFlow<(Double, Double) -> Unit>()
    val requestLocation = _requestLocation.asSharedFlow()

    private val _pointMarker = MutableSharedFlow<LatLng>()
    val pointMarker = _pointMarker.asSharedFlow()

    private val _releaseRefresh = MutableSharedFlow<Unit>()
    val releaseRefresh = _releaseRefresh.asSharedFlow()

    private val _findLocation = MutableSharedFlow<Unit>()
    val findLocation = _findLocation.asSharedFlow()

    private lateinit var credentials: Credentials
    private lateinit var tokenSendContract: HealthToken
    private lateinit var tokenCallContract: HealthToken
    private lateinit var lockEthContract: LockEth

    private var savedLocation = getSavedTargetLocation()

    fun onInit() {
        setupBouncyCastle()
        initNode()
        checkStartState()
        refreshBalance()
    }

    private fun initNode() {
        // init ethereum node
        viewModelScope.launch {
            try {
                val clientVersion = web3.web3ClientVersion().sendAsync().get()
                if (!clientVersion.hasError()) {
                    _showToast.emit("Connected Node")
                } else {
                    _showToast.emit(clientVersion.error.message)
                }
            } catch (e: Exception) {
                _showToast.emit(e.message.toString())
            }
        }

        // init wallet

        // fixme
        val myPath = "juripa"
        val walletFileDir = File("${App.context.filesDir}$myPath")

        if (!walletFileDir.mkdirs()) {
            walletFileDir.mkdirs()
        }

        val walletNameKey = "walletName_$password"
        val savedWalletName = Prefs.getString(App.context, walletNameKey)

        val walletName: String

        if (savedWalletName.isNullOrEmpty()) {
            walletName = WalletUtils.generateLightNewWalletFile(password, walletFileDir)
            Prefs.setString(App.context, walletNameKey, walletName)
        } else {
            walletName = savedWalletName
        }

        // init credential
        credentials = WalletUtils.loadCredentials(password, File(walletFileDir, "/$walletName"))
        _chainAddress.value = credentials.address
        Log.d("takemymoneytest", "address : ${credentials.address}")

        // init contract
        tokenSendContract = HealthToken.load(
            tokenContractAddress,
            web3,
            credentials,
            SendGasProvider
        )

        tokenCallContract = HealthToken.load(
            tokenContractAddress,
            web3,
            credentials,
            CallGasProvider
        )

        lockEthContract = LockEth.load(
            lockEthContractAddress,
            web3,
            credentials,
            SendGasProvider
        )
    }

    private fun checkStartState() {
        _alreadyStarted.value = Prefs.getString(App.context, "started")?.isNotEmpty() == true
    }

    fun onInitMap() {
        if (savedLocation != null) {
            setTargetLocation(savedLocation!!.latitude, savedLocation!!.longitude)
        }
    }

    fun onClickStart() {
        Prefs.setString(App.context, "started", TimeUtil.nowToString())
        _alreadyStarted.value = true

        lockEthContract
            .deposit(TokenRule.LOCK_ETH)
            .sendAsync()
    }

    fun onClickSaveLocation() {
        val currentTokenBalance = tokenBalance.value.toInt()

        if (savedLocation == null || currentTokenBalance >= TokenRule.ENABLE_LOCATION_CHANGE_TOKEN_NUMBER) {
            viewModelScope.launch { _findLocation.emit(Unit) }
        } else {
            viewModelScope.launch { _showToast.emit("Insufficient token!") }
        }
    }

    fun onClickVerify() {
        if (!enableMintToken()) {
            viewModelScope.launch { _showToast.emit("Already verified") }
            return
        }

        viewModelScope.launch {
            _showProgress.emit(Unit)

            _requestLocation.emit { lat, long ->

                viewModelScope.launch {
                    _hideProgress.emit(Unit)
                    checkDistance(LatLng(lat, long))
                }
            }
        }
    }

    fun onClickEtherScan() {
        viewModelScope.launch {
            _loadEtherScan.emit("https://ropsten.etherscan.io/address/${chainAddress.value}")
        }
    }

    fun onRefresh() {
        viewModelScope.launch { _releaseRefresh.emit(Unit) }
        refreshBalance()
    }

    fun setTargetLocation(lat: Double, long: Double) {
        savedLocation = LatLng(lat, long)
        getRealLocation(lat, long)
        setMapMarker(lat, long)
        saveTargetLocation(lat, long)
    }

    private fun enableMintToken(): Boolean {
        val lastMintingTime = Prefs.getString(App.context, "mintTime")

        return if (lastMintingTime.isNullOrEmpty() || !TimeUtil.isOverToday(lastMintingTime)) {
            Prefs.setString(App.context, "mintTime", TimeUtil.nowToString())
            true
        } else {
            false
        }
    }

    private fun checkDistance(current: LatLng) {
        val distance = LatLongDistance.greatCircleInMeters(savedLocation!!, current)

        viewModelScope.launch {
            if (distance < 100) {
                mintToken()

                _showToast.emit("Success")
            } else {
                _showToast.emit("Fail")
            }
        }
    }

    private fun mintToken() {
        Log.d("takemymoneytest", "mint address : ${credentials.address}")
        tokenSendContract
            .mintToken(
                chainAddress.value,
                BigInteger.valueOf(MINT_AMOUNT)
            )
            .sendAsync()

        refreshBalance()
    }

    private fun getSavedTargetLocation(): LatLng? {
        val lat = Prefs.get(App.context, "lat")
        val long = Prefs.get(App.context, "long")

        return if (lat != null && long != null) {
            LatLng(lat, long)
        } else {
            null
        }
    }

    private fun getRealLocation(lat: Double, long: Double) {
        var realLocationAddress: String? = null

        try {
            realLocationAddress = geocoder.getFromLocation(lat, long, 10)?.get(0)?.getAddressLine(0)
        } catch (exception: java.lang.Exception) {

        }

        if (realLocationAddress?.isNotEmpty() == true) {
            _realAddress.value = realLocationAddress
        }
    }

    private fun setMapMarker(lat: Double, long: Double) {
        viewModelScope.launch {
            _pointMarker.emit(LatLng(lat, long))
        }
    }

    private fun saveTargetLocation(lat: Double, long: Double) {
        Prefs.setDouble(App.context, "lat", lat)
        Prefs.setDouble(App.context, "long", long)
    }

    private fun refreshBalance() {
        checkCurrentEthBalance()
        checkLockedEthBalance()
        checkCurrentTokenBalance()
    }

    private fun checkCurrentTokenBalance() {
        val balance = tokenCallContract.balanceOf(credentials.address).sendAsync().get()
        _tokenBalance.value = fromWei(BigDecimal(balance))
    }

    private fun checkLockedEthBalance() {
        val balance = lockEthContract.balanceOf(credentials.address).sendAsync().get()
        _lockEthBalance.value = fromWei(BigDecimal(balance))
    }

    private fun checkCurrentEthBalance() {
        val balanceWei = web3.ethGetBalance(
            credentials.address,
            DefaultBlockParameterName.LATEST
        )
            .sendAsync()
            .get()

        _ethBalance.value = fromWei(BigDecimal(balanceWei.balance))
    }

    private fun fromWei(wei: BigDecimal): BigDecimal {
        return WeiConverter.fromWei(wei, WeiConverter.Unit.ETHER)
    }

    private fun setupBouncyCastle() {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up a provider  when it's used for the first time.
            return
        if (provider is BouncyCastleProvider) {
            return
        }
        //There is a possibility  the bouncy castle registered by android may not have all ciphers
        //so we  substitute with the one bundled in the app.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}