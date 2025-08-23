package com.ritmofit.api.service;

import com.ritmofit.api.dto.ReservaDto;
import com.ritmofit.api.model.entity.Reserva;
import com.ritmofit.api.repository.ReservaRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService implements IReservaService {

    @Autowired
    private final ReservaRespository reservaRepository;

    //Deberia retornar List<reservaDto>
    public List<Reserva> getAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Reserva create(ReservaDto nuevaReserva) {
        Reserva reserva = Reserva.builder()
                .clase(nuevaReserva.getClase())
                .fecha(nuevaReserva.getFecha())
                .horario(nuevaReserva.getHorario())
                .sede(nuevaReserva.getSede())
                .disciplina(nuevaReserva.getDisciplina())
                .profesor(nuevaReserva.getProfesor())
                .build();

        return reservaRepository.save(reserva);
    }


}
