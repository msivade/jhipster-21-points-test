package org.jhipster.health.service.impl;

import org.jhipster.health.service.BloodPressureService;
import org.jhipster.health.domain.BloodPressure;
import org.jhipster.health.repository.BloodPressureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link BloodPressure}.
 */
@Service
@Transactional
public class BloodPressureServiceImpl implements BloodPressureService {

    private final Logger log = LoggerFactory.getLogger(BloodPressureServiceImpl.class);

    private final BloodPressureRepository bloodPressureRepository;

    public BloodPressureServiceImpl(BloodPressureRepository bloodPressureRepository) {
        this.bloodPressureRepository = bloodPressureRepository;
    }

    @Override
    public BloodPressure save(BloodPressure bloodPressure) {
        log.debug("Request to save BloodPressure : {}", bloodPressure);
        return bloodPressureRepository.save(bloodPressure);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BloodPressure> findAll(Pageable pageable) {
        log.debug("Request to get all BloodPressures");
        return bloodPressureRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<BloodPressure> findOne(Long id) {
        log.debug("Request to get BloodPressure : {}", id);
        return bloodPressureRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BloodPressure : {}", id);
        bloodPressureRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BloodPressure> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BloodPressures for query {}", query);
        return bloodPressureRepository.search(BloodPressure.PREFIX, query, pageable);    }
}
