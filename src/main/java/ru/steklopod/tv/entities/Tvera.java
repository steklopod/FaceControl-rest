package ru.steklopod.tv.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tvera {

    String oldId;

    String id;

    String name;

    String placeText;

    String coordinates;

    String comment;

    BigDecimal recognizePercent;

    BigDecimal ocrugId;

    BigDecimal groupId;

    BigDecimal azimut;

    @JsonIgnore
    BigDecimal longitude;

    @JsonIgnore
    BigDecimal latitude;

//    @JsonIgnore

    String ocrugName;

//    @JsonIgnore

    String groupName;

    public Tvera(String oldId, String id, String name) {
        this.oldId = oldId;
        this.id = id;
        this.name = name;
    }

    public Tvera(String id, BigDecimal azimut, BigDecimal recognizePercent, String comment, BigDecimal longitude, BigDecimal latitude, String coordinates) {
        this.id = id;
        this.azimut = azimut;
        this.recognizePercent = recognizePercent;
        this.comment = comment;
        this.longitude = longitude;
        this.latitude = latitude;
        this.coordinates = coordinates;
    }



    public Tvera(String id, String name, String placeText, BigDecimal azimut, BigDecimal recognizePercent, String comment, BigDecimal ocrugId, String ocrugName, BigDecimal groupId, String groupName, String coordinates) {
        this.id = id;
        this.name = name;
        this.placeText = placeText;
        this.azimut = azimut;
        this.recognizePercent = recognizePercent;
        this.comment = comment;
        this.ocrugId = ocrugId;
        this.ocrugName = ocrugName;
        this.groupId = groupId;
        this.groupName = groupName;
        this.coordinates = coordinates;
    }
}
