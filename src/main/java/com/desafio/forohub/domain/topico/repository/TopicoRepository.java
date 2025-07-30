package com.desafio.forohub.domain.topico.repository;

import com.desafio.forohub.domain.topico.Estado;
import com.desafio.forohub.domain.topico.Topico;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long > {
    Page<Topico> findAll(Pageable pageable);
    Page<Topico> findAllByEstadoIsNot(Estado estado, Pageable pageable);
    Boolean existsByTituloAndMensaje(String titulo, String mensaje);//falta hora 1:05
    Topico findByTitulo(String titulo);

}
