package ru.steklopod.tv.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OcrugDiction {

    BigDecimal oldId;

    BigDecimal id;

    String name;

    String define;

    public OcrugDiction(String name) {
        this.name = name;
    }

    public OcrugDiction(BigDecimal id, String name, String define) {
        this.id = id;
        this.name = name;
        this.define = define;
    }
}
