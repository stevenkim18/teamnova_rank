package com.teamnova.teamnova_rank;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 클래스 명 : AdmobActivity class
 * 설명 : 최신데이터 여부를 판단한 후 처리한다.
 *
 *      1. 최신 데이터인 경우 -> 랭킹 확인 화면 이동
 *      2. 최신 데이터가 아닌 경우 -> 2-1. 광고 시청 -> 최신 데이터 업데이트 및 화면으로 이동
 *                              2-2. 광고 시청없이 이전 데이터로 화면 이동
 *      3. 데이터가 없는 경우 -> 광고 시청 -> 최신 데이터 업데이트 및 화면으로 이동
 *
 *
 */
public class AdmobActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    /* activity_admob레이아웃 위젯 */
    private TextView noUpdateTv;
    private TextView dataUpdateTv;
    private TextView dataInfoTv;
    private ImageView basicJavaUncheckIv;
    private ImageView basicJavaCheckIv;
    private ImageView basicAndroidCheckIv;
    private ImageView basicAndroidUncheckIv;
    private ImageView basicPHPUncheckIv;
    private ImageView basicPHPCheckIv;
    private ImageView hard1StepCheckIv;
    private ImageView hard1StepUnheckIv;
    private ImageView hard2StepCheckIv;
    private ImageView hard2StepUnheckIv;
    private TextView crawlDateTv;
    private LinearLayout crawlInfoLl;
    private LottieAnimationView loadingLottieAv;

    /* sqlDB */
    private DatabaseHelper databaseHelper;

    /* admob */
    private RewardedAd rewardedAd;
    // admob app key - 테스트용
    private final String AD_TEST_KEY = "ca-app-pub-3940256099942544/5224354917";
    // admob app key - 실제
    private final String AD_REAL_KEY = "ca-app-pub-7513442765578534~3381765090";

    // 크롤링 할 URL 주소를 담는 리스트
    private ArrayList<String> urls;

    // 크롤링 asyncTask;
    private AdmobActivity.JsoupAsyncTask jsoupAsyncTask;


    boolean isRewardSuccess = false; // 광고를 끝까지 시청했는지 여부
    boolean isCrawlSuccess = false; // 크롤링이 끝났는지 여부
    boolean isAdLoaded = false; // 광고 로딩이 끝났는지 여부
    boolean isUpdateComplete = true; // 오늘 일자 기준 업데이트가 필요한 데이터가 있다면 false;
    /*
        최신 데이터로 업데이트가 필요한 목록
        ex)
            기초 자바         X
            기초 안드로이드     O
            기초 PHP         O
            응용 1단계        X
            응용 2단계        O
       일 경우 목록에는 기초 자바, 응용 1단계만 들어간다.
     */
    public CopyOnWriteArrayList<Integer> updateList = new CopyOnWriteArrayList<>();

    // 크롤링 완료한 단계가 들어간다. onRestart시 데이터 복구
    public List<Integer> completeCrawlList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob);

        /* sqldbHelper */
        databaseHelper = DatabaseHelper.getInstance(this);

        /* 팀노바 오픈 카페 url 셋팅 */
        makeUrlList();

        /* view 초기화 */
        initView();

        /* 크롤링 해야될 목록 */
        initUpdateList();
        initCrawlView(); // 크롤링 전 데이터를 기준으로 셋팅
        startCrawling(); // 크롤링 시작

