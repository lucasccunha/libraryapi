package io.github.spring.libraryapi.controller;

import io.github.spring.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.spring.libraryapi.controller.dto.ErroResposta;
import io.github.spring.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.spring.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService service;

    @PostMapping
    public ResponseEntity<Object> salvar (@RequestBody @Valid CadastroLivroDTO dto){
        try {
            return ResponseEntity.ok(dto);
        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
}
