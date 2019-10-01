package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "스플래쉬 액티비티";
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        animationView = findViewById(R.id.splash_lottie);

        isNetworkConnected(getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }


    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("네트워크 연결 실패");
        builder.setMessage("네트워크를 연결하고 다시 앱을 실행해 주세요");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SplashActivity.this.finish();
            }
        });
        // 다이얼로그 밖에 화면 클릭 시 다이얼로그가 종료 되지 않게 하기
        builder.setCancelable(false);
        builder.show();
    }

    private boolean isNetworkConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // 모바일 네트워크
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        Log.v(TAG, "" + isMobileAvailable);
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        Log.v(TAG, "" + isMobileConnect);

        // 와이파이
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        Log.v(TAG, "" + isWifiAvailable);
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        Log.v(TAG, "" + isWifiConnect);


        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){

            animationView.setRepeatCount(1);
            animationView.playAnimation();
            animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    SplashActivity.this.finish();
                }
            });

            return true;
        }else{

            showDialog();

            return false;
        }
    }
}



