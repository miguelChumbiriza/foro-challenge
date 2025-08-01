package com.desafio.forohub.domain.topico.validations.update;

import com.desafio.forohub.domain.curso.repository.CursoRepository;
import com.desafio.forohub.domain.topico.dto.ActualizarTopicoDTO;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidarCursoActualizado implements ValidarTopicoActualizado {

    @Autowired
    private CursoRepository repository;
    @Override
    public void validate(ActualizarTopicoDTO data) {
        if(data.cursoId() != null){
            var ExisteCurso = repository.existsById(data.cursoId());//falta completar
            if (!ExisteCurso){
                throw new ValidationException("Este curso no existe");
            }
            var cursoHabilitado = repository.findById(data.cursoId()).get().getActivo();//falta completar
            if(!cursoHabilitado){
                throw new ValidationException("Este curso no está disponible por el momento");
            }
        }
    }
}
