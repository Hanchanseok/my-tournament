package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.*;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.mappers.*;
import dev.hcs.mytournament.regexes.UserRegex;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.user.RegisterResult;
import dev.hcs.mytournament.results.user.UpdatePasswordResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class MyPageService {
    private final UserMapper userMapper;
    private final TournamentMapper tournamentMapper;
    private final MyPageMapper myPageMapper;
    private final AdminMapper adminMapper;
    private final StoreMapper storeMapper;

    @Autowired
    public MyPageService(UserMapper userMapper, TournamentMapper tournamentMapper, MyPageMapper myPageMapper, StoreService storeService, AdminMapper adminMapper, StoreMapper storeMapper) {
        this.userMapper = userMapper;
        this.tournamentMapper = tournamentMapper;
        this.myPageMapper = myPageMapper;
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
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

    // 회원 탈퇴
    public Result deleteMyAccount(UserEntity user) {
        if (user == null) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserByEmail(user.getEmail());
        if (dbUser == null) {
            return CommonResult.FAILURE;
        }
        dbUser.setDeleted(true);
        user.setDeleted(true);
        return this.userMapper.updateUser(dbUser) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
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

    // 굿즈 리뷰 작성
    @Transactional
    public Result writeReview(GoodsReviewEntity goodsReview, MultipartFile[] files, UserEntity user) throws IOException {
        if (user == null) { // 비로그인일 경우 작성 불가
            return CommonResult.FAILURE;
        }
        GoodsOrderEntity dbGoodsOrder = this.adminMapper.selectGoodsOrderByIndex(goodsReview.getGoodsOrderIndex());
        if (dbGoodsOrder == null || !dbGoodsOrder.isPaid()) {
            return CommonResult.FAILURE;    // 해당 주문이 없거나 비구매라면 실패
        }
        goodsReview.setGoodsIndex(dbGoodsOrder.getGoodsIndex());
        goodsReview.setUserEmail(user.getEmail());
        goodsReview.setCreatedAt(LocalDateTime.now());
        goodsReview.setModifiedAt(null);
        goodsReview.setReported(false);
        this.myPageMapper.insertGoodsReview(goodsReview);   // 리뷰 작성

        // 만약 등록한 이미지가 있다면 이미지를 DB에 추가
        if (files != null) {
            GoodsReviewImageEntity goodsReviewImage = new GoodsReviewImageEntity();
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                goodsReviewImage.setGoodsReviewIndex(goodsReview.getIndex());
                goodsReviewImage.setImage(file.getBytes());
                goodsReviewImage.setImageFileName(file.getOriginalFilename());
                goodsReviewImage.setImageContentType(file.getContentType());
                goodsReviewImage.setCreatedAt(LocalDateTime.now());
                this.myPageMapper.insertGoodsReviewImage(goodsReviewImage);
            }
        }

        dbGoodsOrder.setDelivered(true);    // 리뷰 작성하면 해당 주문 배송 완료
        return this.storeMapper.updateGoodsOrder(dbGoodsOrder) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 해당 주문의 굿즈 리뷰
    public GoodsReviewDto getGoodsReviewByGoodsOrderIndex(int index) {
        if (this.myPageMapper.selectGoodsReviewByGoodsOrderIndex(index) == null) {
            return null;
        }
        return this.myPageMapper.selectGoodsReviewByGoodsOrderIndex(index);
    }

    // 해당 리뷰의 이미지들
    public GoodsReviewImageEntity[] getGoodsReviewImages(int index) {
        if (this.storeMapper.selectGoodsReviewImageByReview(index) == null) {
            return null;
        }
        return this.storeMapper.selectGoodsReviewImageByReview(index);
    }

    // 토너먼트 삭제
    public Result deleteTournament(int index, UserEntity user) {
        if (user == null) return CommonResult.FAILURE;
        TournamentEntity dbTournament = this.tournamentMapper.selectTournamentByIndex(index);
        if (!user.getEmail().equals(dbTournament.getUserEmail())) {
            return CommonResult.FAILURE;
        }
        return this.tournamentMapper.deleteTournamentByIndex(index) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 토너먼트 타이틀 불러오기
    public TournamentEntity getTournamentByIndex(int index) {
        return this.tournamentMapper.selectTournamentByIndex(index);
    }

    // 토너먼트 타이틀 수정
    public Result modifyTournament(TournamentEntity tournament, MultipartFile file, UserEntity user) throws IOException {
        if (user == null) return CommonResult.FAILURE;
        TournamentEntity dbTournament = this.tournamentMapper.selectTournamentByIndex(tournament.getIndex());
        if (!dbTournament.getUserEmail().equals(user.getEmail())) return CommonResult.FAILURE;

        if (tournament.getTitle().length() > 50 || tournament.getTitle().length() < 2) {
            return CommonResult.FAILURE;
        }
        if (tournament.getContent().length() > 1000 || tournament.getContent().isEmpty()) {
            return CommonResult.FAILURE;
        }

        dbTournament.setTitle(tournament.getTitle());
        dbTournament.setContent(tournament.getContent());
        if (file != null) {
            dbTournament.setThumbnail(file.getBytes());
            dbTournament.setThumbnailFileName(file.getOriginalFilename());
            dbTournament.setThumbnailContentType(file.getContentType());
        }
        return this.tournamentMapper.updateTournament(dbTournament) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 토너먼트 요소 불러오기
    public TournamentProductEntity[] getProductByTournamentIndex(int index) {
        return this.tournamentMapper.selectTournamentProducts(index);
    }

    // 토너먼트 해당 요소 불러오기
    public TournamentProductEntity getProductByIndex(int index) {
        return this.tournamentMapper.selectTournamentProductByIndex(index);
    }

    // 토너먼트 요소 수정
    public Result modifyProduct(TournamentProductEntity product, MultipartFile file, UserEntity user) throws IOException {
        if (user == null) return CommonResult.FAILURE;
        TournamentEntity dbTournament = this.tournamentMapper.selectTournamentByIndex(product.getTournamentIndex());
        if (!dbTournament.getUserEmail().equals(user.getEmail())) return CommonResult.FAILURE;

        TournamentProductEntity dbProduct = this.tournamentMapper.selectTournamentProductByIndex(product.getIndex());

        if (product.getName().length() > 50 || product.getName().isEmpty()) {
            return CommonResult.FAILURE;
        }

        dbProduct.setName(product.getName());
        if (file != null) {
            dbProduct.setProductThumbnail(file.getBytes());
            dbProduct.setProductThumbnailFileName(file.getOriginalFilename());
            dbProduct.setProductThumbnailContentType(file.getContentType());
        }
        return this.tournamentMapper.updateTournamentProduct(dbProduct) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
