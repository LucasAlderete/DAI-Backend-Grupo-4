package com.ritmofit.api.model.entity;

import com.ritmofit.api.dto.NotificacionDto;

public class NotificacionMapper {

    public static NotificacionDto toDto(Notificacion n) {
    if (n == null) return null;

    return NotificacionDto.builder()
            .id(n.getId())
            .claseId(n.getClase() != null ? n.getClase().getId() : null)
            .tipo(n.getTipo())
            .mensaje(n.getMensaje())
            .fechaInicio(n.getFechaEnvio() != null ? n.getFechaEnvio().toString() : null)
            .usuarioId(n.getUsuarioId()) 
            .build();
}   
}
