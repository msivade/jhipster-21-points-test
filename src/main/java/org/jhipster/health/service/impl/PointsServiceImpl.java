package org.jhipster.health.service.impl;

import org.jhipster.health.service.PointsService;
import org.jhipster.health.domain.Points;
import org.jhipster.health.repository.PointsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Points}.
 */
@Service
@Transactional
public class PointsServiceImpl implements PointsService {

    private final Logger log = LoggerFactory.getLogger(PointsServiceImpl.class);

    private final PointsRepository pointsRepository;

    public PointsServiceImpl(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    @Override
    public Points save(Points points) {
        log.debug("Request to save Points : {}", points);
        return pointsRepository.save(points);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Points> findAll(Pageable pageable) {
        log.debug("Request to get all Points");
        return pointsRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Points> findOne(Long id) {
        log.debug("Request to get Points : {}", id);
        return pointsRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Points : {}", id);
        pointsRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Points> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Points for query {}", query);
        return pointsRepository.search(Points.PREFIX, query, pageable);    }
}
