package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.GlobalExceptionHandler;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.models.AlunoModel;
import com.aep.cursoLivre.services.AlunoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
class AlunoControllerTest {

    private static final String PAYLOAD = """
            {
              "nome": "Maria Silva",
              "email": "maria@email.com",
              "dataNascimento": "2000-05-20",
              "cursosMatriculados": [10, 20]
            }
            """;

    @Mock
    private AlunoService alunoService;

    private MockMvc mockMvc;

    private AlunoModel aluno;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AlunoController(alunoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        aluno = new AlunoModel();
        aluno.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        aluno.setAlunoId(1L);
        aluno.setNome("Maria Silva");
        aluno.setEmail("maria@email.com");
        aluno.setDataNascimento(LocalDate.of(2000, 5, 20));
        aluno.setCursosMatriculados(new ArrayList<>(List.of(10L, 20L)));
    }

    @Test
    void deveListarAlunos() throws Exception {
        when(alunoService.listarAlunos()).thenReturn(List.of(aluno));

        mockMvc.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alunoId").value(1))
                .andExpect(jsonPath("$[0].nome").value("Maria Silva"))
                .andExpect(jsonPath("$[0].cursosMatriculados.length()").value(2));
    }

    @Test
    void deveBuscarAlunoPorAlunoId() throws Exception {
        when(alunoService.buscarPorAlunoId(1L)).thenReturn(aluno);

        mockMvc.perform(get("/api/alunos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunoId").value(1))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveRetornar404QuandoAlunoNaoExiste() throws Exception {
        when(alunoService.buscarPorAlunoId(99L))
                .thenThrow(new ResourceNotFoundException("Aluno nao encontrado: 99"));

        mockMvc.perform(get("/api/alunos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Aluno nao encontrado: 99"));
    }

    @Test
    void deveCriarAluno() throws Exception {
        when(alunoService.criarAluno(any(AlunoModel.class))).thenReturn(aluno);

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/alunos/1"))
                .andExpect(jsonPath("$.nome").value("Maria Silva"));

        ArgumentCaptor<AlunoModel> captor = ArgumentCaptor.forClass(AlunoModel.class);
        verify(alunoService).criarAluno(captor.capture());

        AlunoModel enviado = captor.getValue();
        assertThat(enviado.getNome()).isEqualTo("Maria Silva");
        assertThat(enviado.getEmail()).isEqualTo("maria@email.com");
        assertThat(enviado.getDataNascimento()).isEqualTo(LocalDate.of(2000, 5, 20));
        assertThat(enviado.getCursosMatriculados()).containsExactly(10L, 20L);
    }

    @Test
    void deveRetornar400QuandoCriarAlunoInvalido() throws Exception {
        when(alunoService.criarAluno(any(AlunoModel.class)))
                .thenThrow(new BadRequestException("Dados invalidos"));

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados invalidos"));
    }

    @Test
    void deveAtualizarAluno() throws Exception {
        when(alunoService.atualizarAluno(eq(1L), any(AlunoModel.class))).thenReturn(aluno);

        mockMvc.perform(put("/api/alunos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunoId").value(1));

        verify(alunoService).atualizarAluno(eq(1L), any(AlunoModel.class));
    }

    @Test
    void deveDeletarAluno() throws Exception {
        mockMvc.perform(delete("/api/alunos/1"))
                .andExpect(status().isNoContent());

        verify(alunoService).deletarAluno(1L);
    }

    @Test
    void deveMatricularAlunoEmCurso() throws Exception {
        mockMvc.perform(post("/api/alunos/1/cursos/10"))
                .andExpect(status().isNoContent());

        verify(alunoService).adicionarCurso(1L, 10L);
    }

    @Test
    void deveRetornar400QuandoAlunoJaMatriculado() throws Exception {
        doThrow(new BadRequestException("O aluno ja esta matriculado neste curso."))
                .when(alunoService).adicionarCurso(1L, 10L);

        mockMvc.perform(post("/api/alunos/1/cursos/10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRemoverMatriculaDoAluno() throws Exception {
        mockMvc.perform(delete("/api/alunos/1/cursos/10"))
                .andExpect(status().isNoContent());

        verify(alunoService).removerCurso(1L, 10L);
    }

    @Test
    void deveRetornar404QuandoMatriculaNaoExiste() throws Exception {
        doThrow(new ResourceNotFoundException("Matricula nao encontrada"))
                .when(alunoService).removerCurso(1L, 10L);

        mockMvc.perform(delete("/api/alunos/1/cursos/10"))
                .andExpect(status().isNotFound());
    }
}
