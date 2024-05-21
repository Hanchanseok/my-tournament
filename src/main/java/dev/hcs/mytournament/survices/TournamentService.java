package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.RankingDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.TournamentProductEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class TournamentService {
    private final TournamentMapper tournamentMapper;

    @Autowired
    public TournamentService(TournamentMapper tournamentMapper) {
        this.tournamentMapper = tournamentMapper;
    }

    @Transactional
    public Result uploadTournament(
            TournamentEntity tournament,
            TournamentProductEntity product,
            UserEntity user,
            MultipartFile thumbnail,
            MultipartFile[] files,
            String[] productNames
    ) throws IOException {
        if (tournament == null) {
            return CommonResult.FAILURE;
        }
        if (tournament.getTitle().length() > 50 || tournament.getTitle().length() < 2) {
            return CommonResult.FAILURE;
        }
        if (tournament.getContent().length() > 1000 || tournament.getContent().isEmpty()) {
            return CommonResult.FAILURE;
        }

        tournament.setUserEmail(user.getEmail());
        tournament.setThumbnail(thumbnail.getBytes());
        tournament.setThumbnailFileName(thumbnail.getOriginalFilename());
        tournament.setThumbnailContentType(thumbnail.getContentType());
        tournament.setPlayCount(0);
        tournament.setCreatedAt(LocalDateTime.now());
        tournament.setModifiedAt(null);
        tournament.setRecognized(false);
        this.tournamentMapper.insertTournament(tournament);

        if (files == null || files.length != 16) {
            return CommonResult.FAILURE;
        }
        if (productNames == null || productNames.length != 16) {
            return CommonResult.FAILURE;
        }
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String productName = productNames[i];
            if (productName.length() > 50 || productName.isEmpty()) {
                return CommonResult.FAILURE;
            }
            product.setTournamentIndex(tournament.getIndex());
            product.setProductThumbnail(file.getBytes());
            product.setProductThumbnailFileName(file.getOriginalFilename());
            product.setProductThumbnailContentType(file.getContentType());
            product.setName(productName);
            product.setPoint(0);
            this.tournamentMapper.insertTournamentProduct(product);
        }
        return CommonResult.SUCCESS;
    }

    // 토너먼트 선택
    public TournamentEntity get(int index) {
        return this.tournamentMapper.selectTournamentByIndex(index);
    }

    // 토너먼트 요소 선택
    public TournamentProductEntity getProduct(int index) {
        return this.tournamentMapper.selectTournamentProductByIndex(index);
    }


    // 홈 화면에 대회들 정렬(페이징과 검색)
    public TournamentEntity[] getTournaments(SearchDto search) {
        if (search.getKeyword() == null || search.getKeyword().length() > 50) {
            search.setKeyword(null);
        }
        if (search.getBy() == null) {
            search.setBy(null);
        }

        search.setTotalCount(this.tournamentMapper.getTournamentTotalCount(search));    // 전체 업로드 대회들 갯수
        search.setMaxPage(search.getTotalCount() / search.getCountPerPage() +
                ( search.getTotalCount() % search.getCountPerPage() == 0 ? 0 : 1 ));    // 최대 페이지
        search.setMinPage(1);                                                           // 최소 페이지
        search.setOffset(search.getCountPerPage() * (search.getRequestPage() - 1));     // 거를 게시글 수

        search.setTotalPage( (int)(Math.ceil( search.getTotalCount()/(double)search.getCountPerPage() )) );  // 전체 페이지 수 구하기
        search.setBeginPage( ((search.getRequestPage() - 1)/search.getNaviSize()) * search.getNaviSize() + 1 );   // 시작 페이지 번호 구하기
        search.setEndPage(Math.min(search.getBeginPage() + search.getNaviSize() -1, search.getTotalPage()));        // 끝 페이지 번호 구하기

        search.setShowPrev( search.getBeginPage() != 1);        // 이전 표시 여부
        search.setShowNext( search.getEndPage() != search.getTotalPage() ); // 다음 표시 여부
        return this.tournamentMapper.selectTournaments(search);
    }

    // 대회 랭킹 조회
    public RankingDto[] getRanking(int index) {
        // 우선 전체 포인트의 갯수를 구한다.
        int totalPoint = this.tournamentMapper.selectTotalPoint(index);
        // 그 후 파라미터를 넣어 조회
        return this.tournamentMapper.selectRanking(index, totalPoint);
    }
}
