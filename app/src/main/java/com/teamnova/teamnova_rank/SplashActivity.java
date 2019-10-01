package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //리시버등록
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);



    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    //와이파이 상태변화 수신
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){

                //네트워크 상태변화
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    //네트워크 상태값 가져오기
                    NetworkInfo.DetailedState state = info.getDetailedState();

                    String typename = info.getTypeName();
                    if(state==NetworkInfo.DetailedState.CONNECTED){ //네트워크 연결
                        Handler hd = new Handler();

                        hd.postDelayed(new splashhandler(), 1000); // 1초 후에 hd handler 실행  3000ms = 3초
                    }
                    else if(state==NetworkInfo.DetailedState.DISCONNECTED){ //네트워크 끊음

                        standardDialog();//네트워크 연결하라는 다이얼로그 띄우기
                    }
                    break;
            }
        }
    };

    //리시버 해제
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    //목록다이어로그에서 작품 순위 산정 기준을 선택했을 때 보여주는 다이어로그
    public void standardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//다이어로그 생성해줍니다
        builder.setTitle("WIFI 연결상태확인");//다이어로그 제목
        builder.setMessage("WIFI를 연결해주세요");//다이어로그 내용
        builder.show();//다이어로그 보여주기
    }
}



