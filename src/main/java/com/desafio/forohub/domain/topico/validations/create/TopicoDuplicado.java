package com.desafio.forohub.domain.topico.validations.create;

import com.desafio.forohub.domain.topico.dto.CrearTopicoDTO;
import com.desafio.forohub.domain.topico.repository.TopicoRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicoDuplicado implements  ValidarTopicoCreado {
    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    public void validate(CrearTopicoDTO data) {
        var topicoDuplicado = topicoRepository.existsByTituloAndMensaje(data.titulo(), data.mensaje());
        if (topicoDuplicado) {
            throw new ValidationException("Este topico ya existe. Revisa /topicos/" + topicoRepository.findByTitulo(data.titulo()).getId());
        }
    }
}

//Implementacion si el find no encuentra nada
//var topico = topicoRepository.findByTitulo(data.titulo());
//if (topico != null) {
//        throw new ValidationException("Este tópico ya existe. Revisa /topicos/" + topico.getId());
//        }