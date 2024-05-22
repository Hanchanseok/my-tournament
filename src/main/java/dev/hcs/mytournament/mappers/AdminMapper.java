package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    TournamentEntity[] selectTournaments();

    UserEntity[] selectUsers();
}
