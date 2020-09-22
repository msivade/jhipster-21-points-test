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

import org.jhipster.health.domain.Preferences;
import org.jhipster.health.domain.*; // for static metamodels
import org.jhipster.health.repository.PreferencesRepository;
import org.jhipster.health.repository.search.PreferencesSearchRepository;
import org.jhipster.health.service.dto.PreferencesCriteria;

/**
 * Service for executing complex queries for {@link Preferences} entities in the database.
 * The main input is a {@link PreferencesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Preferences} or a {@link Page} of {@link Preferences} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PreferencesQueryService extends QueryService<Preferences> {

    private final Logger log = LoggerFactory.getLogger(PreferencesQueryService.class);

    private final PreferencesRepository preferencesRepository;

    private final PreferencesSearchRepository preferencesSearchRepository;

    public PreferencesQueryService(PreferencesRepository preferencesRepository, PreferencesSearchRepository preferencesSearchRepository) {
        this.preferencesRepository = preferencesRepository;
        this.preferencesSearchRepository = preferencesSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Preferences} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Preferences> findByCriteria(PreferencesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Preferences> specification = createSpecification(criteria);
        return preferencesRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Preferences} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Preferences> findByCriteria(PreferencesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Preferences> specification = createSpecification(criteria);
        return preferencesRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PreferencesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Preferences> specification = createSpecification(criteria);
        return preferencesRepository.count(specification);
    }

    /**
     * Function to convert {@link PreferencesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Preferences> createSpecification(PreferencesCriteria criteria) {
        Specification<Preferences> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Preferences_.id));
            }
            if (criteria.getWeeklyGoal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWeeklyGoal(), Preferences_.weeklyGoal));
            }
            if (criteria.getWeightUnits() != null) {
                specification = specification.and(buildSpecification(criteria.getWeightUnits(), Preferences_.weightUnits));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(Preferences_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
