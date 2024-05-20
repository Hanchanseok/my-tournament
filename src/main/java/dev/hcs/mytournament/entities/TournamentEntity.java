package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode( of = {"index"} )
public class TournamentEntity {
    private int index;
    private String userEmail;
    private byte[] thumbnail;
    private String thumbnailFileName;
    private String thumbnailContentType;
    private String title;
    private String content;
    private int playCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isRecognized;
}
