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

import org.jhipster.health.domain.Points;
import org.jhipster.health.domain.*; // for static metamodels
import org.jhipster.health.repository.PointsRepository;
import org.jhipster.health.service.dto.PointsCriteria;

/**
 * Service for executing complex queries for {@link Points} entities in the database.
 * The main input is a {@link PointsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Points} or a {@link Page} of {@link Points} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PointsQueryService extends QueryService<Points> {

    private final Logger log = LoggerFactory.getLogger(PointsQueryService.class);

    private final PointsRepository pointsRepository;

    public PointsQueryService(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    /**
     * Return a {@link List} of {@link Points} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Points> findByCriteria(PointsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Points> specification = createSpecification(criteria);
        return pointsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Points} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Points> findByCriteria(PointsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Points> specification = createSpecification(criteria);
        return pointsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PointsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Points> specification = createSpecification(criteria);
        return pointsRepository.count(specification);
    }

    /**
     * Function to convert {@link PointsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Points> createSpecification(PointsCriteria criteria) {
        Specification<Points> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Points_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Points_.date));
            }
            if (criteria.getExercise() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExercise(), Points_.exercise));
            }
            if (criteria.getMeals() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMeals(), Points_.meals));
            }
            if (criteria.getAlcohol() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAlcohol(), Points_.alcohol));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), Points_.notes));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(Points_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
