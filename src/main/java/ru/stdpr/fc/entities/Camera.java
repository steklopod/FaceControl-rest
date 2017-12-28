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

    @JsonIgnore
    String oldId;

    String id;

    BigDecimal azimut;

    BigDecimal recognizePercent;

    String comment;

    @JsonIgnore
    BigDecimal longitude;

    @JsonIgnore
    BigDecimal latitude;

    String coordinates;

    public Camera(String id, BigDecimal azimut, BigDecimal recognizePercent, String comment, BigDecimal longitude, BigDecimal latitude, String coordinates) {
        this.id = id;
        this.azimut = azimut;
        this.recognizePercent = recognizePercent;
        this.comment = comment;
        this.longitude = longitude;
        this.latitude = latitude;
        this.coordinates = coordinates;
    }
}
