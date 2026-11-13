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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    private static final String ID = "64f1c2b2a1b2c3d4e5f6a7b8";

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private GeradorSequencialService geradorSequencialService;

    private CursoService cursoService;

    private CursoModel curso;

    @BeforeEach
    void setUp() {
        cursoService = new CursoService(cursoRepository, geradorSequencialService);
        curso = cursoValido();
    }

    private static CursoModel cursoValido() {
        CursoModel c = new CursoModel();
        c.setId(ID);
        c.setCursoId(1L);
        c.setNome("Java para Iniciantes");
        c.setInstituicao("Alura");
        c.setCategoria("Programacao");
        c.setCargaHoraria(40);
        c.setLink("http://exemplo.com/curso");
        c.setProfessorId(5L);
        return c;
    }

    /** Cada argumento: descricao, alteracao que invalida o curso, trecho esperado na mensagem. */
    static Stream<Arguments> cursosInvalidos() {
        return Stream.of(
                invalido("nome nulo", c -> c.setNome(null), "nome do curso"),
                invalido("nome vazio", c -> c.setNome(""), "nome do curso"),
                invalido("nome em branco", c -> c.setNome("   "), "nome do curso"),
                invalido("instituicao nula", c -> c.setInstituicao(null), "institui"),
                invalido("instituicao vazia", c -> c.setInstituicao(" "), "institui"),
                invalido("categoria nula", c -> c.setCategoria(null), "categoria"),
                invalido("categoria vazia", c -> c.setCategoria(" "), "categoria"),
                invalido("carga horaria zero", c -> c.setCargaHoraria(0), "carga hor"),
                invalido("carga horaria negativa", c -> c.setCargaHoraria(-10), "carga hor"),
                invalido("link nulo", c -> c.setLink(null), "link"),
                invalido("link vazio", c -> c.setLink(" "), "link")
        );
    }

    private static Arguments invalido(String descricao, Consumer<CursoModel> alteracao, String mensagem) {
        return Arguments.of(descricao, alteracao, mensagem);
    }

    // ------------------------------------------------------------------ listar

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
                    .hasMessageContaining("Erro ao listar")
                    .hasCauseInstanceOf(DataAccessResourceFailureException.class);
        }
    }

    // ------------------------------------------------------------- buscarPorId

    @Nested
    class BuscarPorId {

        @Test
        void deveRetornarCursoQuandoIdExiste() {
            when(cursoRepository.findById("abc")).thenReturn(Optional.of(curso));

            CursoModel resultado = cursoService.buscarPorId("abc");

            assertThat(resultado).isEqualTo(curso);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void deveLancarBadRequestQuandoIdForInvalido(String id) {
            assertThatThrownBy(() -> cursoService.buscarPorId(id))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoIdNaoExiste() {
            when(cursoRepository.findById("xyz")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cursoService.buscarPorId("xyz"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("xyz");
        }

        @Test
        void deveLancarServiceExceptionQuandoRepositorioFalhar() {
            when(cursoRepository.findById("abc"))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> cursoService.buscarPorId("abc"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao buscar");
        }
    }

    // -------------------------------------------------------- buscarPorCursoId

    @Nested
    class BuscarPorCursoId {

        @Test
        void deveRetornarCursoQuandoCursoIdExiste() {
            when(cursoRepository.findByCursoId(1L)).thenReturn(Optional.of(curso));

            CursoModel resultado = cursoService.buscarPorCursoId(1L);

            assertThat(resultado).isEqualTo(curso);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0L, -5L})
        void deveLancarBadRequestQuandoCursoIdForInvalido(Long cursoId) {
            assertThatThrownBy(() -> cursoService.buscarPorCursoId(cursoId))
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

        @Test
        void deveLancarServiceExceptionQuandoRepositorioFalhar() {
            when(cursoRepository.findByCursoId(1L))
                    .thenThrow(new DataAccessResourceFailureException("falha"));

            assertThatThrownBy(() -> cursoService.buscarPorCursoId(1L))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao buscar");
        }
    }

    // ----------------------------------------------------------------- criar

    @Nested
    class CriarCurso {

        @Test
        void deveCriarCursoGerandoOCursoId() {
            when(geradorSequencialService.generateSequence("curso_sequence")).thenReturn(7L);
            when(cursoRepository.save(any(CursoModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            CursoModel resultado = cursoService.criarCurso(curso);

            assertThat(resultado.getCursoId()).isEqualTo(7L);
            assertThat(resultado.getNome()).isEqualTo("Java para Iniciantes");
            verify(geradorSequencialService).generateSequence("curso_sequence");
            verify(cursoRepository).save(curso);
        }

        @Test
        void deveLancarBadRequestQuandoCursoForNulo() {
            assertThatThrownBy(() -> cursoService.criarCurso(null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(cursoRepository, geradorSequencialService);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.aep.cursoLivre.services.CursoServiceTest#cursosInvalidos")
        void deveLancarBadRequestQuandoCursoForInvalido(
                String descricao, Consumer<CursoModel> alteracao, String mensagem) {

            alteracao.accept(curso);

            assertThatThrownBy(() -> cursoService.criarCurso(curso))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining(mensagem);

            verifyNoInteractions(cursoRepository, geradorSequencialService);
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(geradorSequencialService.generateSequence(anyString())).thenReturn(3L);
            when(cursoRepository.save(any(CursoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("erro ao salvar"));

            assertThatThrownBy(() -> cursoService.criarCurso(curso))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao cadastrar");
        }

        @Test
        void deveLancarServiceExceptionQuandoGerarSequenciaFalhar() {
            when(geradorSequencialService.generateSequence(anyString()))
                    .thenThrow(new DataAccessResourceFailureException("erro na sequencia"));

            assertThatThrownBy(() -> cursoService.criarCurso(curso))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao cadastrar");

            verify(cursoRepository, never()).save(any());
        }
    }

    // ------------------------------------------------------------- atualizar

    @Nested
    class AtualizarCurso {

        private CursoModel dadosAtualizados() {
            CursoModel dados = new CursoModel();
            dados.setNome("Novo Nome");
            dados.setInstituicao("Nova Instituicao");
            dados.setCategoria("Nova Categoria");
            dados.setCargaHoraria(80);
            dados.setLink("http://novo-link.com");
            return dados;
        }

        @Test
        void deveAtualizarCursoComSucesso() {
            when(cursoRepository.findById(ID)).thenReturn(Optional.of(curso));
            when(cursoRepository.save(any(CursoModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            CursoModel resultado = cursoService.atualizarCurso(ID, dadosAtualizados());

            assertThat(resultado).isSameAs(curso);
            assertThat(resultado.getNome()).isEqualTo("Novo Nome");
            assertThat(resultado.getInstituicao()).isEqualTo("Nova Instituicao");
            assertThat(resultado.getCategoria()).isEqualTo("Nova Categoria");
            assertThat(resultado.getCargaHoraria()).isEqualTo(80);
            assertThat(resultado.getLink()).isEqualTo("http://novo-link.com");
            assertThat(resultado.getCursoId()).isEqualTo(1L);
            verify(cursoRepository).save(curso);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void deveLancarBadRequestQuandoIdForInvalido(String id) {
            assertThatThrownBy(() -> cursoService.atualizarCurso(id, dadosAtualizados()))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarBadRequestQuandoDadosAtualizadosForemNulos() {
            assertThatThrownBy(() -> cursoService.atualizarCurso(ID, null))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarBadRequestQuandoDadosAtualizadosForemInvalidos() {
            CursoModel invalido = dadosAtualizados();
            invalido.setNome(" ");

            assertThatThrownBy(() -> cursoService.atualizarCurso(ID, invalido))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("nome do curso");

            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoNaoExistir() {
            when(cursoRepository.findById(ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cursoService.atualizarCurso(ID, dadosAtualizados()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(ID);

            verify(cursoRepository, never()).save(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoBuscarFalhar() {
            when(cursoRepository.findById(ID))
                    .thenThrow(new DataAccessResourceFailureException("erro ao buscar"));

            assertThatThrownBy(() -> cursoService.atualizarCurso(ID, dadosAtualizados()))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao atualizar");
        }

        @Test
        void deveLancarServiceExceptionQuandoSalvarFalhar() {
            when(cursoRepository.findById(ID)).thenReturn(Optional.of(curso));
            when(cursoRepository.save(any(CursoModel.class)))
                    .thenThrow(new DataAccessResourceFailureException("erro ao atualizar"));

            assertThatThrownBy(() -> cursoService.atualizarCurso(ID, dadosAtualizados()))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao atualizar");
        }
    }

    // --------------------------------------------------------------- deletar

    @Nested
    class DeletarCurso {

        @Test
        void deveDeletarCursoComSucesso() {
            when(cursoRepository.findById(ID)).thenReturn(Optional.of(curso));

            cursoService.deletarCurso(ID);

            verify(cursoRepository).delete(curso);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void deveLancarBadRequestQuandoIdForInvalido(String id) {
            assertThatThrownBy(() -> cursoService.deletarCurso(id))
                    .isInstanceOf(BadRequestException.class);

            verifyNoInteractions(cursoRepository);
        }

        @Test
        void deveLancarResourceNotFoundQuandoCursoNaoExistir() {
            when(cursoRepository.findById(ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cursoService.deletarCurso(ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(ID);

            verify(cursoRepository, never()).delete(any());
        }

        @Test
        void deveLancarServiceExceptionQuandoDeletarFalhar() {
            when(cursoRepository.findById(ID)).thenReturn(Optional.of(curso));
            doThrow(new DataAccessResourceFailureException("erro ao deletar"))
                    .when(cursoRepository).delete(curso);

            assertThatThrownBy(() -> cursoService.deletarCurso(ID))
                    .isInstanceOf(ServiceException.class)
                    .hasMessageContaining("Erro ao excluir");
        }
    }
}
