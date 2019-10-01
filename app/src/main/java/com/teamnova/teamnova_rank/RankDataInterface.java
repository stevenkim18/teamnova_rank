package com.teamnova.teamnova_rank;

import java.util.List;

/**
 * 인터페이스 명 : RankDataInterface
 * 설명 : SQLiteDB의 랭크 데이터 처리
 *
 * Class RankData 구조
 *     private int rankID; //식별 문자
 *     private String rankTitle; //게시글 제목
 *     private String rankWriter; //게시글 작성자
 *     private String createDate; //게시글 작성일
 *     private String detailLink; //상세 보기 링크
 *     private String thumbPath; //썸네일 url
 *     private int viewCount; //조회수
 *     private int likeCount; //좋아요 개수
 *     private int replyCount; //댓글 개수
 *     private int rankType; //작품 단계(기초, ..., 응용1, 응용2)
 *     private int ranking; //랭킹
 */
public interface RankDataInterface {

    // 크롤링한 데이터를 저장한다.
    void insertRankData(String rankTitle, String rankWriter, String createDate, String detailLink, String thumbPath
            , int viewCount, int likeCount, int replyCount, int rankType);

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

    // 개인별 평균 점수 목록
    List<RankData> selectIndividualRankList();

    // 팀원 이름 또는 작품명으로 검색
    List<RankData> selectRankListBySearchText();


}
