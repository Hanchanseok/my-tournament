package dev.hcs.mytournament.dtos;

import dev.hcs.mytournament.entities.TournamentProductEntity;
import lombok.Data;

@Data
public class RankingDto extends TournamentProductEntity {
    private double ratio;
}
