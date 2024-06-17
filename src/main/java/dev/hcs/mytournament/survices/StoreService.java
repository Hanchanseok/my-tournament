package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.mappers.StoreMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.store.DeleteOrderResult;
import dev.hcs.mytournament.results.store.OrderResult;
import dev.hcs.mytournament.results.store.UploadGoodsResult;
import dev.hcs.mytournament.results.store.WishlistResult;
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

        search.setTotalCount(this.storeMapper.getGoodsTotalCount(search));    // 전체 업로드 대회들 갯수
        search.setMaxPage(search.getTotalCount() / search.getCountPerPage() +
                ( search.getTotalCount() % search.getCountPerPage() == 0 ? 0 : 1 ));    // 최대 페이지
        search.setMinPage(1);                                                           // 최소 페이지
        search.setOffset(search.getCountPerPage() * (search.getRequestPage() - 1));     // 거를 게시글 수

        search.setTotalPage( (int)(Math.ceil( search.getTotalCount()/(double)search.getCountPerPage() )) );  // 전체 페이지 수 구하기
        search.setBeginPage( ((search.getRequestPage() - 1)/search.getNaviSize()) * search.getNaviSize() + 1 );   // 시작 페이지 번호 구하기
        search.setEndPage(Math.min(search.getBeginPage() + search.getNaviSize() -1, search.getTotalPage()));        // 끝 페이지 번호 구하기

        search.setShowPrev( search.getBeginPage() != 1);        // 이전 표시 여부
        search.setShowNext( search.getEndPage() != search.getTotalPage() ); // 다음 표시 여부
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
        return this.storeMapper.selectGoodsOrderByIndex(index);
    }

    // 굿즈 주문 취소
    @Transactional
    public Result deleteGoodsOrder(int index, UserEntity user, int amount) {
        // 비로그인 상태이거나, 해당 굿즈를 주문한 유저가 아니라면... 실패
        GoodsOrderDto dbGoodsOrder = this.storeMapper.selectGoodsOrderByIndex(index);
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
}
