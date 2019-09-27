package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    //mainJavaBtn:메인에서 자바 작품들 보여주는 버튼
    //mainAndroidBtn:메인에서 안드로이드 작품들 보여주는 버튼
    //mainPhpBtn:메인에서 php 작품들 보여주는 버튼
    //mainDepth1Btn:메인에서 응용1단계 작품들 보여주는 버튼
    //mainDepth2Btn:메인에서 응용2단계 작품들 보여주는 버튼 
    private Button mainJavaBtn, mainAndroidBtn, mainPhpBtn, mainDepth1Btn, mainDepth2Btn;

    //topRankRecyclerview:1,2,3등 상위 랭킹 작품들을 보여주는 리사이클러뷰
    //downRankRecyclerview:1,2,3등 제외한 하위 랭킹 작품들을 보여주는 리사이클러뷰
    private RecyclerView topRankRecyclerview, downRankRecyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainJavaBtn = findViewById(R.id.main_java_btn);
        mainAndroidBtn = findViewById(R.id.main_android_btn);
        mainPhpBtn = findViewById(R.id.main_php_btn);
        mainDepth1Btn = findViewById(R.id.main_depth1_btn);
        mainDepth2Btn = findViewById(R.id.main_depth2_btn);


        //메인 자바버튼 클릭한 경우
        mainJavaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //자바버튼만 selected(선택되고 나머지는 기본상태)
                    case R.id.main_java_btn:
                        mainJavaBtn.setSelected(true);
                        mainAndroidBtn.setSelected(false);
                        mainPhpBtn.setSelected(false);
                        mainDepth1Btn.setSelected(false);
                        mainDepth2Btn.setSelected(false);
                        break;
                }
            }
        });

        //메인 안드로이드버튼 클릭한 경우
        mainAndroidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //안드로이드버튼만 selected(선택되고 나머지는 기본상태)
                    case R.id.main_android_btn:
                        mainJavaBtn.setSelected(false);
                        mainAndroidBtn.setSelected(true);
                        mainPhpBtn.setSelected(false);
                        mainDepth1Btn.setSelected(false);
                        mainDepth2Btn.setSelected(false);
                        break;
                }

            }
        });

        //메인 php버튼 클릭한 경우
        mainPhpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //php버튼만 selected(선택되고 나머지는 기본상태)
                    case R.id.main_php_btn:
                        mainJavaBtn.setSelected(false);
                        mainAndroidBtn.setSelected(false);
                        mainPhpBtn.setSelected(true);
                        mainDepth1Btn.setSelected(false);
                        mainDepth2Btn.setSelected(false);
                        break;
                }
            }
        });

        //메인 응용1단계버튼 클릭한 경우
        mainDepth1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //응용1단계버튼만 selected(선택되고 나머지는 기본상태)
                    case R.id.main_depth1_btn:
                        mainJavaBtn.setSelected(false);
                        mainAndroidBtn.setSelected(false);
                        mainPhpBtn.setSelected(false);
                        mainDepth1Btn.setSelected(true);
                        mainDepth2Btn.setSelected(false);
                        break;
                }
            }
        });

        //메인 응용2단계버튼 클릭한 경우
        mainDepth2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //응용2단계버튼만 selected(선택되고 나머지는 기본상태)
                    case R.id.main_depth2_btn:
                        mainJavaBtn.setSelected(false);
                        mainAndroidBtn.setSelected(false);
                        mainPhpBtn.setSelected(false);
                        mainDepth1Btn.setSelected(false);
                        mainDepth2Btn.setSelected(true);
                        break;
                }
            }
        });
    }
}
