package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CrawlingBroadActivity extends AppCompatActivity {

    // 디버깅을 위한 TAG
    private final static String TAG = "크롤링액티비티";

    // 크롤링 할 URL 주소를 담는 리스트
    private ArrayList<String> urls;

    // 크롤링 시작 버튼
    Button start_crawling_btn;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawling_broad);

        start_crawling_btn = findViewById(R.id.start_crawling_btn);

        //DBHelper 초기화
        databaseHelper = DatabaseHelper.getInstance(this);

        // url 주소 리스트 만들기
        makeUrlList();

        // 버튼 클릭 시
        start_crawling_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CrawlingBroadActivity.this,MainActivity.class);
                startActivity(intent);

               /* JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();*/

            }
        });
    }

    // 크롤링을 실행하는 AsyncTask
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void>{

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
                    databaseHelper.deleteRankData(url_num);
                    Log.v(TAG, "==============================================================================================");

                    int page_num = 0;

                    while (true){

                        //페이지 1증가
                        page_num++;

                        // 각 작품의 페이지 url 과 페이지 숫자를 합침.
                        //ex) "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=10"
                        String crawlingUrl = urls.get(url_num) + page_num;

                        Log.v(TAG, "URL = " + crawlingUrl);

                        // url 주소로 html 파일 가지고 오기
                        Document document = Jsoup.connect(crawlingUrl).get();

                        // 해당 페이지 게시물에 없을 때는 크롤링 멈춤.
                        if(document.select("#main-area > ul.article-movie-sub > li").size() == 0){
                            break;
                        }

                        // 크롤링 시작
                        getDataFromWebPage(document, url_num);

                    }

                    Log.v(TAG, "크롤링한 페이지 수 = " + (page_num - 1));

                }

                Log.v(TAG, "==============================================================================================");

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

        Log.v("크롤링", "크롤링한 개시글 갯수 = " + broad_list.size());

        for (int i = 0; i < broad_list.size(); i++){

            String title = broad_list.get(i).getElementsByClass("inner").text();
            String writer = broad_list.get(i).getElementsByClass("m-tcol-c").text();
            String create_date = broad_list.get(i).getElementsByClass("date").text();
            int view_count = Integer.parseInt(broad_list.get(i).getElementsByClass("num").get(0).text().split(" ")[1]);
            int reply_count = Integer.parseInt(broad_list.get(i).getElementsByClass("comment_area").text().split(" ")[1]);
            int like_count = Integer.parseInt(broad_list.get(i).getElementsByClass("u_cnt num-recomm").text());

            Log.v(TAG, "------------------------------------------------------------ " + (i + 1) + "번째 게시글 -----------------------------------------------------------------");

            // 게시글 제목
            Log.v(TAG, "제목: " + broad_list.get(i).getElementsByClass("inner").text());

            // 작성자
            Log.v(TAG, "작성자: " + broad_list.get(i).getElementsByClass("m-tcol-c").text());

            // 게시 날짜
            Log.v(TAG, "날짜: " + broad_list.get(i).getElementsByClass("date").text());

            // 조회수
            // 조회수와 댓글수의 tag class 가 모두 "num"으로 되어 있기 때문에 get(0)을 해서 조회수 값만 가지고 옴.
            // 조회수 값을 크롤링 할 때 숫자 앞에 "조회" 텍스트가 같이 와서 띄어쓰기로 쪼갠 후 숫자 값만 가지고 옴.
            Log.v(TAG, "조회수: " + broad_list.get(i).getElementsByClass("num").get(0).text().split(" ")[1]);

            // 댓글 수
            // 댓글수 값을 크롤링 할 때 숫자 앞에 "댓글" 텍스트가 같이 와서 띄어쓰기로 쪼갠 후 숫자 값만 가지고 옴.
            Log.v(TAG, "댓글수: " + broad_list.get(i).getElementsByClass("comment_area").text().split(" ")[1]);

            // 좋아요 수
            Log.v(TAG, "좋아요수: " + broad_list.get(i).getElementsByClass("u_cnt num-recomm").text());

            //게시글 url
            String post_url = "https://m.cafe.naver.com/teamnovaopen" + broad_list.get(i).getElementsByClass("tit").attr("href");
            Log.v(TAG, "게시글 태그: " + post_url);

            Document image_tag = Jsoup.parse(broad_list.get(i).getElementsByClass("movie-img").html());

            Log.v(TAG, "이미지 태그 = " + image_tag.text());

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
//                img_url = null;

                // 동영상 썸네일 url
                Log.v(TAG, "썸네일 url: " + img_url);
            }

            // DB에 데이터 들어감.
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