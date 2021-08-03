package org.example.mockito.ejemplos.repositories;

import org.example.mockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();
}
