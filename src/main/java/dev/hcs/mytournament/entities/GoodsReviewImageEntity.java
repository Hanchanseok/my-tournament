package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"index"})
public class GoodsReviewImageEntity {
    private int index;
    private int goodsReviewIndex;
    private byte[] image;
    private String imageFileName;
    private String imageContentType;
    private LocalDateTime createdAt;
}
