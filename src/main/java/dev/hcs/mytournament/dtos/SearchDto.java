package dev.hcs.mytournament.dtos;

import lombok.Data;
import lombok.Setter;

@Data
public class SearchDto {
    private String by;              // 무슨 방식으로 검색할지?
    private String keyword;         // 검색 키워드는 무엇인지?
    private String userEmail;
    private int tournamentIndex;
    private String title;

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

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;       // 전체 갯수
        maxPage = totalCount / countPerPage + (totalCount % countPerPage == 0 ? 0 : 1);     // 최대 페이지
        minPage = 1;    // 최소 페이지
        offset = countPerPage * (requestPage - 1);  // 거를 갯수

        totalPage = (int)(Math.ceil(totalCount/(double)countPerPage));  // 전체 페이지 수
        beginPage = ((requestPage -1)/naviSize) * naviSize + 1;     // 시작 페이지 번호
        endPage = Math.min(beginPage + naviSize -1, totalPage);     // 끝 페이지 번호
        showPrev = beginPage != 1;                                  // 이전 표시 여부
        showNext = endPage != totalPage;                            // 다음 표시 여부
    }
}
