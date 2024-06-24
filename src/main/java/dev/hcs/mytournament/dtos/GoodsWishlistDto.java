package dev.hcs.mytournament.dtos;

import dev.hcs.mytournament.entities.GoodsWishlistEntity;
import lombok.Data;

@Data
public class GoodsWishlistDto extends GoodsWishlistEntity {
    private int index;
    private String title;
}
