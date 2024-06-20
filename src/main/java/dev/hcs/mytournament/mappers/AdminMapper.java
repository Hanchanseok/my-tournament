package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.GoodsEntity;
import dev.hcs.mytournament.entities.TournamentCommentEntity;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    TournamentEntity[] selectTournaments(SearchDto search);

    int selectTournamentsCount();

    UserEntity[] selectUsers(SearchDto search);

    int selectUsersCount();

    TournamentCommentEntity[] selectReportedComments(SearchDto search);

    int selectReportedCommentsCount();
}
