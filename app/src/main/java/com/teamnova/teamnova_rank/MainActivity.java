package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity {


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

    SearchView searchView;

    private long lastClickTime = 0; //lastClickTime:마지막으로 작품카테고리(자바,안드로이드,php,응용1,응용2)를 선택한 시간

    private int currentNum = 0;

    /* 멀티 쓰레드 */
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final ThreadPoolExecutor executor
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    boolean isJavaComplete = false;
    boolean isAndroidComplete = false;
    boolean isPHPComplete = false;
    boolean isHard1Complete = false;
    boolean isHard2Complete = false;

    int javaCrawlUrlLength = 0;

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

        mainToolbar = findViewById(R.id.main_toolbar);

        mainJavaStepBtn.setSelected(true);
        RankRecyclerview = findViewById(R.id.rank_recyclerview);
        setSupportActionBar(mainToolbar);//메인 액티비티에서 툴바를 사용하기 위해


        LinearLayoutManager llm = new LinearLayoutManager(this);//종류는 총 3가지, ListView를 사용하기 위한 사용
        RankRecyclerview.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        RankRecyclerview.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다

        /* 데이터를 저장한 기록이 있는지 확인한다. */
        String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(Constant.RANK_TYPE_BASIC_JAVA);
        if("".equals(lastUpdate)){
            progressOn();

            // 가져올 데이터가 없다면 크롤링 시작
            JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
            jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
            jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                @Override
                public void onProgressUpdate(Integer integer, Document document) {

                    if(integer == -1){
                        isJavaComplete = true;
                    }else{
                        javaCrawlUrlLength ++;
                    }
                    JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,Constant.RANK_TYPE_BASIC_JAVA,databaseHelper);
                    jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                        @Override
                        public void onPostExecute() {
                            javaCrawlUrlLength--;
                            if(isJavaComplete && javaCrawlUrlLength == 0 && currentNum == 0){
                                // 크롤링 성공 저장
                                databaseHelper.insertCrawlScheme(Constant.RANK_TYPE_BASIC_JAVA, true);
                                RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicJavaStepList());
                                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());
                                RankRecyclerviewAdapter.notifyDataSetChanged();
                                progressOFF();
                            }
                        }
                    });
                    if(!isJavaComplete)
                        jsoupPageCrawler.executeOnExecutor(executor);
                }
            });
            jsoupAsyncCrawler.execute(Constant.RANK_TYPE_BASIC_JAVA);
//            jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_BASIC_JAVA);
        }else{
            mRankData = databaseHelper.selectBasicJavaStepList();
        }

        RankRecyclerviewAdapter = new RankRecyclerviewAdapter(getApplicationContext() ,mRankData);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicJavaStepList());
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
                RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicJavaStepList());
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());
                RankRecyclerviewAdapter.notifyDataSetChanged();

                searchView.onActionViewCollapsed();


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

                /* 데이터를 저장한 기록이 있는지 확인한다. */
                String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(currentNum);
                if("".equals(lastUpdate)){
                    progressOn();
                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(Integer integer, Document document) {
                            if(integer == -1){
                                isAndroidComplete = true;
                            }else{
                                javaCrawlUrlLength ++;
                            }
                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,Constant.RANK_TYPE_BASIC_ANDROID,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(isAndroidComplete && javaCrawlUrlLength == 0 && currentNum == 1){
                                        databaseHelper.insertCrawlScheme(Constant.RANK_TYPE_BASIC_ANDROID, true);
                                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicAndroidStepList());
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicAndroidStepList());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                        progressOFF();
                                    }
                                }
                            });
                            if(!isAndroidComplete)
                                jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.execute(Constant.RANK_TYPE_BASIC_ANDROID);
                }

                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicAndroidStepList());
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicAndroidStepList());
                RankRecyclerviewAdapter.notifyDataSetChanged();
                searchView.onActionViewCollapsed();             // 서치뷰 닫음

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
                    progressOn();
                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(Integer integer, Document document) {
                            if(integer == -1){
                                isPHPComplete = true;
                            }else{
                                javaCrawlUrlLength ++;
                            }
                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,Constant.RANK_TYPE_BASIC_PHP,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(isPHPComplete && javaCrawlUrlLength == 0 && currentNum == Constant.RANK_TYPE_BASIC_PHP){
                                        databaseHelper.insertCrawlScheme(Constant.RANK_TYPE_BASIC_PHP, true);
                                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicPhpStepList());
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicPhpStepList());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                        progressOFF();
                                    }
                                }
                            });
                            if(!isPHPComplete)
                                jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.execute(Constant.RANK_TYPE_BASIC_PHP);
