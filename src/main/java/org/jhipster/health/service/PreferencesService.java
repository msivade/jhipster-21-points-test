package org.jhipster.health.service;

import org.jhipster.health.domain.Preferences;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Preferences}.
 */
public interface PreferencesService {

    /**
     * Save a preferences.
     *
     * @param preferences the entity to save.
     * @return the persisted entity.
     */
    Preferences save(Preferences preferences);

    /**
     * Get all the preferences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Preferences> findAll(Pageable pageable);


    /**
     * Get the "id" preferences.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Preferences> findOne(Long id);

    /**
     * Delete the "id" preferences.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the preferences corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Preferences> search(String query, Pageable pageable);
}
