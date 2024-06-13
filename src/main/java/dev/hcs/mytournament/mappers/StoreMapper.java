package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.GoodsEntity;
import dev.hcs.mytournament.entities.GoodsImageEntity;
import dev.hcs.mytournament.entities.GoodsOrderEntity;
import dev.hcs.mytournament.entities.UserAddressEntity;
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

    UserAddressEntity selectUserAddress(@Param("userEmail") String userEmail);

    int updateGoods(GoodsEntity goods);
}
