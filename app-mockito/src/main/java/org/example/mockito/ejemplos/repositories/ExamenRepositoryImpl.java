package org.example.mockito.ejemplos.repositories;

import org.example.mockito.ejemplos.models.Examen;

import java.util.Arrays;
import java.util.List;

public class ExamenRepositoryImpl implements ExamenRepository{
    @Override
    public List<Examen> findAll() {
        return Arrays.asList(new Examen(5L,"Matematicas"),new Examen(6L,"Lenguaje"),
                new Examen(7L,"Historia"));
    }
}
