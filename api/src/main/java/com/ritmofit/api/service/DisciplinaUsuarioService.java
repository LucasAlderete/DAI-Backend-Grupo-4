package com.ritmofit.api.service;

import com.ritmofit.api.dto.DisciplinaUsuarioDto;
import com.ritmofit.api.model.entity.Disciplina;
import com.ritmofit.api.model.entity.DisciplinaUsuario;
import com.ritmofit.api.model.entity.Usuario;
import com.ritmofit.api.repository.DisciplinaRepository;
import com.ritmofit.api.repository.DisciplinaUsuarioRepository;
import com.ritmofit.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DisciplinaUsuarioService {

    private final DisciplinaUsuarioRepository disciplinaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisciplinaRepository disciplinaRepository;

    public List<DisciplinaUsuarioDto> obtenerDisciplinasFavoritasPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return disciplinaUsuarioRepository.findByUsuario(usuario).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public DisciplinaUsuarioDto agregarDisciplinaFavorita(String emailUsuario, Long disciplinaId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));

        // Verificar si ya existe
        if (disciplinaUsuarioRepository.existsByUsuarioIdAndDisciplinaId(usuario.getId(), disciplinaId)) {
            throw new RuntimeException("La disciplina ya está marcada como favorita");
        }

        DisciplinaUsuario disciplinaUsuario = DisciplinaUsuario.builder()
                .usuario(usuario)
                .disciplina(disciplina)
                .build();

        disciplinaUsuario = disciplinaUsuarioRepository.save(disciplinaUsuario);
        return convertirADto(disciplinaUsuario);
    }

    public void eliminarDisciplinaFavorita(String emailUsuario, Long disciplinaUsuarioId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DisciplinaUsuario disciplinaUsuario = disciplinaUsuarioRepository.findById(disciplinaUsuarioId)
                .orElseThrow(() -> new RuntimeException("Disciplina favorita no encontrada"));

        // Verificar que pertenece al usuario
        if (!disciplinaUsuario.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar esta disciplina favorita");
        }

        disciplinaUsuarioRepository.delete(disciplinaUsuario);
    }

    private DisciplinaUsuarioDto convertirADto(DisciplinaUsuario disciplinaUsuario) {
        return DisciplinaUsuarioDto.builder()
                .id(disciplinaUsuario.getId())
                .usuarioId(disciplinaUsuario.getUsuario().getId())
                .disciplinaId(disciplinaUsuario.getDisciplina().getId())
                .disciplinaNombre(disciplinaUsuario.getDisciplina().getNombre())
                .build();
    }
}
