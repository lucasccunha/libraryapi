package io.github.spring.libraryapi.service;

import io.github.spring.libraryapi.model.Autor;
import io.github.spring.libraryapi.model.GeneroLivro;
import io.github.spring.libraryapi.model.Livro;
import io.github.spring.libraryapi.repository.AutorRepository;
import io.github.spring.libraryapi.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransacaoService {

    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private LivroRepository livroRepository;

    @Transactional
    public void atualizacaoSemAtualizar() {
        var livro = livroRepository
                .findById(UUID.fromString("bcf82652-28ea-4f14-9ffa-6a3673168b1d"))
                .orElse(null);

        livro.setDataPublicacao(LocalDate.of(2024,6,1));
    }

    @Transactional
    public void executar() {

        // salva o autor
        Autor autor = new Autor();
        autor.setNome("Francisca");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951, 1, 31));

        autorRepository.save(autor);
        System.out.println("Autor salvo: " + autor);

        // salva o livro
        Livro livro = new Livro();
        livro.setIsbn("90001-00000");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.MISTERIO);
        livro.setTitulo("Livro da Francisca");
        livro.setDataPublicacao(LocalDate.of(2023, 1, 2));

        livro.setAutor(autor);

        livroRepository.save(livro);
        System.out.println("Livro salvo: " + livro);

        if (autor.getNome().equals("Francisco")) {
            throw new RuntimeException("Rollback!");
        }
    }
}
