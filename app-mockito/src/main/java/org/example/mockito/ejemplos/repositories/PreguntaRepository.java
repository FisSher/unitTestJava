package org.example.mockito.ejemplos.repositories;

import java.util.List;

public interface PreguntaRepository {
    List<String> findPreguntaSPorExamenId(Long id);
}
