package org.jhipster.health.service.impl;

import org.jhipster.health.service.PreferencesService;
import org.jhipster.health.domain.Preferences;
import org.jhipster.health.repository.PreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Preferences}.
 */
@Service
@Transactional
public class PreferencesServiceImpl implements PreferencesService {

    private final Logger log = LoggerFactory.getLogger(PreferencesServiceImpl.class);

    private final PreferencesRepository preferencesRepository;

    public PreferencesServiceImpl(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    @Override
    public Preferences save(Preferences preferences) {
        log.debug("Request to save Preferences : {}", preferences);
        return preferencesRepository.save(preferences);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Preferences> findAll(Pageable pageable) {
        log.debug("Request to get all Preferences");
        return preferencesRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Preferences> findOne(Long id) {
        log.debug("Request to get Preferences : {}", id);
        return preferencesRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Preferences : {}", id);
        preferencesRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Preferences> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Preferences for query {}", query);
        return preferencesRepository.search(Preferences.PREFIX, query, pageable);    }
}
