package com.teamnova.teamnova_rank;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
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
        crawlDateTv.setText("기준 일자 : "+DateUtil.getToday());

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
                            AdmobActivity.JsoupAsyncTask jsoupAsyncTask = new AdmobActivity.JsoupAsyncTask();
                            jsoupAsyncTask.execute();
                        }

                        public void onRewardedAdClosed() {
                            // Ad closed.
                        }

                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            Log.d(TAG+" 광고 시청:","완료");
                        }

                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            Log.d(TAG+" 광고 시청 :","실패");
                        }
                    };
                    rewardedAd.show(thisActivity, adCallback);
                } else {
                    Log.d(TAG, "The rewarded ad wasn't loaded yet.");
                }
            }
        });
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

            /* 크롤링 정보 */
            initCrawlView();
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
     * sqlDB에서 오늘 날짜를 기준으로 크롤링을 성공한 기록이 있는지 확인하여 view를 보여준다.
     */
    public void initCrawlView(){
        // 단계별 크롤링 기록 검색
        // true : 이미 크롤링함, false : 크롤링한 기록이 없음
        // 기록중에 하나라도 크롤링한 기록이 없다면 광고 시청 후 데이터를 업데이트 할 수 있음

        // 검색 시작전 true -> 마지막 결과가 false인지 체크후 동영상 시청 여부 결정
        boolean result = true;

        /* 자바 기초 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_JAVA)){
            basicJavaCheckIv.setVisibility(View.VISIBLE);
            basicJavaUncheckIv.setVisibility(View.GONE);
        }else{
            basicJavaCheckIv.setVisibility(View.GONE);
            basicJavaUncheckIv.setVisibility(View.VISIBLE);
            result = false;
        }

        /* 안드로이드 기초 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_ANDROID)){
            basicAndroidCheckIv.setVisibility(View.VISIBLE);
            basicAndroidUncheckIv.setVisibility(View.GONE);
        }else{
            basicAndroidCheckIv.setVisibility(View.GONE);
            basicAndroidUncheckIv.setVisibility(View.VISIBLE);
            result = false;
        }

        /* php 기초 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_BASIC_PHP)){
            basicPHPCheckIv.setVisibility(View.VISIBLE);
            basicPHPUncheckIv.setVisibility(View.GONE);
        }else{
            basicPHPCheckIv.setVisibility(View.GONE);
            basicPHPUncheckIv.setVisibility(View.VISIBLE);
            result = false;
        }

        /* 응용 1단계 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_HARD_1)){
            hard1StepCheckIv.setVisibility(View.VISIBLE);
            hard1StepUnheckIv.setVisibility(View.GONE);
        }else{
            hard1StepCheckIv.setVisibility(View.GONE);
            hard1StepUnheckIv.setVisibility(View.VISIBLE);
            result = false;
        }

        /* 응용 2단계 크롤링 기록 */
        if(databaseHelper.selectCrawlScheme(Constant.RANK_TYPE_HARD_2)){
            hard2StepCheckIv.setVisibility(View.VISIBLE);
            hard2StepUnheckIv.setVisibility(View.GONE);
        }else{
            hard2StepCheckIv.setVisibility(View.GONE);
            hard2StepUnheckIv.setVisibility(View.VISIBLE);
            result = false;
        }

        if(result == false){
            dataInfoTv.setText("'최신 데이터가 아닙니다.'");
            dataUpdateTv.setVisibility(View.VISIBLE);
        }else{
            dataInfoTv.setText("'최신 데이터입니다.'");
            dataUpdateTv.setVisibility(View.GONE);
            noUpdateTv.setVisibility(View.VISIBLE);
        }

        crawlInfoLl.setVisibility(View.VISIBLE);
        crawlDateTv.setVisibility(View.VISIBLE);
        dataInfoTv.setVisibility(View.VISIBLE);
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
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        // AsyncTask 실행 전
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        // AsyncTask 실행
        @Override
        protected Void doInBackground(Void... voids) {

            try {

                for(int url_num = 0; url_num < urls.size(); url_num++){
                    if(databaseHelper.selectCrawlScheme(url_num)) continue;

                    // 저장된 작품 단계별 삭제
                    databaseHelper.deleteRankData(url_num);

                    databaseHelper.insertCrawlScheme(url_num, true);

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
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        // AsyncTask 실행 후
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }

    //웹페이지에서 크롤링하기
    private void getDataFromWebPage(Document document, int STEP){
        // 팀노바 오픈 카페 작품 페이지에서 게시글이 있는 부분만 크롤링 함.
        Elements broad_list = document.select("#main-area > ul.article-movie-sub > li");
        for (int i = 0; i < broad_list.size(); i++){

            Log.v(TAG, "------------------------------------------------------------ " + (i + 1) + "번째 게시글 -----------------------------------------------------------------");

            String title = broad_list.get(i).getElementsByClass("inner").text();
            String writer = broad_list.get(i).getElementsByClass("m-tcol-c").text();
            String create_date = broad_list.get(i).getElementsByClass("date").text();
            int view_count = Integer.parseInt(broad_list.get(i).getElementsByClass("num").get(0).text().split(" ")[1]);
            int reply_count = Integer.parseInt(broad_list.get(i).getElementsByClass("comment_area").text().split(" ")[1]);
            int like_count = Integer.parseInt(broad_list.get(i).getElementsByClass("u_cnt num-recomm").text());

            //게시글 url
            String post_url = "https://cafe.naver.com/teamnovaopen" + broad_list.get(i).getElementsByClass("tit").attr("href");

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

                // 동영상 썸네일 url
                Log.v(TAG, "썸네일 url: " + img_url);

            }
            else {
                // "img"태그의 "src" 값을 저장
                // 동영상 썸네일 url
                Log.v(TAG, "썸네일 url: " + img_url);
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
}
