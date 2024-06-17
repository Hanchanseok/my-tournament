package dev.hcs.mytournament.dtos;

import dev.hcs.mytournament.entities.GoodsOrderEntity;
import lombok.Data;

@Data
public class GoodsOrderDto extends GoodsOrderEntity {
    private String title;
}
