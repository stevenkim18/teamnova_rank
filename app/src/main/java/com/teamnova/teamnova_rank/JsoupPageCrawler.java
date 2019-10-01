package com.teamnova.teamnova_rank;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JsoupPageCrawler extends AsyncTask<Integer, Integer, Integer> {

    public JsoupAsyncListener jsoupAsyncListener;
    public DatabaseHelper databaseHelper;
    public Document pageDocument;

    public interface JsoupAsyncListener{
        void onPostExecute(Integer integer,Document pageDocument);
    }

    public void setJsoupAsyncListener(JsoupAsyncListener jsoupAsyncListener) {
        this.jsoupAsyncListener = jsoupAsyncListener;
    }

    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    // AsyncTask 실행
    @Override
    protected Integer doInBackground(Integer... voids) {
        final int RANK_TYPE = voids[0];
        try {

//            Log.d("크롤링 시작 URL 번호 >", RANK_TYPE+"");

            // 저장된 작품 단계별 삭제
            databaseHelper.deleteRankData(RANK_TYPE);

            int page_num = 0;

            while (true){

                //페이지 1증가
                page_num++;

                // 각 작품의 페이지 url 과 페이지 숫자를 합침.
                //ex) "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=10"
                String crawlingUrl = Constant.CAFE_TEAMNOVA_URL_LIST.get(RANK_TYPE) + page_num;

                // url 주소로 html 파일 가지고 오기
                Document document = Jsoup.connect(crawlingUrl).get();

                // 해당 페이지 게시물에 없을 때는 크롤링 멈춤.
                if(document.select("#main-area > ul.article-movie-sub > li").size() == 0){
                    break;
                }

                pageDocument = document;
                // 크롤링 시작
//                getDataFromWebPage(document, RANK_TYPE);
                publishProgress(RANK_TYPE);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // 크롤링 실패 저장
            databaseHelper.insertCrawlScheme(RANK_TYPE, false);
        }
        // 크롤링 성공 저장
        databaseHelper.insertCrawlScheme(RANK_TYPE, true);
        return RANK_TYPE;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(jsoupAsyncListener != null)
            jsoupAsyncListener.onPostExecute(values[0],pageDocument);
    }

    @Override
    protected void onPostExecute(Integer integer) {
//        if(jsoupAsyncListener != null)
//            jsoupAsyncListener.onPostExecute(integer,pageList);
//        Log.d("크롤링 완료 번호 ",integer+"");
//        Log.d("크기",pageList.size()+"");
    }


    //웹페이지에서 크롤링하기
    private void getDataFromWebPage(Document document, int RANK_TYPE){
        // 팀노바 오픈 카페 작품 페이지에서 게시글이 있는 부분만 크롤링 함.
        Elements broad_list = document.select("#main-area > ul.article-movie-sub > li");
        for (int i = 0; i < broad_list.size(); i++){

            // sleep 시간을 조절해서 아이피 차단을 피해야 한다. 계속 수정중. ..
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

            databaseHelper.insertRankData(title,writer,create_date,post_url,img_url,view_count,like_count,reply_count,RANK_TYPE);
        }
    }

}


