package ru.stdpr.fc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO-класс сущности ответа распознающего сервиса. Getter/Setter, Equals&Hashcode создаются
 * при компиляции, благодаря аннотации @Data
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    @JsonProperty
    private String model;

    @JsonProperty("model_score")
    private Float modelScore;

    @JsonProperty
    private Float trash;

    @JsonProperty
    private String grz;

    @JsonProperty("grz_score")
    private Float grzScore;

    @JsonProperty("MT")
    private Float mt;

    @JsonProperty("not_MT")
    private Float notMt;

    @JsonProperty
    private Float taxi;

    @JsonProperty
    private Float car;

    @JsonProperty
    private Float motorcycle;

    @JsonProperty
    private Float bus;

    @JsonProperty
    private Float truck;

}
