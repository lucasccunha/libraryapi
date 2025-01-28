package io.github.spring.libraryapi.controller.mappers;

import io.github.spring.libraryapi.controller.dto.AutorDTO;
import io.github.spring.libraryapi.model.Autor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AutorMapper {

    Autor toEntity(AutorDTO dto);

    AutorDTO toDTO(Autor autor);

}
