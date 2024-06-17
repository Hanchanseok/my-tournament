package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"index"})
public class GoodsOrderEntity {
    private int index;
    private int goodsIndex;
    private String userEmail;
    private int amount;
    private int price;
    private int discount;
    private String addressPostal;
    private String addressPrimary;
    private String addressSecondary;
    private LocalDateTime orderAt;
    private boolean isPaid;
    private boolean isDelivered;
}
