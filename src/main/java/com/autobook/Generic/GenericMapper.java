package com.autobook.Generic;

import java.util.List;

/**
 * A Generic Mapper interface to define a standardized contract for Entity to
 * DTO conversions.
 *
 * @param <E> The Entity type
 * @param <D> The Primary Response DTO type
 */
public interface GenericMapper<E, D> {

    /**
     * Maps an entity to its primary card DTO representation.
     *
     * @param entity the source entity
     * @return the mapped DTO
     */
    D toCardResponse(E entity);

    /**
     * Maps a list of entities to a list of primary card DTOs.
     *
     * @param entities the list of source entities
     * @return the list of mapped DTOs
     */
    default List<D> toCardResponseList(List<E> entities) {
        if (entities == null) {
            return java.util.Collections.emptyList();
        }
        return entities.stream()
                .map(this::toCardResponse)
                .toList();
    }
}
