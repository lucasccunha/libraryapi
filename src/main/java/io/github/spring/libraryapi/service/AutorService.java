package io.github.spring.libraryapi.service;

import io.github.spring.libraryapi.model.Autor;
import io.github.spring.libraryapi.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AutorService {

    private final AutorRepository repository;


    public AutorService(AutorRepository repository) {
        this.repository = repository;
    }

    public Autor salvar(Autor autor) {
        return repository.save(autor);
    }

    public void atualizar(Autor autor) {
        if(autor.getId() == null) {
            throw new IllegalArgumentException("Para atualizar, é necessario que o autor já esteja salvo na base");
        }
         repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id) {
        return repository.findById(id);
    }

    public void deletar(Autor autor) {
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
}
