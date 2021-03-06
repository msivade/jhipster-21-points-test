package org.jhipster.health.web.rest;

import io.micrometer.core.annotation.Timed;
import org.jhipster.health.domain.Points;
import org.jhipster.health.security.AuthoritiesConstants;
import org.jhipster.health.security.SecurityUtils;
import org.jhipster.health.service.PointsService;
import org.jhipster.health.service.UserService;
import org.jhipster.health.web.rest.errors.BadRequestAlertException;
import org.jhipster.health.service.dto.PointsCriteria;
import org.jhipster.health.service.PointsQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.jhipster.health.web.rest.vm.PointsPerWeekVM;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link org.jhipster.health.domain.Points}.
 */
@RestController
@RequestMapping("/api")
public class PointsResource {

    private final Logger log = LoggerFactory.getLogger(PointsResource.class);

    private static final String ENTITY_NAME = "points";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PointsService pointsService;

    private final PointsQueryService pointsQueryService;

    private final UserService userService;

    public PointsResource(PointsService pointsService, PointsQueryService pointsQueryService,
                          UserService userService) {
        this.pointsService = pointsService;
        this.pointsQueryService = pointsQueryService;
        this.userService = userService;
    }

    /**
     * {@code POST  /points} : Create a new points.
     *
     * @param points the points to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new points, or with status {@code 400 (Bad Request)} if the points has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/points")
    public ResponseEntity<Points> createPoints(@Valid @RequestBody Points points) throws URISyntaxException {
        log.debug("REST request to save Points : {}", points);
        if (points.getId() != null) {
            throw new BadRequestAlertException("A new points cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
            log.debug("No user passed in, using current user: {}", SecurityUtils.getCurrentUserLogin());
            points.setUser(userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get());
        }
        Points result = pointsService.save(points);
        return ResponseEntity.created(new URI("/api/points/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /points} : Updates an existing points.
     *
     * @param points the points to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated points,
     * or with status {@code 400 (Bad Request)} if the points is not valid,
     * or with status {@code 500 (Internal Server Error)} if the points couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/points")
    public ResponseEntity<Points> updatePoints(@Valid @RequestBody Points points) throws URISyntaxException {
        log.debug("REST request to update Points : {}", points);
        if (points.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Points result = pointsService.save(points);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, points.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /points} : get all the points.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of points in body.
     */
    @GetMapping("/points")
    public ResponseEntity<List<Points>> getAllPoints(PointsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Points by criteria: {}", criteria);
        Page<Points> page;
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            page = pointsService.findAllByOrderByDateDesc(pageable);
        } else {
            page = pointsService.findByUserIsCurrentUser(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /points/count} : count all the points.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/points/count")
    public ResponseEntity<Long> countPoints(PointsCriteria criteria) {
        log.debug("REST request to count Points by criteria: {}", criteria);
        return ResponseEntity.ok().body(pointsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /points/:id} : get the "id" points.
     *
     * @param id the id of the points to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the points, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/points/{id}")
    public ResponseEntity<Points> getPoints(@PathVariable Long id) {
        log.debug("REST request to get Points : {}", id);
        Optional<Points> points = pointsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(points);
    }

    /**
     * {@code DELETE  /points/:id} : delete the "id" points.
     *
     * @param id the id of the points to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/points/{id}")
    public ResponseEntity<Void> deletePoints(@PathVariable Long id) {
        log.debug("REST request to delete Points : {}", id);
        pointsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/points?query=:query} : search for the points corresponding
     * to the query.
     *
     * @param query the query of the points search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/points")
    public ResponseEntity<List<Points>> searchPoints(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Points for query {}", query);
        Page<Points> page = pointsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET /points : get all the points for the current week.
     */
    @GetMapping("/points-this-week")
    @Timed
    public ResponseEntity<PointsPerWeekVM> getPointsThisWeek(
        @RequestParam(value="tz", required=false) String timezone) {
// Get current date (with timezone if passed in)
        LocalDate now = LocalDate.now();
        if (timezone != null) {
            now = LocalDate.now(ZoneId.of(timezone));
        }
// Get first day of week
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
// Get last day of week
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        log.debug("Looking for points between: {} and {}", startOfWeek, endOfWeek);
        List<Points> points =
            pointsService.findAllByDateBetweenAndUserLogin(
                startOfWeek, endOfWeek, SecurityUtils.getCurrentUserLogin());
        return calculatePoints(startOfWeek, points);
    }
    private ResponseEntity<PointsPerWeekVM> calculatePoints(LocalDate startOfWeek,
                                                          List<Points> points) {
        Integer numPoints = points.stream()
            .mapToInt(p -> p.getExercise() + p.getMeals() + p.getAlcohol())
            .sum();
        PointsPerWeekVM count = new PointsPerWeekVM(startOfWeek, numPoints);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
