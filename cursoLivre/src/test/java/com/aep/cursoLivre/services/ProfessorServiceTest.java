package com.aep.cursoLivre.services;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.models.FormacaoModel;
import com.aep.cursoLivre.models.ProfessorModel;
import com.aep.cursoLivre.repositories.CursoRepository;
import com.aep.cursoLivre.repositories.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private GeradorSequencialService sequencialService;

    private ProfessorService professorService;

    private ProfessorModel professor;

    @BeforeEach
    void setUp() {
        professorService = new ProfessorService(professorRepository, cursoRepository, sequencialService);

        professor = new ProfessorModel();
        professor.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        professor.setProfessorId(3L);
        professor.setNome("Carlos Souza");
        professor.setEmail("carlos@email.com");
        professor.setEspecialidade("Backend");
        professor.setFormacoes(List.of(new FormacaoModel("USP", "Computacao", "Graduacao", 2010)));
    }

    private ProfessorModel novosDados() {
        ProfessorModel dados = new ProfessorModel();
        dados.setNome("Ana Lima");
        dados.setEmail("ana@email.com");
        dados.setEspecialidade("Frontend");
        dados.setFormacoes(List.of(new FormacaoModel("UNICAMP", "Design", "Mestrado", 2015)));
        return dados;
    }

    @Nested
    class ListarProfessores {

        @Test
        void deveRetornarTodosOsProfessores() {
            when(professorRepository.findAll()).thenReturn(List.of(professor));

            assertThat(professorService.listarProfessores()).containsExactly(professor);
        }

        @Test
        void deveLancarServiceExceptionQuandoRepositorioFalhar() {
            when(professorRepository.findAll())
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> professorService.listarProfessores())
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao listar professores");
        }
    }

    @Nested
    class BuscarPorProfessorId {

        @Test
        void deveRetornarProfessorQuandoExiste() {
            when(professorRepository.findByProfessorId(3L)).thenReturn(Optional.of(professor));

            assertThat(professorService.buscarPorProfessorId(3L)).isSameAs(professor);
        }

        @Test
        void deveLancarBadRequestQuandoIdForNulo() {
            assertThatThrownBy(() -> professorService.buscarPorProfessorId(null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(professorRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoNaoExiste() {
            when(professorRepository.findByProfessorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> professorService.buscarPorProfessorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    class BuscarPorId {

        @Test
        void deveRetornarProfessorQuandoExiste() {
            when(professorRepository.findById("abc")).thenReturn(Optional.of(professor));

            assertThat(professorService.buscarPorId("abc")).isSameAs(professor);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void deveLancarBadRequestQuandoIdForInvalido(String id) {
            assertThatThrownBy(() -> professorService.buscarPorId(id))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(professorRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoNaoExiste() {
            when(professorRepository.findById("xyz")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> professorService.buscarPorId("xyz"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("xyz");
        }
    }

    @Nested
    class CriarProfessor {

        @Test
        void deveCriarProfessorGerandoProfessorId() {
            when(sequencialService.generateSequence("professor_sequence")).thenReturn(8L);
            when(professorRepository.save(any(ProfessorModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ProfessorModel resultado = professorService.criarProfessor(novosDados());

            assertThat(resultado.getProfessorId()).isEqualTo(8L);
            assertThat(resultado.getNome()).isEqualTo("Ana Lima");
        }

        @Test
        void deveLancarBadRequestQuandoProfessorForNulo() {
            assertThatThrownBy(() -> professorService.criarProfessor(null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(professorRepository, sequencialService);
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(sequencialService.generateSequence("professor_sequence")).thenReturn(9L);
            when(professorRepository.save(any(ProfessorModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> professorService.criarProfessor(novosDados()))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao salvar professor");
        }
    }

    @Nested
    class AtualizarProfessor {

        @Test
        void deveAtualizarDadosDoProfessor() {
            when(professorRepository.findByProfessorId(3L)).thenReturn(Optional.of(professor));
            when(professorRepository.save(any(ProfessorModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ProfessorModel resultado = professorService.atualizarProfessor(3L, novosDados());

            assertThat(resultado).isSameAs(professor);
            assertThat(resultado.getNome()).isEqualTo("Ana Lima");
            assertThat(resultado.getEmail()).isEqualTo("ana@email.com");
            assertThat(resultado.getEspecialidade()).isEqualTo("Frontend");
            assertThat(resultado.getFormacoes()).hasSize(1);
            assertThat(resultado.getFormacoes().get(0).getInstituicao()).isEqualTo("UNICAMP");
            assertThat(resultado.getProfessorId()).isEqualTo(3L);
        }

        @Test
        void deveLancarBadRequestQuandoDadosForemNulos() {
            assertThatThrownBy(() -> professorService.atualizarProfessor(3L, null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(professorRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoProfessorNaoExistir() {
            when(professorRepository.findByProfessorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> professorService.atualizarProfessor(99L, novosDados()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(professorRepository, never()).save(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(professorRepository.findByProfessorId(3L)).thenReturn(Optional.of(professor));
            when(professorRepository.save(any(ProfessorModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> professorService.atualizarProfessor(3L, novosDados()))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao atualizar professor");
        }
    }

    @Nested
    class DeletarProfessor {

        @Test
        void deveDeletarProfessorSemCursos() {
            when(professorRepository.findByProfessorId(3L)).thenReturn(Optional.of(professor));
            when(cursoRepository.findByProfessorId(3L)).thenReturn(List.of());

            professorService.deletarProfessor(3L);

            verify(professorRepository).delete(professor);
        }

        @Test
        void deveLancarResourceNotFoundQuandoProfessorNaoExistir() {
            when(professorRepository.findByProfessorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> professorService.deletarProfessor(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(cursoRepository);
            verify(professorRepository, never()).delete(any());
        }

        @Test
        void deveLancarBadRequestQuandoProfessorPossuirCursos() {
            when(professorRepository.findByProfessorId(3L)).thenReturn(Optional.of(professor));
            when(cursoRepository.findByProfessorId(3L)).thenReturn(List.of(new CursoModel()));

            assertThatThrownBy(() -> professorService.deletarProfessor(3L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("cursos");

            verify(professorRepository, never()).delete(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoDeletarFalhar() {
            when(professorRepository.findByProfessorId(3L)).thenReturn(Optional.of(professor));
            when(cursoRepository.findByProfessorId(3L)).thenReturn(List.of());
            doThrow(new DataAccessResourceFailureException("falha"))
                    .when(professorRepository).delete(professor);

            assertThatThrownBy(() -> professorService.deletarProfessor(3L))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao deletar professor");
        }
    }

    /**
     * validarProfessor e um metodo privado que hoje nao e chamado por nenhum metodo publico.
     * Ele e exercitado por reflexao para que seu comportamento fique garantido caso seja reaproveitado
     * (por exemplo, para validar o professorId ao criar um curso).
     */
    @Nested
    class ValidarProfessorPrivado {

        private void invocar(Long professorId) throws Throwable {
            Method metodo = ProfessorService.class.getDeclaredMethod("validarProfessor", Long.class);
            metodo.setAccessible(true);
            try {
                metodo.invoke(professorService, professorId);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        @Test
        void naoDeveLancarExcecaoQuandoProfessorExiste() {
            when(professorRepository.findByProfessorId(3L)).thenReturn(Optional.of(professor));

            assertThatCode(() -> invocar(3L)).doesNotThrowAnyException();
        }

        @Test
        void deveLancarBadRequestQuandoIdForNulo() {
            assertThatThrownBy(() -> invocar(null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(professorRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoProfessorNaoExistir() {
            when(professorRepository.findByProfessorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> invocar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
