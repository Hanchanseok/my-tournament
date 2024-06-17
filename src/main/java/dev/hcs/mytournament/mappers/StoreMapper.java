package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
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

    GoodsOrderDto selectGoodsOrderByIndex(@Param("index") int index);

    int deleteGoodsOrder(@Param("index") int index);

    UserAddressEntity selectUserAddress(@Param("userEmail") String userEmail);

    UserAddressEntity[] selectUserAddressByEmail(@Param("userEmail") String userEmail);

    int updateGoods(GoodsEntity goods);

    int deleteMyAddress(@Param("index") int index);

    int insertWishlist(GoodsWishlistEntity goodsWishlist);

    int selectWishlistCount(@Param("userEmail") String userEmail,
                       @Param("goodsIndex") int goodsIndex);
}
