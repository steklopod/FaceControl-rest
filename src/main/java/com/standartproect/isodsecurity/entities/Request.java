package com.standartproect.isodsecurity.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * POJO-класс сущности запроса к распознающему сервису. Getter/Setter, Equals&Hashcode создаются
 * при компиляции.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

    @JsonIgnore
    private BigDecimal deltaId;

    @JsonIgnore
    private Long queryId;

    @JsonProperty("photo_ts")
    private byte[] mainPhoto;

    @JsonProperty("photo_grz")
    private byte[] regnoPhoto;

}
