package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"userEmail", "goodsIndex"})
public class GoodsWishlistEntity {
    private String userEmail;
    private int goodsIndex;
    private LocalDateTime createdAt;
}
