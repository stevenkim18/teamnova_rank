package com.teamnova.teamnova_rank;

/**
 * 클래스명 : RankData class
 * 설명 : 크롤링한 데이터가 담긴다.
 * 참조 : https://trello.com/b/i59vsfKE/scstnghks - 규칙 - 데이터 모델 구조 카드
 */
public class RankData {

    /**
     * sqlLite에 저장시 autoincrement되는 정보
     */
    private int rankID;
    private String rankTitle;
    private String rankWriter;
    private String createDate;
    private String detailLink;
    private String thumbPath;
    private int viewCount;
    private int likeCount;
    private int replyCount;
    private int rankType;

    /**
     * 목록이 다 만들어진 후 좋아요, 조회수 점수를 매긴다.
     */
    private int ranking;

    public RankData() {

    }

    /**
     * RankData 생성자이다.
     * DB등록시 입력되는 rankID 제외, 모든 목록이 만들어진 후 랭킹이 입력되기때문에 ranking 제외
     *
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
    public RankData(String rankTitle, String rankWriter, String createDate, String detailLink, String thumbPath, int viewCount, int likeCount, int replyCount, int rankType) {
        this.rankTitle = rankTitle;
        this.rankWriter = rankWriter;
        this.createDate = createDate;
        this.detailLink = detailLink;
        this.thumbPath = thumbPath;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.rankType = rankType;
    }

    public int getRankID() {
        return rankID;
    }

    public void setRankID(int rankID) {
        this.rankID = rankID;
    }

    public String getRankTitle() {
        return rankTitle;
    }

    public void setRankTitle(String rankTitle) {
        this.rankTitle = rankTitle;
    }

    public String getRankWriter() {
        return rankWriter;
    }

    public void setRankWriter(String rankWriter) {
        this.rankWriter = rankWriter;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getRankType() {
        return rankType;
    }

    public void setRankType(int rankType) {
        this.rankType = rankType;
    }
}

