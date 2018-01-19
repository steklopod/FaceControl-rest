package ru.stdpr.fc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TerritoryDiction {

    BigDecimal oldId;

    BigDecimal id;

    String name;

    String define;

    public TerritoryDiction(String name) {
        this.name = name;
    }

    public TerritoryDiction(BigDecimal id, String name, String define) {
        this.id = id;
        this.name = name;
        this.define = define;
    }
}
