package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"index"})
public class GoodsImageEntity {
    private int index;
    private int goodsIndex;
    private byte[] image;
    private String imageFileName;
    private String imageContentType;
}
