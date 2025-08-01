package com.desafio.forohub.controller;


import com.desafio.forohub.domain.curso.Curso;
import com.desafio.forohub.domain.curso.dto.ActualizarCursoDTO;
import com.desafio.forohub.domain.curso.dto.CrearCursoDTO;
import com.desafio.forohub.domain.curso.dto.DetalleCursoDTO;
import com.desafio.forohub.domain.curso.repository.CursoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; //en algun lugar debe implementar revisar
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cursos")
@SecurityRequirement(name="bearer-key")
@Tag(name="Curso", description="Puede pertenecer a una de las muchas caterorias definidas")
public class CursoController {
    @Autowired
    private CursoRepository repository;

    @PostMapping
    @Transactional
    @Operation(summary= "Registar un nuevo curso en la BD")
    public ResponseEntity<DetalleCursoDTO>crearTopico(@RequestBody @Valid CrearCursoDTO crearCursoDTO,
                                                      UriComponentsBuilder uriBuilder){
        Curso curso = new Curso(crearCursoDTO);
        repository.save(curso);
        var uri = uriBuilder.path("/cursos{id}").buildAndExpand(curso.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetalleCursoDTO(curso));

    }

    @GetMapping("/all")
    @Operation(summary = "Lee todos los cursos independiente de su estado")
    public ResponseEntity<Page<DetalleCursoDTO>> listarCurso(@PageableDefault(size= 5, sort={"id"}) Pageable pageable)

    {
        var pagina = repository.findAll(pageable).map(DetalleCursoDTO::new);//Revisar porque se completado de chatG
        return ResponseEntity.ok(pagina);

    }

    @GetMapping
    @Operation (summary = "Lista de cursos activos")
    public ResponseEntity<Page<DetalleCursoDTO>> listarCursosActivos(@PageableDefault(size= 5, sort={"id"}) Pageable pageable){//Revisar porque se completado de chatG mint 37
        var pagina = repository.findAllByActivoTrue(pageable).map(DetalleCursoDTO::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    @Operation (summary = "Lee un solo curso por su ID")
    public ResponseEntity<DetalleCursoDTO> ListarUnCurso(@PathVariable Long id){//Revisar xq se completo suponiendo
        Curso curso = repository.getReferenceById(id);
        var datosDelCurso = new DetalleCursoDTO(
                curso.getId(),
                curso.getName(),
                curso.getCategoria(),
                curso.getActivo()
        );
        return ResponseEntity.ok(datosDelCurso);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation (summary = "Actualiza el nombre, la categoria")
    public ResponseEntity<DetalleCursoDTO> actualizarCurso(@RequestBody @Valid ActualizarCursoDTO actualizarCursoDTO, @PathVariable Long id){
        Curso curso = repository.getReferenceById(id);
        curso.actualizarCurso(actualizarCursoDTO);
        var datosDelCurso = new DetalleCursoDTO(
        curso.getId(),
        curso.getName(),
        curso.getCategoria(),
        curso.getActivo()
             );
        return ResponseEntity.ok(datosDelCurso);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Elimina un curso")
    public ResponseEntity<?> eliminarCurso(@PathVariable Long id){
        Curso curso = repository.getReferenceById(id);
        curso.eliminarCurso();
        return ResponseEntity.noContent().build();
    }

}




