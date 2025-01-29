package io.github.spring.libraryapi.controller;

import io.github.spring.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.spring.libraryapi.controller.dto.ErroResposta;
import io.github.spring.libraryapi.controller.mappers.LivroMapper;
import io.github.spring.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.spring.libraryapi.model.Livro;
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
public class LivroController implements GenericController {

    private final LivroService service;
    private final LivroMapper mapper;

    @PostMapping
    public ResponseEntity<Object> salvar (@RequestBody @Valid CadastroLivroDTO dto){
        try {
            Livro livro = mapper.toEntity(dto);
            service.salvar(livro);
            var url = gerarHeaderLocation(livro.getId());
            return ResponseEntity.created(url).build();
        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
}
