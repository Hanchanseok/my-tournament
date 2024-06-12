package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.entities.GoodsEntity;
import dev.hcs.mytournament.entities.GoodsImageEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.StoreMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
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

    public GoodsEntity[] getGoods() {
        return this.storeMapper.selectGoods();
    }

    public GoodsImageEntity[] getGoodsImageByGoodsIndex(int index) {
        return this.storeMapper.selectGoodsImageByGoodsIndex(index);
    }
}
