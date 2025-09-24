package com.ritmofit.api.config;

import com.ritmofit.api.model.entity.*;
import com.ritmofit.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DisciplinaRepository disciplinaRepository;
    private final SedeRepository sedeRepository;
    private final InstructorRepository instructorRepository;
    private final ClaseRepository claseRepository;

    @Override
    public void run(String... args) throws Exception {
        // Solo cargar datos si no existen
        if (disciplinaRepository.count() == 0) {
            cargarDatosIniciales();
        }
    }

    private void cargarDatosIniciales() {
        // Crear disciplinas
        Disciplina yoga = Disciplina.builder()
                .nombre("Yoga")
                .descripcion("Práctica de posturas, respiración y meditación para mejorar la flexibilidad y reducir el estrés")
                .activo(true)
                .build();

        Disciplina pilates = Disciplina.builder()
                .nombre("Pilates")
                .descripcion("Método de ejercicios que fortalece el core y mejora la postura")
                .activo(true)
                .build();

        Disciplina crossfit = Disciplina.builder()
                .nombre("CrossFit")
                .descripcion("Entrenamiento de alta intensidad que combina ejercicios funcionales")
                .activo(true)
                .build();

        Disciplina spinning = Disciplina.builder()
                .nombre("Spinning")
                .descripcion("Clase de ciclismo indoor con música y rutinas de alta energía")
                .activo(true)
                .build();

        Disciplina zumba = Disciplina.builder()
                .nombre("Zumba")
                .descripcion("Baile fitness que combina movimientos de baile con ejercicios aeróbicos")
                .activo(true)
                .build();

        disciplinaRepository.save(yoga);
        disciplinaRepository.save(pilates);
        disciplinaRepository.save(crossfit);
        disciplinaRepository.save(spinning);
        disciplinaRepository.save(zumba);

        // Crear sedes
        Sede sedeCentro = Sede.builder()
                .nombre("RitmoFit Centro")
                .direccion("Av. Corrientes 1234, CABA")
                .telefono("+54 11 4567-8900")
                .activo(true)
                .build();

        Sede sedePalermo = Sede.builder()
                .nombre("RitmoFit Palermo")
                .direccion("Av. Santa Fe 5678, Palermo, CABA")
                .telefono("+54 11 4567-8901")
                .activo(true)
                .build();

        Sede sedeRecoleta = Sede.builder()
                .nombre("RitmoFit Recoleta")
                .direccion("Av. Callao 9012, Recoleta, CABA")
                .telefono("+54 11 4567-8902")
                .activo(true)
                .build();

        sedeRepository.save(sedeCentro);
        sedeRepository.save(sedePalermo);
        sedeRepository.save(sedeRecoleta);

        // Crear instructores
        Instructor maria = Instructor.builder()
                .nombre("María")
                .apellido("González")
                .email("maria.gonzalez@ritmofit.com")
                .telefono("+54 11 1234-5678")
                .activo(true)
                .build();

        Instructor carlos = Instructor.builder()
                .nombre("Carlos")
                .apellido("Rodríguez")
                .email("carlos.rodriguez@ritmofit.com")
                .telefono("+54 11 2345-6789")
                .activo(true)
                .build();

        Instructor ana = Instructor.builder()
                .nombre("Ana")
                .apellido("Martínez")
                .email("ana.martinez@ritmofit.com")
                .telefono("+54 11 3456-7890")
                .activo(true)
                .build();

        Instructor diego = Instructor.builder()
                .nombre("Diego")
                .apellido("López")
                .email("diego.lopez@ritmofit.com")
                .telefono("+54 11 4567-8901")
                .activo(true)
                .build();

        instructorRepository.save(maria);
        instructorRepository.save(carlos);
        instructorRepository.save(ana);
        instructorRepository.save(diego);

        // Crear clases para los próximos días
        LocalDateTime ahora = LocalDateTime.now();
        
        // Clases de mañana
        Clase claseYogaManana = Clase.builder()
                .nombre("Yoga Matutino")
                .descripcion("Clase de yoga para comenzar el día con energía y tranquilidad")
                .disciplina(yoga)
                .instructor(maria)
                .sede(sedeCentro)
                .fechaInicio(ahora.plusDays(1).withHour(8).withMinute(0).withSecond(0))
                .fechaFin(ahora.plusDays(1).withHour(9).withMinute(0).withSecond(0))
                .cupoMaximo(20)
                .cupoActual(5)
                .activo(true)
                .build();

        Clase clasePilatesManana = Clase.builder()
                .nombre("Pilates Core")
                .descripcion("Enfoque en fortalecimiento del core y mejora de la postura")
                .disciplina(pilates)
                .instructor(carlos)
                .sede(sedePalermo)
                .fechaInicio(ahora.plusDays(1).withHour(9).withMinute(30).withSecond(0))
                .fechaFin(ahora.plusDays(1).withHour(10).withMinute(30).withSecond(0))
                .cupoMaximo(15)
                .cupoActual(8)
                .activo(true)
                .build();

        // Clases de tarde
        Clase claseCrossfitTarde = Clase.builder()
                .nombre("CrossFit Intenso")
                .descripcion("WOD de alta intensidad para quemar calorías y ganar fuerza")
                .disciplina(crossfit)
                .instructor(diego)
                .sede(sedeCentro)
                .fechaInicio(ahora.plusDays(1).withHour(18).withMinute(0).withSecond(0))
                .fechaFin(ahora.plusDays(1).withHour(19).withMinute(0).withSecond(0))
                .cupoMaximo(12)
                .cupoActual(3)
                .activo(true)
                .build();

        Clase claseSpinningTarde = Clase.builder()
                .nombre("Spinning Power")
                .descripcion("Rutina de spinning con música energizante")
                .disciplina(spinning)
                .instructor(ana)
                .sede(sedeRecoleta)
                .fechaInicio(ahora.plusDays(1).withHour(19).withMinute(30).withSecond(0))
                .fechaFin(ahora.plusDays(1).withHour(20).withMinute(30).withSecond(0))
                .cupoMaximo(25)
                .cupoActual(12)
                .activo(true)
                .build();

        // Clases del día siguiente
        Clase claseZumbaManana = Clase.builder()
                .nombre("Zumba Fiesta")
                .descripcion("Baile fitness con ritmos latinos y música internacional")
                .disciplina(zumba)
                .instructor(ana)
                .sede(sedePalermo)
                .fechaInicio(ahora.plusDays(2).withHour(10).withMinute(0).withSecond(0))
                .fechaFin(ahora.plusDays(2).withHour(11).withMinute(0).withSecond(0))
                .cupoMaximo(30)
                .cupoActual(18)
                .activo(true)
                .build();

        Clase claseYogaTarde = Clase.builder()
                .nombre("Yoga Restaurativo")
                .descripcion("Yoga suave para relajación y recuperación")
                .disciplina(yoga)
                .instructor(maria)
                .sede(sedeRecoleta)
                .fechaInicio(ahora.plusDays(2).withHour(17).withMinute(0).withSecond(0))
                .fechaFin(ahora.plusDays(2).withHour(18).withMinute(0).withSecond(0))
                .cupoMaximo(18)
                .cupoActual(7)
                .activo(true)
                .build();

        claseRepository.save(claseYogaManana);
        claseRepository.save(clasePilatesManana);
        claseRepository.save(claseCrossfitTarde);
        claseRepository.save(claseSpinningTarde);
        claseRepository.save(claseZumbaManana);
        claseRepository.save(claseYogaTarde);
    }
}
