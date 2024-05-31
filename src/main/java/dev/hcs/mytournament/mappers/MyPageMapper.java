package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.dtos.TournamentCommentDto;
import dev.hcs.mytournament.entities.TournamentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyPageMapper {
    TournamentEntity[] selectTournamentsByEmail(String userEmail);
    TournamentCommentDto[] selectCommentsByEmail(String userEmail);
}
