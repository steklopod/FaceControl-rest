package ru.steklopod.tv.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ocrug {

    //    @JsonIgnore
    BigDecimal id;

    String ocrug;

    //    @JsonIgnore
//    TODO - изменить название поля
    String define;

    List<Group> groups;

    public Ocrug(String ocrug, List<Group> groups) {
        this.ocrug = ocrug;
        this.groups = groups;
    }

    public Ocrug(BigDecimal id, String ocrug, List<Group> groups) {
        this.id = id;
        this.ocrug = ocrug;
        this.groups = groups;
    }
}
