package ru.stdpr.fc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {

    String oldId;

    String id;

    String name;

    String placeText;

    String coordinates;

    String comment;

    BigDecimal recognizePercent;

    BigDecimal territoryId;

    BigDecimal groupId;

    BigDecimal azimut;

    @JsonIgnore
    BigDecimal longitude;

    @JsonIgnore
    BigDecimal latitude;

//    @JsonIgnore

    String territoryName;

//    @JsonIgnore

    String groupName;

    public Camera(String oldId, String id, String name) {
        this.oldId = oldId;
        this.id = id;
        this.name = name;
    }

    public Camera(String id, BigDecimal azimut, BigDecimal recognizePercent, String comment, BigDecimal longitude, BigDecimal latitude, String coordinates) {
        this.id = id;
        this.azimut = azimut;
        this.recognizePercent = recognizePercent;
        this.comment = comment;
        this.longitude = longitude;
        this.latitude = latitude;
        this.coordinates = coordinates;
    }



    public Camera(String id, String name, String placeText, BigDecimal azimut, BigDecimal recognizePercent, String comment, BigDecimal territoryId, String territoryName, BigDecimal groupId, String groupName, String coordinates) {
        this.id = id;
        this.name = name;
        this.placeText = placeText;
        this.azimut = azimut;
        this.recognizePercent = recognizePercent;
        this.comment = comment;
        this.territoryId = territoryId;
        this.territoryName = territoryName;
        this.groupId = groupId;
        this.groupName = groupName;
        this.coordinates = coordinates;
    }
}
