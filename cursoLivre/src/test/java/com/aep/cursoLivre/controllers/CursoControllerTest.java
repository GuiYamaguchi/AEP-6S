package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.GlobalExceptionHandler;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.services.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CursoControllerTest {

    private static final String ID = "64f1c2b2a1b2c3d4e5f6a7b8";

    private static final String PAYLOAD = """
            {
              "nome": "Spring Boot na Pratica",
              "instituicao": "Alura",
              "categoria": "Backend",
              "cargaHoraria": 60,
              "link": "http://exemplo.com/spring-boot",
              "professorId": 3
            }
            """;

    @Mock
    private CursoService cursoService;

    private MockMvc mockMvc;

    private CursoModel curso;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CursoController(cursoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        curso = new CursoModel();
        curso.setId(ID);
        curso.setCursoId(1L);
        curso.setNome("Spring Boot na Pratica");
        curso.setInstituicao("Alura");
        curso.setCategoria("Backend");
        curso.setCargaHoraria(60);
        curso.setLink("http://exemplo.com/spring-boot");
        curso.setProfessorId(3L);
    }

    @Test
    void deveListarTodosOsCursos() throws Exception {
        when(cursoService.listarCursos()).thenReturn(List.of(curso));

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Spring Boot na Pratica"))
                .andExpect(jsonPath("$[0].cursoId").value(1))
                .andExpect(jsonPath("$[0].professorId").value(3));

        verify(cursoService).listarCursos();
    }

    @Test
    void deveListarVazioQuandoNaoHouverCursos() throws Exception {
        when(cursoService.listarCursos()).thenReturn(List.of());

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveBuscarCursoPeloIdDoMongo() throws Exception {
        when(cursoService.buscarPorId(ID)).thenReturn(curso);

        mockMvc.perform(get("/api/cursos/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.nome").value("Spring Boot na Pratica"));
    }

    @Test
    void deveRetornar404QuandoIdNaoExiste() throws Exception {
        when(cursoService.buscarPorId("inexistente"))
                .thenThrow(new ResourceNotFoundException("Curso nao encontrado: inexistente"));

        mockMvc.perform(get("/api/cursos/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Curso nao encontrado: inexistente"));
    }

    @Test
    void deveBuscarCursoPeloCursoId() throws Exception {
        when(cursoService.buscarPorCursoId(1L)).thenReturn(curso);

        mockMvc.perform(get("/api/cursos/curso-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursoId").value(1))
                .andExpect(jsonPath("$.nome").value("Spring Boot na Pratica"));
    }

    @Test
    void deveRetornar404QuandoCursoIdNaoExiste() throws Exception {
        when(cursoService.buscarPorCursoId(99L))
                .thenThrow(new ResourceNotFoundException("Curso nao encontrado: 99"));

        mockMvc.perform(get("/api/cursos/curso-id/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Curso nao encontrado: 99"));
    }

    @Test
    void deveCriarCurso() throws Exception {
        when(cursoService.criarCurso(any(CursoModel.class))).thenReturn(curso);

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/cursos/" + ID))
                .andExpect(jsonPath("$.nome").value("Spring Boot na Pratica"));

        ArgumentCaptor<CursoModel> captor = ArgumentCaptor.forClass(CursoModel.class);
        verify(cursoService).criarCurso(captor.capture());

        CursoModel enviado = captor.getValue();
        assertThat(enviado.getNome()).isEqualTo("Spring Boot na Pratica");
        assertThat(enviado.getInstituicao()).isEqualTo("Alura");
        assertThat(enviado.getCategoria()).isEqualTo("Backend");
        assertThat(enviado.getCargaHoraria()).isEqualTo(60);
        assertThat(enviado.getLink()).isEqualTo("http://exemplo.com/spring-boot");
        assertThat(enviado.getProfessorId()).isEqualTo(3L);
    }

    @Test
    void deveRetornar400QuandoCriarCursoInvalido() throws Exception {
        when(cursoService.criarCurso(any(CursoModel.class)))
                .thenThrow(new BadRequestException("O nome do curso e obrigatorio."));

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O nome do curso e obrigatorio."));
    }

    @Test
    void deveRetornar500QuandoServicoFalhar() throws Exception {
        when(cursoService.listarCursos()).thenThrow(new ServiceException("Erro ao listar os cursos."));

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void deveAtualizarCurso() throws Exception {
        when(cursoService.atualizarCurso(eq(ID), any(CursoModel.class))).thenReturn(curso);

        mockMvc.perform(put("/api/cursos/" + ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.nome").value("Spring Boot na Pratica"));

        verify(cursoService).atualizarCurso(eq(ID), any(CursoModel.class));
    }

    @Test
    void deveRetornar404AoAtualizarCursoInexistente() throws Exception {
        when(cursoService.atualizarCurso(eq("inexistente"), any(CursoModel.class)))
                .thenThrow(new ResourceNotFoundException("Curso nao encontrado"));

        mockMvc.perform(put("/api/cursos/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarCurso() throws Exception {
        mockMvc.perform(delete("/api/cursos/" + ID))
                .andExpect(status().isNoContent());

        verify(cursoService).deletarCurso(ID);
    }
}
