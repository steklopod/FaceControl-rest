package ru.steklopod.tv.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class KeyWords {
    private String label;
    private String value;

    private String icon;

    private String stamp;


    public KeyWords(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
