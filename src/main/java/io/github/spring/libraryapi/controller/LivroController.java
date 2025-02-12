package io.github.spring.libraryapi.controller;

import io.github.spring.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.spring.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.spring.libraryapi.controller.mappers.LivroMapper;
import io.github.spring.libraryapi.model.GeneroLivro;
import io.github.spring.libraryapi.model.Livro;
import io.github.spring.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
public class LivroController implements GenericController {

    private final LivroService service;
    private final LivroMapper mapper;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @PostMapping
    public CompletableFuture<ResponseEntity<Void>> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            Livro livro = mapper.toEntity(dto);
            service.salvar(livro);
            URI url = gerarHeaderLocation(livro.getId());
            return ResponseEntity.created(url).build();
        }, executor).thenApply(response -> ResponseEntity.created(response.getHeaders().getLocation()).build());
    }

    @GetMapping("{id}")
    public CompletableFuture<ResponseEntity<ResultadoPesquisaLivroDTO>> obterDetalhes(@PathVariable("id") String id) {
        return CompletableFuture.supplyAsync(() -> {
            return service.obterPorId(UUID.fromString(id))
                    .map(livro -> {
                        var dto = mapper.toDTO(livro);
                        return ResponseEntity.ok(dto);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        }, executor).orTimeout(5, TimeUnit.SECONDS);
    }

    @DeleteMapping("{id}")
    public CompletableFuture<ResponseEntity<Void>> deletar(@PathVariable String id) {
        return CompletableFuture.supplyAsync(() -> {
            return service.obterPorId(UUID.fromString(id))
                    .map(livro -> {
                        service.deletar(livro);
                        return ResponseEntity.noContent().<Void>build();
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        }, executor).orTimeout(5, TimeUnit.SECONDS);
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<ResultadoPesquisaLivroDTO>>> pesquisa(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "nome-autor", required = false) String nomeAutor,
            @RequestParam(value = "genero", required = false) GeneroLivro genero,
            @RequestParam(value = "ano-publicacao", required = false) Integer anoPublicacao
    ) {
        return CompletableFuture.supplyAsync(() -> {
            var resultado = service.pesquisa(isbn, titulo, nomeAutor, genero, anoPublicacao);
            var lista = resultado
                    .stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(lista);
        }, executor).orTimeout(5, TimeUnit.SECONDS);
    }
}
