package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    //mainJavaStepBtn:mainJavaStepBtn을 클릭하면 자바 작품들이 RankRecyclerview에 보인다.
    //mainAndroidStepBtn:mainAndroidStepBtn을 클릭하면 안드로이드 작품들이 RankRecyclerview에 보인다.
    //mainPhpStepBtn:mainPhpStepBtn을 클릭하면 PHP 작품들이 RankRecyclerview에 보인다.
    //mainHard1StepBtn:mainHard1StepBtn을 클릭하면 응용1단계 작품들이 RankRecyclerview에 보인다.
    //mainHard2StepBtn:mainHard2StepBtn을 클릭하면 응용2단계 작품들이 RankRecyclerview에 보인다.
    private Button mainJavaStepBtn, mainAndroidStepBtn, mainPhpStepBtn, mainHard1StepBtn,
            mainHard2StepBtn;

    private TextView rankView,rankName,rankLike,rankReply;
    //RankRecyclerview:선택한 작품들을 보여주는 리사이클러뷰
    private RecyclerView RankRecyclerview;

    //mainToolbar:메인액티비티에서 사용하는 툴바
    private Toolbar mainToolbar;

    private List<RankData> mRankData;

    private RankDescriptionActivity alertDialog ;
//    AlertDialog alertDialog;

    /* sqlDB */
    private DatabaseHelper databaseHelper;

    RankRecyclerviewAdapter RankRecyclerviewAdapter;

    private long lastClickTime = 0; //lastClickTime:마지막으로 작품카테고리(자바,안드로이드,php,응용1,응용2)를 선택한 시간

    private int currentNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* sqldbHelper */
        databaseHelper = DatabaseHelper.getInstance(this);

        mainJavaStepBtn = findViewById(R.id.main_java_step_btn);
        mainAndroidStepBtn = findViewById(R.id.main_android_step_btn);
        mainPhpStepBtn = findViewById(R.id.main_php_step_btn);
        mainHard1StepBtn = findViewById(R.id.main_hard1_step_btn);
        mainHard2StepBtn = findViewById(R.id.main_hard2_step_btn);

/*        rankName = findViewById(R.id.rank_name);
        rankLike = findViewById(R.id.rank_like);
        rankReply = findViewById(R.id.rank_reply);
        rankView = findViewById(R.id.rank_view);*/

        mainToolbar = findViewById(R.id.main_toolbar);

        mainJavaStepBtn.setSelected(true);
        RankRecyclerview = findViewById(R.id.rank_recyclerview);
        setSupportActionBar(mainToolbar);//메인 액티비티에서 툴바를 사용하기 위해


        LinearLayoutManager llm = new LinearLayoutManager(this);//종류는 총 3가지, ListView를 사용하기 위한 사용
        RankRecyclerview.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        RankRecyclerview.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다
        RankRecyclerview.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));


        RankRecyclerviewAdapter = new RankRecyclerviewAdapter(getApplicationContext() ,mRankData);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.

        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());
        RankRecyclerview.setAdapter(RankRecyclerviewAdapter);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
        RankRecyclerviewAdapter.setOnClickItemListener(onClickItemListener);

