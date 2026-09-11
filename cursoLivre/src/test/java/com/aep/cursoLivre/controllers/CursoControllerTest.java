package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.exceptions.GlobalExceptionHandler;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.services.CursoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CursoControllerTest {

    @Mock
    private CursoService cursoService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CursoModel curso;

    @BeforeEach
    void setUp() {
        CursoController controller = new CursoController(cursoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        curso = new CursoModel();
        curso.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        curso.setCursoId(1L);
        curso.setNome("Spring Boot na Pratica");
        curso.setInstituicao("Alura");
        curso.setCategoria("Backend");
        curso.setCargaHoraria(60);
        curso.setLink("http://exemplo.com/spring-boot");
    }

    @Test
    void deveListarTodosOsCursos() throws Exception {
        when(cursoService.listarCursos()).thenReturn(List.of(curso));

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Spring Boot na Pratica"))
                .andExpect(jsonPath("$[0].cursoId").value(1));

        verify(cursoService).listarCursos();
    }

    @Test
    void deveBuscarCursoPorCursoId() throws Exception {
        when(cursoService.buscarPorCursoId(1L)).thenReturn(curso);

        mockMvc.perform(get("/api/cursos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Spring Boot na Pratica"));
    }

    @Test
    void deveRetornar404QuandoCursoIdNaoExiste() throws Exception {
        when(cursoService.buscarPorCursoId(anyLong()))
                .thenThrow(new ResourceNotFoundException("Curso nao encontrado com o cursoId: 99"));

        mockMvc.perform(get("/api/cursos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Curso nao encontrado com o cursoId: 99"));
    }

    @Test
    void deveBuscarCursoPorId() throws Exception {
        when(cursoService.buscarPorId("64f1c2b2a1b2c3d4e5f6a7b8")).thenReturn(curso);

        mockMvc.perform(get("/api/cursos/mongo/64f1c2b2a1b2c3d4e5f6a7b8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("64f1c2b2a1b2c3d4e5f6a7b8"));
    }

    @Test
    void deveCriarCurso() throws Exception {
        when(cursoService.criarCurso(any(CursoModel.class))).thenReturn(curso);

        String payload = objectMapper.writeValueAsString(
                new Object() {
                    public final String nome = "Spring Boot na Pratica";
                    public final String instituicao = "Alura";
                    public final String categoria = "Backend";
                    public final int cargaHoraria = 60;
                    public final String link = "http://exemplo.com/spring-boot";
                }
        );

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/cursos/" + curso.getId()))
                .andExpect(jsonPath("$.nome").value("Spring Boot na Pratica"));

        verify(cursoService).criarCurso(any(CursoModel.class));
    }

    @Test
    void deveAtualizarCurso() throws Exception {
        when(cursoService.atualizarCurso(eq(1L), any(CursoModel.class))).thenReturn(curso);

        String payload = objectMapper.writeValueAsString(
                new Object() {
                    public final String nome = "Spring Boot na Pratica - Atualizado";
                    public final String instituicao = "Alura";
                    public final String categoria = "Backend";
                    public final int cargaHoraria = 60;
                    public final String link = "http://exemplo.com/spring-boot";
                }
        );

        mockMvc.perform(put("/api/cursos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        verify(cursoService).atualizarCurso(eq(1L), any(CursoModel.class));
    }

    @Test
    void deveDeletarCurso() throws Exception {
        doNothing().when(cursoService).deletarCurso(1L);

        mockMvc.perform(delete("/api/cursos/1"))
                .andExpect(status().isNoContent());

        verify(cursoService).deletarCurso(1L);
    }
}
