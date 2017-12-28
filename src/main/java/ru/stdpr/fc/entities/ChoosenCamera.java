package ru.stdpr.fc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoosenCamera {

    @JsonIgnore
    String oldId;

    String id;

    String choosenTerritory;

    String choosenGroup;

    BigDecimal choosenAzimut;

    @JsonIgnore
    String comment;

    String choosenCoordinates;

    BigDecimal procentsOfRecognize;

    @JsonIgnore
    BigDecimal longitude;

    @JsonIgnore
    BigDecimal latitude;

    public ChoosenCamera(String id, String choosenTerritory, String choosenGroup, BigDecimal choosenAzimut, String comment, String choosenCoordinates, BigDecimal procentsOfRecognize) {
        this.id = id;
        this.choosenTerritory = choosenTerritory;
        this.choosenGroup = choosenGroup;
        this.choosenAzimut = choosenAzimut;
        this.comment = comment;
        this.choosenCoordinates = choosenCoordinates;
        this.procentsOfRecognize = procentsOfRecognize;
    }
    public ChoosenCamera(String id, String choosenTerritory, String choosenGroup, BigDecimal choosenAzimut, String choosenCoordinates, BigDecimal procentsOfRecognize) {
        this.id = id;
        this.choosenTerritory = choosenTerritory;
        this.choosenGroup = choosenGroup;
        this.choosenAzimut = choosenAzimut;
        this.choosenCoordinates = choosenCoordinates;
        this.procentsOfRecognize = procentsOfRecognize;
    }


}
