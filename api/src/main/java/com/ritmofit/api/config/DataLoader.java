package com.ritmofit.api.config;

import com.ritmofit.api.model.entity.Clase;
import com.ritmofit.api.model.entity.Disciplina;
import com.ritmofit.api.model.entity.Instructor;
import com.ritmofit.api.model.entity.Sede;
import com.ritmofit.api.repository.ClaseRepository;
import com.ritmofit.api.repository.DisciplinaRepository;
import com.ritmofit.api.repository.InstructorRepository;
import com.ritmofit.api.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private DisciplinaRepository disciplinaRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private ClaseRepository claseRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear Sedes
        Sede sedePalermo = Sede.builder().nombre("Palermo").direccion("Av. Santa Fe 4850").build();
        Sede sedeBelgrano = Sede.builder().nombre("Belgrano").direccion("Av. Cabildo 2345").build();
        sedeRepository.saveAll(Arrays.asList(sedePalermo, sedeBelgrano));

        // Crear Disciplinas
        Disciplina funcional = Disciplina.builder().nombre("Funcional").build();
        Disciplina yoga = Disciplina.builder().nombre("Yoga").build();
        Disciplina spinning = Disciplina.builder().nombre("Spinning").build();
        disciplinaRepository.saveAll(Arrays.asList(funcional, yoga, spinning));

        // Crear Instructores
        Instructor instructorA = Instructor.builder().nombre("Juan Perez").build();
        Instructor instructorB = Instructor.builder().nombre("Maria Garcia").build();
        instructorRepository.saveAll(Arrays.asList(instructorA, instructorB));

        // Crear Clases
        Clase clase1 = Clase.builder()
                .fechaHora(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0))
                .duracionMinutos(60)
                .cupoDisponible(20)
                .sede(sedePalermo)
                .disciplina(funcional)
                .instructor(instructorA)
                .build();

        Clase clase2 = Clase.builder()
                .fechaHora(LocalDateTime.now().plusDays(1).withHour(19).withMinute(0).withSecond(0))
                .duracionMinutos(50)
                .cupoDisponible(15)
                .sede(sedePalermo)
                .disciplina(yoga)
                .instructor(instructorB)
                .build();

        Clase clase3 = Clase.builder()
                .fechaHora(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0))
                .duracionMinutos(45)
                .cupoDisponible(25)
                .sede(sedeBelgrano)
                .disciplina(spinning)
                .instructor(instructorA)
                .build();

        Clase clase4 = Clase.builder()
                .fechaHora(LocalDateTime.now().plusDays(2).withHour(18).withMinute(30).withSecond(0))
                .duracionMinutos(60)
                .cupoDisponible(20)
                .sede(sedeBelgrano)
                .disciplina(funcional)
                .instructor(instructorB)
                .build();

        claseRepository.saveAll(Arrays.asList(clase1, clase2, clase3, clase4));
    }
}
