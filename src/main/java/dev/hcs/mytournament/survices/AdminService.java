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

    public TournamentEntity[] getTournaments() {
        return this.adminMapper.selectTournaments();
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

    public UserEntity[] getUsers() {
        return this.adminMapper.selectUsers();
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
    public TournamentCommentEntity[] getReportedComments() {
        return this.adminMapper.selectReportedComments();
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

    // 굿즈 목록들
    GoodsEntity[] getGoods() {
        return null;
    }
}
