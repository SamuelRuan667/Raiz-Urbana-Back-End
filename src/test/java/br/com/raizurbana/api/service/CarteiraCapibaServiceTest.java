
package br.com.raizurbana.api.service;

import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.CarteiraCapibaRepository;
import br.com.raizurbana.api.repository.MovimentacaoCapibaRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarteiraCapibaServiceTest {

    @Mock
    private CarteiraCapibaRepository carteiraRepository;

    @Mock
    private MovimentacaoCapibaRepository movimentacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CarteiraCapibaService service;

    @Test
    void naoDeveCreditarRecompensaDuasVezes() {

        Usuario usuario = Usuario.builder()
                .id(1L)
                .build();

        SolicitacaoPlantio solicitacao =
                SolicitacaoPlantio.builder()
                        .id(10L)
                        .usuario(usuario)
                        .build();

        when(movimentacaoRepository
                .existsBySolicitacaoIdAndTipo(10L, "ENTRADA"))
                .thenReturn(true);

        assertDoesNotThrow(
                () -> service.creditarPlantioConcluido(solicitacao)
        );

        verify(carteiraRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }
}
