package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode( of = {"index"} )
public class TournamentCommentEntity {
    private int index;
    private int tournamentIndex;
    private String userEmail;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isReported;
}
