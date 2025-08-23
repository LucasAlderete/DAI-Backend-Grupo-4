package com.ritmofit.api.controller;

import com.ritmofit.api.dto.ReservaDto;
import com.ritmofit.api.model.entity.Reserva;
import com.ritmofit.api.service.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    
    @Autowired
    private IReservaService reservaService;
    
    @GetMapping
    public ResponseEntity<List<Reserva>> getTodasMisReservas() {
        List<Reserva> reservas = reservaService.getAll();
        return ResponseEntity.ok(reservas);
    }
    
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaDto reservaDto) {
        Reserva nuevaReserva = reservaService.create(reservaDto);
        return ResponseEntity.ok(nuevaReserva);
    }
}
