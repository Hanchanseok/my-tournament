package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.GoodsReviewDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

@Mapper
public interface StoreMapper {
    int insertGoods(GoodsEntity goods);

    int insertGoodsImage(GoodsImageEntity goodsImage);

    GoodsEntity selectGoodsByIndex(@Param("index") int index);

    GoodsImageEntity selectGoodsImageByIndex(@Param("index") int index);

    GoodsEntity[] selectGoods(SearchDto search);

    int getGoodsTotalCount(SearchDto search);

    GoodsImageEntity[] selectGoodsImageByGoodsIndex(int goodsIndex);

    int insertGoodsOrder(GoodsOrderEntity goodsOrder);

    int insertUserAddress(UserAddressEntity userAddress);

    GoodsOrderDto getGoodsOrderByIndex(@Param("index") int index);

    GoodsOrderEntity selectGoodsOrderByIndex(@Param("index") int index);

    int updateGoodsOrder(GoodsOrderEntity goodsOrder);

    int deleteGoodsOrder(@Param("index") int index);

    UserAddressEntity selectUserAddress(@Param("userEmail") String userEmail);

    UserAddressEntity[] selectUserAddressByEmail(@Param("userEmail") String userEmail);

    int updateGoods(GoodsEntity goods);

    int deleteMyAddress(@Param("index") int index);

    int insertWishlist(GoodsWishlistEntity goodsWishlist);

    int selectWishlistCount(@Param("userEmail") String userEmail,
                       @Param("goodsIndex") int goodsIndex);

    GoodsOrderDto[] getGoodsOrderByEmail(@Param("userEmail") String userEmail);

    double selectGoodsRating(@Param("totalRating") int totalRating, @Param("index") int index);

    int selectGoodsTotalRating(@Param("index") int index);

    GoodsReviewDto[] selectGoodsReviews(SearchDto search);

    int countGoodsReviews(@Param("index")int index);

    GoodsReviewImageEntity selectGoodsReviewImageByIndex(@Param("index")int index);

    GoodsReviewDto selectGoodsReviewByIndex(@Param("index") int index);

    GoodsReviewImageEntity[] selectGoodsReviewImageByReview(@Param("index") int index);

    int updateGoodsReview(GoodsReviewEntity goodsReview);
}
