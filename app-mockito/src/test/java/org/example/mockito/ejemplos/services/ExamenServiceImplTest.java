package org.example.mockito.ejemplos.services;

import org.example.mockito.ejemplos.models.Examen;
import org.example.mockito.ejemplos.repositories.ExamenRepository;
import org.example.mockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExamenServiceImplTest {

    @Test
    void findExamenPorNombre() {
        ExamenRepository repository = new ExamenRepositoryImpl();
        ExamenService service = new ExamenServiceImpl(repository);
        Examen examen = service.findExamenPorNombre("Matematicas");
    }
}