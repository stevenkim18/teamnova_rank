package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AppCompatActivity;

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

public class CrawlingBroadActivity extends AppCompatActivity {

    // 디버깅을 위한 TAG
    private final static String TAG = "크롤링액티비티";

    // 크롤링할 url 주보
    private final static String URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=1";

    // 크롤링 시작 버튼
    Button start_crawling_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawling_broad);

        start_crawling_btn = findViewById(R.id.start_crawling_btn);

        // 버튼 클릭 시
        start_crawling_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();

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
                // url 주소로 html 파일 가지고 오기
                Document document = Jsoup.connect(URL).get();

                // 팀노바 오픈 카페 작품 페이지에서 게시글이 있는 부분만 크롤링 함.
                Elements broad_list = document.select("#main-area > ul.article-movie-sub > li");

                Log.v("크롤링", "크롤링한 개시글 갯수 = " + broad_list.size());

                for (int i = 0; i < broad_list.size(); i++){

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



                    // 게시물 Url과 썸네일 Url을 가지고 오기기 a태그 부분만 html 파일을 불러옴.
                    /*
                    <a href="/ArticleRead.nhn?clubid=29412673&amp;menuid=22&amp;boardtype=C&amp;page=1&amp;articleid=1136&amp;referrerAllArticles=false">
						<img src="https://phinf.pstatic.net/image.nmv/cafe_2019_09_23_1220/834d0881-dddf-11e9-9adc-48df37269fd0_01.jpg?type=f100x100" width="120" height="120" alt="썸네일 이미지">
                            <em class="ico_newcafe ico_play">
                            동영상
                            </em>
					</a>
					*/
                    Document imageTag = Jsoup.parse(broad_list.get(i).getElementsByClass("movie-img").html());

                    // "a"태그에 접근
                    Elements aTag = imageTag.select("a");
                    // "img"태그에 접근
                    Elements imgTag = imageTag.select("a > img");

                    // "a"태그의 "href" 값을 저장
                    String postUrl = "https://cafe.naver.com/teamnovaopen" + aTag.get(0).attr("href");
                    // "img"태그의 "src" 값을 저장
                    String imgUrl = imgTag.get(0).attr("src");

                    // 게시글 보기 링크 url
                    Log.v(TAG, "게시글 url: " + postUrl);
                    // 동영상 썸네일 url
                    Log.v(TAG, "썸네일 url: " + imgUrl);


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
}
