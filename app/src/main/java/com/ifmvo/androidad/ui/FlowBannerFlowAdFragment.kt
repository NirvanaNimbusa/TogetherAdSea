package com.ifmvo.androidad.ui

import android.view.View
import android.view.View.INVISIBLE
import android.widget.*
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.ads.AdIconView
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAdLayout
import com.facebook.ads.NativeBannerAd
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.ifmvo.androidad.R
import com.ifmvo.androidad.adExtend.FlowBannerFlowAdManager
import com.ifmvo.quicklist.BaseRecyclerViewFragment

/*
 * (●ﾟωﾟ●) Google 原生 && Facebook 原生横幅 列表中展示
 * 
 * Created by Matthew_Chen on 2019-07-05.
 */
class FlowBannerFlowAdFragment : BaseRecyclerViewFragment<MutitypeBean, BaseViewHolder>() {

    private var nextAdPosition = 0//默认第 0 个内容后面加一个 AD
    private var lastUseAdPosition = 0 //无法描述 😝

    private val contentList = mutableListOf(
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅"),
        MyContentBean(R.mipmap.ic_logo, "Google 原生 && Facebook 原生横幅")
    )

    override fun initBeforeGetData() {
        FlowBannerFlowAdManager.requestAd(3)
    }

    override fun getData(currentPage: Int, showLoading: Boolean) {
        flTopView.postDelayed({

            nextAdPosition = 0
            lastUseAdPosition = 0

            val adList = FlowBannerFlowAdManager.getAdList()
            FlowBannerFlowAdManager.requestAd(3)

            val dataList = mutableListOf<MutitypeBean>()
            repeat(contentList.size) {
                dataList.add(MutitypeBean(MutitypeBean.type_content, contentList[it]))

                if (adList.isNotEmpty() && nextAdPosition == it) {
                    if (lastUseAdPosition > adList.size - 1) {
                        lastUseAdPosition = 0
                    }
                    when (val any = adList[lastUseAdPosition].realAd) {
                        is UnifiedNativeAd -> {
                            dataList.add(MutitypeBean(MutitypeBean.type_google, any))

                        }
                        is NativeBannerAd -> {
                            dataList.add(MutitypeBean(MutitypeBean.type_facebook, any))
                        }
                    }
                    lastUseAdPosition += 1
                    nextAdPosition += 3//每隔 n 个内容插一个广告
                }
            }
            handleListData(dataList, currentPage)

        }, 2000)//模拟网络加载效果
    }

    private fun convertContent(helper: BaseViewHolder, item: MyContentBean) {
        helper.setText(R.id.tvTxt, item.txtTitle).setImageResource(R.id.ivImage, item.imgRes)
    }

    private fun convertGoogle(helper: BaseViewHolder, nativeAd: UnifiedNativeAd) {
        // You must call destroy on old ads when you are done with them,
        // otherwise you will have a memory leak.
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        val adView = helper.getView<UnifiedNativeAdView>(R.id.UnifiedNativeAdView)
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline is guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd)
    }

    private fun convertFacebook(helper: BaseViewHolder, nativeBannerAd: NativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView()

        // Add the Ad view into the ad container.
        val nativeAdLayout = helper.getView<NativeAdLayout>(R.id.native_banner_ad_container)
//        val inflater = LayoutInflater.from(this@NativeBannerAdActivity)
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        val adView = helper.getView<LinearLayout>(R.id.ad_unit)
//        nativeAdLayout.addView(adView)

        // Add the AdChoices icon
        val adChoicesContainer = helper.getView<RelativeLayout>(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(mContext, nativeBannerAd, nativeAdLayout)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        val nativeAdTitle = helper.getView<TextView>(R.id.native_ad_title)
        val nativeAdSocialContext = helper.getView<TextView>(R.id.native_ad_social_context)
        val sponsoredLabel = helper.getView<TextView>(R.id.native_ad_sponsored_label)
        val nativeAdIconView = helper.getView<AdIconView>(R.id.native_icon_view)
        val nativeAdCallToAction = helper.getView<Button>(R.id.native_ad_call_to_action)

        // Set the Text.
        nativeAdCallToAction.text = nativeBannerAd.adCallToAction
        nativeAdCallToAction.visibility = if (nativeBannerAd.hasCallToAction()) View.VISIBLE else INVISIBLE
        nativeAdTitle.text = nativeBannerAd.advertiserName
        nativeAdSocialContext.text = nativeBannerAd.adSocialContext
        sponsoredLabel.text = nativeBannerAd.sponsoredTranslation

        // Register the Title and CTA button to listen for clicks.
        val clickableViews = mutableListOf<View>()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)
        clickableViews.add(nativeAdIconView)
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews)

    }

    override fun getRecyclerViewAdapter(): BaseQuickAdapter<MutitypeBean, BaseViewHolder> {
        return object : BaseMultiItemQuickAdapter<MutitypeBean, BaseViewHolder>(null) {

            init {
                addItemType(MutitypeBean.type_content, R.layout.list_item_content)
                addItemType(MutitypeBean.type_google, R.layout.list_item_google)
                addItemType(MutitypeBean.type_facebook, R.layout.list_item_facebook_flow_bannerflow)
            }

            override fun convert(helper: BaseViewHolder, item: MutitypeBean) {
                when (item.itemType) {
                    MutitypeBean.type_facebook -> {
                        convertFacebook(helper, item.adOrContentObject as NativeBannerAd)
                    }
                    MutitypeBean.type_google -> {
                        convertGoogle(helper, item.adOrContentObject as UnifiedNativeAd)
                    }
                    MutitypeBean.type_content -> {
                        convertContent(helper, item.adOrContentObject as MyContentBean)
                    }
                }
            }
        }
    }
}