//                    jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_BASIC_PHP);
                }

                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicPhpStepList());
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicPhpStepList());
                RankRecyclerviewAdapter.notifyDataSetChanged();

                searchView.onActionViewCollapsed();             // 서치뷰 닫음

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

                /* 데이터를 저장한 기록이 있는지 확인한다. */
                String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(currentNum);
                if("".equals(lastUpdate)){
                    progressOn();
                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(Integer integer, Document document) {
                            if(integer == -1){
                                isHard1Complete = true;
                            }else{
                                javaCrawlUrlLength ++;
                            }
                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,Constant.RANK_TYPE_HARD_1,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(isHard1Complete && javaCrawlUrlLength == 0 && currentNum == 3){
                                        databaseHelper.insertCrawlScheme(Constant.RANK_TYPE_HARD_1, true);
                                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectHardStep1List());
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep1List());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                        progressOFF();
                                    }
                                }
                            });
                            if(!isHard1Complete)
                                jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.execute(Constant.RANK_TYPE_HARD_1);
//                    jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_HARD_1);
                }

                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectHardStep1List());
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep1List());
                RankRecyclerviewAdapter.notifyDataSetChanged();

                searchView.onActionViewCollapsed();             // 서치뷰 닫음

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

                /* 데이터를 저장한 기록이 있는지 확인한다. */
                String lastUpdate = databaseHelper.selectLastCourseTypeUpdateDate(currentNum);
                if("".equals(lastUpdate)){
                    progressOn();
                    // 가져올 데이터가 없다면 크롤링 시작
                    JsoupPageCrawler jsoupAsyncCrawler = new JsoupPageCrawler();
                    jsoupAsyncCrawler.setDatabaseHelper(databaseHelper);
                    jsoupAsyncCrawler.setJsoupAsyncListener(new JsoupPageCrawler.JsoupAsyncListener() {
                        @Override
                        public void onProgressUpdate(final Integer integer, Document document) {
                            if(integer == -1){
                                isHard2Complete = true;
                            }else{
                                javaCrawlUrlLength ++;
                            }

                            JsoupDocumentCrawler jsoupPageCrawler = new JsoupDocumentCrawler(document,Constant.RANK_TYPE_HARD_2,databaseHelper);
                            jsoupPageCrawler.setJsoupDocumentListener(new JsoupDocumentCrawler.JsoupDocumentListener() {
                                @Override
                                public void onPostExecute() {
                                    javaCrawlUrlLength--;
                                    if(isHard2Complete && javaCrawlUrlLength == 0 && currentNum == 4){
                                        databaseHelper.insertCrawlScheme(Constant.RANK_TYPE_HARD_2, true);
                                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectHardStep2List());
                                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep2List());
                                        RankRecyclerviewAdapter.notifyDataSetChanged();
                                        progressOFF();
                                    }
                                }
                            });
                            if(!isHard2Complete)
                                jsoupPageCrawler.executeOnExecutor(executor);
                        }
                    });
                    jsoupAsyncCrawler.execute(Constant.RANK_TYPE_HARD_2);
