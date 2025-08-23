package com.ritmofit.api.service;

import com.ritmofit.api.dto.ReservaDto;
import com.ritmofit.api.model.entity.Reserva;

import java.util.List;

public interface IReservaService {
    List<Reserva> getAll();
    Reserva create(ReservaDto reservaDto);
}
