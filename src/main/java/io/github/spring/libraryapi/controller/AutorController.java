package io.github.spring.libraryapi.controller;

import io.github.spring.libraryapi.controller.dto.AutorDTO;
import io.github.spring.libraryapi.controller.dto.ErroResposta;
import io.github.spring.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.spring.libraryapi.mapper.AutorMapper;
import io.github.spring.libraryapi.model.Autor;
import io.github.spring.libraryapi.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/autores")
public class AutorController {

    private final AutorService service;

    private final AutorMapper mapper = AutorMapper.INSTANCE;

    public AutorController(AutorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody @Valid AutorDTO autorDTO) {
        try {

            Autor autorEntidade = mapper.toEntity(autorDTO);
            service.salvar(autorEntidade);


            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(autorEntidade.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }


    @GetMapping("{id}")
    public ResponseEntity<AutorDTO> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.obterPorId(idAutor);


        return autorOptional.map(autor -> ResponseEntity.ok(mapper.toDTO(autor)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AutorDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {

        List<Autor> resultado = service.pesquisaByExample(nome, nacionalidade);


        List<AutorDTO> lista = resultado.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(
            @PathVariable("id") String id, @RequestBody @Valid AutorDTO dto) {
        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.obterPorId(idAutor);

            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var autor = mapper.toEntity(dto);
            autor.setId(idAutor);
            service.atualizar(autor);

            return ResponseEntity.noContent().build();
        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
}
