
package ru.steklopod.tv.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoosenTvera {

    String oldId;

    String id;

    String choosenocrug;

    String choosenGroup;

    String choosenCoordinates;

    String comment;

    BigDecimal procentsOfRecognize;

    @JsonIgnore
    BigDecimal longitude;

    @JsonIgnore
    BigDecimal latitude;


    BigDecimal choosenAzimut;


    public ChoosenTvera(String id, String choosenocrug, String choosenGroup, BigDecimal choosenAzimut, String comment, String choosenCoordinates, BigDecimal procentsOfRecognize) {
        this.id = id;
        this.choosenocrug = choosenocrug;
        this.choosenGroup = choosenGroup;
        this.choosenAzimut = choosenAzimut;
        this.comment = comment;
        this.choosenCoordinates = choosenCoordinates;
        this.procentsOfRecognize = procentsOfRecognize;
    }

    public ChoosenTvera(String id, String choosenocrug, String choosenGroup, BigDecimal choosenAzimut, String choosenCoordinates, BigDecimal procentsOfRecognize) {
        this.id = id;
        this.choosenocrug = choosenocrug;
        this.choosenGroup = choosenGroup;
        this.choosenAzimut = choosenAzimut;
        this.choosenCoordinates = choosenCoordinates;
        this.procentsOfRecognize = procentsOfRecognize;
    }

    public ChoosenTvera(String id, String choosenocrug, String choosenGroup, String choosenCoordinates, String comment, BigDecimal procentsOfRecognize, BigDecimal choosenAzimut) {
        this.id = id;
        this.choosenocrug = choosenocrug;
        this.choosenGroup = choosenGroup;
        this.choosenCoordinates = choosenCoordinates;
        this.comment = comment;
        this.procentsOfRecognize = procentsOfRecognize;
        this.choosenAzimut = choosenAzimut;
    }
}
