package com.ritmofit.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsDto {
    private Long id;
    private String titulo;
    private String descripcion;
    @JsonProperty("order")
    private Integer orden;
    @JsonProperty("img_url")
    private String imgUrl;
}
