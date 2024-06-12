package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.entities.GoodsEntity;
import dev.hcs.mytournament.entities.GoodsImageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

@Mapper
public interface StoreMapper {
    int insertGoods(GoodsEntity goods);

    int insertGoodsImage(GoodsImageEntity goodsImage);

    GoodsEntity selectGoodsByIndex(@Param("index") int index);

    GoodsImageEntity selectGoodsImageByIndex(@Param("index") int index);

    GoodsEntity[] selectGoods();

    GoodsImageEntity[] selectGoodsImageByGoodsIndex(int goodsIndex);
}
