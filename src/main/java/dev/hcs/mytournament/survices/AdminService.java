package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.mappers.AdminMapper;
import dev.hcs.mytournament.mappers.StoreMapper;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.mappers.UserMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AdminService {
    private final UserMapper userMapper;
    private final TournamentMapper tournamentMapper;
    private final AdminMapper adminMapper;
    private final StoreMapper storeMapper;

    @Autowired
    public AdminService(UserMapper userMapper, TournamentMapper tournamentMapper, AdminMapper adminMapper, StoreMapper storeMapper) {
        this.userMapper = userMapper;
        this.tournamentMapper = tournamentMapper;
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
    }

    public TournamentEntity[] getTournaments(SearchDto search) {
        search.setTotalCount(this.adminMapper.selectTournamentsCount());
        return this.adminMapper.selectTournaments(search);
    }

    // 토너먼트 승인
    public Result recognizeTournament(int index) {
        TournamentEntity dbTournament = this.tournamentMapper.selectTournamentByIndex(index);
        if (dbTournament == null) {
            return CommonResult.FAILURE;
        }

        if (!dbTournament.isRecognized()) {
            dbTournament.setRecognized(true);
        } else {
            dbTournament.setRecognized(false);
        }

        return this.tournamentMapper.updateTournament(dbTournament) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public UserEntity[] getUsers(SearchDto search) {
        search.setTotalCount(this.adminMapper.selectUsersCount());
        return this.adminMapper.selectUsers(search);
    }

    // 유저 계정 정지
    public Result suspendUser (String email) {
        UserEntity dbUser = this.userMapper.selectUserByEmail(email);
        // db에 해당 유저가 없거나 관리자일 경우 정지 실패
        if (dbUser == null || dbUser.isAdmin()) {
            return CommonResult.FAILURE;
        }
        // 비정지 계정일 경우 정지하고, 정지 계정일 경우 정지를 푼다.
        if (!dbUser.isSuspended()) {
            dbUser.setSuspended(true);
        } else {
            dbUser.setSuspended(false);
        }
        return this.userMapper.updateUser(dbUser) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 신고받은 댓글들 모두 조회
    public TournamentCommentEntity[] getReportedComments(SearchDto search) {
        search.setTotalCount(this.adminMapper.selectReportedCommentsCount());
        return this.adminMapper.selectReportedComments(search);
    }

    // 신고받은 댓글 하나 조회
    public TournamentCommentEntity getComment(int index) {
        return this.tournamentMapper.selectTournamentCommentByIndex(index);
    }

    // 신고 받은 댓글 문제 없음
    public Result updateReportedComment(int index) {
        TournamentCommentEntity dbComment = this.tournamentMapper.selectTournamentCommentByIndex(index);
        if (dbComment == null || !dbComment.isReported()) {
            return CommonResult.FAILURE;
        }
        dbComment.setReported(false);
        return this.tournamentMapper.updateTournamentComment(dbComment) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 신고 받은 댓글 삭제
    public Result deleteReportedComment(int index) {
        TournamentCommentEntity dbComment = this.tournamentMapper.selectTournamentCommentByIndex(index);
        if (dbComment == null || !dbComment.isReported()) {
            return CommonResult.FAILURE;
        }
        return this.tournamentMapper.deleteTournamentComment(index) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 인덱스별 해당 굿즈 정보 불러오기
    public GoodsEntity getGoodsByIndex(int index) {
        return this.storeMapper.selectGoodsByIndex(index);
    }

    // 인덱스별 해당 굿즈의 이미지들 불러오기
    public GoodsImageEntity[] getGoodsImages(int index) {
        return this.storeMapper.selectGoodsImageByGoodsIndex(index);
    }

    // 굿즈 수정
    @Transactional
    public Result goodsModify(GoodsEntity goods, MultipartFile[] files, int[] deletedImagesIndex, UserEntity user) throws IOException {
        // 관리자가 아닐 경우 수정 실패
        if (user == null || !user.isAdmin()) {
            return CommonResult.FAILURE;
        }
        if (goods == null) {
            return CommonResult.FAILURE;
        }
        if (goods.getTitle().length() < 2 || goods.getTitle().length() > 50) {
            return CommonResult.FAILURE;
        }
        if (goods.getContent().isEmpty() || goods.getContent().length() > 1000) {
            return CommonResult.FAILURE;
        }
        if (goods.getDiscount() < 0 || goods.getDiscount() > 100) {
            return CommonResult.FAILURE;
        }
        GoodsEntity dbGoods = this.storeMapper.selectGoodsByIndex(goods.getIndex());
        if (dbGoods == null) {
            return CommonResult.FAILURE;
        }

        // 기존의 이미지들이 삭제 되었을 경우
        if (deletedImagesIndex != null) {
            // 그 이미지들을 삭제
            for (int i = 0; i < deletedImagesIndex.length; i++) {
                this.adminMapper.deleteGoodsImage(deletedImagesIndex[i]);
            }
        }

        // 만일 새로 추가한 이미지가 있을 경우 이미지 DB에 insert
        if (files != null) {
            GoodsImageEntity goodsImage = new GoodsImageEntity();
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                goodsImage.setGoodsIndex(goods.getIndex());
                goodsImage.setImage(file.getBytes());
                goodsImage.setImageFileName(file.getOriginalFilename());
                goodsImage.setImageContentType(file.getContentType());
                this.storeMapper.insertGoodsImage(goodsImage);
            }
        }

        dbGoods.setTitle(goods.getTitle());
        dbGoods.setContent(goods.getContent());
        dbGoods.setPrice(goods.getPrice());
        dbGoods.setDiscount(goods.getDiscount());
        dbGoods.setStoke(goods.getStoke());
        dbGoods.setSale(dbGoods.isSale());
        return this.adminMapper.updateGoods(dbGoods) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
    
    // 굿즈 판매 여부 전환
    public Result changeGoodsSale(int index, UserEntity user) {
        // 관리자가 아닐 경우 판매 여부 전환 실패
        if (user == null || !user.isAdmin()) {
            return CommonResult.FAILURE;
        }
        GoodsEntity dbGoods = this.storeMapper.selectGoodsByIndex(index);
        if (dbGoods == null) {
            return CommonResult.FAILURE;
        }
        // 만약 해당 굿즈의 판매여부가 false면 true로 true면 false로 변경
        if (!dbGoods.isSale()) {
            dbGoods.setSale(true);
        } else {
            dbGoods.setSale(false);
        }

        return this.adminMapper.updateGoods(dbGoods) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
