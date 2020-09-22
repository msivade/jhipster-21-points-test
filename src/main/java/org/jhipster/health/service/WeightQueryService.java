package org.jhipster.health.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import org.jhipster.health.domain.Weight;
import org.jhipster.health.domain.*; // for static metamodels
import org.jhipster.health.repository.WeightRepository;
import org.jhipster.health.repository.search.WeightSearchRepository;
import org.jhipster.health.service.dto.WeightCriteria;

/**
 * Service for executing complex queries for {@link Weight} entities in the database.
 * The main input is a {@link WeightCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Weight} or a {@link Page} of {@link Weight} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WeightQueryService extends QueryService<Weight> {

    private final Logger log = LoggerFactory.getLogger(WeightQueryService.class);

    private final WeightRepository weightRepository;

    private final WeightSearchRepository weightSearchRepository;

    public WeightQueryService(WeightRepository weightRepository, WeightSearchRepository weightSearchRepository) {
        this.weightRepository = weightRepository;
        this.weightSearchRepository = weightSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Weight} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Weight> findByCriteria(WeightCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Weight> specification = createSpecification(criteria);
        return weightRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Weight} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Weight> findByCriteria(WeightCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Weight> specification = createSpecification(criteria);
        return weightRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WeightCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Weight> specification = createSpecification(criteria);
        return weightRepository.count(specification);
    }

    /**
     * Function to convert {@link WeightCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Weight> createSpecification(WeightCriteria criteria) {
        Specification<Weight> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Weight_.id));
            }
            if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), Weight_.timestamp));
            }
            if (criteria.getWeight() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWeight(), Weight_.weight));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(Weight_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
