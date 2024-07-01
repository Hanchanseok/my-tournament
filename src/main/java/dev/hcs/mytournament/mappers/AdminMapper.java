package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.GoodsReviewDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Mapper
public interface AdminMapper {
    TournamentEntity[] selectTournaments(SearchDto search);

    int selectTournamentsCount();

    UserEntity[] selectUsers(SearchDto search);

    int selectUsersCount();

    TournamentCommentEntity[] selectReportedComments(SearchDto search);

    int selectReportedCommentsCount();

    int updateGoods(GoodsEntity goods);

    int deleteGoodsImage(int index);

    GoodsImageEntity selectOneGoodsImageByGoodsIndex(int goodsIndex);

    GoodsOrderDto[] selectGoodsOrder(SearchDto search);

    int countGoodsOrder();

    int deleteGoodsOrderByIndex(@Param("index")int index);

    GoodsOrderDto selectGoodsOrderByIndex(@Param("index")int index);

    GoodsReviewDto[] selectGoodsReviews(SearchDto search);

    int countGoodsReviews();

    GoodsReviewDto selectGoodsReviewByIndex(@Param("index")int index);

    int deleteGoodsReviewByIndex(@Param("index")int index);

    int deleteGoodsByIndex(@Param("index") int index);
}
