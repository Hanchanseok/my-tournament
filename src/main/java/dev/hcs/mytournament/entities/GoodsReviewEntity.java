package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"index"})
public class GoodsReviewEntity {
    private int index;
    private int goodsIndex;
    private int goodsOrderIndex;
    private String userEmail;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isReported;
}
