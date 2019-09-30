package com.teamnova.teamnova_rank;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 클래스 명 : DatabaseHelper class
 * 설명 : 크롤링한 데이터를 저장하고 사용한다.
 * extends SQLiteOpenHelper한 이유 : 앱 최초 실행시 테이블을 생성, 기존 테이블이 있는 경우 삭제후 다시 생성한다. 에러 발생을 막아준다.
 *
 * 규칙 (https://trello.com/b/i59vsfKE/scstnghks - 규칙카드)
 * 테이블 명 : TABLE_NAME_테이블명
 * 컬럼 명 : 테이블명_컬럼명
 */
public class DatabaseHelper extends SQLiteOpenHelper implements RankDataInterface {

    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper instance = null;
    private static SQLiteDatabase database;

    /**
     * 싱글톤 패턴
     * 사용 : DatabaseHelper.getInstance(getApplicationContext()).selectBasicAndroidStepList();
     */
    public static DatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new DatabaseHelper(context.getApplicationContext());
            database = instance.getWritableDatabase(); //읽고 쓰기가 가능하다.
        }
        return instance;
    }


    /* 데이터베이스 버전 및 이름 */
    private static final int DATABASE_VERSION = 75;
    private static final String DATABASE_NAME = "teamnova_rank.db";

    /* 테이블 명*/
    private static final String TABLE_NAME_RANK = "RANK_INFO_TB"; // 크롤링한 정보
    private static final String TABLE_NAME_CRAWL_SCHEME = "CRAWL_SCHEME_TB"; //크롤링한 날짜 및 기타 정보

    /* 데이터 타입 */
    // RANK_INFO_TB
    private static final String RANK_RANK_ID = "RANK_ID"; //식별 문자
    private static final String RANK_TITLE = "TITLE"; //게시글 제목
    private static final String RANK_WRITER = "WRITER"; //게시글 작성자
    private static final String RANK_CREATE_DATE = "CREATE_DATE"; //게시글 작성일
    private static final String RANK_DETAIL_LINK = "DETAIL_LINK"; //상세 보기 링크
    private static final String RANK_THUMB_PATH = "THUMB_PATH"; //썸네일 url
    private static final String RANK_VIEW_COUNT = "VIEW_COUNT"; //조회수
    private static final String RANK_LIKE_COUNT = "LIKE_COUNT"; //좋아요 개수
    private static final String RANK_REPLY_COUNT = "REPLY_COUNT"; //댓글 개수
    private static final String RANK_RANKING = "RANKING"; //랭킹
    private static final String RANK_TYPE = "TYPE";  //작품 단계(기초, ..., 응용1, 응용2)
    private static final String RANK_RANK_POINT = "RANK_POINT";  //작품 단계(기초, ..., 응용1, 응용2)


    // CRAWL_DATE_TB
    private static final String CRAWL_SCHEME_CRAWL_ID = "CRAWL_ID"; // 식별 문자
    private static final String CRAWL_SCHEME_CRAWL_DATE = "CRAWL_DATE"; // 크롤링한 날짜
    private static final String CRAWL_SCHEME_CRAWL_SUCCESS = "CRAWL_SUCCESS"; // 크롤링 성공 여부 0:실패, 1:성공
    private static final String CRAWL_SCHEME_CRAWL_COURSE_TYPE = "CRAWL_COURSE_TYPE"; // 크롤링 데이터 유형

    /* 테이블 생성 쿼리 */
    // RANK_INFO_TB 생성
    private static final String CREATE_RANK_TABLE =
            "CREATE TABLE " + TABLE_NAME_RANK + "(" +
                    RANK_RANK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RANK_TITLE + " TEXT, " +
                    RANK_WRITER + " TEXT, " +
                    RANK_CREATE_DATE + " TEXT, " +
                    RANK_DETAIL_LINK + " TEXT, " +
                    RANK_THUMB_PATH + " TEXT, " +
                    RANK_VIEW_COUNT + " INTEGER, " +
                    RANK_LIKE_COUNT + " INTEGER, " +
                    RANK_REPLY_COUNT + " INTEGER, " +
                    RANK_RANKING + " INTEGER, " +
                    RANK_TYPE + " INTEGER, " +
                    RANK_RANK_POINT + " INTEGER " +
                    ")";

    // CRAWL_DATE_TB 생성
    private static final String CREATE_CRAWL_SCHEME_TABLE =
            "CREATE TABLE " + TABLE_NAME_CRAWL_SCHEME + "(" +
                    CRAWL_SCHEME_CRAWL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CRAWL_SCHEME_CRAWL_DATE + " TEXT ," +
                    CRAWL_SCHEME_CRAWL_SUCCESS + " INTEGER ," +
                    CRAWL_SCHEME_CRAWL_COURSE_TYPE + " INTEGER " +
                    ")";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 앱을 삭제후 앱을 재설치하면 기존 DB파일은 앱 삭제시 지워지지 않기 때문에
        // 테이블이 이미 있다고 생성 에러남
        // 앱을 재설치시 데이터베이스를 삭제해줘야함.

        // RANK 정보 테이블 생성
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RANK);
        db.execSQL(CREATE_RANK_TABLE);

        // 크롤링 정보 테이블 생성
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CRAWL_SCHEME);
        db.execSQL(CREATE_CRAWL_SCHEME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_RANK);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_CRAWL_SCHEME);
        onCreate(db);
    }

    public String selectLastCourseTypeUpdateDate(int COURSE_TYPE){
        String query = "";
        query = "SELECT " +
                    CRAWL_SCHEME_CRAWL_DATE +
                " FROM " +
                    TABLE_NAME_CRAWL_SCHEME +
                " WHERE " +
                    CRAWL_SCHEME_CRAWL_SUCCESS + " = '1'" +
                    " AND " + CRAWL_SCHEME_CRAWL_COURSE_TYPE + " = " + COURSE_TYPE +
                " ORDER BY " +
                    CRAWL_SCHEME_CRAWL_ID + " DESC LIMIT 1";
        Log.d(TAG, "selectLastCourseTypeUpdateDate 쿼리 : "+query);
        Cursor cursor = database.rawQuery(query,null);
        String result = "";
        if(cursor != null && cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex(CRAWL_SCHEME_CRAWL_DATE));
            cursor.close();
        }
        return result;
    }

    /**
     * 마지막 업데이트 날짜를 가져온다.
     * @return
     */
    public String selectLastUpdateDate(){
        String query = "";
        query = "SELECT " +
                        CRAWL_SCHEME_CRAWL_DATE +
                " FROM " +
                        TABLE_NAME_CRAWL_SCHEME +
                " GROUP BY " +
                        CRAWL_SCHEME_CRAWL_DATE +
                " HAVING " +
                        "COUNT("+CRAWL_SCHEME_CRAWL_DATE+") == 5";
        Log.d(TAG, "selectLastUpdateDate 쿼리 : "+query);
        Cursor cursor = database.rawQuery(query,null);
        String result = "";
        if(cursor != null && cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex(CRAWL_SCHEME_CRAWL_DATE));
            cursor.close();
        }
        return result;
    }

    /**
     * 오늘 일자의 크롤링이 모두 완료되었는지 확인한다.
     * @return true : 크롤링 모두 완료, false : 최신 데이터가 아니라 크롤링 필요
     */
    public boolean isCompleteCrawling(){
        String query = "";
        query = "SELECT "  +
                        "CASE WHEN COUNT(*) = 5 THEN 1 ELSE 0 END AS RESULT" +
                " FROM " +
                        "(SELECT " +
                                    CRAWL_SCHEME_CRAWL_COURSE_TYPE +
                        " FROM " +
                                TABLE_NAME_CRAWL_SCHEME+
                        " WHERE " +
                                CRAWL_SCHEME_CRAWL_DATE +" = strftime('%Y-%m-%d','now', 'localtime')" +
                                " AND " + CRAWL_SCHEME_CRAWL_SUCCESS + " = '1'" +
                        " GROUP BY " +
                                CRAWL_SCHEME_CRAWL_COURSE_TYPE +")";


        Cursor cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        boolean result = cursor.getInt(0) > 0 ? true : false;

        Log.d(TAG, "isCompleteCrawling 쿼리 : "+query);
        Log.d(TAG, "결과 : "+result);
        return result;
    }

    /**
     * 광고 시청을 중간에 취소할 경우 크롤링을 완료 여부를 false로 바꾼다.
     * @param updateList
     */
    public void updateCrawlScheme(List<Integer> updateList, boolean isSuccess){

        String query = "UPDATE " +
                                TABLE_NAME_CRAWL_SCHEME +
                        " SET " +
                                CRAWL_SCHEME_CRAWL_SUCCESS + " = " + (isSuccess ? 1 : 0) +
                        " WHERE " +
                                 CRAWL_SCHEME_CRAWL_DATE + " = strftime('%Y-%m-%d','now', 'localtime')" +
                                " AND " + CRAWL_SCHEME_CRAWL_COURSE_TYPE + " IN ("+ TextUtils.join(",",updateList) +") ";
        database.execSQL(query);
//        String whereQuery = CRAWL_SCHEME_CRAWL_DATE + " = ? AND "+CRAWL_SCHEME_CRAWL_COURSE_TYPE + " = ?";

//        ContentValues contentValues = new ContentValues();
//        contentValues.put(CRAWL_SCHEME_CRAWL_SUCCESS,0);
//        database.update(TABLE_NAME_CRAWL_SCHEME,contentValues,whereQuery,new String[]{"strftime('%Y-%m-%d','now', 'localtime')",COURSE_TYPE+""});
        Log.d(TAG,"updateCrawlScheme 실행"+query);
    }

    /**
     * 해당 단계를 같은 날짜에 크롤링한 결과가 있는지 반환한다.
     *
     * ex)
     * --------------------------------------------------
     * SELECT
     * 	    COUNT(*)
     * FROM
     * 	    CRAWL_SCHEME_TB
     * WHERE
     * 	    CRAWL_DATE = strftime('%Y-%m-%d','now', 'localtime')
     * 	    AND CRAWL_SUCCESS = 1
     * 	    AND CRAWL_COURSE_TYPE = 5
     * --------------------------------------------------
     *
     * @return true : 이미 크롤링함, false : 크롤링한 기록이 없음
     *
     */
    public boolean selectCrawlScheme(final int COURSE_TYPE){
        String query = "SELECT "+
                                "COUNT(*) " +
                        " FROM "+
                                TABLE_NAME_CRAWL_SCHEME +
                        " WHERE " +
                                CRAWL_SCHEME_CRAWL_DATE + " = strftime('%Y-%m-%d','now', 'localtime')" +
                                "AND "+ CRAWL_SCHEME_CRAWL_SUCCESS + " = '1'" +
                                "AND "+ CRAWL_SCHEME_CRAWL_COURSE_TYPE + " = "+ COURSE_TYPE;

        Cursor cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        boolean result = cursor.getInt(0) > 0 ? true : false;

        Log.d(TAG, "selectCrawlScheme 쿼리 : "+query);
        Log.d(TAG, "결과 : "+result);
        return result;
    }

    /**
     * 단계별 크롤링 완료 후 크롤링 성공 여부 및 날짜등을 기록한다.
     * > 같은 날짜에 기록이 있는경우 크롤링을 하지 않고 DB에서 정보를 찾기 위함
     *
     * @param COURSE_TYPE : 코스 단계
     * @param IS_SUCCESS : 성공 여부
     */
    public void insertCrawlScheme(int COURSE_TYPE, boolean IS_SUCCESS){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CRAWL_SCHEME_CRAWL_DATE               ,DateUtil.getToday());
        contentValues.put(CRAWL_SCHEME_CRAWL_SUCCESS            ,IS_SUCCESS ? 1 : 0);
        contentValues.put(CRAWL_SCHEME_CRAWL_COURSE_TYPE        ,COURSE_TYPE);

        database.insert(TABLE_NAME_CRAWL_SCHEME, null, contentValues);
    }

    /**
     * 해당 타입 목록 모두 삭제
     * @param rankType
     */
    public void deleteRankData(int rankType){
        database.delete(TABLE_NAME_RANK, RANK_TYPE + "="+rankType,null);
    }

    /**
     * 네이버 팀노바 오픈 카페 - 작품 크롤링 데이터를 DB에 저장한다.
     * @param rankTitle 게시글 제목
     * @param rankWriter 게시글 작성자
     * @param createDate 게시글 작성일
     * @param detailLink 상세 보기 링크
     * @param thumbPath 썸네일 url
     * @param viewCount 조회수
     * @param likeCount 좋아요 개수
     * @param replyCount 댓글 개수
     * @param rankType 작품 단계(기초, ..., 응용1, 응용2)
     */
    @Override
    public void insertRankData(String rankTitle, String rankWriter, String createDate, String detailLink, String thumbPath
            , int viewCount, int likeCount, int replyCount, int rankType) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(RANK_TITLE          ,rankTitle);
        contentValues.put(RANK_WRITER         ,rankWriter);
        contentValues.put(RANK_CREATE_DATE    ,createDate);
        contentValues.put(RANK_DETAIL_LINK    ,detailLink);
        contentValues.put(RANK_THUMB_PATH     ,thumbPath);
        contentValues.put(RANK_VIEW_COUNT     ,viewCount);
        contentValues.put(RANK_LIKE_COUNT     ,likeCount);
        contentValues.put(RANK_REPLY_COUNT    ,replyCount);
        contentValues.put(RANK_TYPE           ,rankType);
        contentValues.put(RANK_RANK_POINT     ,viewCount + (likeCount * 5));

        //데이터 목록 등록
        database.insert(TABLE_NAME_RANK, null, contentValues);

    }


    /**
     * 개인별 작품 평균 점수로 순위를 매긴 목록을 반환한다.
     * @return
     */
    @Override
    public List<RankData> selectIndividualRankList() {
        List<RankData> resultList = new ArrayList<>();
        String query = "";
        query = "SELECT " +
                        RANK_WRITER +
                        ", (SELECT "+
                                RANK_THUMB_PATH+
                            " FROM "+
                                TABLE_NAME_RANK+" T "+
                            "WHERE "+
                                "T."+RANK_WRITER+" = RIT."+RANK_WRITER+
                            " GROUP BY "+
                                RANK_WRITER+
                            " HAVING MAX("+RANK_RANK_POINT+")) AS "+RANK_THUMB_PATH +
                        ", SUM("+RANK_RANK_POINT+")/COUNT("+RANK_WRITER+") AS "+RANK_RANK_POINT+
                        ",COUNT("+RANK_RANK_ID+") AS WORK_COUNT"+
                " FROM " +
                        TABLE_NAME_RANK+" RIT" +
                " GROUP BY "+
                        RANK_WRITER+
                " ORDER BY "+
                        "SUM("+RANK_RANK_POINT+")/COUNT("+RANK_WRITER+") DESC";


        Log.d(TAG, "selectIndividualRankList 쿼리 : "+query);

        Cursor cursor = database.rawQuery(query,null);
        int index = 1;
        while(cursor.moveToNext()){
            RankData rankData = new RankData();
            rankData.setRanking(index++);
            rankData.setRankWriter(cursor.getString(0));
            rankData.setThumbPath(cursor.getString(1));
            rankData.setRankingPoint(cursor.getInt(2));
            resultList.add(rankData);
        }
        return resultList;
    }

    /**
     * 작성자 이름 또는 작품명으로 검색한 결과를 반환한다.
     *
     * @return
     */
    @Override
    public List<RankData> selectRankListBySearchText() {
        List<RankData> resultList = new ArrayList<>();
        String query = "";

        return null;
    }

    /**
     * 기초 자바 단계 목록을 반환한다.
     * @return
     */
    @Override
    public List<RankData> selectBasicJavaStepList() {
        return selectRankListByType(Constant.RANK_TYPE_BASIC_JAVA);
    }

    /**
     * 기초 안드로이드 목록을 반환한다.
     * @return
     */
    @Override
    public List<RankData> selectBasicAndroidStepList() {
        return selectRankListByType(Constant.RANK_TYPE_BASIC_ANDROID);
    }

    /**
     * 기초 php 목록을 반환한다.
     * @return
     */
    @Override
    public List<RankData> selectBasicPhpStepList() {
        return selectRankListByType(Constant.RANK_TYPE_BASIC_PHP);
    }

    /**
     * 응용 1단계 목록을 반환한다.
     * @return
     */
    @Override
    public List<RankData> selectHardStep1List() {
        return selectRankListByType(Constant.RANK_TYPE_HARD_1);
    }

    /**
     * 응용 2단계 목록을 반환한다.
     * @return
     */
    @Override
    public List<RankData> selectHardStep2List() {
        return selectRankListByType(Constant.RANK_TYPE_HARD_2);
    }

    /**
     * 단계별로 작품 목록을 가져온다.
     *
     * @param TYPE_STEP : 작품의 단계
     * @return
     */
    private List<RankData> selectRankListByType(int TYPE_STEP){
        List<RankData> resultList = new ArrayList<>();
        // 우선순위는 조회수 + (좋아요 개수 * 5)가 높은 순
        String query = "SELECT "+
//                                "ROW_NUMBER() OVER(ORDER BY VIEW_COUNT + (5 * LIKE_COUNT) ASC) AS RANKING ,"+
//                                "'1' AS RANKING , " +
//                                "(SELECT COUNT(*) FROM "+TABLE_NAME_RANK+" TEMP WHERE RANK.RANK_ID) AS RANKING ," +
                                "1 AS RANKING ," +
                                "TITLE ,"+
                                "WRITER ,"+
                                "CREATE_DATE ,"+
                                "DETAIL_LINK ,"+
                                "THUMB_PATH ,"+
                                "VIEW_COUNT ,"+
                                "LIKE_COUNT ,"+
                                "REPLY_COUNT ,"+
                                "TYPE ,"+
                                "RANK_ID ,"+
                                "RANK_POINT "+
                        " FROM " +
                                TABLE_NAME_RANK +" RANK " +
                        " WHERE " +
                                "TYPE = '"+TYPE_STEP +"'" +
                        " ORDER BY " +
                                "VIEW_COUNT + (5 * LIKE_COUNT) DESC";

        Log.d(TAG, "쿼리 : "+query);

        Cursor cursor = database.rawQuery(query,null);
        int index = 1;
        while(cursor.moveToNext()){
//            int ranking         = cursor.getInt(0);
            int ranking           = index;
            String title        = cursor.getString(1);
            String writer       = cursor.getString(2);
            String create_date  = cursor.getString(3);
            String detail_link  = cursor.getString(4);
            String thumb_path   = cursor.getString(5);
            int view_count      = cursor.getInt(6);
            int like_count      = cursor.getInt(7);
            int reply_count     = cursor.getInt(8);
            int type            = cursor.getInt(9);
            int rankId          = cursor.getInt(10);
            int rankPoint       = cursor.getInt(11);

            RankData rankData = new RankData(rankId, title, writer, create_date, detail_link, thumb_path, view_count, like_count, reply_count, type, ranking, rankPoint);
            resultList.add(rankData);
            index++;
        }
        return resultList;
    }

}

