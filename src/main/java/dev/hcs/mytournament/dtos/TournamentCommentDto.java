package dev.hcs.mytournament.dtos;

import dev.hcs.mytournament.entities.TournamentCommentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TournamentCommentDto extends TournamentCommentEntity {
    private String userNickname;
    private String tournamentTitle;
}
