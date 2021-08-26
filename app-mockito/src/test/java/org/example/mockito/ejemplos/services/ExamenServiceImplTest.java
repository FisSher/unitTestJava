package org.example.mockito.ejemplos.services;

import org.example.mockito.ejemplos.Datos;
import org.example.mockito.ejemplos.models.Examen;
import org.example.mockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.example.mockito.ejemplos.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
        //->lo mismo que MockitoAnnotations.openMocks(this) para la injeccion de dep.
class ExamenServiceImplTest {

    @Mock
    ExamenRepositoryImpl repository;

    @Mock
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;


//    @BeforeEach
//    void setUp() {
//        //I can use tags for this
//        repository = mock(ExamenRepository.class);
//        preguntaRepository = mock(PreguntaRepository.class);
//        service = new ExamenServiceImpl(repository,preguntaRepository);
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    void findExamenPorNombre() {

        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");


        assertAll(
                () -> assertTrue(examen.isPresent()),
                () -> assertEquals(5L, examen.orElseThrow().getId()),
                () -> assertEquals("Matematicas", examen.get().getNombre())
        );
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertTrue(examen.isPresent());
    }

    @Test
    void testPreguntasExamen() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertAll(
                () -> assertEquals(5, examen.getPreguntas().size()),
                () -> assertTrue(examen.getPreguntas().contains("Aritmetica"))
        );
    }

    @Test
    void testPreguntasExamenVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertAll(
                () -> assertEquals(5, examen.getPreguntas().size()),
                () -> assertTrue(examen.getPreguntas().contains("Aritmetica"))
        );
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testNoExisteExamenVerify() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertNull(examen);
        verify(repository).findAll();
    }

    @Test
    void testGuardarExamen() {
        //GIVEN
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });
        // WHEN
        Examen examen = service.guardar(newExamen);

        //THEN
        assertAll(
                () -> assertNotNull(examen.getId()),
                () -> assertEquals(8L, examen.getId()),
                () -> assertEquals("Fisica", examen.getNombre())
        );
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias((anyList()));

    }

    @Test
    void testManejoExceptions() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matematicas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();

        //Multiple ways to do the same.
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg >= 5L));
        verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));

    }


    //Will fail
    @Test
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVO);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematicas");

        // ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class); -> can be done by annotations.
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertAll(
                () -> assertEquals(5L, captor.getValue())
        );
    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(org.example.mockito.ejemplos.Datos.PREGUNTAS);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Datos.PREGUNTAS : null;
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Geometria"));
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(anyLong());
    }


    @Test
    void testDoAnswerGuardarExamen() {
        //GIVEN
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        doAnswer(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).guardar(any(Examen.class));

        // WHEN
        Examen examen = service.guardar(newExamen);

        //THEN
        assertAll(
                () -> assertNotNull(examen.getId()),
                () -> assertEquals(8L, examen.getId()),
                () -> assertEquals("Fisica", examen.getNombre())
        );
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias((anyList()));
    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    //ArgumentMatchers2
    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "personalized error message, " + argument +
                    " argument must be greater than 0";
        }
    }
}