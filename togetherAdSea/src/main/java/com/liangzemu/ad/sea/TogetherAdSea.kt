package com.liangzemu.ad.sea

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds

/* 
 * (●ﾟωﾟ●)
 * 
 * Created by Matthew_Chen on 2019-04-22.
 */
object TogetherAdSea {

    /**
     * 位ID的Map
     */
    var idMapGoogle = mutableMapOf<String, String?>()
        private set
    var idMapFacebook = mutableMapOf<String, String?>()
        private set

    var idListGoogleMap = mutableMapOf<String, MutableList<String>>()

    var idListFacebookMap = mutableMapOf<String, MutableList<String>>()
    /**
     * google测试id
     */
    var testDeviceID: String? = null
        private set
    /**
     * 当前正在加载中的广告
     */
    val loadingAdTask = HashMap<String, Int>() //HashMap<adConstStr,lastLevel>()
    /**
     * 保存application
     */
    lateinit var context: Application
    /**
     * 超时时间
     */
     var timeoutMillsecond: Long=10000
    /**
     * 缓存广告
     */
    internal val adCacheMap = HashMap<String, Any>()//HashMap<adConstStr,AD>()

    /**
     * 超时时间
     */
//    var timeOutMillis: Long = 5000
//        private set
    /**
     *
     * @param channel String 标识 google/facebook的key
     * @param type String
     * @param vertical Boolean
     * @return Unit
     */

    /**
     * 初始化广告
     */
    fun initAdGoogle(
        @NonNull context: Application, @NonNull googleAdAppId: String, googleIdMap: MutableMap<String, MutableList<String>>,
        testDeviceID: String? = null
    ) {
        this.context = context
        idListGoogleMap = googleIdMap
        MobileAds.initialize(context, googleAdAppId)
        this.testDeviceID = testDeviceID
    }

    fun initAdFacebook(
        @NonNull context: Application, @NonNull facebookIdMap: MutableMap<String, MutableList<String>>, testMode: Boolean = false
    ) {
        this.context = context
        idListFacebookMap = facebookIdMap
        // Example for setting the SDK to crash when in debug mode
        AudienceNetworkAds.isInAdsProcess(context)
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
        AudienceNetworkAds.initialize(context)
        AdSettings.setTestMode(testMode)
    }

    /**
     * 初始化广告
     */
    fun initGoogleAd(@NonNull context: Context, @NonNull googleAdAppId: String, googleIdMap: MutableMap<String, String?>) {
        idMapGoogle = googleIdMap
//        MobileAds.initialize(context, googleAdAppId)
    }

    fun initFacebookAd(@NonNull context: Context, @NonNull facebookIdMap: MutableMap<String, String?>) {
        idMapFacebook = facebookIdMap
        // Example for setting the SDK to crash when in debug mode
//        AudienceNetworkAds.isInAdsProcess(context)
//        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
//        AudienceNetworkAds.initialize(context)
    }

//    fun setAdTimeOutMillis(millis: Long) {
//        timeOutMillis = millis
//    }
}