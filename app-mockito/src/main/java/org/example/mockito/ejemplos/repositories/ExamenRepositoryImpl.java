package org.example.mockito.ejemplos.repositories;

import org.example.mockito.ejemplos.Datos;
import org.example.mockito.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements ExamenRepository{
    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return Datos.EXAMEN;
    }

    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
        try{
            System.out.println("ExamenRepositoryOtro");
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Datos.EXAMENES;
    }
}
