package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.CarteiraCapibaResponse;
import br.com.raizurbana.api.dto.MovimentacaoCapibaResponse;
import br.com.raizurbana.api.service.CarteiraCapibaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carteiras-capiba")
public class CarteiraCapibaController {

    private final CarteiraCapibaService carteiraCapibaService;

    public CarteiraCapibaController(
            CarteiraCapibaService carteiraCapibaService
    ) {
        this.carteiraCapibaService = carteiraCapibaService;
    }

    @GetMapping("/minha")
    public ResponseEntity<CarteiraCapibaResponse> buscarMinhaCarteira(
            Authentication authentication
    ) {

        Long usuarioId =
                (Long) authentication.getPrincipal();

        CarteiraCapibaResponse carteira =
                carteiraCapibaService.buscarPorUsuario(
                        usuarioId
                );

        return ResponseEntity.ok(carteira);
    }

    @GetMapping("/minha/movimentacoes")
    public ResponseEntity<List<MovimentacaoCapibaResponse>> listarMinhasMovimentacoes(
            Authentication authentication
    ) {

        Long usuarioId =
                (Long) authentication.getPrincipal();

        List<MovimentacaoCapibaResponse> movimentacoes =
                carteiraCapibaService.listarMovimentacoes(
                        usuarioId
                );

        return ResponseEntity.ok(movimentacoes);
    }
}