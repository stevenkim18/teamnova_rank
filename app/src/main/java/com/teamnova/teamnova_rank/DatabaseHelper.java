package com.teamnova.teamnova_rank;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * 클래스 명 : DatabaseHelper class
 * 설명 : 크롤링한 데이터를 저장하고 사용한다.
 * extends SQLiteOpenHelper한 이유 : 앱 최초 실행시 테이블을 생성, 기존 테이블이 있는 경우 삭제후 다시 생성한다. 에러 발생을 막아준다.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements RankDataInterface {

    /* 데이터베이스 버전 및 이름 */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "teamnova_rank.db";

    /* 테이블 명*/
    private static final String TABLE_NAME_RANK = "RANK_INFO_TB";

    /* 데이터 타입 */
    // https://trello.com/b/i59vsfKE/scstnghks : 데이터 모델 구조 참조
    private static final String RANK_COLUMN_RANK_ID = "RANK_ID";
    private static final String RANK_COLUMN_TITLE = "TITLE";
    private static final String RANK_COLUMN_WRITER = "WRITER";
    private static final String RANK_COLUMN_CREATE_DATE = "CREATE_DATE";
    private static final String RANK_COLUMN_DETAIL_LINK = "DETAIL_LINK";
    private static final String RANK_COLUMN_THUMB_PATH = "THUMB_PATH";
    private static final String RANK_COLUMN_VIEW_COUNT = "VIEW_COUNT";
    private static final String RANK_COLUMN_LIKE_COUNT = "LIKE_COUNT";
    private static final String RANK_COLUMN_REPLY_COUNT = "REPLY_COUNT";
    private static final String RANK_COLUMN_RANKING = "RANKING";
    private static final String RANK_COLUMN_TYPE = "TYPE";


    /* 테이블 생성 쿼리 */
    private static final String CREATE_RANK_TABLE =
            "CREATE TABLE " + TABLE_NAME_RANK + "(" +
                    RANK_COLUMN_RANK_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RANK_COLUMN_TITLE + " TEXT, " +
                    RANK_COLUMN_WRITER + " TEXT, " +
                    RANK_COLUMN_CREATE_DATE + " TEXT, " +
                    RANK_COLUMN_DETAIL_LINK + " TEXT, " +
                    RANK_COLUMN_THUMB_PATH + " TEXT, " +
                    RANK_COLUMN_VIEW_COUNT + " INTEGER, " +
                    RANK_COLUMN_LIKE_COUNT + " INTEGER, " +
                    RANK_COLUMN_REPLY_COUNT + " INTEGER, " +
                    RANK_COLUMN_RANKING + " INTEGER, " +
                    RANK_COLUMN_TYPE + " INTEGER " +
                    ")";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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

    @Override
    public void insertRankData(RankData rankData) {

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