//        makeTestData();

        //메인 자바버튼 클릭한 경우
        mainJavaStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //3초이내에 다시 눌리면 이벤트를 불러올수있다
                if (currentNum == 0) {
                    return;
                }

                currentNum = 0;
                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());
                RankRecyclerviewAdapter.notifyDataSetChanged();


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


                if (currentNum == 1) {
                    return;
                }

                currentNum = 1;

                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicAndroidStepList());
                RankRecyclerviewAdapter.notifyDataSetChanged();

                switch (view.getId()) {
                    //안드로이드버튼만 selected(선택)되어서 안드로이드버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_android_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(true);
//                      mainAndroidStepBtn.setClickable(false);
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


                if (currentNum == 2) {
                    return;
                }

                currentNum = 2;
                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicPhpStepList());
                RankRecyclerviewAdapter.notifyDataSetChanged();
                switch (view.getId()) {
                    //PHP버튼만 selected(선택)되어서 PHP버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_php_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(false);
                        mainPhpStepBtn.setSelected(true);
//                        mainPhpStepBtn.setClickable(false);
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

                if (currentNum == 3) {
                    return;
                }

                currentNum =3;
                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep1List());
                RankRecyclerviewAdapter.notifyDataSetChanged();
                switch (view.getId()) {
                    //응용1단계버튼만 selected(선택)되어서 응용1단계버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_hard1_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(false);
                        mainPhpStepBtn.setSelected(false);
                        mainHard1StepBtn.setSelected(true);
//                        mainHard1StepBtn.setClickable(false);
                        mainHard2StepBtn.setSelected(false);
                        break;
                }


            }
        });


        //메인 응용2단계버튼 클릭한 경우
        mainHard2StepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentNum == 4) {
                    return;
                }

                currentNum = 4;
                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep2List());
                RankRecyclerviewAdapter.notifyDataSetChanged();
                switch (view.getId()) {
                    //응용2단계버튼만 selected(선택)되어서 응용2단계버튼에만 색깔이 변경됩니다 다른 버튼들은 default(기본)색상입니다
                    case R.id.main_hard2_step_btn:
                        mainJavaStepBtn.setSelected(false);
                        mainAndroidStepBtn.setSelected(false);
                        mainPhpStepBtn.setSelected(false);
                        mainHard1StepBtn.setSelected(false);
                        mainHard2StepBtn.setSelected(true);
//                        mainHard2StepBtn.setClickable(false);
                        break;
                }

            }
        });



    }





    RankRecyclerviewAdapter.OnclickItemListener onClickItemListener = new RankRecyclerviewAdapter.OnclickItemListener() {
        @Override
        public void clickDetaiInfo(RankData rankData) {
//            Intent intent = new Intent(MainActivity.this,RankDescriptionActivity.class);
//            intent.putExtra("rankData",rankData);
//            startActivity(intent);
            Log.v("메인액티비티", "ID = " + rankData.getRankID());

            showAlertDialog(rankData);

        }
    };

    //리사이클러뷰 목록에 있는 작품 아이템을 선택하면 자세하게 보여주는 다이어로그
    private void showAlertDialog(RankData rankData) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
//        View layoutView = getLayoutInflater().inflate(R.layout.activity_rank_description, null);
//        Button dialogButton = layoutView.findViewById(R.id.btnDialog); //다이어로그 btnDialog레이아웃보여준다
//        dialogBuilder.setView(layoutView);
//        alertDialog = dialogBuilder.create(); //다이어로그 생성
        //생성될때 선언하는 위치
        alertDialog = new RankDescriptionActivity(MainActivity.this);
        alertDialog.setRankData(rankData);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //다이어로그 애니메이션방식
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        rankLike.setText(rankData.getLikeCount()+" ");

        alertDialog.show();
        //확인 버튼 클릭시 다이어로그 사라짐
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
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


/*    //테스트하기위해서 만든 메소드입니다
    public void makeTestData() {

//        mRankData = new ArrayList<>();

        mRankData = databaseHelper.selectBasicAndroidStepList();
//      mRankData.add(new RankData(rankID,rankTitle,rankWriter,createDate,detailLink,thumbPath,
//      viewCount,likeCount,replyCount,rankType,ranking,rankPoint));


//        mRankData.add(new RankData(1, "제목","작성자","만든날짜","URL링크","썸네일",0,0,0,0,1, 0));
//        mRankData.add(new RankData(1, "제목","작성자","만든날짜","URL링크","썸네일",0,0,0,0,2, 0));
//        mRankData.add(new RankData(1, "제목","작성자","만든날짜","URL링크","썸네일",0,0,0,0,3, 0));
//        mRankData.add(new RankData(1, "제목","작성자","만든날짜","URL링크","썸네일",0,0,0,0,4, 0));
//        mRankData.add(new RankData(1, "제목","작성자","만든날짜","URL링크","썸네일",0,0,0,0,5, 0));
//        mRankData.add(new RankData(1, "제목","작성자","만든날짜","URL링크","썸네일",0,0,0,0,6, 0));

        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,1, 1000));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,2, 900));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,3, 800));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,4, 700));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,5, 600));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,6, 500));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,7, 400));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,8, 300));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,9, 200));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,10, 999));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,11, 10));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,12, 78));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,13, 1));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,14, 45));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,15, 9));
        mRankData.add(new RankData(1, "[JAVA] 기초단계 -5기 김승우[담당강사 : 성훈파트장님]","작성자","만든날짜","URL링크","썸네일",0,0,0,0,16, 9999));

    }

    }*/
}
