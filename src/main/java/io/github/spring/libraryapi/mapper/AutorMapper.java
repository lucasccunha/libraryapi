package io.github.spring.libraryapi.mapper;

import io.github.spring.libraryapi.controller.dto.AutorDTO;
import io.github.spring.libraryapi.model.Autor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AutorMapper {

    AutorMapper INSTANCE = Mappers.getMapper(AutorMapper.class);

    AutorDTO toDTO(Autor autor);

    Autor toEntity(AutorDTO autorDTO);
}

