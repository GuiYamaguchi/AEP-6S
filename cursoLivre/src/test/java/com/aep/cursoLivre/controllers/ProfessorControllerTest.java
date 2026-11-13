package com.aep.cursoLivre.controllers;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.GlobalExceptionHandler;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.models.FormacaoModel;
import com.aep.cursoLivre.models.ProfessorModel;
import com.aep.cursoLivre.services.ProfessorService;
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
class ProfessorControllerTest {

    private static final String PAYLOAD = """
            {
              "nome": "Carlos Souza",
              "email": "carlos@email.com",
              "especialidade": "Backend",
              "formacoes": [
                {
                  "instituicao": "USP",
                  "curso": "Ciencia da Computacao",
                  "nivel": "Graduacao",
                  "anoConclusao": 2010
                }
              ]
            }
            """;

    @Mock
    private ProfessorService professorService;

    private MockMvc mockMvc;

    private ProfessorModel professor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProfessorController(professorService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        professor = new ProfessorModel();
        professor.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        professor.setProfessorId(3L);
        professor.setNome("Carlos Souza");
        professor.setEmail("carlos@email.com");
        professor.setEspecialidade("Backend");
        professor.setFormacoes(List.of(
                new FormacaoModel("USP", "Ciencia da Computacao", "Graduacao", 2010)));
    }

    @Test
    void deveListarProfessores() throws Exception {
        when(professorService.listarProfessores()).thenReturn(List.of(professor));

        mockMvc.perform(get("/api/professores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].professorId").value(3))
                .andExpect(jsonPath("$[0].nome").value("Carlos Souza"))
                .andExpect(jsonPath("$[0].formacoes[0].instituicao").value("USP"));
    }

    @Test
    void deveBuscarProfessorPorProfessorId() throws Exception {
        when(professorService.buscarPorProfessorId(3L)).thenReturn(professor);

        mockMvc.perform(get("/api/professores/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.professorId").value(3))
                .andExpect(jsonPath("$.especialidade").value("Backend"));
    }

    @Test
    void deveRetornar404QuandoProfessorNaoExiste() throws Exception {
        when(professorService.buscarPorProfessorId(99L))
                .thenThrow(new ResourceNotFoundException("Professor nao encontrado: 99"));

        mockMvc.perform(get("/api/professores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Professor nao encontrado: 99"));
    }

    @Test
    void deveCriarProfessor() throws Exception {
        when(professorService.criarProfessor(any(ProfessorModel.class))).thenReturn(professor);

        mockMvc.perform(post("/api/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/professores/3"))
                .andExpect(jsonPath("$.nome").value("Carlos Souza"));

        ArgumentCaptor<ProfessorModel> captor = ArgumentCaptor.forClass(ProfessorModel.class);
        verify(professorService).criarProfessor(captor.capture());

        ProfessorModel enviado = captor.getValue();
        assertThat(enviado.getNome()).isEqualTo("Carlos Souza");
        assertThat(enviado.getEmail()).isEqualTo("carlos@email.com");
        assertThat(enviado.getEspecialidade()).isEqualTo("Backend");
        assertThat(enviado.getFormacoes()).hasSize(1);
        assertThat(enviado.getFormacoes().get(0).getAnoConclusao()).isEqualTo(2010);
    }

    @Test
    void deveRetornar400QuandoCriarProfessorInvalido() throws Exception {
        when(professorService.criarProfessor(any(ProfessorModel.class)))
                .thenThrow(new BadRequestException("Dados invalidos"));

        mockMvc.perform(post("/api/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarProfessor() throws Exception {
        when(professorService.atualizarProfessor(eq(3L), any(ProfessorModel.class))).thenReturn(professor);

        mockMvc.perform(put("/api/professores/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAYLOAD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.professorId").value(3));

        verify(professorService).atualizarProfessor(eq(3L), any(ProfessorModel.class));
    }

    @Test
    void deveDeletarProfessor() throws Exception {
        mockMvc.perform(delete("/api/professores/3"))
                .andExpect(status().isNoContent());

        verify(professorService).deletarProfessor(3L);
    }

    @Test
    void deveRetornar400AoDeletarProfessorComCursos() throws Exception {
        doThrow(new BadRequestException("Professor possui cursos"))
                .when(professorService).deletarProfessor(3L);

        mockMvc.perform(delete("/api/professores/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Professor possui cursos"));
    }
}
