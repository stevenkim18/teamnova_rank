package com.teamnova.teamnova_rank;

import java.util.List;

/**
 * 인터페이스 명 : RankDataInterface
 * 설명 : SQLiteDB의 랭크 데이터 처리
 */
public interface RankDataInterface {

    // 크롤링한 데이터를 저장한다.
    void insertRankData(RankData rankData);

    // 기초 자바단계의 작품 목록만 가져온다.
    List<RankData> selectBasicJavaStepList();

    // 기초 안드로이드단계의 작품 목록만 가져온다.
    List<RankData> selectBasicAndroidStepList();

    // 기초 php단계의 작품 목록만 가져온다.
    List<RankData> selectBasicPhpStepList();

    // 기초 응용 1단계의 작품 목록만 가져온다.
    List<RankData> selectHardStep1List();

    // 기초 응용 2단계의 작품 목록만 가져온다.
    List<RankData> selectHardStep2List();

}
