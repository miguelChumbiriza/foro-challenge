package com.desafio.forohub.domain.respuesta.validations.update;
import com.desafio.forohub.domain.respuesta.Respuesta;
import com.desafio.forohub.domain.respuesta.dto.ActualizarRespuestaDTO;
import com.desafio.forohub.domain.respuesta.repository.RespuestaRepository;
import com.desafio.forohub.domain.topico.Estado;
import com.desafio.forohub.domain.topico.repository.TopicoRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SolucionDuplicada implements ValidarRespuestaActualizada {
    @Autowired
    private RespuestaRepository respuestaRepository;
    @Autowired
    private TopicoRepository topicoRepository;

    @Override
    public void validate(ActualizarRespuestaDTO data, Long respuestaId) {
        if (data.solucion()) {
            Respuesta respuesta = respuestaRepository.getReferenceById(respuestaId);
            var topicoResuelto = topicoRepository.getReferenceById(respuesta.getTopico().getId());
            if (topicoResuelto.getEstado() == Estado.CLOSED) {
                throw new ValidationException("Este topico ya está solucionado");
            }
        }
    }
}

