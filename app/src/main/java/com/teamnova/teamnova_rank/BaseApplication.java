package com.teamnova.teamnova_rank;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.airbnb.lottie.LottieAnimationView;

public class BaseApplication extends Application {
    private static BaseApplication baseApplication;
    AppCompatDialog progressDialog;

    public static BaseApplication getInstance() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
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
        rankLoadingAv.setAnimation("round_loading.json");
        rankLoadingAv.loop(true);
        rankLoadingAv.playAnimation();
        final TextView rankLoadingAlertTv = progressDialog.findViewById(R.id.rank_loading_alert_tv);
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
