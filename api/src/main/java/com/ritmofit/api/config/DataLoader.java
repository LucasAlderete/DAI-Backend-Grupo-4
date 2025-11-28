package com.ritmofit.api.config;

import com.ritmofit.api.model.entity.*;
import com.ritmofit.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DisciplinaRepository disciplinaRepository;
    private final SedeRepository sedeRepository;
    private final InstructorRepository instructorRepository;
    private final ClaseRepository claseRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final PasswordEncoder passwordEncoder;
    private final NewsRepository newsRepository;

    @Override
    public void run(String... args) throws Exception {
        // Solo cargar datos si no existen
        if (disciplinaRepository.count() == 0) {
            cargarDatosIniciales();
        }

        if (newsRepository.count() == 0) {
            cargarNewsIniciales();
        }
        
        // Siempre intentar crear datos de prueba para calificaciones
        // (solo si el usuario existe o se puede crear)
        crearUsuarioPrueba();
        Usuario usuarioPrueba = usuarioRepository.findByEmail("lucasm.alderete@gmail.com").orElse(null);
        if (usuarioPrueba != null) {
            // Verificar si el usuario ya tiene asistencias recientes sin calificar
            LocalDateTime fechaMinima = LocalDateTime.now().minusHours(24);
            long asistenciasRecientes = asistenciaRepository.findAll().stream()
                    .filter(a -> a.getUsuario().getId().equals(usuarioPrueba.getId()))
                    .filter(a -> a.getFechaCheckin().isAfter(fechaMinima))
                    .filter(a -> a.getCalificacion() == null)
                    .count();
            
            if (asistenciasRecientes == 0) {
                // Solo crear asistencias si no tiene ninguna reciente sin calificar
                Disciplina yoga = disciplinaRepository.findAll().stream()
                        .filter(d -> "Yoga".equals(d.getNombre())).findFirst().orElse(null);
                Disciplina pilates = disciplinaRepository.findAll().stream()
                        .filter(d -> "Pilates".equals(d.getNombre())).findFirst().orElse(null);
                Disciplina crossfit = disciplinaRepository.findAll().stream()
                        .filter(d -> "CrossFit".equals(d.getNombre())).findFirst().orElse(null);
                Disciplina spinning = disciplinaRepository.findAll().stream()
                        .filter(d -> "Spinning".equals(d.getNombre())).findFirst().orElse(null);
                
                Instructor maria = instructorRepository.findAll().stream()
                        .filter(i -> "María".equals(i.getNombre())).findFirst().orElse(null);
                Instructor carlos = instructorRepository.findAll().stream()
                        .filter(i -> "Carlos".equals(i.getNombre())).findFirst().orElse(null);
                Instructor diego = instructorRepository.findAll().stream()
                        .filter(i -> "Diego".equals(i.getNombre())).findFirst().orElse(null);
                Instructor ana = instructorRepository.findAll().stream()
                        .filter(i -> "Ana".equals(i.getNombre())).findFirst().orElse(null);
                
                Sede sedeCentro = sedeRepository.findAll().stream()
                        .filter(s -> "RitmoFit Centro".equals(s.getNombre())).findFirst().orElse(null);
                Sede sedePalermo = sedeRepository.findAll().stream()
                        .filter(s -> "RitmoFit Palermo".equals(s.getNombre())).findFirst().orElse(null);
                Sede sedeRecoleta = sedeRepository.findAll().stream()
                        .filter(s -> "RitmoFit Recoleta".equals(s.getNombre())).findFirst().orElse(null);
                
                if (yoga != null && pilates != null && crossfit != null && spinning != null &&
                    maria != null && carlos != null && diego != null && ana != null &&
                    sedeCentro != null && sedePalermo != null && sedeRecoleta != null) {
                    crearDatosCalificacion(yoga, pilates, crossfit, spinning, maria, carlos, diego, ana, 
                                         sedeCentro, sedePalermo, sedeRecoleta);
                }
            }
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

    private void cargarNewsIniciales() {
        News n1 = News.builder()
                .titulo("Black Friday: 30% OFF en membresías")
                .descripcion("Aprovechá el 30% de descuento en planes trimestrales. Solo por esta semana.")
                .orden(1)
                .imgUrl("https://media.istockphoto.com/id/1658502745/vector/black-friday.jpg?s=612x612&w=0&k=20&c=xwIMr3zgpa2AFKIme-oxYcABGqjoILDxDjzHCt3Z33Y=")
                .build();
        News n2 = News.builder()
                .titulo("Nueva clase de Yoga")
                .descripcion("Sumamos clases de Yoga los martes y jueves a las 19 hs.")
                .orden(2)
                .imgUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIqFZozo1io81bgDEt3IVlo9EF1MKbv35yEw&s")
                .build();
        News n3 = News.builder()
                .titulo("Evento especial de Zumba")
                .descripcion("Este sábado, masterclass de Zumba en la sede Palermo. ¡Reservá tu lugar!")
                .orden(3)
                .imgUrl("https://t4.ftcdn.net/jpg/02/41/90/89/360_F_241908982_eJlON17X6EbDhuSUAYOz52VGQQNOy8dT.jpg")
                .build();
        newsRepository.save(n1);
        newsRepository.save(n2);
        newsRepository.save(n3);
    }

    private void crearUsuarioPrueba() {
        String emailPrueba = "lucasm.alderete@gmail.com";
        
        // Solo crear si no existe
        if (!usuarioRepository.existsByEmail(emailPrueba)) {
            Usuario usuarioPrueba = Usuario.builder()
                    .email(emailPrueba)
                    .nombre("Lucas Alderete")
                    .password(passwordEncoder.encode("River"))
                    .fechaRegistro(LocalDateTime.now().minusDays(30))
                    .ultimoAcceso(LocalDateTime.now())
                    .activo(true)
                    .emailVerificado(true) // Ya verificado para poder hacer login
                    .build();
            
            usuarioRepository.save(usuarioPrueba);
        }
    }

    private void crearDatosCalificacion(Disciplina yoga, Disciplina pilates, Disciplina crossfit, 
                                        Disciplina spinning, Instructor maria, Instructor carlos, 
                                        Instructor diego, Instructor ana, Sede sedeCentro, 
                                        Sede sedePalermo, Sede sedeRecoleta) {
        
        LocalDateTime ahora = LocalDateTime.now();
        Usuario usuarioPrueba = usuarioRepository.findByEmail("lucasm.alderete@gmail.com").orElse(null);
        
        if (usuarioPrueba == null) {
            return; // Si no existe el usuario, no crear datos
        }

        // Crear clases pasadas (hace algunas horas) para poder tener asistencias
        Clase claseYogaPasada = Clase.builder()
                .nombre("Yoga Matutino - Pasada")
                .descripcion("Clase de yoga que ya ocurrió")
                .disciplina(yoga)
                .instructor(maria)
                .sede(sedeCentro)
                .fechaInicio(ahora.minusHours(3))
                .fechaFin(ahora.minusHours(2))
                .cupoMaximo(20)
                .cupoActual(15)
                .activo(true)
                .build();

        Clase clasePilatesPasada = Clase.builder()
                .nombre("Pilates Core - Pasada")
                .descripcion("Clase de pilates que ya ocurrió")
                .disciplina(pilates)
                .instructor(carlos)
                .sede(sedePalermo)
                .fechaInicio(ahora.minusHours(6))
                .fechaFin(ahora.minusHours(5))
                .cupoMaximo(15)
                .cupoActual(12)
                .activo(true)
                .build();

        Clase claseCrossfitPasada = Clase.builder()
                .nombre("CrossFit Intenso - Pasada")
                .descripcion("Clase de crossfit que ya ocurrió")
                .disciplina(crossfit)
                .instructor(diego)
                .sede(sedeCentro)
                .fechaInicio(ahora.minusHours(12))
                .fechaFin(ahora.minusHours(11))
                .cupoMaximo(12)
                .cupoActual(10)
                .activo(true)
                .build();

        Clase claseSpinningPasada = Clase.builder()
                .nombre("Spinning Power - Pasada")
                .descripcion("Clase de spinning que ya ocurrió")
                .disciplina(spinning)
                .instructor(ana)
                .sede(sedeRecoleta)
                .fechaInicio(ahora.minusHours(18))
                .fechaFin(ahora.minusHours(17))
                .cupoMaximo(25)
                .cupoActual(20)
                .activo(true)
                .build();

        // Guardar clases pasadas
        claseYogaPasada = claseRepository.save(claseYogaPasada);
        clasePilatesPasada = claseRepository.save(clasePilatesPasada);
        claseCrossfitPasada = claseRepository.save(claseCrossfitPasada);
        claseSpinningPasada = claseRepository.save(claseSpinningPasada);

        // Crear asistencias recientes (dentro de las últimas 24 horas) SIN calificación
        // Asistencia hace 2 horas (aún calificable)
        Asistencia asistencia1 = Asistencia.builder()
                .usuario(usuarioPrueba)
                .clase(claseYogaPasada)
                .fechaAsistencia(ahora.minusHours(3))
                .fechaCheckin(ahora.minusHours(3))
                .duracionMinutos(60)
                .calificacion(null) // Sin calificar
                .comentario(null)
                .fechaCalificacion(null)
                .build();

        // Asistencia hace 5 horas (aún calificable)
        Asistencia asistencia2 = Asistencia.builder()
                .usuario(usuarioPrueba)
                .clase(clasePilatesPasada)
                .fechaAsistencia(ahora.minusHours(6))
                .fechaCheckin(ahora.minusHours(6))
                .duracionMinutos(60)
                .calificacion(null) // Sin calificar
                .comentario(null)
                .fechaCalificacion(null)
                .build();

        // Asistencia hace 11 horas (aún calificable)
        Asistencia asistencia3 = Asistencia.builder()
                .usuario(usuarioPrueba)
                .clase(claseCrossfitPasada)
                .fechaAsistencia(ahora.minusHours(12))
                .fechaCheckin(ahora.minusHours(12))
                .duracionMinutos(60)
                .calificacion(null) // Sin calificar
                .comentario(null)
                .fechaCalificacion(null)
                .build();

        // Asistencia hace 17 horas (aún calificable, cerca del límite)
        Asistencia asistencia4 = Asistencia.builder()
                .usuario(usuarioPrueba)
                .clase(claseSpinningPasada)
                .fechaAsistencia(ahora.minusHours(18))
                .fechaCheckin(ahora.minusHours(18))
                .duracionMinutos(60)
                .calificacion(null) // Sin calificar
                .comentario(null)
                .fechaCalificacion(null)
                .build();

        // Guardar asistencias
        asistenciaRepository.save(asistencia1);
        asistenciaRepository.save(asistencia2);
        asistenciaRepository.save(asistencia3);
        asistenciaRepository.save(asistencia4);
    }
}
