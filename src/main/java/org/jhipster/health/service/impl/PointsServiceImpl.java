package org.jhipster.health.service.impl;

import org.jhipster.health.service.PointsService;
import org.jhipster.health.domain.Points;
import org.jhipster.health.repository.PointsRepository;
import org.jhipster.health.repository.search.PointsSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Points}.
 */
@Service
@Transactional
public class PointsServiceImpl implements PointsService {

    private final Logger log = LoggerFactory.getLogger(PointsServiceImpl.class);

    private final PointsRepository pointsRepository;

    private final PointsSearchRepository pointsSearchRepository;

    public PointsServiceImpl(PointsRepository pointsRepository, PointsSearchRepository pointsSearchRepository) {
        this.pointsRepository = pointsRepository;
        this.pointsSearchRepository = pointsSearchRepository;
    }

    @Override
    public Points save(Points points) {
        log.debug("Request to save Points : {}", points);
        Points result = pointsRepository.save(points);
        pointsSearchRepository.save(result);
        return result;
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
        pointsSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Points> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Points for query {}", query);
        return pointsSearchRepository.search(queryStringQuery(query), pageable);    }

    @Override
    @Transactional(readOnly = true)
    public Page<Points> findAllByOrderByDateDesc(Pageable pageable) {
        log.debug("Request to get all Points by order of date desc");
        return pointsRepository.findAllByOrderByDateDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Points> findByUserIsCurrentUser(Pageable pageable) {
        log.debug("Request to get all Points from current user only");
        return pointsRepository.findByUserIsCurrentUser(pageable);
    }

    @Override
    public List<Points> findAllByDateBetweenAndUserLogin(LocalDate startOfWeek,
                                                         LocalDate endOfWeek,
                                                         Optional<String> currentUserLogin) {
        log.info("Request to get all Points of the week from current user only");
        return pointsRepository.findAllByDateBetweenAndUserLoginOrderByDateDesc(startOfWeek,
            endOfWeek, currentUserLogin.get());
    }
}
