package ru.steklopod.tv.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDiction {

    BigDecimal groupId;

    String name;

    BigDecimal ocrugId;

    String define;

    public GroupDiction(String name) {
        this.name = name;
    }
}
