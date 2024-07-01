package dev.hcs.mytournament.dtos;

import dev.hcs.mytournament.entities.GoodsReviewEntity;
import lombok.Data;

@Data
public class GoodsReviewDto extends GoodsReviewEntity {
    private String ratingStar;
    private Byte[] image;
    private String imageFileName;
    private String imageContentType;
    private int reviewImageIndex;
    private String nickname;
    private String title;
}
