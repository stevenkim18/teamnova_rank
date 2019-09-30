package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    //mainJavaStepBtn:mainJavaStepBtn을 클릭하면 자바 작품들이 RankRecyclerview에 보인다.
    //mainAndroidStepBtn:mainAndroidStepBtn을 클릭하면 안드로이드 작품들이 RankRecyclerview에 보인다.
    //mainPhpStepBtn:mainPhpStepBtn을 클릭하면 PHP 작품들이 RankRecyclerview에 보인다.
    //mainHard1StepBtn:mainHard1StepBtn을 클릭하면 응용1단계 작품들이 RankRecyclerview에 보인다.
    //mainHard2StepBtn:mainHard2StepBtn을 클릭하면 응용2단계 작품들이 RankRecyclerview에 보인다.
    private Button mainJavaStepBtn, mainAndroidStepBtn, mainPhpStepBtn, mainHard1StepBtn,
            mainHard2StepBtn;


    //RankRecyclerview:선택한 작품들을 보여주는 리사이클러뷰
    private RecyclerView RankRecyclerview;

    //mainToolbar:메인액티비티에서 사용하는 툴바
    private Toolbar mainToolbar;


    // 슬랙 깃허브 연동 테스트
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mainJavaStepBtn = findViewById(R.id.main_java_step_btn);
        mainAndroidStepBtn = findViewById(R.id.main_android_step_btn);
        mainPhpStepBtn = findViewById(R.id.main_php_step_btn);
        mainHard1StepBtn = findViewById(R.id.main_hard1_step_btn);
        mainHard2StepBtn = findViewById(R.id.main_hard2_step_btn);

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.setTitle(""); //메인 툴바에 나오는 앱 제목을 지우기 위해 공백으로 표현

        setSupportActionBar(mainToolbar);//메인 액티비티에서 툴바를 사용하기 위해


        //메인 자바버튼 클릭한 경우
        mainJavaStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    //자바버튼만 selected(선택)되어서 자바버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_java_step_btn:
                        mainJavaStepBtn.setSelected(true);
                        mainAndroidStepBtn.setSelected(false);
                        mainPhpStepBtn.setSelected(false);
                        mainHard1StepBtn.setSelected(false);
                        mainHard2StepBtn.setSelected(false);
                        break;
                }
            }
        });

        //메인 안드로이드버튼 클릭한 경우
        mainAndroidStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //안드로이드버튼만 selected(선택)되어서 안드로이드버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_android_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(true);
                        mainPhpStepBtn.setSelected(false);
                        mainHard1StepBtn.setSelected(false);
                        mainHard2StepBtn.setSelected(false);
                        break;
                }

            }
        });

        //메인 php버튼 클릭한 경우
        mainPhpStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //PHP버튼만 selected(선택)되어서 PHP버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_php_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(false);
                        mainPhpStepBtn.setSelected(true);
                        mainHard1StepBtn.setSelected(false);
                        mainHard2StepBtn.setSelected(false);
                        break;
                }
            }
        });

        //메인 응용1단계버튼 클릭한 경우
        mainHard1StepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //응용1단계버튼만 selected(선택)되어서 응용1단계버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_hard1_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(false);
                        mainPhpStepBtn.setSelected(false);
                        mainHard1StepBtn.setSelected(true);
                        mainHard2StepBtn.setSelected(false);
                        break;
                }
            }
        });

        //메인 응용2단계버튼 클릭한 경우
        mainHard2StepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    //응용2단계버튼만 selected(선택)되어서 응용2단계버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_hard2_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(false);
                        mainPhpStepBtn.setSelected(false);
                        mainHard1StepBtn.setSelected(false);
                        mainHard2StepBtn.setSelected(true);
                        break;
                }
            }
        });

    }


    //메인 툴바에 사용하기위한 옵션메뉴를 생성합니다
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    //메인 툴바에 옵션메뉴에서 선택한 옵션마다 기능을 구현합니다
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //검색버튼을 클릭한경우
            case R.id.main_search:
                Toast.makeText(getApplicationContext(), "검색하기", Toast.LENGTH_SHORT).show();
                return true;

            //목록버튼을 클릭한경우
            case R.id.main_list:
                listDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //메인툴바에서 목록버튼을 클릭한경우 생기는 다이어로그
    public void listDialog() {

        //다이어로그에 보여질 목록들을 작성합니다
        CharSequence info[] = new CharSequence[]{"작품 산정 기준", "개발자 안내"};

        //다이어로그 생성해줍니다
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //다이어로그의 제목을 정합니다
        builder.setTitle("목록");

        //다이어로그 목록중 선택했을 때 기능 구현
        builder.setItems(info, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    //작품 산정 기준을 선택했을 때
                    case 0:

                        //작품 산정 기준 다이어로그 생성
                        standardDialog();
                        break;

                        //개발자 보기를 선택했을 때
                    case 1:

                        //개발자 소개 다이어로그 생성
                        developerDialog();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show(); //다이어로그 보여주기
    }


    //목록다이어로그에서 작품 순위 산정 기준을 선택했을 때 보여주는 다이어로그
    public void standardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//다이어로그 생성해줍니다
        builder.setTitle("작품 순위 산정 기준");//다이어로그 제목
        builder.setMessage("조회수 + 좋아요*5 = 합산 점수");//다이어로그 내용
        builder.show();//다이어로그 보여주기
    }

    //목록다이어로그에서 개발자 소개가 선택되었을 때 보여주는 다이어로그
    public void developerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//다이어로그 생성해줍니다
        builder.setTitle("개발자 소개");//다이어로그 제목
        builder.setMessage("심영현, 김수환, 김승우 , +파트장님");//다이어로그 내용
        builder.show();//다이어로그 보여주기
    }


}
