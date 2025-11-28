package com.ritmofit.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckinRequestDto {

    @NotNull(message = "El ID de la clase es obligatorio")
    private Long claseId;
}
