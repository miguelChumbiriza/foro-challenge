package com.desafio.forohub.controller;

import com.desafio.forohub.domain.respuesta.Respuesta;
import com.desafio.forohub.domain.respuesta.dto.ActualizarRespuestaDTO;
import com.desafio.forohub.domain.respuesta.dto.CrearRespuestaDTO;
import com.desafio.forohub.domain.respuesta.dto.DetalleRespuestaDTO;
import com.desafio.forohub.domain.respuesta.validations.create.ValidarRespuestaCreada;
import com.desafio.forohub.domain.respuesta.validations.update.ValidarRespuestaActualizada;
import com.desafio.forohub.domain.topico.Estado;
import com.desafio.forohub.domain.topico.Topico;
import com.desafio.forohub.domain.topico.repository.TopicoRepository;
import com.desafio.forohub.domain.usuario.Usuario;
import com.desafio.forohub.domain.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.desafio.forohub.domain.respuesta.repository.RespuestaRepository;

import java.util.List;

@RestController
@RequestMapping("/respuestas")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Respuesta", description = "Solo uno puede ser la solucion al tema")
public class RespuestaController {

    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    List<ValidarRespuestaCreada> crearValidadores;

    @Autowired
    List<ValidarRespuestaActualizada> actualizarValidadores;



    @PostMapping
    @Transactional
    @Operation(summary = "Registra una nueva respuesta en la base de datos, vinculada a un usuari")

    public ResponseEntity<DetalleRespuestaDTO> crearRespuesta(@RequestBody @Valid CrearRespuestaDTO crearRespuestaDTO,
        UriComponentsBuilder uriBuilder){
                crearValidadores.forEach(v -> v.validate(crearRespuestaDTO));
        Usuario usuario = usuarioRepository.getReferenceById(crearRespuestaDTO.usuarioId());
        Topico topico = topicoRepository.findById(crearRespuestaDTO.topicoId()).get();
        var respuesta = new Respuesta(crearRespuestaDTO, usuario, topico);
        respuestaRepository.save(respuesta);
        var uri = uriBuilder.path("/respuestas/{id}").buildAndExpand(respuesta.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetalleRespuestaDTO(respuesta));
    }


    @GetMapping ("/topico/{topicoId}")
    @Operation(summary = "Lee todas las respuestas del tema dado")
    public ResponseEntity<Page<DetalleRespuestaDTO>>
    LeerRespuestaDeTopico(@PageableDefault(size = 5,
            sort = {"ultimaActualizacion"},
            direction = Sort.Direction.ASC) Pageable pageable, @PathVariable Long topicoId){
        var pagina = respuestaRepository.findAllByTopicoId(topicoId, pageable).map(DetalleRespuestaDTO::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/usuario/{nombreUsuario}")
    @Operation (summary = "Lee todas las respuestas del nombre de usuario proporcionado.")
    public ResponseEntity<Page<DetalleRespuestaDTO>>
    LeerRespuestasDeUsuarios(@PageableDefault(size = 5, sort = {"ultimaActualizacion"},
            direction = Sort.Direction.ASC)Pageable pageable, @PathVariable Long usuarioId) {

        var pagina = respuestaRepository.findAllByUsuarioId(usuarioId, pageable).map(DetalleRespuestaDTO::new);
        return ResponseEntity.ok(pagina);


    }
    @GetMapping("/{id}")
    @Operation (summary = "Lee una única respuesta por su ID")
    public ResponseEntity<DetalleRespuestaDTO> LeerUnaRespuesta(@PathVariable Long id){
        Respuesta respuesta = respuestaRepository.getReferenceById(id);

        var datosRespuesta = new DetalleRespuestaDTO(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getFechaCreacion(),
                respuesta.getUltimaActualizacion(),
                respuesta.getSolucion(),
                respuesta.getBorrado(),
                respuesta. getUsuario().getId(),
                respuesta.getUsuario().getUsername(),
                respuesta.getTopico().getId(),
                respuesta.getTopico().getTitulo()
        );
        return ResponseEntity.ok(datosRespuesta);
    }

    @PutMapping ("/{id}")
    @Transactional
    @Operation (summary = "Actualiza el mensaje de la respuesta, la solucion o el estado de la respuesta")
    public ResponseEntity<DetalleRespuestaDTO> actualizarRespuesta(@RequestBody @Valid ActualizarRespuestaDTO actualizarRespuestaDTO, @PathVariable Long id){
        actualizarValidadores.forEach( v -> v.validate(actualizarRespuestaDTO, id));
        Respuesta respuesta = respuestaRepository.getReferenceById(id);
        respuesta.actualizarRespuesta(actualizarRespuestaDTO);

        if(actualizarRespuestaDTO.solucion()) {
            var temaResuelto = topicoRepository.getReferenceById(respuesta.getTopico().getId());
            temaResuelto.setEstado(Estado.CLOSED);
        }
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

    @DeleteMapping ("/{id}")
    @Transactional
    @Operation (summary = "Elimina una respuesta por su Id")
    public ResponseEntity<?> borrarRespuesta(@PathVariable Long id){
        Respuesta respuesta = respuestaRepository.getReferenceById(id) ;
        respuesta.eliminarRespuesta();
        return ResponseEntity.noContent().build();
    }

}





