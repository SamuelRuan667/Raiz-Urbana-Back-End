
package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.ConsultaProtocoloResponse;
import br.com.raizurbana.api.dto.SolicitacaoPlantioRequest;
import br.com.raizurbana.api.dto.SolicitacaoPlantioResponse;
import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.StatusSolicitacao;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.SolicitacaoPlantioRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoPlantioServiceTest {

    @Mock
    private SolicitacaoPlantioRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CarteiraCapibaService carteiraCapibaService;

    @InjectMocks
    private SolicitacaoPlantioService service;

    @Test
    void deveCriarSolicitacaoComUsuarioExistente() throws Exception {

        Usuario usuario = Usuario.builder()
                .id(1L)
                .build();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(repository.save(any(SolicitacaoPlantio.class)))
                .thenAnswer(invocation -> {
                    SolicitacaoPlantio solicitacao =
                            invocation.getArgument(0);

                    if (solicitacao.getId() == null) {
                        solicitacao.setId(123L);
                    }

                    return solicitacao;
                });

        SolicitacaoPlantioRequest request =
                new SolicitacaoPlantioRequest();

        request.setNomeCompleto("João da Silva");
        request.setCpf("12345678900");
        request.setTelefone("81999999999");
        request.setEmail("joao@email.com");
        request.setCep("50000000");
        request.setBairro("Boa Viagem");
        request.setRuaAvenida("Rua Exemplo");
        request.setNumero("100");
        request.setTipoLocal("CALCADA");

        SolicitacaoPlantioResponse response =
                service.criar(request, null, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getUsuarioId());
        assertEquals(StatusSolicitacao.PENDENTE, response.getStatus());
        assertEquals("João da Silva", response.getNomeCompleto());
        assertNotNull(response.getProtocolo());
        assertTrue(response.getProtocolo().matches("\\d{10}"));

        verify(repository, times(2))
                .save(any(SolicitacaoPlantio.class));
    }

    @Test
    void naoDeveCriarSolicitacaoComUsuarioInexistente() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        SolicitacaoPlantioRequest request =
                new SolicitacaoPlantioRequest();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.criar(request, null, 99L)
                );

        assertEquals(
                "Usuário autenticado não encontrado.",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(SolicitacaoPlantio.class));
    }

    @Test
    void deveConsultarSolicitacaoPeloProtocolo() {

        String protocolo = "2026000123";

        SolicitacaoPlantio solicitacao =
                SolicitacaoPlantio.builder()
                        .id(123L)
                        .protocolo(protocolo)
                        .bairro("Boa Viagem")
                        .tipoLocal("CALCADA")
                        .status(StatusSolicitacao.PENDENTE)
                        .dataSolicitacao(LocalDateTime.now())
                        .build();

        when(repository.findByProtocolo(protocolo))
                .thenReturn(Optional.of(solicitacao));

        ConsultaProtocoloResponse response =
                service.buscarPorProtocolo(protocolo);

        assertNotNull(response);
        assertEquals(protocolo, response.getProtocolo());
        assertEquals("Boa Viagem", response.getBairro());
        assertEquals(StatusSolicitacao.PENDENTE, response.getStatus());

        verify(repository).findByProtocolo(protocolo);
    }
}
