package com.desafio.forohub.domain.curso.dto;

import com.desafio.forohub.domain.curso.Categoria;
import jakarta.persistence.*;

public record ActualizarCursoDTO(
        String name,
        Categoria categoria,
        Boolean activo
) {


}
