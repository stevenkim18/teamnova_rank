package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /* activity_main레이아웃 위젯 */
    TextView textView;
    Button button;
    CheckBox checkbox; //테스트변수

    // 슬랙 깃허브 연동 테스트
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}
