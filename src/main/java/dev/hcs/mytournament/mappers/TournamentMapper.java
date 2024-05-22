package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.RankingDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.TournamentProductEntity;
import org.apache.ibatis.annotations.Mapper;

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
}
