package dev.hcs.mytournament.dtos;

import lombok.Data;

@Data
public class SearchDto {
    private String by;              // 무슨 방식으로 검색할지?
    private String keyword;         // 검색 키워드는 무엇인지?

    private int countPerPage = 12;   // 한 페이지당 보여줄 게시글 개수
    private int requestPage;        // 니가 요청한 페이지 번호
    private int totalCount;         // 전체 게시글의 개수
    private int maxPage;            // 조회할 수 있는 최대 페이지
    private int minPage = 1;        // 조회할 수 있는 최소 페이지
    private int offset;             // 거를 게시글 개수

    private int naviSize = 5;       // 한 페이지에 표시할 페이지 번호 수
    private int totalPage;          // 전체 페이지 수
    private int beginPage;          // 시작 페이지 번호
    private int endPage;            // 끝 페이지 번호
    private boolean showPrev;       // 이전 표시 여부
    private boolean showNext;       // 다음 표시 여부
}
