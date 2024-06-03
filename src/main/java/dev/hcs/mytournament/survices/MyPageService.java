package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.TournamentCommentDto;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.MyPageMapper;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.mappers.UserMapper;
import dev.hcs.mytournament.regexes.UserRegex;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.user.RegisterResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class MyPageService {
    private final UserMapper userMapper;
    private final TournamentMapper tournamentMapper;
    private final MyPageMapper myPageMapper;

    @Autowired
    public MyPageService(UserMapper userMapper, TournamentMapper tournamentMapper, MyPageMapper myPageMapper) {
        this.userMapper = userMapper;
        this.tournamentMapper = tournamentMapper;
        this.myPageMapper = myPageMapper;
    }

    public UserEntity getUser(UserEntity user) {
        if (user == null) return null;
        return this.userMapper.selectUserByEmail(user.getEmail());
    }

    public int getTournamentCount(UserEntity user) {
        if (user == null) return 0;
        return this.tournamentMapper.selectTournamentCountByEmail(user.getEmail());
    }

    public int getCommentCount(UserEntity user) {
        if (user == null) return 0;
        return this.tournamentMapper.selectCommentCountByEmail(user.getEmail());
    }

    public TournamentEntity[] getMyTournaments(UserEntity user) {
        if (user == null) return null;
        return this.myPageMapper.selectTournamentsByEmail(user.getEmail());
    }

    public TournamentCommentDto[] getMyComments(UserEntity user) {
        if (user == null) return null;
        return this.myPageMapper.selectCommentsByEmail(user.getEmail());
    }

    public Result checkMyPassword(UserEntity user, String password) {
        if (user == null || password == null) {
            return CommonResult.FAILURE;
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    public Result checkDuplicateNickname(String nickname) {
        if (nickname == null) {
            return CommonResult.FAILURE;
        }
        if (!UserRegex.nickname.tests(nickname)) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserByNickname(nickname);
        if (dbUser != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        return CommonResult.SUCCESS;
    }

    public Result updateMyNickname(UserEntity user, String nickname) {
        if (user == null || nickname == null) {
            return CommonResult.FAILURE;
        }
        if (!UserRegex.nickname.tests(nickname)) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserByNickname(nickname);
        if (dbUser != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        UserEntity myUser = this.userMapper.selectUserByEmail(user.getEmail());
        myUser.setNickname(nickname);
        return this.userMapper.updateUser(myUser) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