//        crawlDateTv.setText("기준 일자 : "+DateUtil.getToday());
        String updateDate = databaseHelper.selectLastUpdateDate();
        crawlDateTv.setText("마지막 업데이트 : "+ ("".equals(updateDate) ? "없음" : updateDate));

        /* 랭킹 페이지 이동 */
        noUpdateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdmobActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /* 광고 영상 초기화 */
        rewardedAd = new RewardedAd(AdmobActivity.this, AD_TEST_KEY);
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        /* 광고 시청 버튼 클릭 시 광고 재생, 크롤링 시작 */
        dataUpdateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd.isLoaded()) {
                    Activity thisActivity = AdmobActivity.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        public void onRewardedAdOpened() {
                            // Ad opened.
                            Log.d("광고 오픈","onRewardedAdOpened");
                        }

                        public void onRewardedAdClosed() {
                            // Ad closed.
                            Log.d("광고 끝남","true");
                            if(isRewardSuccess){
                                // 크롤링 중이라면 랭킹 화면 이동 버튼을 안보이게 처리한다.
//                                if (jsoupAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
//                                    noUpdateTv.setVisibility(View.GONE);
//                                }else{
                                    if(isCrawlSuccess) initCrawlView();
//                                }
                            }else{
                                // 광고 다시 재생을 위해서는 광고 영상을 다시 받아와야함
                                rewardedAd = createAndLoadRewardedAd();

                                // 랭킹 이동 화면 안보이게
                                noUpdateTv.setVisibility(View.GONE);
                                /*
                                    크롤링 중이라면 취소한다. - > 변경 2019/09/30
                                    속도가 느리기 때문에 사용자 광고 취소 여부 상관없이 크롤링 진행 ..
                                    데이터는 최신이 맞지만 광고를 다 보지않았다면 랭킹 페이지로 안넘어가게 변경
                                */
//                                if (jsoupAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
//                                    jsoupAsyncTask.cancel(true);
//                                }
                            }
                        }

                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            Log.d(TAG+" 광고 시청:","완료");
                            isRewardSuccess = true;
                        }

                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            Log.d(TAG+" 광고 시청 :","실패");
                        }
                    };
                    rewardedAd.show(thisActivity, adCallback);
                } else {
                    Log.d(TAG, "The rewarded ad wasn't loaded yet.");
                    Toast.makeText(AdmobActivity.this, "광고 로딩중입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startCrawling(){
        // 크롤링 해야 하는 url 목록이 존재 한다면
        if(updateList.size() > 0){
            for(int updateCrawlUrlNum : updateList){
                jsoupAsyncTask = new AdmobActivity.JsoupAsyncTask();
                jsoupAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,updateCrawlUrlNum);
            }
        }
    }

    /**
     * 크롤링 성공기록을 수정한다.
     */
    public void updateCrawlScheme(boolean isSuccess){
        // 중간에 광고를 나온 경우 데이터 업데이트를 취소한다.
        databaseHelper.updateCrawlScheme(updateList,isSuccess);

    }

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
            stopLoadingLottie();
            isAdLoaded = true;
            adLoadedAndView(); // 크롤링 전 데이터를 기준으로 뷰를 보여준다.

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

    public void stopLoadingLottie(){
        loadingLottieAv.setVisibility(View.GONE);
    }

    /**
     * 크롤링해야될 목록을 검색해서 목록에 저장한다.
     */
    public void initUpdateList(){
        /* 자바 기초 크롤링 기록 */
        if(!databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_JAVA)){
            updateList.add(Constant.RANK_TYPE_BASIC_JAVA);
        }
        /* 안드로이드 기초 크롤링 기록 */
        if(!databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_ANDROID)){
            updateList.add(Constant.RANK_TYPE_BASIC_ANDROID);
        }
        /* php 기초 크롤링 기록 */
        if(!databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_PHP)){
            updateList.add(Constant.RANK_TYPE_BASIC_PHP);
        }
        /* 응용 1단계 크롤링 기록 */
        if(!databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_HARD_1)){
            updateList.add(Constant.RANK_TYPE_HARD_1);
        }
        /* 응용 2단계 크롤링 기록 */
        if(!databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_HARD_2)){
            updateList.add(Constant.RANK_TYPE_HARD_2);
        }

        Log.d("크롤링 해야하는 목록", TextUtils.join(",",updateList));
    }

    /**
     * sqlDB에서 오늘 날짜를 기준으로 크롤링을 성공한 기록이 있는지 확인하여 view를 보여준다.
     */
    public void initCrawlView(){
        // 단계별 크롤링 기록 검색
        // true : 이미 크롤링함, false : 크롤링한 기록이 없음
        // 기록중에 하나라도 크롤링한 기록이 없다면 광고 시청 후 데이터를 업데이트 할 수 있음

        isUpdateComplete = true;
        /* 자바 기초 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_JAVA)){
            basicJavaCheckIv.setVisibility(View.VISIBLE);
            basicJavaUncheckIv.setVisibility(View.GONE);
        }else{
            basicJavaCheckIv.setVisibility(View.GONE);
            basicJavaUncheckIv.setVisibility(View.VISIBLE);
            isUpdateComplete = false;
        }

        /* 안드로이드 기초 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_ANDROID)){
            basicAndroidCheckIv.setVisibility(View.VISIBLE);
            basicAndroidUncheckIv.setVisibility(View.GONE);
        }else{
            basicAndroidCheckIv.setVisibility(View.GONE);
            basicAndroidUncheckIv.setVisibility(View.VISIBLE);
            isUpdateComplete = false;
        }

        /* php 기초 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_PHP)){
            basicPHPCheckIv.setVisibility(View.VISIBLE);
            basicPHPUncheckIv.setVisibility(View.GONE);
        }else{
            basicPHPCheckIv.setVisibility(View.GONE);
            basicPHPUncheckIv.setVisibility(View.VISIBLE);
            isUpdateComplete = false;
        }

        /* 응용 1단계 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_HARD_1)){
            hard1StepCheckIv.setVisibility(View.VISIBLE);
            hard1StepUnheckIv.setVisibility(View.GONE);
        }else{
            hard1StepCheckIv.setVisibility(View.GONE);
            hard1StepUnheckIv.setVisibility(View.VISIBLE);
            isUpdateComplete = false;
        }

        /* 응용 2단계 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_HARD_2)){
            hard2StepCheckIv.setVisibility(View.VISIBLE);
            hard2StepUnheckIv.setVisibility(View.GONE);
        }else{
            hard2StepCheckIv.setVisibility(View.GONE);
            hard2StepUnheckIv.setVisibility(View.VISIBLE);
            isUpdateComplete = false;
        }

        if(isAdLoaded){
            /* 업데이트 필요 항목이 1개라도 있는 경우 동영상 시청후 데이터를 업데이트해야 한다. */
            if(isUpdateComplete == false){
                dataInfoTv.setText("'최신 데이터가 아닙니다.'");
                dataUpdateTv.setVisibility(View.VISIBLE);
                noUpdateTv.setVisibility(View.GONE);
            }else{
                dataInfoTv.setText("'최신 데이터입니다.'");
                dataUpdateTv.setVisibility(View.GONE);
                noUpdateTv.setVisibility(View.VISIBLE);
            }

                crawlInfoLl.setVisibility(View.VISIBLE);
                crawlDateTv.setVisibility(View.VISIBLE);
                dataInfoTv.setVisibility(View.VISIBLE);
        }
    }

    public void adLoadedAndView(){
        /* 업데이트 필요 항목이 1개라도 있는 경우 동영상 시청후 데이터를 업데이트해야 한다. */
        if(isUpdateComplete == false){
            dataInfoTv.setText("'최신 데이터가 아닙니다.'");
            if(isAdLoaded){
                dataUpdateTv.setVisibility(View.VISIBLE);
                noUpdateTv.setVisibility(View.GONE);
            }
        }else{
            dataInfoTv.setText("'최신 데이터입니다.'");
            if(isAdLoaded){
                dataUpdateTv.setVisibility(View.GONE);
                noUpdateTv.setVisibility(View.VISIBLE);
            }
        }
        if(isAdLoaded){
            crawlInfoLl.setVisibility(View.VISIBLE);
            crawlDateTv.setVisibility(View.VISIBLE);
            dataInfoTv.setVisibility(View.VISIBLE);
        }
    }

    /* 위젯 초기화 */
    public void initView(){
        noUpdateTv = findViewById(R.id.no_update_tv);
        dataUpdateTv = findViewById(R.id.data_update_tv);
        dataInfoTv = findViewById(R.id.data_info_tv);
        loadingLottieAv = findViewById(R.id.loading_lottie_av);
        basicJavaUncheckIv = findViewById(R.id.basic_java_uncheck_iv);
        basicJavaCheckIv = findViewById(R.id.basic_java_check_iv);;
        basicAndroidCheckIv = findViewById(R.id.basic_android_check_iv);;
        basicAndroidUncheckIv = findViewById(R.id.basic_android_uncheck_iv);;
        basicPHPUncheckIv = findViewById(R.id.basic_php_uncheck_iv);;
        basicPHPCheckIv = findViewById(R.id.basic_php_check_iv);;
        hard1StepCheckIv = findViewById(R.id.hard1_step_check_iv);
        hard1StepUnheckIv = findViewById(R.id.hard1_step_uncheck_iv);
        hard2StepCheckIv = findViewById(R.id.hard2_step_check_iv);
        hard2StepUnheckIv = findViewById(R.id.hard2_step_uncheck_iv);
        crawlDateTv = findViewById(R.id.crawl_date_tv);
        crawlInfoLl = findViewById(R.id.crawl_info_ll);

        /* lottie animation 시작 */
        loadingLottieAv.setAnimation("simple-loader.json");
        loadingLottieAv.loop(true);
        loadingLottieAv.playAnimation();
    }


    // 크롤링을 실행하는 AsyncTask
    private class JsoupAsyncTask extends AsyncTask<Integer, Void, Integer> {

        // AsyncTask 실행
        @Override
        protected Integer doInBackground(Integer... voids) {
            int url_num = voids[0];
            try {

//                for(int url_num = 0; url_num < urls.size(); url_num++){
                    Log.d("크롤링 시작 URL 번호 >", url_num+"");
//                    if(!updateList.contains(url_num)) continue;

                    completeCrawlList.add(url_num);
                    // 저장된 작품 단계별 삭제
                    databaseHelper.deleteRankData(url_num);

                    int page_num = 0;

                    while (true){

                        //페이지 1증가
                        page_num++;

                        // 각 작품의 페이지 url 과 페이지 숫자를 합침.
                        //ex) "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=10"
                        String crawlingUrl = urls.get(url_num) + page_num;

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
                // 크롤링 실패 저장
                databaseHelper.insertCrawlScheme(url_num, false);
            }
            // 크롤링 성공 저장
            databaseHelper.insertCrawlScheme(url_num, true);
            return url_num;
        }

        // AsyncTask 실행 후
        @Override
        protected void onPostExecute(Integer updatedCrawlPageNum) {
            super.onPostExecute(updatedCrawlPageNum);
            // 완료된 순서대로 뷰를 초기화 시켜준다.
//            switch (updatedCrawlPageNum){
//                case Constant.RANK_TYPE_BASIC_JAVA : // 기초 자바 뷰
//                    basicJavaCheckIv.setVisibility(View.VISIBLE);
//                    basicJavaUncheckIv.setVisibility(View.GONE);
//                    break;
//                case Constant.RANK_TYPE_BASIC_ANDROID : // 기초 안드로이드 뷰
//                    basicAndroidCheckIv.setVisibility(View.VISIBLE);
//                    basicAndroidUncheckIv.setVisibility(View.GONE);
//                    break;
//                case Constant.RANK_TYPE_BASIC_PHP : // 기초 php 뷰
//                    basicPHPCheckIv.setVisibility(View.VISIBLE);
//                    basicPHPUncheckIv.setVisibility(View.GONE);
//                    break;
//                case Constant.RANK_TYPE_HARD_1 : // 응용 1단계 뷰
//                    hard1StepCheckIv.setVisibility(View.VISIBLE);
//                    hard1StepUnheckIv.setVisibility(View.GONE);
//                    break;
//                case Constant.RANK_TYPE_HARD_2 : // 응용 2단계 뷰
//                    hard2StepCheckIv.setVisibility(View.VISIBLE);
//                    hard2StepUnheckIv.setVisibility(View.GONE);
//                    break;
//            }

            // 5개의 크롤링이 모두 완료되었는지 확인한다.
            if(databaseHelper.isCompleteCrawling()){
                isCrawlSuccess = true; // 크롤링 모두 완료
                if(isRewardSuccess){ // 광고를 끝까지 봤는지 확인한다.
                    // 데이터 목록 view 초기화
                    initCrawlView();
//                    dataInfoTv.setText("'최신 데이터입니다.'");
//                    dataUpdateTv.setVisibility(View.GONE);
//                    noUpdateTv.setVisibility(View.VISIBLE);

                    // 랭킹 화면 이동 버튼 보이게
                    noUpdateTv.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    //웹페이지에서 크롤링하기
    private void getDataFromWebPage(Document document, int STEP){
        // 팀노바 오픈 카페 작품 페이지에서 게시글이 있는 부분만 크롤링 함.
        Elements broad_list = document.select("#main-area > ul.article-movie-sub > li");
        for (int i = 0; i < broad_list.size(); i++){

            // sleep 시간을 조절해서 아이피 차단을 피해야 한다. 계속 수정중...
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String title = broad_list.get(i).getElementsByClass("inner").text();
            String writer = broad_list.get(i).getElementsByClass("m-tcol-c").text();
            String create_date = broad_list.get(i).getElementsByClass("date").text();
            int view_count = Integer.parseInt(broad_list.get(i).getElementsByClass("num").get(0).text().split(" ")[1]);
            int reply_count = Integer.parseInt(broad_list.get(i).getElementsByClass("comment_area").text().split(" ")[1]);
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

            databaseHelper.insertRankData(title,writer,create_date,post_url,img_url,view_count,like_count,reply_count,STEP);
        }
    }

    // 크롤링 할 주소들을 리스트에 넣기
    private void makeUrlList(){
        // URL 주소 리스트 생성
        urls = new ArrayList<>();

        //주소 추가
        urls.add(Constant.CAFE_TEAMNOVA_JAVA_URL);   //자바
        urls.add(Constant.CAFE_TEAMNOVA_ANDROID_URL);   //안드로이드
        urls.add(Constant.CAFE_TEAMNOVA_PHP_URL);    //PHP
        urls.add(Constant.CAFE_TEAMNOVA_HARD1_URL);    //응용1단계
        urls.add(Constant.CAFE_TEAMNOVA_HARD2_URL);    //응용2단계
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        databaseHelper.updateCrawlScheme(completeCrawlList, true);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("stop","stop");
        if(!isRewardSuccess){
            // 광고를 끝까지 보지 않은 경우 액티비티가 죽기전 성공 기록을 취소한다.
            updateCrawlScheme(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
