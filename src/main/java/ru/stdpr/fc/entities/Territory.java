package ru.stdpr.fc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Territory {

//    @JsonIgnore
    BigDecimal id;

    String territory;

    @JsonIgnore
    String define;

    List<Group> groups;

    public Territory(String territory, List<Group> groups) {
        this.territory = territory;
        this.groups = groups;
    }

    public Territory(BigDecimal id, String territory, List<Group> groups) {
        this.id = id;
        this.territory = territory;
        this.groups = groups;
    }
}
