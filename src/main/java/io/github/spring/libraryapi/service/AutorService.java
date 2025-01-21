package io.github.spring.libraryapi.service;

import io.github.spring.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.spring.libraryapi.model.Autor;
import io.github.spring.libraryapi.repository.AutorRepository;
import io.github.spring.libraryapi.repository.LivroRepository;
import io.github.spring.libraryapi.validator.AutorValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AutorService {

    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;

    public AutorService(AutorRepository repository,
                        AutorValidator validator,
                        LivroRepository livroRepository) {
        this.repository = repository;
        this.validator = validator;
        this.livroRepository = livroRepository;
    }

    public Autor salvar(Autor autor) {
        validator.validar(autor);
        return repository.save(autor);
    }

    public void atualizar(Autor autor) {
        if (autor.getId() == null) {
            throw new IllegalArgumentException("Para atualizar, é necessario que o autor já esteja salvo na base");
        }
        validator.validar(autor);
        repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id) {
        return repository.findById(id);
    }

    public void deletar(Autor autor) {
        if(possuiLivro(autor)) {
            throw new OperacaoNaoPermitidaException(
                    "Não é permitido excluir um Autor que possui livros cadastrados!");
        }
        repository.delete(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade) {
        List<Autor> autores = repository.findAll();

        if (nome != null && nacionalidade != null) {
            return autores.stream()
                    .filter(autor -> autor.getNome().equalsIgnoreCase(nome) &&
                            autor.getNacionalidade().equalsIgnoreCase(nacionalidade))
                    .collect(Collectors.toList());
        }
        if (nome != null) {
            return autores.stream()
                    .filter(autor -> autor.getNome().equalsIgnoreCase(nome))
                    .collect(Collectors.toList());
        }
        if (nacionalidade != null) {
            return autores.stream()
                    .filter(autor -> autor.getNacionalidade().equalsIgnoreCase(nacionalidade))
                    .collect(Collectors.toList());
        }
        return autores;
    }

    public boolean possuiLivro(Autor autor) {
        return livroRepository.existsByAutor(autor);
    }
}
