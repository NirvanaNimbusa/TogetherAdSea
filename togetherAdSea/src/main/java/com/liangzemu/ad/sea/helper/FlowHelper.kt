package com.liangzemu.ad.sea.helper

import android.os.CountDownTimer
import androidx.annotation.NonNull
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.liangzemu.ad.sea.*
import com.liangzemu.ad.sea.TogetherAdSea.context
import com.liangzemu.ad.sea.other.*


/**
 * 原生广告
 *
 * Created by Matthew_Chen on 2019-04-22.
 */

class FlowHelper(adConstStr: String,destroyAfterShow:Boolean=true) : BaseAdHelp(adConstStr,destroyAfterShow) {
    @Throws(IllegalArgumentException::class)
    override fun initAD(id: String, adNameType: AdNameType): Pair<Any, String> {
        return when(adNameType){
            AdNameType.GOOGLE_ADMOB->{
                val ad = AdLoader.Builder(context, id)
                Pair(ad,ad.toString())
            }
            AdNameType.FACEBOOK->{
                val ad = NativeAd(context, id)
                Pair(ad,ad.toString())
            }
            else ->{
                throw IllegalArgumentException("没有此广告类型:${adNameType.type}")
            }
        }
    }

    override fun setGoogleAdListenerAndStart(
        id: String,
        adOrBuilder: Any,
        adListener: IAdListener,
        timer: CountDownTimer,
        errorCallback: (String?) -> Unit
    ) {
        adOrBuilder as AdLoader.Builder
        adOrBuilder.forUnifiedNativeAd { ad: UnifiedNativeAd ->
            logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.prepared)}")
            timer.cancel()
            adListener.onAdPrepared(AdNameType.GOOGLE_ADMOB.type, AdWrapper(ad,adOrBuilder.toString()))
        }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    errorCallback(errorCode.toString())
                }

                override fun onAdImpression() {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.exposure)}")
                    //(TogetherAdSea.adCacheMap[adConstStr] as ArrayList<Any>).remove()
                    adListener.onAdShow(AdNameType.GOOGLE_ADMOB.type,adOrBuilder.toString())
                }

                override fun onAdClicked() {
                    logd("${AdNameType.GOOGLE_ADMOB.type}: ${context.getString(R.string.clicked)}")
                    adListener.onAdClick(AdNameType.GOOGLE_ADMOB.type,adOrBuilder.toString())
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()
            .loadAd(getGoogleAdRequest())
    }

    override fun setFaceBookAdListenerAndStart(
        adOrBuilder: Any,
        adListener: IAdListener,
        timer: CountDownTimer,
        errorCallback: (String?) -> Unit
    ) {
        adOrBuilder as NativeAd
        adOrBuilder.setAdListener(object : NativeAdListener {
            override fun onAdClicked(ad: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.clicked)}")
                adListener.onAdClick(AdNameType.FACEBOOK.type,ad.toString())
            }

            override fun onMediaDownloaded(ad: Ad?) {
            }

            override fun onError(ad: Ad, adError: AdError?) {
                errorCallback(adError?.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.prepared)}")
                timer.cancel()
                adListener.onAdPrepared(AdNameType.FACEBOOK.type, AdWrapper(ad,ad.toString()))
            }

            override fun onLoggingImpression(ad: Ad) {
                logd("${AdNameType.FACEBOOK.type}: ${context.getString(R.string.exposure)}")
                adListener.onAdShow(AdNameType.FACEBOOK.type,ad.toString())
            }
        })
        adOrBuilder.loadAd()
    }

}


