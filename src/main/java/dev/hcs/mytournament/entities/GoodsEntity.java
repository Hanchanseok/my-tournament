package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"index"})
public class GoodsEntity {
    private int index;
    private byte[] thumbnail;
    private String thumbnailFileName;
    private String thumbnailContentType;
    private String title;
    private String content;
    private int price;
    private int stoke;
    private LocalDateTime createdAt;
    private boolean isSale;
}
