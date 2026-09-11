package com.aep.cursoLivre.services;

import com.aep.cursoLivre.exceptions.BadRequestException;
import com.aep.cursoLivre.exceptions.ResourceNotFoundException;
import com.aep.cursoLivre.exceptions.ServiceException;
import com.aep.cursoLivre.models.CursoModel;
import com.aep.cursoLivre.repositories.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private GeradorSequencialService geradorSequencialService;

    private CursoService cursoService;

    private CursoModel curso;

    @BeforeEach
    void setUp() {
        cursoService = new CursoService(cursoRepository, geradorSequencialService);

        curso = new CursoModel();
        curso.setId("64f1c2b2a1b2c3d4e5f6a7b8");
        curso.setCursoId(1L);
        curso.setNome("Java para Iniciantes");
        curso.setInstituicao("Alura");
        curso.setCategoria("Programacao");
        curso.setCargaHoraria(40);
        curso.setLink("http://exemplo.com/curso");
    }

    @Nested
    class ListarCursos {

        @Test
        void deveRetornarListaDeCursosComSucesso() {
            when(cursoRepository.findAll()).thenReturn(List.of(curso));

            List<CursoModel> resultado = cursoService.listarCursos();

            assertThat(resultado).hasSize(1).containsExactly(curso);
            verify(cursoRepository, times(1)).findAll();
        }

        @Test
        void deveLancarServiceExceptionQuandoRepositorioFalhar() {
            when(cursoRepository.findAll())
                    .thenThrow(new DataAccessResourceFailureException("falha de conexao"));

            assertThatThrownBy(() -> cursoService.listarCursos())
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao buscar a lista de cursos");
        }
    }

    @Nested
    class BuscarPorId {

        @Test
        void deveRetornarCursoQuandoIdExiste() {
            when(cursoRepository.findById("abc")).thenReturn(Optional.of(curso));

            CursoModel resultado = cursoService.buscarPorId("abc");

            assertThat(resultado).isEqualTo(curso);
        }

        @Test
        void deveLancarBadRequestQuandoIdForNulo() {
            assertThatThrownBy(() -> cursoService.buscarPorId(null))
                    .isInstanceOf(BadRequestException.class);
            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarBadRequestQuandoIdForVazio() {
            assertThatThrownBy(() -> cursoService.buscarPorId("   "))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        void deveLancarResourceNotFoundQuandoIdNaoExiste() {
            when(cursoRepository.findById("xyz")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cursoService.buscarPorId("xyz"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("xyz");
        }
    }

    @Nested
    class BuscarPorCursoId {

        @Test
        void deveRetornarCursoQuandoCursoIdExiste() {
            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.of(curso));

            CursoModel resultado = cursoService.buscarPorCursoId(1L);

            assertThat(resultado).isEqualTo(curso);
        }

        @Test
        void deveLancarBadRequestQuandoCursoIdForNulo() {
            assertThatThrownBy(() -> cursoService.buscarPorCursoId(null))
                    .isInstanceOf(BadRequestException.class);
            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoIdNaoExiste() {
            when(cursoRepository.findByCursoId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cursoService.buscarPorCursoId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    class CriarCurso {

        @Test
        void deveCriarCursoComSucesso() {
            CursoModel novoCurso = new CursoModel();
            novoCurso.setNome("Spring Boot Avancado");

            when(geradorSequencialService.generateSequence("curso_sequence")).thenReturn(2L);
            when(cursoRepository.save(any(CursoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            CursoModel resultado = cursoService.criarCurso(novoCurso);

            assertThat(resultado.getCursoId()).isEqualTo(2L);
            verify(geradorSequencialService).generateSequence("curso_sequence");
            verify(cursoRepository).save(novoCurso);
        }

        @Test
        void deveLancarBadRequestQuandoCursoForNulo() {
            assertThatThrownBy(() -> cursoService.criarCurso(null))
                    .isInstanceOf(BadRequestException.class);
            verifyNoInteractions(cursoRepository, geradorSequencialService);
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(geradorSequencialService.generateSequence(anyString())).thenReturn(3L);
            when(cursoRepository.save(any(CursoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("erro ao salvar"));

            assertThatThrownBy(() -> cursoService.criarCurso(curso))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao salvar o curso");
        }
    }

    @Nested
    class AtualizarCurso {

        @Test
        void deveAtualizarCursoComSucesso() {
            CursoModel dadosAtualizados = new CursoModel();
            dadosAtualizados.setNome("Novo Nome");
            dadosAtualizados.setInstituicao("Nova Instituicao");
            dadosAtualizados.setCategoria("Nova Categoria");
            dadosAtualizados.setCargaHoraria(80);
            dadosAtualizados.setLink("http://novo-link.com");

            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.of(curso));
            when(cursoRepository.save(any(CursoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            CursoModel resultado = cursoService.atualizarCurso(1L, dadosAtualizados);

            assertThat(resultado.getNome()).isEqualTo("Novo Nome");
            assertThat(resultado.getInstituicao()).isEqualTo("Nova Instituicao");
            assertThat(resultado.getCategoria()).isEqualTo("Nova Categoria");
            assertThat(resultado.getCargaHoraria()).isEqualTo(80);
            assertThat(resultado.getLink()).isEqualTo("http://novo-link.com");
        }

        @Test
        void deveLancarBadRequestQuandoDadosAtualizadosForemNulos() {
            assertThatThrownBy(() -> cursoService.atualizarCurso(1L, null))
                    .isInstanceOf(BadRequestException.class);
            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoNaoExistir() {
            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cursoService.atualizarCurso(1L, curso))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.of(curso));
            when(cursoRepository.save(any(CursoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("erro ao atualizar"));

            assertThatThrownBy(() -> cursoService.atualizarCurso(1L, curso))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao atualizar o curso");
        }
    }

    @Nested
    class DeletarCurso {

        @Test
        void deveDeletarCursoComSucesso() {
            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.of(curso));

            cursoService.deletarCurso(1L);

            verify(cursoRepository).delete(curso);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoNaoExistir() {
            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cursoService.deletarCurso(1L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(cursoRepository, never()).delete(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoDeletarFalhar() {
            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.of(curso));
            doThrow(new DataAccessResourceFailureException("erro ao deletar"))
                    .when(cursoRepository).delete(curso);

            assertThatThrownBy(() -> cursoService.deletarCurso(1L))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao deletar o curso");
        }
    }
}
