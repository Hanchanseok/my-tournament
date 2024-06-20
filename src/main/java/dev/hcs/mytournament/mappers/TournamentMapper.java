package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.RankingDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.dtos.TournamentCommentDto;
import dev.hcs.mytournament.entities.TournamentCommentEntity;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.TournamentProductEntity;
import dev.hcs.mytournament.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TournamentMapper {
    void insertTournament(TournamentEntity tournament);

    void insertTournamentProduct(TournamentProductEntity product);

    TournamentEntity selectTournamentByIndex(int index);

    TournamentProductEntity selectTournamentProductByIndex(int index);

    TournamentEntity[] selectTournaments(SearchDto search);

    TournamentProductEntity[] selectTournamentProducts(int tournamentIndex);

    int getTournamentTotalCount(SearchDto search);

    int updateTournament(TournamentEntity tournament);

    RankingDto[] selectRanking(int index, int totalPoint);

    int selectTotalPoint(int index);

    int updateTournamentProduct(TournamentProductEntity product);

    int insertTournamentComment(TournamentCommentEntity comment);

    TournamentCommentDto[] selectTournamentComments(SearchDto search);

    int selectTournamentCommentsCount(int tournamentIndex);

    TournamentCommentEntity selectTournamentCommentByIndex(int index);

    int updateTournamentComment(TournamentCommentEntity comment);

    int deleteTournamentComment(int index);

    int selectTournamentCountByEmail(String userEmail);

    int selectCommentCountByEmail(String userEmail);

}
