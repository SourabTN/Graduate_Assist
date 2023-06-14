package com.mdnhs.graduateassist.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.DataList;
import com.mdnhs.graduateassist.util.Constant;
import com.mdnhs.graduateassist.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.wortise.ads.natives.GoogleNativeAd;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Method method;
    private Activity activity;
    private String string;
    private int columnWidth;
    private List<DataList> dataLists;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_Ad = 2;

    public DataAdapter(Activity activity, List<DataList> dataLists, String string, OnClick onClick) {
        this.activity = activity;
        this.string = string;
        this.dataLists = dataLists;
        method = new Method(activity, onClick);
        columnWidth = (method.getScreenWidth());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.data_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType == VIEW_TYPE_Ad) {
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_ads, parent, false);
            return new AdOption(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.con.setLayoutParams(new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, columnWidth / 3 + 80));

            Glide.with(activity).load(dataLists.get(position).getMovie_thumbnail_s())
                    .placeholder(R.drawable.placeholder_portable).into(viewHolder.imageView);

            viewHolder.textViewLagName.setText(dataLists.get(position).getLanguage_name());
            viewHolder.textViewName.setText(dataLists.get(position).getMovie_title());
            viewHolder.ratingView.setRating(Float.parseFloat(dataLists.get(position).getRate_avg()));
            viewHolder.textViewRating.setText("(" + dataLists.get(position).getRate_avg()
                    + "/" + dataLists.get(position).getTotal_rate() + ")");

            try {
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(GradientDrawable.RECTANGLE);
                gd.setColor(Color.parseColor(dataLists.get(position).getLanguage_bg()));
                if (method.isRtl()) {
                    gd.setCornerRadii(new float[]{40.0f, 40.0f, 0, 0, 0, 0, 40.0f, 40.0f}); // Make the border rounded border corner radius
                } else {
                    gd.setCornerRadii(new float[]{0, 0, 40.0f, 40.0f, 40.0f, 40.0f, 0, 0}); // Make the border rounded border corner radius
                }
                viewHolder.textViewLagName.setBackground(gd);
            } catch (Exception e) {
                Log.d("error_show", e.toString());
            }

            viewHolder.con.setOnClickListener(v -> method.click(position, string, dataLists.get(position).getId(), dataLists.get(position).getMovie_title()));
        } else if (holder.getItemViewType() == VIEW_TYPE_Ad) {
            AdOption adOption = (AdOption) holder;
            if (adOption.rl_native_ad.getChildCount() == 0 && Constant.appRP.isNative_ad() && !adOption.isAdRequested) {

                adOption.isAdRequested = true;

                switch (Constant.appRP.getNative_ad_type()) {
                    case Constant.AD_TYPE_ADMOB:
                    case Constant.AD_TYPE_FACEBOOK:

                        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);

                        AdLoader adLoader = new AdLoader.Builder(activity, Constant.appRP.getNative_ad_id())
                                .forNativeAd(nativeAd -> {
                                    populateUnifiedNativeAdView(nativeAd, adView);
                                    adOption.rl_native_ad.removeAllViews();
                                    adOption.rl_native_ad.addView(adView);
                                    adOption.card_view.setVisibility(View.VISIBLE);
                                })
                                .build();

                        AdRequest.Builder builder = new AdRequest.Builder();
                        if (Method.personalization_ad) {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                        }
                        adLoader.loadAd(builder.build());

                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        StartAppNativeAd nativeAd = new StartAppNativeAd(activity);

                        nativeAd.loadAd(new NativeAdPreferences()
                                .setAdsNumber(1)
                                .setAutoBitmapDownload(true)
                                .setPrimaryImageSize(2), new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                try {
                                    if (nativeAd.getNativeAds().size() > 0) {
                                        RelativeLayout nativeAdView = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);

                                        ImageView icon = nativeAdView.findViewById(R.id.icon);
                                        TextView title = nativeAdView.findViewById(R.id.title);
                                        TextView description = nativeAdView.findViewById(R.id.description);
                                        Button button = nativeAdView.findViewById(R.id.button);

                                        Glide.with(activity)
                                                .load(nativeAd.getNativeAds().get(0).getImageUrl())
                                                .into(icon);
                                        title.setText(nativeAd.getNativeAds().get(0).getTitle());
                                        description.setText(nativeAd.getNativeAds().get(0).getDescription());
                                        button.setText(nativeAd.getNativeAds().get(0).isApp() ? "Install" : "Open");

                                        adOption.rl_native_ad.removeAllViews();
                                        adOption.rl_native_ad.addView(nativeAdView);
                                        adOption.card_view.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailedToReceiveAd(Ad ad) {
                                adOption.isAdRequested = false;
                            }
                        });
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.appRP.getNative_ad_id(), activity);
                        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                            @Override
                            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                nativeAdView.setPadding(0, 0, 0, 10);
                                nativeAdView.setBackgroundColor(Color.WHITE);
                                adOption.rl_native_ad.removeAllViews();
                                adOption.rl_native_ad.addView(nativeAdView);
                                adOption.card_view.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                adOption.isAdRequested = false;
                            }

                            @Override
                            public void onNativeAdClicked(final MaxAd ad) {
                            }
                        });

                        nativeAdLoader.loadAd();
                        break;
                    case Constant.AD_TYPE_WORTISE:
                        GoogleNativeAd googleNativeAd = new GoogleNativeAd(activity, Constant.appRP.getNative_ad_id(), new GoogleNativeAd.Listener() {
                            @Override
                            public void onNativeClicked(@NonNull GoogleNativeAd googleNativeAd) {

                            }

                            @Override
                            public void onNativeFailed(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.wortise.ads.AdError adError) {
                                adOption.isAdRequested = false;
                            }

                            @Override
                            public void onNativeImpression(@NonNull GoogleNativeAd googleNativeAd) {

                            }

                            @Override
                            public void onNativeLoaded(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                                NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_wortise, null);
                                populateUnifiedNativeAdView(nativeAd, adView);
                                adOption.rl_native_ad.removeAllViews();
                                adOption.rl_native_ad.addView(adView);
                                adOption.card_view.setVisibility(View.VISIBLE);
                            }
                        });
                        googleNativeAd.load();
                        break;
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return dataLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return position == dataLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataLists.size() == position) {
            return VIEW_TYPE_LOADING;
        } else if (dataLists.get(position) == null) {
            return VIEW_TYPE_Ad;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ConstraintLayout con;
        private RatingView ratingView;
        private MaterialTextView textViewName, textViewRating, textViewLagName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            con = itemView.findViewById(R.id.con_movie_adapter);
            imageView = itemView.findViewById(R.id.imageView_movie_adapter);
            ratingView = itemView.findViewById(R.id.ratingBar_movie_adapter);
            textViewLagName = itemView.findViewById(R.id.textView_lag_movie_adapter);
            textViewName = itemView.findViewById(R.id.textView_name_movie_adapter);
            textViewRating = itemView.findViewById(R.id.textView_rating_movie_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

    public class AdOption extends RecyclerView.ViewHolder {

        private CardView card_view;
        private final RelativeLayout rl_native_ad;
        private boolean isAdRequested;

        public AdOption(View itemView) {
            super(itemView);
            card_view = itemView.findViewById(R.id.card_view);
            rl_native_ad = itemView.findViewById(R.id.rl_native_ad);
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }
}
