package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.GoodsWishlistDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.dtos.TournamentCommentDto;
import dev.hcs.mytournament.entities.GoodsReviewEntity;
import dev.hcs.mytournament.entities.GoodsReviewImageEntity;
import dev.hcs.mytournament.entities.GoodsWishlistEntity;
import dev.hcs.mytournament.entities.TournamentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyPageMapper {
    TournamentEntity[] selectTournamentsByEmail(SearchDto search);

    int countTournamentsByEmail(String userEmail);

    TournamentCommentDto[] selectCommentsByEmail(SearchDto search);

    GoodsWishlistDto[] selectGoodsWishlistByEmail(SearchDto search);

    int countGoodsWishlistByEmail(String userEmail);

    GoodsWishlistEntity selectGoodsWishlist(@Param("goodsIndex")int goodsIndex,
                                            @Param("userEmail")String userEmail);
    int deleteGoodsWishlist(@Param("goodsIndex")int goodsIndex,
                            @Param("userEmail")String userEmail);

    GoodsOrderDto[] selectGoodsOrderByEmail(SearchDto search);

    int countGoodsOrderByEmail(String userEmail);

    int insertGoodsReview(GoodsReviewEntity goodsReview);

    int insertGoodsReviewImage(GoodsReviewImageEntity goodsReviewImage);
}
