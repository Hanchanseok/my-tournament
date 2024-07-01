package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.GoodsReviewDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.mappers.StoreMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.store.DeleteOrderResult;
import dev.hcs.mytournament.results.store.OrderResult;
import dev.hcs.mytournament.results.store.UploadGoodsResult;
import dev.hcs.mytournament.results.store.WishlistResult;
import dev.hcs.mytournament.results.tournament.ReportCommentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class StoreService {
    private final StoreMapper storeMapper;

    @Autowired
    public StoreService(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    // 굿즈 업로드
    @Transactional
    public Result uploadGoods(UserEntity user, GoodsEntity goods, GoodsImageEntity goodsImage, MultipartFile[] files) throws IOException {
        // 관리자 아닐 경우 굿즈 업로드 불가
        if (user == null || !user.isAdmin()) {
            return UploadGoodsResult.FAILURE_NOT_MANAGER;
        }

        // 굿즈 이미지파일은 5개 이하만 가능
        if (files == null || files.length > 5) {
            return UploadGoodsResult.FAILURE_IMAGE_LENGTH_OVER;
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
        if (goods.getStoke() < 0 || goods.getPrice() < 0) {
            return CommonResult.FAILURE;
        }

        // 검증이 끝나면 굿즈 객체에 썸네일(첫번째 파일) set
        goods.setCreatedAt(LocalDateTime.now());
        goods.setSale(true);
        goods.setThumbnail(files[0].getBytes());
        goods.setThumbnailFileName(files[0].getOriginalFilename());
        goods.setThumbnailContentType(files[0].getContentType());
        this.storeMapper.insertGoods(goods);

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            goodsImage.setGoodsIndex(goods.getIndex());
            goodsImage.setImage(file.getBytes());
            goodsImage.setImageFileName(file.getOriginalFilename());
            goodsImage.setImageContentType(file.getContentType());
            this.storeMapper.insertGoodsImage(goodsImage);
        }

        return CommonResult.SUCCESS;
    }

    public GoodsEntity getGoodsByIndex(int index) {
        return this.storeMapper.selectGoodsByIndex(index);
    }

    public GoodsImageEntity getGoodsImageByIndex(int index) {
        return this.storeMapper.selectGoodsImageByIndex(index);
    }

    public GoodsEntity[] getGoods(SearchDto search) {
        if (search.getKeyword() == null || search.getKeyword().length() > 50) {
            search.setKeyword(null);
        }
        if (search.getBy() == null) {
            search.setBy(null);
        }

        search.setTotalCount(this.storeMapper.getGoodsTotalCount(search));
        return this.storeMapper.selectGoods(search);
    }

    public GoodsImageEntity[] getGoodsImageByGoodsIndex(int index) {
        return this.storeMapper.selectGoodsImageByGoodsIndex(index);
    }

    // 굿즈 주문 및 재고 수정
    @Transactional
    public Result orderGoods(GoodsOrderEntity goodsOrder, UserAddressEntity userAddress, UserEntity user) {
        if (user == null) {
            return OrderResult.FAILURE_NONE_LOGIN;    // 비로그인 권한 없음
        }

        GoodsEntity dbGoods = this.storeMapper.selectGoodsByIndex(goodsOrder.getGoodsIndex());

        if (dbGoods == null || dbGoods.getStoke() == 0 || goodsOrder.getAmount() > dbGoods.getStoke()) {
            return OrderResult.FAILURE_STOKE_OVER;    // 해당 굿즈가 없거나 품절이면 실패
        }
        if (!dbGoods.isSale()) {
            return OrderResult.FAILURE_NONE_SALE;   // 판매중단 제품이면 실패
        }
        if (goodsOrder.getPrice() != dbGoods.getPrice() * goodsOrder.getAmount()) {
            return OrderResult.FAILURE_WRONG_PRICE; // 잘못된 가격이면 실패
        }

        dbGoods.setStoke(dbGoods.getStoke() - goodsOrder.getAmount());  // 주문량으로 재고 빼기
        this.storeMapper.updateGoods(dbGoods);  // 굿즈 정보 수정

        UserAddressEntity[] dbUserAddress = this.storeMapper.selectUserAddressByEmail(user.getEmail()); // 유저 주소 목록들 조회
        int count = 0;
        for (int i = 0; i < dbUserAddress.length; i++) {
            if (dbUserAddress[i].getAddressPostal().equals(userAddress.getAddressPostal()) && dbUserAddress[i].getAddressPrimary().equals(userAddress.getAddressPrimary()) && dbUserAddress[i].getAddressSecondary().equals(userAddress.getAddressSecondary())) {
                count++;    // 유저 주소 조회하고 같은 주소가 있으면 count + 1
            }
        }
        if (count == 0) {    // 같은 주소가 없다.
            userAddress.setUserEmail(user.getEmail());
            this.storeMapper.insertUserAddress(userAddress);    // 유저 주소에 추가
        }

        goodsOrder.setUserEmail(user.getEmail());
        goodsOrder.setOrderAt(LocalDateTime.now());
        goodsOrder.setDiscount(dbGoods.getDiscount());
        goodsOrder.setPaid(false);
        goodsOrder.setDelivered(false);
        return this.storeMapper.insertGoodsOrder(goodsOrder) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public UserAddressEntity[] selectUserAddresses(UserEntity user) {
        if (user == null) {
            return null;
        }
        return this.storeMapper.selectUserAddressByEmail(user.getEmail());
    }

    public Result deleteMyAddress(int index) {
        return this.storeMapper.deleteMyAddress(index) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 굿즈 찜하기
    public Result postWishlist(UserEntity user, int goodsIndex) {
        if (user == null) {
            return WishlistResult.FAILURE_NONE_LOGIN;   // 로그인 유저가 아닐경우
        }
        if (this.storeMapper.selectWishlistCount(user.getEmail(), goodsIndex) > 0) {
            return WishlistResult.FAILURE_ALREADY_WISHLIST; // 이미 찜한 것이라면
        }
        GoodsWishlistEntity goodsWishlist = new GoodsWishlistEntity();
        goodsWishlist.setUserEmail(user.getEmail());
        goodsWishlist.setGoodsIndex(goodsIndex);
        goodsWishlist.setCreatedAt(LocalDateTime.now());
        return this.storeMapper.insertWishlist(goodsWishlist) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 인덱스별 굿즈 주문내역 조회
    public GoodsOrderDto goodsOrderByIndex(int index) {
        return this.storeMapper.getGoodsOrderByIndex(index);
    }

    // 굿즈 주문 취소
    @Transactional
    public Result deleteGoodsOrder(int index, UserEntity user, int amount) {
        // 비로그인 상태이거나, 해당 굿즈를 주문한 유저가 아니라면... 실패
        GoodsOrderDto dbGoodsOrder = this.storeMapper.getGoodsOrderByIndex(index);
        if (user == null || !Objects.equals(user.getEmail(), dbGoodsOrder.getUserEmail())) {
            return DeleteOrderResult.FAILURE_WRONG_USER;
        }

        // 주문을 취소했으면 취소된 수량을 굿즈 재고에 다시 더함
        if (dbGoodsOrder.getAmount() != amount) {
            return CommonResult.FAILURE;    // 수량이 맞는지 확인
        }
        GoodsEntity dbGoods = this.storeMapper.selectGoodsByIndex(dbGoodsOrder.getGoodsIndex());
        dbGoods.setStoke(dbGoods.getStoke() + amount);
        this.storeMapper.updateGoods(dbGoods);

        return this.storeMapper.deleteGoodsOrder(index) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 굿즈 결제
    public Result payGoods(GoodsOrderEntity goodsOrder, UserEntity user) {
        GoodsOrderEntity dbGoodsOrder = this.storeMapper.selectGoodsOrderByIndex(goodsOrder.getIndex());
        // 비로그인 상태이거나, 해당 굿즈를 주문한 유저가 아니라면... 실패
        if (user == null || !Objects.equals(user.getEmail(), dbGoodsOrder.getUserEmail())) {
            return CommonResult.FAILURE;
        }
        dbGoodsOrder.setPaid(true);     // 결제여부 True
        return this.storeMapper.updateGoodsOrder(dbGoodsOrder) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 아직 결재 안된 제품 목록
    public GoodsOrderDto[] getOrderList(UserEntity user) {
        if (user == null) {
            return null;
        }
        return this.storeMapper.getGoodsOrderByEmail(user.getEmail());
    }

    // 굿즈 평균 평점 구하기
    public double getAverage(int index) {
        int totalRating = this.storeMapper.selectGoodsTotalRating(index);
        return this.storeMapper.selectGoodsRating(totalRating, index);
    }

    // 리뷰 이미지 썸네일
    public GoodsReviewImageEntity getReviewImageByIndex(int index) {
        return this.storeMapper.selectGoodsReviewImageByIndex(index);
    }

    // 각 굿즈별 리뷰 목록들 가져오기
    public GoodsReviewDto[] getGoodsReviewDto(SearchDto search, int index) {
        search.setGoodsIndex(index);
        search.setTotalCount(this.storeMapper.countGoodsReviews(index));
        return this.storeMapper.selectGoodsReviews(search);
    }

    // 각 리뷰별 정보
    public GoodsReviewDto getGoodsReviewByIndex(int index) {
        return this.storeMapper.selectGoodsReviewByIndex(index);
    }

    // 각 리뷰별 이미지
    public GoodsReviewImageEntity[] getGoodsReviewImage(int index) {
        return this.storeMapper.selectGoodsReviewImageByReview(index);
    }

    // 리뷰 신고
    public Result reportReview(int index, UserEntity user) {
        GoodsReviewEntity dbGoodsReview = this.storeMapper.selectGoodsReviewByIndex(index);
        if (dbGoodsReview == null) {
            return CommonResult.FAILURE;
        }
        if (user != null) {
            // 자신의 코멘트는 신고할 수 없다.
            if (dbGoodsReview.getUserEmail().equals(user.getEmail())) {
                return ReportCommentResult.FAILURE_REPORT_OWN_COMMENT;
            }
        }
        dbGoodsReview.setReported(true);
        return this.storeMapper.updateGoodsReview(dbGoodsReview) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
