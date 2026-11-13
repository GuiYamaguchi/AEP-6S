package com.aep.cursoLivre.services;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.AlunoModel;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.repositories.AlunoRepository;
import com.aep.cursoLivre.repositories.CursoRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private GeradorSequencialService sequencialService;

    private AlunoService alunoService;

    private AlunoModel aluno;

    @BeforeEach
    void setUp() {
        alunoService = new AlunoService(alunoRepository, cursoRepository, sequencialService);

        aluno = new AlunoModel();
        aluno.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        aluno.setAlunoId(1L);
        aluno.setNome("Maria Silva");
        aluno.setEmail("maria@email.com");
        aluno.setDataNascimento(LocalDate.of(2000, 5, 20));
        aluno.setCursosMatriculados(new ArrayList<>(List.of(10L)));
    }

    private AlunoModel novoAluno(List<Long> cursos) {
        AlunoModel novo = new AlunoModel();
        novo.setNome("Joao");
        novo.setEmail("joao@email.com");
        novo.setDataNascimento(LocalDate.of(1995, 3, 10));
        novo.setCursosMatriculados(cursos);
        return novo;
    }

    @Nested
    class ListarAlunos {

        @Test
        void deveRetornarTodosOsAlunos() {
            when(alunoRepository.findAll()).thenReturn(List.of(aluno));

            assertThat(alunoService.listarAlunos()).containsExactly(aluno);
        }

        @Test
        void deveLancarServiceExceptionQuandoRepositorioFalhar() {
            when(alunoRepository.findAll())
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> alunoService.listarAlunos())
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao listar alunos")
                    .hasCauseInstanceOf(DataAccessResourceFailureException.class);
        }
    }

    @Nested
    class BuscarPorAlunoId {

        @Test
        void deveRetornarAlunoQuandoExiste() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));

            assertThat(alunoService.buscarPorAlunoId(1L)).isSameAs(aluno);
        }

        @Test
        void deveLancarBadRequestQuandoAlunoIdForNulo() {
            assertThatThrownBy(() -> alunoService.buscarPorAlunoId(null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(alunoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoNaoExiste() {
            when(alunoRepository.findByAlunoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.buscarPorAlunoId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    class BuscarPorId {

        @Test
        void deveRetornarAlunoQuandoExiste() {
            when(alunoRepository.findById("abc")).thenReturn(Optional.of(aluno));

            assertThat(alunoService.buscarPorId("abc")).isSameAs(aluno);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void deveLancarBadRequestQuandoIdForInvalido(String id) {
            assertThatThrownBy(() -> alunoService.buscarPorId(id))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(alunoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoNaoExiste() {
            when(alunoRepository.findById("xyz")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.buscarPorId("xyz"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("xyz");
        }
    }

    @Nested
    class CriarAluno {

        @Test
        void deveCriarAlunoComCursosValidosGerandoAlunoId() {
            when(cursoRepository.findByCursoId(anyLong())).thenReturn(Optional.of(new CursoModel()));
            when(sequencialService.generateSequence("aluno_sequence")).thenReturn(5L);
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            AlunoModel resultado = alunoService.criarAluno(novoAluno(new ArrayList<>(List.of(10L, 20L))));

            assertThat(resultado.getAlunoId()).isEqualTo(5L);
            assertThat(resultado.getCursosMatriculados()).containsExactly(10L, 20L);
            verify(cursoRepository).findByCursoId(10L);
            verify(cursoRepository).findByCursoId(20L);
        }

        @Test
        void deveCriarListaVaziaQuandoCursosForemNulos() {
            when(sequencialService.generateSequence("aluno_sequence")).thenReturn(6L);
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            AlunoModel resultado = alunoService.criarAluno(novoAluno(null));

            assertThat(resultado.getAlunoId()).isEqualTo(6L);
            assertThat(resultado.getCursosMatriculados()).isNotNull().isEmpty();
            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarBadRequestQuandoAlunoForNulo() {
            assertThatThrownBy(() -> alunoService.criarAluno(null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(alunoRepository, cursoRepository, sequencialService);
        }

        @Test
        void deveLancarBadRequestQuandoListaDeCursosTiverIdNulo() {
            List<Long> cursos = new ArrayList<>();
            cursos.add(null);

            assertThatThrownBy(() -> alunoService.criarAluno(novoAluno(cursos)))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("nulos");

            verifyNoInteractions(alunoRepository, sequencialService);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoNaoExistir() {
            when(cursoRepository.findByCursoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.criarAluno(novoAluno(new ArrayList<>(List.of(99L)))))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verifyNoInteractions(alunoRepository, sequencialService);
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(sequencialService.generateSequence("aluno_sequence")).thenReturn(7L);
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> alunoService.criarAluno(novoAluno(null)))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao salvar aluno");
        }
    }

    @Nested
    class AtualizarAluno {

        @Test
        void deveAtualizarDadosDoAluno() {
            when(cursoRepository.findByCursoId(20L)).thenReturn(Optional.of(new CursoModel()));
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            AlunoModel dados = novoAluno(new ArrayList<>(List.of(20L)));

            AlunoModel resultado = alunoService.atualizarAluno(1L, dados);

            assertThat(resultado).isSameAs(aluno);
            assertThat(resultado.getNome()).isEqualTo("Joao");
            assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
            assertThat(resultado.getDataNascimento()).isEqualTo(LocalDate.of(1995, 3, 10));
            assertThat(resultado.getCursosMatriculados()).containsExactly(20L);
            assertThat(resultado.getAlunoId()).isEqualTo(1L);
        }

        @Test
        void deveSubstituirCursosNulosPorListaVazia() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            AlunoModel resultado = alunoService.atualizarAluno(1L, novoAluno(null));

            assertThat(resultado.getCursosMatriculados()).isNotNull().isEmpty();
        }

        @Test
        void deveLancarBadRequestQuandoDadosForemNulos() {
            assertThatThrownBy(() -> alunoService.atualizarAluno(1L, null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(alunoRepository, cursoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoInformadoNaoExistir() {
            when(cursoRepository.findByCursoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    alunoService.atualizarAluno(1L, novoAluno(new ArrayList<>(List.of(99L)))))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(alunoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoAlunoNaoExistir() {
            when(alunoRepository.findByAlunoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.atualizarAluno(99L, novoAluno(null)))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(alunoRepository, never()).save(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> alunoService.atualizarAluno(1L, novoAluno(null)))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao atualizar aluno");
        }
    }

    @Nested
    class AdicionarCurso {

        @Test
        void deveMatricularAlunoNoCurso() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(cursoRepository.findByCursoId(20L)).thenReturn(Optional.of(new CursoModel()));

            alunoService.adicionarCurso(1L, 20L);

            assertThat(aluno.getCursosMatriculados()).containsExactly(10L, 20L);
            verify(alunoRepository).save(aluno);
        }

        @Test
        void deveCriarListaQuandoAlunoNaoTiverCursos() {
            aluno.setCursosMatriculados(null);
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(cursoRepository.findByCursoId(20L)).thenReturn(Optional.of(new CursoModel()));

            alunoService.adicionarCurso(1L, 20L);

            assertThat(aluno.getCursosMatriculados()).containsExactly(20L);
            verify(alunoRepository).save(aluno);
        }

        @Test
        void deveLancarBadRequestQuandoCursoIdForNulo() {
            assertThatThrownBy(() -> alunoService.adicionarCurso(1L, null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(alunoRepository, cursoRepository);
        }

        @Test
        void deveLancarBadRequestQuandoAlunoIdForNulo() {
            assertThatThrownBy(() -> alunoService.adicionarCurso(null, 20L))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        void deveLancarResourceNotFoundQuandoAlunoNaoExistir() {
            when(alunoRepository.findByAlunoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.adicionarCurso(99L, 20L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoNaoExistir() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(cursoRepository.findByCursoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.adicionarCurso(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(alunoRepository, never()).save(any());
        }

        @Test
        void deveLancarBadRequestQuandoAlunoJaEstiverMatriculado() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(cursoRepository.findByCursoId(10L)).thenReturn(Optional.of(new CursoModel()));

            assertThatThrownBy(() -> alunoService.adicionarCurso(1L, 10L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("matriculado");

            verify(alunoRepository, never()).save(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(cursoRepository.findByCursoId(20L)).thenReturn(Optional.of(new CursoModel()));
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> alunoService.adicionarCurso(1L, 20L))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao matricular");
        }
    }

    @Nested
    class RemoverCurso {

        @Test
        void deveRemoverMatriculaDoAluno() {
            aluno.setCursosMatriculados(new ArrayList<>(List.of(10L, 20L)));
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));

            alunoService.removerCurso(1L, 10L);

            assertThat(aluno.getCursosMatriculados()).containsExactly(20L);
            verify(alunoRepository).save(aluno);
        }

        @Test
        void deveLancarBadRequestQuandoCursoIdForNulo() {
            assertThatThrownBy(() -> alunoService.removerCurso(1L, null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(alunoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoAlunoNaoExistir() {
            when(alunoRepository.findByAlunoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.removerCurso(99L, 10L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void deveLancarResourceNotFoundQuandoAlunoNaoTiverCursos() {
            aluno.setCursosMatriculados(null);
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));

            assertThatThrownBy(() -> alunoService.removerCurso(1L, 10L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Matr");

            verify(alunoRepository, never()).save(any());
        }

        @Test
        void deveLancarResourceNotFoundQuandoNaoEstiverMatriculadoNoCurso() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));

            assertThatThrownBy(() -> alunoService.removerCurso(1L, 999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(alunoRepository, never()).save(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            when(alunoRepository.save(any(AlunoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> alunoService.removerCurso(1L, 10L))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao remover");
        }
    }

    @Nested
    class DeletarAluno {

        @Test
        void deveDeletarAluno() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));

            alunoService.deletarAluno(1L);

            verify(alunoRepository).delete(aluno);
        }

        @Test
        void deveLancarResourceNotFoundQuandoAlunoNaoExistir() {
            when(alunoRepository.findByAlunoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> alunoService.deletarAluno(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(alunoRepository, never()).delete(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoDeletarFalhar() {
            when(alunoRepository.findByAlunoId(1L)).thenReturn(Optional.of(aluno));
            doThrow(new DataAccessResourceFailureException("falha"))
                    .when(alunoRepository).delete(aluno);

            assertThatThrownBy(() -> alunoService.deletarAluno(1L))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao deletar aluno");
        }
    }
}
