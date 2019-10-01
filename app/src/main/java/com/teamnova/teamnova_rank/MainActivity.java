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
import android.os.AsyncTask;
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

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private Button mainJavaStepBtn, mainAndroidStepBtn, mainPhpStepBtn, mainHard1StepBtn,
            mainHard2StepBtn;

    private RecyclerView RankRecyclerview;

    private Toolbar mainToolbar;

    private List<RankData> mRankData;

    private RankDescriptionActivity alertDialog ;

    /* sqlDB */
    private DatabaseHelper databaseHelper;

    RankRecyclerviewAdapter RankRecyclerviewAdapter;

    private long lastClickTime = 0; //lastClickTime:마지막으로 작품카테고리(자바,안드로이드,php,응용1,응용2)를 선택한 시간

    private int currentNum = 0;

    /* 멀티 쓰레드 */
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final ThreadPoolExecutor executor
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    boolean isJavaComplete = false;
    int javaCrawlUrlLength = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* sqldbHelper */
        databaseHelper = DatabaseHelper.getInstance(this);

        mainJavaStepBtn = findViewById(R.id.main_java_step_btn);//mainJavaStepBtn:mainJavaStepBtn을 클릭하면 자바 작품들이 RankRecyclerview에 보인다.
        mainAndroidStepBtn = findViewById(R.id.main_android_step_btn);//mainAndroidStepBtn:mainAndroidStepBtn을 클릭하면 안드로이드 작품들이 RankRecyclerview에 보인다.
        mainPhpStepBtn = findViewById(R.id.main_php_step_btn);//mainPhpStepBtn:mainPhpStepBtn을 클릭하면 PHP 작품들이 RankRecyclerview에 보인다.
        mainHard1StepBtn = findViewById(R.id.main_hard1_step_btn);//mainHard1StepBtn:mainHard1StepBtn을 클릭하면 응용1단계 작품들이 RankRecyclerview에 보인다.
        mainHard2StepBtn = findViewById(R.id.main_hard2_step_btn); //mainHard2StepBtn:mainHard2StepBtn을 클릭하면 응용2단계 작품들이 RankRecyclerview에 보인다.

        mainToolbar = findViewById(R.id.main_toolbar);  //mainToolbar:메인액티비티에서 사용하는 툴바

        mainJavaStepBtn.setSelected(true);//앱이 첫 실행할 때 자바버튼 눌러져있도록 보이기위해
        RankRecyclerview = findViewById(R.id.rank_recyclerview);//RankRecyclerview:선택한 작품들을 보여주는 리사이클러뷰
        setSupportActionBar(mainToolbar);//메인 액티비티에서 툴바를 사용하기 위해


        LinearLayoutManager llm = new LinearLayoutManager(this);//종류는 총 3가지, LinearLayoutManager을 사용하기 위한 사용
        RankRecyclerview.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        RankRecyclerview.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다


        /* 데이터를 저장한 기록이 있는지 확인한다. */
        String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(Constant.RANK_TYPE_BASIC_JAVA);
        if("".equals(lastUpdate)){

            // 가져올 데이터가 없다면 크롤링 시작
            JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
            jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
            jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                @Override
                public void onProgressUpdate(final Integer integer, Document document) {
                    javaCrawlUrlLength ++;
                    JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,0,databaseHelper);
                    jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                        @Override
                        public void onPostExecute() {
                            javaCrawlUrlLength--;
                            if(integer == -1 && javaCrawlUrlLength == 0 && currentNum == 0){
                                // 크롤링 성공 저장
                                databaseHelper.insertCrawlScheme(currentNum, true);
                                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());
                                RankRecyclerviewAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    jsoupPageCrawler.executeOnExecutor(executor);
                }
            });
            jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_BASIC_JAVA);
        }else{
            mRankData = databaseHelper.selectBasicJavaStepList();
        }


        RankRecyclerviewAdapter = new RankRecyclerviewAdapter(getApplicationContext() ,mRankData);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());
        RankRecyclerview.setAdapter(RankRecyclerviewAdapter);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
        RankRecyclerviewAdapter.setOnClickItemListener(onClickItemListener);



        //메인 자바버튼 클릭한 경우
        mainJavaStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //자바버튼 중복 눌렀을 때 예외처리
                if (currentNum == 0) {
                    return;
                }
                currentNum = 0;

                RankRecyclerview.scrollToPosition(0);//스크롤 위치 맨위로 이동시켜준다
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());//selectBasicJavaStepList 값 연결
                RankRecyclerviewAdapter.notifyDataSetChanged();//리스트 항목 갱신



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

                //안드로이드버튼 중복 눌렀을 때 예외처리
                if (currentNum == 1) {
                    return;
                }

                currentNum = 1;


                /* 데이터를 저장한 기록이 있는지 확인한다. */
                String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(currentNum);
                if("".equals(lastUpdate)){

                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(final Integer integer, Document document) {
                            javaCrawlUrlLength ++;
                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,currentNum,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(integer == -1 && javaCrawlUrlLength == 0 && currentNum == 1){
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicAndroidStepList());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_BASIC_JAVA);
                }

                RankRecyclerview.scrollToPosition(0);//스크롤 위치 맨위로 이동시켜준다
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicAndroidStepList()); //selectBasicAndroidStepList 값 연결
                RankRecyclerviewAdapter.notifyDataSetChanged(); //리스트 항목 갱신


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


                /* 데이터를 저장한 기록이 있는지 확인한다. */
                String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(currentNum);
                if("".equals(lastUpdate)){

                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(final Integer integer, Document document) {
                            javaCrawlUrlLength ++;
                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,currentNum,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(integer == -1 && javaCrawlUrlLength == 0 && currentNum == 2){
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicPhpStepList());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_BASIC_JAVA);
                }

                RankRecyclerview.scrollToPosition(0);//스크롤 위치 맨위로 이동시켜준다
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicPhpStepList());//selectBasicPhpStepList 값 연결
                RankRecyclerviewAdapter.notifyDataSetChanged();//리스트 항목 갱신

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

                if (currentNum == 3) {
                    return;
                }

                currentNum =3;


                /* 데이터를 저장한 기록이 있는지 확인한다. */
                String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(currentNum);
                if("".equals(lastUpdate)){

                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(final Integer integer, Document document) {
                            javaCrawlUrlLength ++;
                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,currentNum,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(integer == -1 && javaCrawlUrlLength == 0 && currentNum == 3){
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep1List());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_BASIC_JAVA);
                }

                RankRecyclerview.scrollToPosition(0);//스크롤 위치 맨위로 이동시켜준다
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep1List());//selectHardStep1List 값 연결
                RankRecyclerviewAdapter.notifyDataSetChanged();//리스트 항목 갱신

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

                if (currentNum == 4) {
                    return;
                }

                currentNum = 4;


                /* 데이터를 저장한 기록이 있는지 확인한다. */
                String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(currentNum);
                if("".equals(lastUpdate)){

                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(final Integer integer, Document document) {
                            javaCrawlUrlLength ++;
                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,currentNum,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(integer == -1 && javaCrawlUrlLength == 0 && currentNum == 4){
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep2List());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_BASIC_JAVA);
                }

                RankRecyclerview.scrollToPosition(0);//스크롤 위치 맨위로 이동시켜준다
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep2List());//selectHardStep2List 값 연결
                RankRecyclerviewAdapter.notifyDataSetChanged();//리스트 항목 갱신

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




    //리사이클러뷰 안에 목록 아이템을 선택했을 때 이벤트처리
    RankRecyclerviewAdapter.OnclickItemListener onClickItemListener = new RankRecyclerviewAdapter.OnclickItemListener() {
        @Override
        public void clickDetaiInfo(RankData rankData) {

//            Log.v("메인액티비티", "ID = " + rankData.getRankID());

            showAlertDialog(rankData);

        }
    };

    //리사이클러뷰 목록에 있는 작품 아이템을 선택하면 자세하게 보여주는 다이어로그
    private void showAlertDialog(RankData rankData) {
/*        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View layoutView = getLayoutInflater().inflate(R.layout.activity_rank_description, null);
        Button dialogButton = layoutView.findViewById(R.id.btnDialog); //다이어로그 btnDialog레이아웃보여준다
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create(); //다이어로그 생성*/

        alertDialog = new RankDescriptionActivity(MainActivity.this);//메인액티비티에 다이어로그 생성
        alertDialog.setRankData(rankData);//다이어로그 rankData 가져오기
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //다이어로그 애니메이션방식
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//다이어로그배경


        alertDialog.show();//다이어로그 보여주기
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

        mRankData = new ArrayList<>();

        mRankData = databaseHelper.selectBasicAndroidStepList();
      mRankData.add(new RankData(rankID,rankTitle,rankWriter,createDate,detailLink,thumbPath,
      viewCount,likeCount,replyCount,rankType,ranking,rankPoint));

    }

    }*/
}