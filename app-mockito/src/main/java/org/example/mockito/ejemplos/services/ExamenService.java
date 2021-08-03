package org.example.mockito.ejemplos.services;

import org.example.mockito.ejemplos.models.Examen;

public interface ExamenService {
    Examen findExamenPorNombre(String nombre);
}
