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
        void onProgressUpdate(Integer integer,Document pageDocument);
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
//                    publishProgress(-1);
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
        return RANK_TYPE;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(jsoupAsyncListener != null)
            jsoupAsyncListener.onProgressUpdate(values[0],pageDocument);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if(jsoupAsyncListener != null)
            jsoupAsyncListener.onProgressUpdate(-1,pageDocument);
//        if(jsoupAsyncListener != null)
//            jsoupAsyncListener.onPostExecute(integer,pageList);
//        Log.d("크롤링 완료 번호 ",integer+"");
//        Log.d("크기",pageList.size()+"");
    }

}