package com.teamnova.teamnova_rank;

import java.util.ArrayList;
import java.util.List;

/**
 * 클래스 명 : Constant class
 * 설명 : 상수를 기록해놓은 클래스이다.
 */
public class Constant {

    // 기초 자바 작품
    public static final int RANK_TYPE_BASIC_JAVA = 0;

    // 기초 안드로이드 작품
    public static final int RANK_TYPE_BASIC_ANDROID = 1;

    // 기초 PHP 작품
    public static final int RANK_TYPE_BASIC_PHP = 2;

    // 응용 1단계 작품
    public static final int RANK_TYPE_HARD_1 = 3;

    // 응용 2단계 작품
    public static final int RANK_TYPE_HARD_2 = 4;

    // 기초 자바 작품 url
    public static final String CAFE_TEAMNOVA_JAVA_URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=22&search.boardtype=C&search.totalCount=151&search.page=";

    // 기초 안드로이드 작품 url
    public static final String CAFE_TEAMNOVA_ANDROID_URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=26&search.boardtype=C&search.totalCount=101&search.page=";

    // 기초 PHP 작품 url
    public static final String CAFE_TEAMNOVA_PHP_URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=27&search.boardtype=C&search.totalCount=81&search.page=";

    // 응용 1단계 작품 url
    public static final String CAFE_TEAMNOVA_HARD1_URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=23&search.boardtype=C&search.totalCount=44&search.page=";

    // 응용 2단계 작품
    public static final String CAFE_TEAMNOVA_HARD2_URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=29412673&search.menuid=24&search.boardtype=C&search.totalCount=31&search.page=";

    //  팀노바 카페 목록 리스트
    public static final List<String> CAFE_TEAMNOVA_URL_LIST = new ArrayList<String>(){{
        add(CAFE_TEAMNOVA_JAVA_URL);
        add(CAFE_TEAMNOVA_ANDROID_URL);
        add(CAFE_TEAMNOVA_PHP_URL);
        add(CAFE_TEAMNOVA_HARD1_URL);
        add(CAFE_TEAMNOVA_HARD2_URL);
    }};

    // 팀노바 카페 목록 번호 리스트
    public static final List<Integer> CAFE_TEAMNOVA_NUMBER_LIST = new ArrayList<Integer>(){{
        add(RANK_TYPE_BASIC_JAVA);
        add(RANK_TYPE_BASIC_ANDROID);
        add(RANK_TYPE_BASIC_PHP);
        add(RANK_TYPE_HARD_1);
        add(RANK_TYPE_HARD_2);
    }};
}
