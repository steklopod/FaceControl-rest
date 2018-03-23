package ru.steklopod.tv.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    //    @JsonIgnore
    BigDecimal id;

    String group;

    //    @JsonIgnore
    BigDecimal ocrugId;

    String define;

    List<Tvera> tv;

    public Group(String group, List<Tvera> tv) {
        this.group = group;
        this.tv = tv;
    }

    public Group(BigDecimal id, String group, List<Tvera> tv) {
        this.id = id;
        this.group = group;
        this.tv = tv;
    }

    public Group(String group, BigDecimal ocrugId, List<Tvera> tv) {
        this.group = group;
        this.ocrugId = ocrugId;
        this.tv = tv;
    }

    public Group(BigDecimal id, String group, BigDecimal ocrugId, List<Tvera> tv) {
        this.id = id;
        this.group = group;
        this.ocrugId = ocrugId;
        this.tv = tv;
    }
}
