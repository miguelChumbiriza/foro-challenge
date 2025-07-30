package com.desafio.forohub.controller;

import com.desafio.forohub.domain.curso.Curso;
import com.desafio.forohub.domain.curso.repository.CursoRepository;
import com.desafio.forohub.domain.respuesta.Respuesta;
import com.desafio.forohub.domain.respuesta.dto.DetalleRespuestaDTO;
import com.desafio.forohub.domain.topico.Estado;
import com.desafio.forohub.domain.topico.Topico;
import com.desafio.forohub.domain.topico.dto.ActualizarTopicoDTO;
import com.desafio.forohub.domain.topico.dto.CrearTopicoDTO;
import com.desafio.forohub.domain.topico.dto.DetallesTopicoDTO;
import com.desafio.forohub.domain.topico.repository.TopicoRepository;
import com.desafio.forohub.domain.topico.validations.create.ValidarTopicoCreado;
import com.desafio.forohub.domain.topico.validations.update.ValidarTopicoActualizado;
import com.desafio.forohub.domain.usuario.Usuario;
import com.desafio.forohub.domain.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Topic", description = "Está vincualdo a un curso y usuario especificos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    List<ValidarTopicoCreado> crearValidadores;

    @Autowired
    List<ValidarTopicoActualizado> actualizarValidadores;

    @PostMapping
    @Transactional
    @Operation(summary = "Registra un unevo topico en la BD")


    public ResponseEntity<DetallesTopicoDTO> crearTopico(@RequestBody @Valid
    CrearTopicoDTO crearTopicoDTO, UriComponentsBuilder uriBuilder){

        crearValidadores.forEach(v -> v.validate(crearTopicoDTO));

        Usuario usuario = usuarioRepository.findById(crearTopicoDTO.usuarioId()).get() ;
        Curso curso = cursoRepository.findById(crearTopicoDTO.cursoId()).get();
        Topico topico = new Topico(crearTopicoDTO,usuario,curso);//crearTopicoDTO, usuario, curso

        topicoRepository.save(topico);
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetallesTopicoDTO(topico));
    }

    @GetMapping("/all")
    @Operation (summary = "Lee todos los temas independientemente de su estado")
    public ResponseEntity<Page<DetallesTopicoDTO>> LeerTodosTopicos(@PageableDefault(size = 5,sort = {"id"}) {

        var pagina = topicoRepository.findAll(pageable).map(DetallesTopicoDTO::new);
        return ResponseEntity.ok(pagina);
    }
        @GetMapping
        @Operation (summary = "Lista de temas abiertos y cerrados")
        public ResponseEntity<Page<DetallesTopicoDTO>> LeerTopicosNoEliminados(@PageableDefault(size = 5,sort = {"id"}){
            var pagina = topicoRepository.findAllByEstadoIsNot(Estado.DELETED, pageable).map(Detalles);
            return ResponseEntity.ok(pagina);
    }



    @GetMapping ("/{id}")
    @Operation (summary = "Lee un nico tema por su ID")
    public ResponseEntity<DetallesTopicoDTO> leerUnTopico@PathVariable Long id){

        Topico topico = topicoRepository.getReferenceById(id);
        var datosTopico = new DetallesTopicoDTO(
            topico.getId(),
            topico.getTitulo(),
            topico.getMensaje(),
            topico.getFechaCreacion(),
            topico.getUltimaActualizacion(),
            topico.getEstado(),
            topico.getUsuario().getUsername(),
            topico.getCurso().getName(),
            topico. getCurso().getCategoria()
        ) ;
            return ResponseEntity.ok(datosTopico);

        }

        @GetMapping ("/{id}/solucion")
        @Operation (summary = "Lee la respuesta del topico marcada como su solución")
        public ResponseEntity<DetalleRespuestaDTO> leerSolucionTopico(@PathVariable Long id){
            Respuesta respuesta = respuestaRepository.getReferenceByTopicoId(id);
            var datosRespuesta = new DetalleRespuestaDTO(
                    respuesta.getId(),
                    respuesta.getMensaje(),
                    respuesta.getFechaCreacion(),
                    respuesta.getUltimaActualizacion(),
                    respuesta.getSolucion(),
                    respuesta.getBorrado(),
                    respuesta.getUsuario().getId(),
                    respuesta.getUsuario().getUsername(),
                    respuesta.getTopico().getId(),
                    respuesta.getTopico().getTitulo()
            );

            return ResponseEntity.ok(datosRespuesta);

        }

    @PutMapping ("/{id}")
    @Transactional
    @Operation (summary = "Actualiza el titulo, el mensaje, el estado o el ID del curso de un tem.")
    public ResponseEntity<DetallesTopicoDTO> actualizarTopico(@RequestBody @Valid ActualizarTopicoDTO actualizarTopicoDTO,
                                                              @PathVariable Long id){

        actualizarValidadores.forEach(v -> v.validate(actualizarTopicoDTO));

        Topico topico = topicoRepository. getReferenceById(id);

        if(actualizarTopicoDTO.cursoId() != null){
            Curso curso = cursoRepository.getReferenceById(actualizarTopicoDTO.cursoId());
            topico.actualizarTopicoConCurso(actualizarTopicoDTO, curso);
        }else{
            topico.actualizarTopico(actualizarTopicoDTO);
        }
        var datosTopico = new DetallesTopicoDTO(
            topico.getId(),
            topico.getTitulo(),
            topico.getMensaje(),
            topico.getFechaCreacion(),
            topico.getUltimaActualizacion(),
            topico.getEstado(),
            topico.getUsuario().getUsername(),
            topico.getCurso().getName(),
            topico.getCurso().getCategoria()
        );
        return ResponseEntity.ok(datosTopico);

    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation (summary = "Elimina un topic")
    public ResponseEntity<?> eliminarTopico(@PathVariable Long id)

    {
        Topico topico = topicoRepository.getReferenceById(id);
        topico.eliminarTopico();
        return ResponseEntity.noContent().build();
    }
}




