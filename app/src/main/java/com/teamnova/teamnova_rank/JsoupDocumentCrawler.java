package com.teamnova.teamnova_rank;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class JsoupDocumentCrawler extends AsyncTask<Void, Void, Void > {
    public Document document;
    public int RANK_TYPE;
    public DatabaseHelper databaseHelper;
    public JsoupDocumentListener jsoupDocumentListener;

    public void setJsoupDocumentListener(JsoupDocumentListener jsoupDocumentListener) {
        this.jsoupDocumentListener = jsoupDocumentListener;
    }

    public interface JsoupDocumentListener{
        void onPostExecute();
    }

    public JsoupDocumentCrawler(Document document, int RANK_TYPE, DatabaseHelper databaseHelper) {
        this.document = document;
        this.RANK_TYPE = RANK_TYPE;
        this.databaseHelper = databaseHelper;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        // 팀노바 오픈 카페 작품 페이지에서 게시글이 있는 부분만 크롤링 함.
        Elements broad_list = document.select("#main-area > ul.article-movie-sub > li");
        for (int i = 0; i < broad_list.size(); i++){

            // sleep 시간을 조절해서 아이피 차단을 피해야 한다. 계속 수정중.. .
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String title = broad_list.get(i).getElementsByClass("inner").text().replace("[JAVA]","").replace("[Android]","").replace("[PHP]","").replace("[","\n[");
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

            databaseHelper.insertRankData(title,writer,create_date,post_url,img_url,view_count,like_count,reply_count,RANK_TYPE);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("JsoupDocumentCrawler","완료");
        if(jsoupDocumentListener != null)
            jsoupDocumentListener.onPostExecute();

    }
}