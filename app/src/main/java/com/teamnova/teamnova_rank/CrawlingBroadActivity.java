package com.teamnova.teamnova_rank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.ads.rewarded.RewardedAdCallback.ERROR_CODE_INTERNAL_ERROR;

public class CrawlingBroadActivity extends AppCompatActivity {

    // 디버깅을 위한 TAG
    private final static String TAG = "크롤링액티비티";

    // 크롤링 할 URL 주소를 담는 리스트
    private ArrayList<String> urls;

    // 크롤링 시작 버튼
    Button start_crawling_btn;

    private DatabaseHelper databaseHelper;

    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawling_broad);
        databaseHelper = DatabaseHelper.getInstance(this);
        start_crawling_btn = findViewById(R.id.start_crawling_btn);

        // url 주소 리스트 만들기
        makeUrlList();

        for(String COURSC_STEP : urls){

        }

        for(int url_num = 0; url_num < urls.size(); url_num++){
            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
//            jsoupAsyncTask.execute(url_num);
            jsoupAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url_num);
        }

        //광고
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });

//        rewardedAd = new RewardedAd(CrawlingBroadActivity.this, "ca-app-pub-3940256099942544/5224354917");
//        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
//            @Override
//            public void onRewardedAdLoaded() {
//                // Ad successfully loaded.
//            }
//
//            @Override
//            public void onRewardedAdFailedToLoad(int errorCode) {
//                // Ad failed to load.
////                경고: onRewardedAdFailedToLoad() 메소드에서 새 광고를 로드하려고 시도하는 것은 권장되지 않습니다.
////                ERROR_CODE_INTERNAL_ERROR: 광고 서버에서 잘못된 응답을 받는 등 내부적으로 오류가 발생했다는 의미입니다.
////                ERROR_CODE_INVALID_REQUEST: 광고 단위 ID가 잘못된 경우처럼 광고 요청이 잘못되었다는 의미입니다.
////                ERROR_CODE_NETWORK_ERROR: 네트워크 연결로 인해 광고 요청에 성공하지 못했다는 의미입니다.
////                ERROR_CODE_NO_FILL: 광고 요청에는 성공했지만 광고 인벤토리의 부족으로 광고가 반환되지 않았다는 의미입니다.
//            }
//        };
//        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
//
//        // 버튼 클릭 시
//        start_crawling_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (rewardedAd.isLoaded()) {
//                    Activity thisActivity = CrawlingBroadActivity.this;
//                    RewardedAdCallback adCallback = new RewardedAdCallback() {
//                        public void onRewardedAdOpened() {
//                            // Ad opened.
//                            Log.d("광고 오픈","onRewardedAdOpened");
////                            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
////                            jsoupAsyncTask.execute();
//                        }
//
//                        public void onRewardedAdClosed() {
//                            // Ad closed.
//                        }
//
//                        public void onUserEarnedReward(@NonNull RewardItem reward) {
//                            // User earned reward.
//                            Log.d("시청 완료","완료");
//                        }
//
//                        public void onRewardedAdFailedToShow(int errorCode) {
//                            // Ad failed to display
//                            Log.d("시청 중도 포기","실패");
//                        }
//                    };
//                    rewardedAd.show(thisActivity, adCallback);
//                } else {
//                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
//                }
//
//            }
//        });



    }

    // 크롤링을 실행하는 AsyncTask
    private class JsoupAsyncTask extends AsyncTask<Integer, Void, Integer>{

        // AsyncTask 실행 전
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        // AsyncTask 실행
        @Override
        protected Integer doInBackground(Integer... voids) {
            int url_num = voids[0];
            try {

//                for(int url_num = 0; url_num < urls.size(); url_num++){
//                    if(databaseHelper.selectCrawlScheme(url_num)) continue;


                    // 저장된 작품 단계별 삭제
                    databaseHelper.deleteRankData(url_num);

                    databaseHelper.insertCrawlScheme(url_num, true);

//                    Log.v(TAG, "==============================================================================================");

                    int page_num = 0;

                    while (true){

                        //페이지 1증가
                        page_num++;

                        // 각 작품의 페이지 url 과 페이지 숫자를 합침.
                        //ex) "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=10"
                        String crawlingUrl = urls.get(url_num) + page_num;

//                        Log.v(TAG, "URL = " + crawlingUrl);

                        // url 주소로 html 파일 가지고 오기
                        Document document = Jsoup.connect(crawlingUrl).get();

                        // 해당 페이지 게시물에 없을 때는 크롤링 멈춤.
                        if(document.select("#main-area > ul.article-movie-sub > li").size() == 0){
                            break;
                        }

                        // 크롤링 시작
                        getDataFromWebPage(document, url_num);

                    }

//                    Log.v(TAG, "크롤링한 페이지 수 = " + (page_num - 1));

//                }

//                Log.v(TAG, " ==============================================================================================");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return url_num;
        }

        // AsyncTask 실행 후
        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            Log.d("완료",aVoid+"");
        }

    }

    //웹페이지에서 크롤링하기
    private void getDataFromWebPage(Document document, int STEP){
        // 팀노바 오픈 카페 작품 페이지에서 게시글이 있는 부분만 크롤링 함.
        Elements broad_list = document.select("#main-area > ul.article-movie-sub > li");

        Log.v("크롤링", "크롤링한 개시글 갯수 = " + broad_list.size());

        for (int i = 0; i < broad_list.size(); i++){

//            Log.v(TAG, "------------------------------------------------------------ " + (i + 1) + "번째 게시글 -----------------------------------------------------------------");

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String title = broad_list.get(i).getElementsByClass("inner").text();
            String writer = broad_list.get(i).getElementsByClass("m-tcol-c").text();
            String create_date = broad_list.get(i).getElementsByClass("date").text();
            int view_count = Integer.parseInt(broad_list.get(i).getElementsByClass("num").get(0).text().split(" ")[1]);
            int reply_count = Integer.parseInt(broad_list.get(i).getElementsByClass("comment_area").text().split(" ")[1]);
            int like_count = Integer.parseInt(broad_list.get(i).getElementsByClass("u_cnt num-recomm").text());

            // 게시글 제목
//            Log.v(TAG, "제목: " + broad_list.get(i).getElementsByClass("inner").text());

            // 작성자
//            Log.v(TAG, "작성자: " + broad_list.get(i).getElementsByClass("m-tcol-c").text());

            // 게시 날짜
//            Log.v(TAG, "날짜: " + broad_list.get(i).getElementsByClass("date").text());

            // 조회수
            // 조회수와 댓글수의 tag class 가 모두 "num"으로 되어 있기 때문에 get(0)을 해서 조회수 값만 가지고 옴.
            // 조회수 값을 크롤링 할 때 숫자 앞에 "조회" 텍스트가 같이 와서 띄어쓰기로 쪼갠 후 숫자 값만 가지고 옴.
//            Log.v(TAG, "조회수: " + broad_list.get(i).getElementsByClass("num").get(0).text().split(" ")[1]);

            // 댓글 수
            // 댓글수 값을 크롤링 할 때 숫자 앞에 "댓글" 텍스트가 같이 와서 띄어쓰기로 쪼갠 후 숫자 값만 가지고 옴.
//            Log.v(TAG, "댓글수: " + broad_list.get(i).getElementsByClass("comment_area").text().split(" ")[1]);

            // 좋아요 수
//            Log.v(TAG, "좋아요수: " + broad_list.get(i).getElementsByClass("u_cnt num-recomm").text());

            //게시글 url
            String post_url = "https://cafe.naver.com/teamnovaopen" + broad_list.get(i).getElementsByClass("tit").attr("href");
//            Log.v(TAG, "게시글 태그: " + post_url);

            Document image_tag = Jsoup.parse(broad_list.get(i).getElementsByClass("movie-img").html());

//            Log.v(TAG, "이미지 태그 = " + image_tag.text());

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
//                Log.v(TAG, "썸네일 url: " + img_url);

            }
            else {
                // "img"태그의 "src" 값을 저장

                // 동영상 썸네일 url
//                Log.v(TAG, "썸네일 url: " + img_url);
            }

            databaseHelper.insertRankData(title,writer,create_date,post_url,img_url,view_count,like_count,reply_count,STEP);
        }
    }

    // 크롤링 할 주소들을 리스트에 넣기
    private void makeUrlList(){
        // URL 주소 리스트 생성
        urls = new ArrayList<>();

        //주소 추가
        urls.add("https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=");   //자바
        urls.add("https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=26&search.boardtype=C&search.totalCount=101&search.page=");   //안드로이드
        urls.add("https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=27&search.boardtype=C&search.totalCount=81&search.page=");    //PHP
        urls.add("https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=23&search.boardtype=C&search.totalCount=44&search.page=");    //응용1단계
        urls.add("https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=24&search.boardtype=C&search.totalCount=31&search.page=");    //응용2단계

    }
}
