package ru.stdpr.fc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TerritoryDiction {
    BigDecimal id;

    String name;

    String define;
}
