package com.teamnova.teamnova_rank;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class BaseApplication extends Application {
    private static BaseApplication baseApplication;
    AppCompatDialog progressDialog;
    /* admob */
    private RewardedAd rewardedAd;
    // admob app key - 테스트용
    private final String AD_TEST_KEY = "ca-app-pub-3940256099942544/5224354917";
    // admob app key - 실제
    private final String AD_REAL_KEY = "ca-app-pub-7513442765578534~3381765090";

    public static BaseApplication getInstance() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

        /* 광고 영상 초기화 */
        rewardedAd = new RewardedAd(this, AD_TEST_KEY);
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    /**
     * admob RewardedAd클래스 - loadAd 메소드의 콜백 메소드 정의
     */
    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {

        /**
         * 광고 로딩 완료 후
         */
        @Override
        public void onRewardedAdLoaded() {
            // Ad successfully loaded.
            Log.d("광고 로딩 완료 !!","성공!!!!");
        }

        /**
         * 하단 google api 참조
         *
         * 경고: onRewardedAdFailedToLoad() 메소드에서 새 광고를 로드하려고 시도하는 것은 권장되지 않습니다.
         *
         * ERROR_CODE_INTERNAL_ERROR: 광고 서버에서 잘못된 응답을 받는 등 내부적으로 오류가 발생했다는 의미입니다.
         * ERROR_CODE_INVALID_REQUEST: 광고 단위 ID가 잘못된 경우처럼 광고 요청이 잘못되었다는 의미입니다.
         * ERROR_CODE_NETWORK_ERROR: 네트워크 연결로 인해 광고 요청에 성공하지 못했다는 의미입니다.
         * ERROR_CODE_NO_FILL: 광고 요청에는 성공했지만 광고 인벤토리의 부족으로 광고가 반환되지 않았다는 의미입니다.
         * @param errorCode
         */
        @Override
        public void onRewardedAdFailedToLoad(int errorCode) {
            // Ad failed to load.
        }
    };

    /**
     * RewardedAd 객체는 일회용 객체로 다시 광고를 재생할 수 없다. 광고가 끝난 메소드 호출 시
     * 다른 광고를 로드한 후 넣어준다.
     * @return
     */
    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.show();

         }


        LottieAnimationView rankLoadingAv = progressDialog.findViewById(R.id.rank_loading_av);
        final TextView rankLoadingAlertTv = progressDialog.findViewById(R.id.rank_loading_alert_tv);
        TextView adRecommendTv = progressDialog.findViewById(R.id.ad_recommend_tv);
        adRecommendTv.setText(Html.fromHtml("<u style=\"color:#FFFFFF\">오래걸리는데.. 광고 한편 보실래요?</u>"));
        adRecommendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd.isLoaded()) {
                    Activity thisActivity = progressDialog.getOwnerActivity();
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        public void onRewardedAdOpened() {
                            // Ad opened.
                            Log.d("광고 오픈","onRewardedAdOpened");
                        }

                        public void onRewardedAdClosed() {
                            // Ad closed.
                            Log.d("광고 끝남","true");

                            // 광고 다시 재생을 위해서는 광고 영상을 다시 받아와야함
                            rewardedAd = createAndLoadRewardedAd();

                        }

                        public void onUserEarnedReward(@NonNull RewardItem reward) {

                        }

                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                        }
                    };
                    rewardedAd.show(thisActivity, adCallback);
                } else {
                    Toast.makeText(BaseApplication.this, "광고 로딩중입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rankLoadingAv.setAnimation("round_loading.json");
        rankLoadingAv.loop(true);
        rankLoadingAv.playAnimation();

        if (!TextUtils.isEmpty(message)) {
            rankLoadingAlertTv.setText(message);
        }


    }

    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }


        TextView rankLoadingAlertTv = progressDialog.findViewById(R.id.rank_loading_alert_tv);
        if (!TextUtils.isEmpty(message)) {
            rankLoadingAlertTv.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
