package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.mappers.StoreMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.store.OrderResult;
import dev.hcs.mytournament.results.store.UploadGoodsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

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

        UserAddressEntity dbUserAddress = this.storeMapper.selectUserAddress(user.getEmail());
        // db에 유저 주소가 없다면 해당 주소를 추가
        if (dbUserAddress == null) {
            userAddress.setUserEmail(user.getEmail());
            this.storeMapper.insertUserAddress(userAddress);
        } else if (!dbUserAddress.getAddressPostal().equals(userAddress.getAddressPostal()) && !dbUserAddress.getAddressPrimary().equals(userAddress.getAddressPrimary()) && !dbUserAddress.getAddressSecondary().equals(userAddress.getAddressSecondary())) {
            // 만약 유저 주소는 있는데, 같은 주소가 아니라면 주소 추가
            userAddress.setUserEmail(user.getEmail());
            this.storeMapper.insertUserAddress(userAddress);
        }

        goodsOrder.setUserEmail(user.getEmail());
        goodsOrder.setOrderAt(LocalDateTime.now());
        goodsOrder.setPaid(false);
        return this.storeMapper.insertGoodsOrder(goodsOrder) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
