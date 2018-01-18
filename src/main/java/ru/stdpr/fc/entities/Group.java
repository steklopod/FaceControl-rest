package ru.stdpr.fc.entities;


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
    BigDecimal territoryId;

    String define;

    List<Camera> cameras;

    public Group(String group, List<Camera> cameras) {
        this.group = group;
        this.cameras = cameras;
    }

    public Group(BigDecimal id, String group, List<Camera> cameras) {
        this.id = id;
        this.group = group;
        this.cameras = cameras;
    }

    public Group(String group, BigDecimal territoryId, List<Camera> cameras) {
        this.group = group;
        this.territoryId = territoryId;
        this.cameras = cameras;
    }

    public Group(BigDecimal id, String group, BigDecimal territoryId, List<Camera> cameras) {
        this.id = id;
        this.group = group;
        this.territoryId = territoryId;
        this.cameras = cameras;
    }
}
