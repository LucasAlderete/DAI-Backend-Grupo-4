package com.ritmofit.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpDto {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    @NotBlank(message = "El código OTP es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El código OTP debe tener 6 dígitos")
    private String codigo;
    
    private String nombre; // Opcional, para registro
}
