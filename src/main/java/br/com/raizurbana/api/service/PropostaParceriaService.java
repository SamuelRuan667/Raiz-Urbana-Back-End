
package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.PropostaParceriaRequest;
import br.com.raizurbana.api.model.PropostaParceria;
import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.PropostaParceriaRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

@Service
public class PropostaParceriaService {

    private final PropostaParceriaRepository propostaRepository;
    private final UsuarioRepository usuarioRepository;

    public PropostaParceriaService(
            PropostaParceriaRepository propostaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.propostaRepository = propostaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PropostaParceria enviarProposta(
            Long usuarioId,
            PropostaParceriaRequest request
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        )
                );

        if (usuario.getTipo() != TipoUsuario.EMPRESA_PARCEIRA) {
            throw new IllegalArgumentException(
                    "Somente empresas podem enviar propostas de parceria."
            );
        }

        String cnpj = somenteNumeros(request.getCnpj());

        if (usuario.getCnpj() == null ||
                !usuario.getCnpj().equals(cnpj)) {
            throw new IllegalArgumentException(
                    "O CNPJ informado não corresponde à empresa autenticada."
            );
        }

        if (propostaRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException(
                    "Esta empresa já possui uma proposta cadastrada."
            );
        }

        String tipoParceria = request.getTipoParceria()
                .trim()
                .toUpperCase(Locale.ROOT);

        List<String> tiposPermitidos = List.of(
                "BENEFICIOS",
                "PATROCINIO",
                "APOIO_AMBIENTAL",
                "DOACAO",
                "OUTRO"
        );

        if (!tiposPermitidos.contains(tipoParceria)) {
            throw new IllegalArgumentException(
                    "Tipo de parceria inválido."
            );
        }

        String descricao = request.getDescricao().trim();

        String textoProposta =
                "Tipo de parceria: " + tipoParceria +
                "\n\nDescrição:\n" + descricao;

        PropostaParceria proposta = PropostaParceria.builder()
                .usuario(usuario)
                .razaoSocial(request.getRazaoSocial().trim())
                .nomeFantasia(
                        request.getNomeFantasia() == null
                                ? null
                                : request.getNomeFantasia().trim()
                )
                .cnpj(cnpj)
                .responsavel(request.getResponsavel().trim())
                .email(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .telefone(somenteNumeros(request.getTelefone()))
                .cep(somenteNumeros(request.getCep()))
                .numero(request.getNumero())
                .rua(request.getRua().trim())
                .bairro(request.getBairro().trim())
                .cidade(request.getCidade().trim())
                .estado(request.getEstado().trim().toUpperCase(Locale.ROOT))
                .proposta(textoProposta)
                .status("PENDENTE")
                .dataEnvio(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        return propostaRepository.save(proposta);
    }

    @Transactional(readOnly = true)
    public List<PropostaParceria> listarMinhasPropostas(
            Long usuarioId
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        )
                );

        if (usuario.getTipo() != TipoUsuario.EMPRESA_PARCEIRA) {
            throw new IllegalArgumentException(
                    "Acesso permitido apenas para empresas."
            );
        }

        return propostaRepository
                .findByUsuarioIdOrderByDataEnvioDesc(usuarioId);
    }

    private String somenteNumeros(String valor) {
        return valor == null
                ? ""
                : valor.replaceAll("\\D", "");
    }
}
