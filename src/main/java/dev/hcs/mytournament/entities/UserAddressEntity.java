package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"index"})
public class UserAddressEntity {
    private int index;
    private String userEmail;
    private String addressPostal;
    private String addressPrimary;
    private String addressSecondary;
}
