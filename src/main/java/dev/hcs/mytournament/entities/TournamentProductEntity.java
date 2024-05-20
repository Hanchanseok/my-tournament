package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( of = {"index"} )
public class TournamentProductEntity {
    private int index;
    private int tournamentIndex;
    private byte[] productThumbnail;
    private String productThumbnailFileName;
    private String productThumbnailContentType;
    private String name;
    private int point;
}
