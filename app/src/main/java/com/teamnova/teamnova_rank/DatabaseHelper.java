package com.teamnova.teamnova_rank;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "teamnova_rank.db";

    /* 테이블 명*/
    private static final String TABLE_NAME_RANK = "RANK_INFO_TB";

    /* 데이터 타입 */
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


    /* 테이블 생성 쿼리 */
    private static final String CREATE_RANK_TABLE =
            "CREATE TABLE " + TABLE_NAME_RANK + "(" +
                    RANK_RANK_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RANK_TITLE + " TEXT, " +
                    RANK_WRITER + " TEXT, " +
                    RANK_CREATE_DATE + " TEXT, " +
                    RANK_DETAIL_LINK + " TEXT, " +
                    RANK_THUMB_PATH + " TEXT, " +
                    RANK_VIEW_COUNT + " INTEGER, " +
                    RANK_LIKE_COUNT + " INTEGER, " +
                    RANK_REPLY_COUNT + " INTEGER, " +
                    RANK_RANKING + " INTEGER, " +
                    RANK_TYPE + " INTEGER " +
                    ")";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 앱을 삭제후 앱을 재설치하면 기존 DB파일은 앱 삭제시 지워지지 않기 때문에
        // 테이블이 이미 있다고 생성 에러남
        // 앱을 재설치시 데이터베이스를 삭제해줘야함.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RANK);
        db.execSQL(CREATE_RANK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_RANK);
        onCreate(db);
    }


    // ----------------- 하단 구현할 로직 -----------------

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
//        String query = "INSERT INTO "+TABLE_NAME_RANK +"(" +
//                RANK_TITLE + ", " +
//                RANK_WRITER + ", " +
//                RANK_CREATE_DATE + ", " +
//                RANK_DETAIL_LINK + ", " +
//                RANK_THUMB_PATH + ", " +
//                RANK_VIEW_COUNT + ", " +
//                RANK_LIKE_COUNT + ", " +
//                RANK_REPLY_COUNT + ", " +
//                RANK_RANKING + ", " +
//                RANK_TYPE +
//                ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        database.execSQL(query);

        ContentValues contentValues = new ContentValues();
        contentValues.put("RANK_TITLE"          ,rankTitle);
        contentValues.put("RANK_WRITER"         ,rankWriter);
        contentValues.put("RANK_CREATE_DATE"    ,createDate);
        contentValues.put("RANK_DETAIL_LINK"    ,detailLink);
        contentValues.put("RANK_THUMB_PATH"     ,thumbPath);
        contentValues.put("RANK_VIEW_COUNT"     ,viewCount);
        contentValues.put("RANK_LIKE_COUNT"     ,likeCount);
        contentValues.put("RANK_REPLY_COUNT"    ,replyCount);
        contentValues.put("RANK_TYPE"           ,rankType);

        database.insert(TABLE_NAME_RANK, null, contentValues);
    }

    @Override
    public List<RankData> selectBasicJavaStepList() {
        return null;
    }

    @Override
    public List<RankData> selectBasicAndroidStepList() {
        return null;
    }

    @Override
    public List<RankData> selectBasicPhpStepList() {
        return null;
    }

    @Override
    public List<RankData> selectHardStep1List() {
        return null;
    }

    @Override
    public List<RankData> selectHardStep2List() {
        return null;
    }

}

