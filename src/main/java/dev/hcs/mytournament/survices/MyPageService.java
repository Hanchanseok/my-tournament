package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.GoodsWishlistDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.dtos.TournamentCommentDto;
import dev.hcs.mytournament.entities.GoodsWishlistEntity;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.MyPageMapper;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.mappers.UserMapper;
import dev.hcs.mytournament.regexes.UserRegex;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.user.RegisterResult;
import dev.hcs.mytournament.results.user.UpdatePasswordResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    // 이메일로 유저 불러오기
    public UserEntity getUser(UserEntity user) {
        if (user == null) return null;
        return this.userMapper.selectUserByEmail(user.getEmail());
    }

    // 이메일로 토너먼트 갯수 불러오기
    public int getTournamentCount(UserEntity user) {
        if (user == null) return 0;
        return this.tournamentMapper.selectTournamentCountByEmail(user.getEmail());
    }

    // 이메일로 댓글 갯수 가져오기
    public int getCommentCount(UserEntity user) {
        if (user == null) return 0;
        return this.tournamentMapper.selectCommentCountByEmail(user.getEmail());
    }

    // 이메일로 주문 갯수 가져오기
    public int getOrderCount(UserEntity user) {
        if (user == null) return 0;
        return this.myPageMapper.countGoodsOrderByEmail(user.getEmail());
    }

    // 이메일로 내 토너먼트 전부 불러오기
    public TournamentEntity[] getMyTournaments(UserEntity user, SearchDto search) {
        if (user == null) return null;
        search.setTotalCount(this.myPageMapper.countTournamentsByEmail(user.getEmail()));
        search.setUserEmail(user.getEmail());
        return this.myPageMapper.selectTournamentsByEmail(search);
    }

    // 이메일로 내 댓글 전부 불러오기
    public TournamentCommentDto[] getMyComments(UserEntity user, SearchDto search) {
        if (user == null) return null;
        search.setTotalCount(this.tournamentMapper.selectCommentCountByEmail(user.getEmail()));
        search.setUserEmail(user.getEmail());
        return this.myPageMapper.selectCommentsByEmail(search);
    }

    // 정보 수정 시 비밀번호 입력한게 맞는지 확인
    public Result checkMyPassword(UserEntity user, String password) {
        if (user == null || password == null) {
            return CommonResult.FAILURE;
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    // 중복 닉네임 검사
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

    // 닉네임 변경
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
        user.setNickname(nickname);     // 현재 로그인한 내 정보 세션도 닉네임 변경
        return this.userMapper.updateUser(myUser) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 비밀번호 변경
    public Result updateMyPassword(UserEntity user, String password, String passwordCheck) {
        if (user == null || password == null || passwordCheck == null) {
            return UpdatePasswordResult.FAILURE;
        }
        if (!UserRegex.password.tests(password)) {  // 비밀번호 구성 안 맞음
            return UpdatePasswordResult.FAILURE_PASSWORD_REGEX;
        }
        if (!password.equals(passwordCheck)) {  // 비밀번호 체크 안 맞음
            return UpdatePasswordResult.FAILURE_PASSWORD_CHECK;
        }
        if (BCrypt.checkpw(password, user.getPassword())) {  // 현재 비밀번호와 같은 것이라면..
            return UpdatePasswordResult.FAILURE_CURRENT_PASSWORD;
        }
        UserEntity dbUser = this.userMapper.selectUserByEmail(user.getEmail());
        if (dbUser == null) {
            return UpdatePasswordResult.FAILURE;
        }
        dbUser.setPassword( new BCryptPasswordEncoder().encode(password) );
        // 현재 로그인한 내 정보 세션도 비밀번호 변경
        user.setPassword( new BCryptPasswordEncoder().encode(password) );
        return this.userMapper.updateUser(dbUser) > 0
                ? UpdatePasswordResult.SUCCESS
                : UpdatePasswordResult.FAILURE;
    }

    // 내가 찜한 굿즈 목록 조회
    public GoodsWishlistDto[] getMyWishlist(SearchDto search, UserEntity user) {
        if (user == null) return null;
        search.setTotalCount(this.myPageMapper.countGoodsWishlistByEmail(user.getEmail()));
        search.setUserEmail(user.getEmail());
        return this.myPageMapper.selectGoodsWishlistByEmail(search);
    }

    // 굿즈 찜 삭제
    public Result deleteMyWishlist(int goodsIndex, UserEntity user) {
        if (user == null) {
            return CommonResult.FAILURE;
        }
        GoodsWishlistEntity dbWishlist = this.myPageMapper.selectGoodsWishlist(goodsIndex, user.getEmail());
        if (dbWishlist == null) {
            return CommonResult.FAILURE;
        }
        return this.myPageMapper.deleteGoodsWishlist(goodsIndex, user.getEmail()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 내 굿즈 주문내역
    public GoodsOrderDto[] getMyGoodsOrder(SearchDto search, UserEntity user) {
        if (user == null) return null;
        search.setUserEmail(user.getEmail());
        search.setTotalCount(this.myPageMapper.countGoodsOrderByEmail(search.getUserEmail()));
        return this.myPageMapper.selectGoodsOrderByEmail(search);
    }
}