//                    jsoupAsyncCrawler.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Constant.RANK_TYPE_HARD_2);
                }

                RankRecyclerview.smoothScrollToPosition(0);
                RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectHardStep2List());
                RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep2List());
                RankRecyclerviewAdapter.notifyDataSetChanged();

                searchView.onActionViewCollapsed();             // 서치뷰 닫음

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

        // searchView 생성
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        // 검색 버튼을 눌렀을 때 뷰가 꽉차게 해주기
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // searchView 힌트
        searchView.setQueryHint("이름 검색");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                RankRecyclerviewAdapter.getFilter().filter(newText);

                return false;
            }
        });

        return true;
    }

    //메인 툴바에 옵션메뉴에서 선택한 옵션마다 기능을 구현합니다
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //검색버튼을 클릭한경우
            case R.id.action_search:

                return true;

             //목록버튼을 클릭한경우
            case R.id.action_refresh:
                //listDialog();
                for(int updateCrawlUrlNum : Constant.CAFE_TEAMNOVA_NUMBER_LIST){
                    JsoupAsyncTask jsoupAsyncTask = new MainActivity.JsoupAsyncTask();
                    jsoupAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,updateCrawlUrlNum);
                }

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

    private class JsoupAsyncTask extends AsyncTask<Integer, Void, Void>{
        int ASYNC_URL_NUM;
        // AsyncTask 실행 전
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressOn("전체 데이터를 가져오는 중입니다...");
        }

        // AsyncTask 실행
        @Override
        protected Void doInBackground(Integer... voids) {
            int url_num = voids[0];
            ASYNC_URL_NUM = url_num;
            try {

//                for(int url_num = 0; url_num < Constant.CAFE_TEAMNOVA_URL_LIST.size(); url_num++){
                    databaseHelper.deleteRankData(url_num);

                    databaseHelper.insertCrawlScheme(url_num, true);
                    int page_num = 0;

                    while (true){

                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //페이지 1증가
                        page_num++;

                        // 각 작품의 페이지 url 과 페이지 숫자를 합침.
                        //ex) "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=10"
                        String crawlingUrl = Constant.CAFE_TEAMNOVA_URL_LIST.get(url_num) + page_num;

                        // url 주소로 html 파일 가지고 오기
                        Document document = Jsoup.connect(crawlingUrl).get();

                        // 해당 페이지 게시물에 없을 때는 크롤링 멈춤.
                        if(document.select("#main-area > ul.article-movie-sub > li").size() == 0){
                            break;
                        }

                        // 크롤링 시작
                        getDataFromWebPage(document, url_num);

                    }

//                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        // AsyncTask 실행 후
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(ASYNC_URL_NUM == Constant.RANK_TYPE_BASIC_JAVA){
                switch (currentNum){
                    case Constant.RANK_TYPE_BASIC_JAVA :
                        RankRecyclerview.smoothScrollToPosition(0);
                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicJavaStepList());
                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicJavaStepList());
                        RankRecyclerviewAdapter.notifyDataSetChanged();
                        break;
                    case Constant.RANK_TYPE_BASIC_ANDROID:
                        RankRecyclerview.smoothScrollToPosition(0);
                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicAndroidStepList());
                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicAndroidStepList());
                        RankRecyclerviewAdapter.notifyDataSetChanged();
                        break;
                    case Constant.RANK_TYPE_BASIC_PHP:
                        RankRecyclerview.smoothScrollToPosition(0);
                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectBasicPhpStepList());
                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectBasicPhpStepList());
                        RankRecyclerviewAdapter.notifyDataSetChanged();
                        break;
                    case Constant.RANK_TYPE_HARD_1:
                        RankRecyclerview.smoothScrollToPosition(0);
                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectHardStep1List());
                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep1List());
                        RankRecyclerviewAdapter.notifyDataSetChanged();
                        break;
                    case Constant.RANK_TYPE_HARD_2:
                        RankRecyclerview.smoothScrollToPosition(0);
                        RankRecyclerviewAdapter.setFullListAdapter(databaseHelper.selectHardStep2List());
                        RankRecyclerviewAdapter.setRankDataList(databaseHelper.selectHardStep2List());
                        RankRecyclerviewAdapter.notifyDataSetChanged();
                        break;
                }
                progressOFF();
            }
        }
    }

    //웹페이지에서 크롤링하기
    private void getDataFromWebPage(Document document, int STEP){
        // 팀노바 오픈 카페 작품 페이지에서 게시글이 있는 부분만 크롤링 함.
        Elements broad_list = document.select("#main-area > ul.article-movie-sub > li");

        for (int i = 0; i < broad_list.size(); i++){

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 게시글 제목
            String title = broad_list.get(i).getElementsByClass("inner").text().replace("[JAVA]","").replace("[Android]","").replace("[PHP]","").replace("[","\n[");

            // 작성자
            String writer = broad_list.get(i).getElementsByClass("m-tcol-c").text();

            // 게시 날짜
            String create_date = broad_list.get(i).getElementsByClass("date").text();

            // 조회수
            // 조회수와 댓글수의 tag class 가 모두 "num"으로 되어 있기 때문에 get(0)을 해서 조회수 값만 가지고 옴.
            // 조회수 값을 크롤링 할 때 숫자 앞에 "조회" 텍스트가 같이 와서 띄어쓰기로 쪼갠 후 숫자 값만 가지고 옴.
            int view_count = Integer.parseInt(broad_list.get(i).getElementsByClass("num").get(0).text().split(" ")[1]);

            // 댓글 수
            // 댓글수 값을 크롤링 할 때 숫자 앞에 "댓글" 텍스트가 같이 와서 띄어쓰기로 쪼갠 후 숫자 값만 가지고 옴.
            int reply_count = Integer.parseInt(broad_list.get(i).getElementsByClass("comment_area").text().split(" ")[1]);

            // 좋아요 수
            int like_count = Integer.parseInt(broad_list.get(i).getElementsByClass("u_cnt num-recomm").text());

            //게시글 url
            String post_url = "https://m.cafe.naver.com/teamnovaopen" + broad_list.get(i).getElementsByClass("tit").attr("href");

            Document image_tag = Jsoup.parse(broad_list.get(i).getElementsByClass("movie-img").html());

            // 동영상이 2개 이상 올린 게시글이 있어서 이미지태그에 "동영상" 이라는 텍스트가 포함 되면 썸네일 주소를 가지고 옴.
            // 동영상 1개 --> "동영상"
            // 동영상 2개 --> "동영상 1개의 추가 이미지가 있습니다"
            // 동영상 3개 --> "동영상 2개의 추가 이미지가 있습니다"
            String img_url = "";
            if(image_tag.text().contains("동영상")){
                // "img"태그에 접근
                Elements img_tag = image_tag.select("a > img");

                // "img"태그의 "src" 값을 저장
                img_url = img_tag.get(0).attr("src");

            }
            // DB에 데이터 들어감.
            databaseHelper.insertRankData(title,writer,create_date,post_url,img_url,view_count,like_count,reply_count,STEP);


        }

    }
}