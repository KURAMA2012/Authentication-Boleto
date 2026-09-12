package com.A3.projeto.Faculdade.AuthenticationBoleto.mapper;

import java.util.List;

/**
 * Interface genérica para Mappers do MapStruct.
 * @param <E> Entidade (ex: Usuario)
 * @param <D> DTO (ex: UsuarioDTO ou UsuarioRequestDTO)
 */
public interface GenericMapper<E, D> {

    // Converte DTO em Entidade
    E toEntity(D dto);

    // Converte Entidade em DTO
    D toDto(E entity);

    // Converte listas automaticamente
    List<E> toEntityList(List<D> dtoList);

    List<D> toDtoList(List<E> entityList);
}
