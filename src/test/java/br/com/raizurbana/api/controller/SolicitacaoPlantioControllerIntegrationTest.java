
package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.SolicitacaoPlantioRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;
import br.com.raizurbana.api.service.JwtService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SolicitacaoPlantioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitacaoPlantioRepository solicitacaoRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarSolicitacaoPelaApiEPersistirNoBanco() throws Exception {

        Usuario usuario = Usuario.builder()
                .nomeCompleto("Usuário Teste")
                .cpf("52998224725")
                .telefone("81999999999")
                .email("teste.integracao@raizurbana.com")
                .cep("50000000")
                .numero("100")
                .rua("Rua de Teste")
                .bairro("Boa Viagem")
                .cidade("Recife")
                .estado("PE")
                .senhaHash("hash-teste")
                .tipo(TipoUsuario.USUARIO)
                .createdAt(LocalDateTime.now())
                .build();

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario);

        String dadosJson = """
                {
                    "usuarioId": %d,
                    "nomeCompleto": "Usuário Teste",
                    "cpf": "52998224725",
                    "telefone": "81999999999",
                    "email": "teste.integracao@raizurbana.com",
                    "cep": "50000000",
                    "bairro": "Boa Viagem",
                    "ruaAvenida": "Rua de Teste",
                    "numero": "100",
                    "pontoReferencia": "Próximo à praça",
                    "tipoLocal": "Calcada",
                    "observacoes": "Solicitação criada pelo teste de integração"
                }
                """.formatted(usuario.getId());

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                dadosJson.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult resultado = mockMvc.perform(
                        multipart("/api/solicitacoes")
                                .file(dados)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode resposta = objectMapper.readTree(
                resultado.getResponse().getContentAsString()
        );

        String protocolo = resposta.get("protocolo").asText();

        assertNotNull(protocolo);
        assertEquals(true, protocolo.matches("\\d{10}"));

        SolicitacaoPlantio solicitacaoSalva =
                solicitacaoRepository.findByProtocolo(protocolo)
                        .orElseThrow(() ->
                                new AssertionError(
                                        "Solicitação não foi persistida no banco."
                                )
                        );

        assertEquals(usuario.getId(), solicitacaoSalva.getUsuario().getId());
        assertEquals("Usuário Teste", solicitacaoSalva.getNomeCompleto());
        assertEquals("Boa Viagem", solicitacaoSalva.getBairro());
        assertEquals("PENDENTE", solicitacaoSalva.getStatus().name());
    }
}
