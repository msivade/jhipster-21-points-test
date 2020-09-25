package org.jhipster.health.web.rest;

import io.micrometer.core.annotation.Timed;
import org.jhipster.health.domain.Preferences;
import org.jhipster.health.security.AuthoritiesConstants;
import org.jhipster.health.security.SecurityUtils;
import org.jhipster.health.service.PreferencesService;
import org.jhipster.health.service.UserService;
import org.jhipster.health.web.rest.errors.BadRequestAlertException;
import org.jhipster.health.service.dto.PreferencesCriteria;
import org.jhipster.health.service.PreferencesQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link org.jhipster.health.domain.Preferences}.
 */
@RestController
@RequestMapping("/api")
public class PreferencesResource {

    private final Logger log = LoggerFactory.getLogger(PreferencesResource.class);

    private static final String ENTITY_NAME = "preferences";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PreferencesService preferencesService;

    private final PreferencesQueryService preferencesQueryService;

    private final UserService userService;

    public PreferencesResource(PreferencesService preferencesService,
                               PreferencesQueryService preferencesQueryService,
                               UserService userService) {
        this.preferencesService = preferencesService;
        this.preferencesQueryService = preferencesQueryService;
        this.userService = userService;
    }

    /**
     * {@code POST  /preferences} : Create a new preferences.
     *
     * @param preferences the preferences to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new preferences, or with status {@code 400 (Bad Request)} if the preferences has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/preferences")
    public ResponseEntity<Preferences> createPreferences(@Valid @RequestBody Preferences preferences) throws URISyntaxException {
        log.debug("REST request to save Preferences : {}", preferences);
        if (preferences.getId() != null) {
            throw new BadRequestAlertException("A new preferences cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
            log.debug("No user passed in, using current user: {}", SecurityUtils.getCurrentUserLogin());
            preferences.setUser(userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get());
        }
        Preferences result = preferencesService.save(preferences);
        return ResponseEntity.created(new URI("/api/preferences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /preferences} : Updates an existing preferences.
     *
     * @param preferences the preferences to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated preferences,
     * or with status {@code 400 (Bad Request)} if the preferences is not valid,
     * or with status {@code 500 (Internal Server Error)} if the preferences couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/preferences")
    public ResponseEntity<Preferences> updatePreferences(@Valid @RequestBody Preferences preferences) throws URISyntaxException {
        log.debug("REST request to update Preferences : {}", preferences);
        if (preferences.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Preferences result = preferencesService.save(preferences);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, preferences.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /preferences} : get all the preferences.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of preferences in body.
     */
    @GetMapping("/preferences")
    public ResponseEntity<List<Preferences>> getAllPreferences(PreferencesCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Preferences by criteria: {}", criteria);
        Page<Preferences> page = preferencesQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /preferences/count} : count all the preferences.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/preferences/count")
    public ResponseEntity<Long> countPreferences(PreferencesCriteria criteria) {
        log.debug("REST request to count Preferences by criteria: {}", criteria);
        return ResponseEntity.ok().body(preferencesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /preferences/:id} : get the "id" preferences.
     *
     * @param id the id of the preferences to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the preferences, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/preferences/{id}")
    public ResponseEntity<Preferences> getPreferences(@PathVariable Long id) {
        log.debug("REST request to get Preferences : {}", id);
        Optional<Preferences> preferences = preferencesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(preferences);
    }

    /**
     * {@code DELETE  /preferences/:id} : delete the "id" preferences.
     *
     * @param id the id of the preferences to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/preferences/{id}")
    public ResponseEntity<Void> deletePreferences(@PathVariable Long id) {
        log.debug("REST request to delete Preferences : {}", id);
        preferencesService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/preferences?query=:query} : search for the preferences corresponding
     * to the query.
     *
     * @param query the query of the preferences search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/preferences")
    public ResponseEntity<List<Preferences>> searchPreferences(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Preferences for query {}", query);
        Page<Preferences> page = preferencesService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }

    /**
     * GET /my-preferences -> get the current user's preferences.
     */
    @GetMapping("/my-preferences")
    @Timed
    public ResponseEntity<Preferences> getUserPreferences() {
        String username = SecurityUtils.getCurrentUserLogin().get();
        log.debug("REST request to get Preferences : {}", username);
        Optional<Preferences> preferences =
            preferencesService.findOneByUserLogin(username);
        if (preferences.isPresent()) {
            return new ResponseEntity<>(preferences.get(), HttpStatus.OK);
        } else {
            Preferences defaultPreferences = new Preferences();
            defaultPreferences.setWeeklyGoal(10); // default
            return new ResponseEntity<>(defaultPreferences, HttpStatus.OK);
        }
    }
}
