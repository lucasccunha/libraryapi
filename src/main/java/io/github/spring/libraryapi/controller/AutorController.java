package io.github.spring.libraryapi.controller;

import io.github.spring.libraryapi.controller.dto.AutorDTO;
import io.github.spring.libraryapi.controller.mappers.AutorMapper;
import io.github.spring.libraryapi.model.Autor;
import io.github.spring.libraryapi.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/autores")
@RequiredArgsConstructor
public class AutorController implements GenericController {

    private final AutorService service;
    private final AutorMapper mapper;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @PostMapping
    public CompletableFuture<ResponseEntity<Void>> salvar(@RequestBody @Valid AutorDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            Autor autor = mapper.toEntity(dto);
            service.salvar(autor);
            URI location = gerarHeaderLocation(autor.getId());
            return ResponseEntity.created(location).<Void>build();
        }, executor).orTimeout(5, TimeUnit.SECONDS);
    }

    @GetMapping("{id}")
    public CompletableFuture<ResponseEntity<AutorDTO>> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        return CompletableFuture.supplyAsync(() -> {
            Optional<Autor> autorOptional = service.obterPorId(idAutor);
            return autorOptional
                    .map(autor -> {
                        AutorDTO dto = mapper.toDTO(autor);
                        return ResponseEntity.ok(dto);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        }, executor).orTimeout(5, TimeUnit.SECONDS);
    }

    @DeleteMapping("{id}")
    public CompletableFuture<ResponseEntity<Void>> deletar(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        return CompletableFuture.supplyAsync(() -> {
            Optional<Autor> autorOptional = service.obterPorId(idAutor);
            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            service.deletar(autorOptional.get());
            return ResponseEntity.noContent().build();
        }, executor).thenApply(response -> ResponseEntity.status(response.getStatusCode()).build());
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<AutorDTO>>> pesquisar(@RequestParam(value = "nome", required = false) String nome, @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {
        return CompletableFuture.supplyAsync(() -> {
            List<Autor> resultado = service.pesquisaByExample(nome, nacionalidade);
            List<AutorDTO> lista = resultado
                    .stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(lista);
        }, executor).orTimeout(5, TimeUnit.SECONDS);
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> atualizar(@PathVariable("id") String id, @RequestBody @Valid AutorDTO dto) {
        var idAutor = UUID.fromString(id);
        return CompletableFuture.supplyAsync(() -> {
            Optional<Autor> autorOptional = service.obterPorId(idAutor);
            if (autorOptional.isEmpty()) {
                return ResponseEntity.<Void>notFound().build();
            }
            var autor = autorOptional.get();
            autor.setNome(dto.nome());
            autor.setNacionalidade(dto.nacionalidade());
            autor.setDataNascimento(dto.dataNascimento());
            service.atualizar(autor);
            return ResponseEntity.<Void>noContent().build();
        }, executor).thenApply(response -> ResponseEntity.status(response.getStatusCode()).build());
    }
}