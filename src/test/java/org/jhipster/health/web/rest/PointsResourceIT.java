package org.jhipster.health.web.rest;

import org.jhipster.health.TwentyOnePointsApp;
import org.jhipster.health.domain.Points;
import org.jhipster.health.domain.User;
import org.jhipster.health.repository.PointsRepository;
import org.jhipster.health.repository.UserRepository;
import org.jhipster.health.repository.search.PointsSearchRepository;
import org.jhipster.health.service.PointsService;
import org.jhipster.health.service.dto.PointsCriteria;
import org.jhipster.health.service.PointsQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PointsResource} REST controller.
 */
@SpringBootTest(classes = TwentyOnePointsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PointsResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_EXERCISE = 1;
    private static final Integer UPDATED_EXERCISE = 2;
    private static final Integer SMALLER_EXERCISE = 1 - 1;

    private static final Integer DEFAULT_MEALS = 1;
    private static final Integer UPDATED_MEALS = 2;
    private static final Integer SMALLER_MEALS = 1 - 1;

    private static final Integer DEFAULT_ALCOHOL = 1;
    private static final Integer UPDATED_ALCOHOL = 2;
    private static final Integer SMALLER_ALCOHOL = 1 - 1;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private PointsService pointsService;

    /**
     * This repository is mocked in the org.jhipster.health.repository.search test package.
     *
     * @see org.jhipster.health.repository.search.PointsSearchRepositoryMockConfiguration
     */
    @Autowired
    private PointsSearchRepository mockPointsSearchRepository;

    @Autowired
    private PointsQueryService pointsQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPointsMockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private Points points;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Points createEntity(EntityManager em) {
        Points points = new Points()
            .date(DEFAULT_DATE)
            .exercise(DEFAULT_EXERCISE)
            .meals(DEFAULT_MEALS)
            .alcohol(DEFAULT_ALCOHOL)
            .notes(DEFAULT_NOTES);
        return points;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Points createUpdatedEntity(EntityManager em) {
        Points points = new Points()
            .date(UPDATED_DATE)
            .exercise(UPDATED_EXERCISE)
            .meals(UPDATED_MEALS)
            .alcohol(UPDATED_ALCOHOL)
            .notes(UPDATED_NOTES);
        return points;
    }

    @BeforeEach
    public void initTest() {
        points = createEntity(em);
    }

    @Test
    @Transactional
    public void createPoints() throws Exception {
        int databaseSizeBeforeCreate = pointsRepository.findAll().size();
        // Create security-aware mockMvc
        restPointsMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        // Create the Points
        restPointsMockMvc.perform(post("/api/points")
            .with(user("user"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(points)))
            .andExpect(status().isCreated());

        // Validate the Points in the database
        List<Points> pointsList = pointsRepository.findAll();
        assertThat(pointsList).hasSize(databaseSizeBeforeCreate + 1);
        Points testPoints = pointsList.get(pointsList.size() - 1);
        assertThat(testPoints.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPoints.getExercise()).isEqualTo(DEFAULT_EXERCISE);
        assertThat(testPoints.getMeals()).isEqualTo(DEFAULT_MEALS);
        assertThat(testPoints.getAlcohol()).isEqualTo(DEFAULT_ALCOHOL);
        assertThat(testPoints.getNotes()).isEqualTo(DEFAULT_NOTES);

        // Validate the Points in Elasticsearch
        verify(mockPointsSearchRepository, times(1)).save(testPoints);
    }

    @Test
    @Transactional
    public void createPointsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pointsRepository.findAll().size();

        // Create the Points with an existing ID
        points.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPointsMockMvc.perform(post("/api/points")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(points)))
            .andExpect(status().isBadRequest());

        // Validate the Points in the database
        List<Points> pointsList = pointsRepository.findAll();
        assertThat(pointsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Points in Elasticsearch
        verify(mockPointsSearchRepository, times(0)).save(points);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = pointsRepository.findAll().size();
        // set the field null
        points.setDate(null);

        // Create the Points, which fails.


        restPointsMockMvc.perform(post("/api/points")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(points)))
            .andExpect(status().isBadRequest());

        List<Points> pointsList = pointsRepository.findAll();
        assertThat(pointsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPoints() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Create security-aware mockMvc
        restPointsMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        // Get all the pointsList
        restPointsMockMvc.perform(get("/api/points?sort=id,desc")
            .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(points.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].exercise").value(hasItem(DEFAULT_EXERCISE)))
            .andExpect(jsonPath("$.[*].meals").value(hasItem(DEFAULT_MEALS)))
            .andExpect(jsonPath("$.[*].alcohol").value(hasItem(DEFAULT_ALCOHOL)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    public void getPoints() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get the points
        restPointsMockMvc.perform(get("/api/points/{id}", points.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(points.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.exercise").value(DEFAULT_EXERCISE))
            .andExpect(jsonPath("$.meals").value(DEFAULT_MEALS))
            .andExpect(jsonPath("$.alcohol").value(DEFAULT_ALCOHOL))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }


    @Test
    @Transactional
    public void getPointsByIdFiltering() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        Long id = points.getId();

        defaultPointsShouldBeFound("id.equals=" + id);
        defaultPointsShouldNotBeFound("id.notEquals=" + id);

        defaultPointsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPointsShouldNotBeFound("id.greaterThan=" + id);

        defaultPointsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPointsShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPointsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date equals to DEFAULT_DATE
        defaultPointsShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the pointsList where date equals to UPDATED_DATE
        defaultPointsShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPointsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date not equals to DEFAULT_DATE
        defaultPointsShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the pointsList where date not equals to UPDATED_DATE
        defaultPointsShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPointsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date in DEFAULT_DATE or UPDATED_DATE
        defaultPointsShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the pointsList where date equals to UPDATED_DATE
        defaultPointsShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPointsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date is not null
        defaultPointsShouldBeFound("date.specified=true");

        // Get all the pointsList where date is null
        defaultPointsShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllPointsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date is greater than or equal to DEFAULT_DATE
        defaultPointsShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the pointsList where date is greater than or equal to UPDATED_DATE
        defaultPointsShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPointsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date is less than or equal to DEFAULT_DATE
        defaultPointsShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the pointsList where date is less than or equal to SMALLER_DATE
        defaultPointsShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    public void getAllPointsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date is less than DEFAULT_DATE
        defaultPointsShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the pointsList where date is less than UPDATED_DATE
        defaultPointsShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPointsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where date is greater than DEFAULT_DATE
        defaultPointsShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the pointsList where date is greater than SMALLER_DATE
        defaultPointsShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }


    @Test
    @Transactional
    public void getAllPointsByExerciseIsEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise equals to DEFAULT_EXERCISE
        defaultPointsShouldBeFound("exercise.equals=" + DEFAULT_EXERCISE);

        // Get all the pointsList where exercise equals to UPDATED_EXERCISE
        defaultPointsShouldNotBeFound("exercise.equals=" + UPDATED_EXERCISE);
    }

    @Test
    @Transactional
    public void getAllPointsByExerciseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise not equals to DEFAULT_EXERCISE
        defaultPointsShouldNotBeFound("exercise.notEquals=" + DEFAULT_EXERCISE);

        // Get all the pointsList where exercise not equals to UPDATED_EXERCISE
        defaultPointsShouldBeFound("exercise.notEquals=" + UPDATED_EXERCISE);
    }

    @Test
    @Transactional
    public void getAllPointsByExerciseIsInShouldWork() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise in DEFAULT_EXERCISE or UPDATED_EXERCISE
        defaultPointsShouldBeFound("exercise.in=" + DEFAULT_EXERCISE + "," + UPDATED_EXERCISE);

        // Get all the pointsList where exercise equals to UPDATED_EXERCISE
        defaultPointsShouldNotBeFound("exercise.in=" + UPDATED_EXERCISE);
    }

    @Test
    @Transactional
    public void getAllPointsByExerciseIsNullOrNotNull() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise is not null
        defaultPointsShouldBeFound("exercise.specified=true");

        // Get all the pointsList where exercise is null
        defaultPointsShouldNotBeFound("exercise.specified=false");
    }

    @Test
    @Transactional
    public void getAllPointsByExerciseIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise is greater than or equal to DEFAULT_EXERCISE
        defaultPointsShouldBeFound("exercise.greaterThanOrEqual=" + DEFAULT_EXERCISE);

        // Get all the pointsList where exercise is greater than or equal to UPDATED_EXERCISE
        defaultPointsShouldNotBeFound("exercise.greaterThanOrEqual=" + UPDATED_EXERCISE);
    }

    @Test
    @Transactional
    public void getAllPointsByExerciseIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise is less than or equal to DEFAULT_EXERCISE
        defaultPointsShouldBeFound("exercise.lessThanOrEqual=" + DEFAULT_EXERCISE);

        // Get all the pointsList where exercise is less than or equal to SMALLER_EXERCISE
        defaultPointsShouldNotBeFound("exercise.lessThanOrEqual=" + SMALLER_EXERCISE);
    }

    @Test
    @Transactional
    public void getAllPointsByExerciseIsLessThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise is less than DEFAULT_EXERCISE
        defaultPointsShouldNotBeFound("exercise.lessThan=" + DEFAULT_EXERCISE);

        // Get all the pointsList where exercise is less than UPDATED_EXERCISE
        defaultPointsShouldBeFound("exercise.lessThan=" + UPDATED_EXERCISE);
    }

    @Test
    @Transactional
    public void getAllPointsByExerciseIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where exercise is greater than DEFAULT_EXERCISE
        defaultPointsShouldNotBeFound("exercise.greaterThan=" + DEFAULT_EXERCISE);

        // Get all the pointsList where exercise is greater than SMALLER_EXERCISE
        defaultPointsShouldBeFound("exercise.greaterThan=" + SMALLER_EXERCISE);
    }


    @Test
    @Transactional
    public void getAllPointsByMealsIsEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals equals to DEFAULT_MEALS
        defaultPointsShouldBeFound("meals.equals=" + DEFAULT_MEALS);

        // Get all the pointsList where meals equals to UPDATED_MEALS
        defaultPointsShouldNotBeFound("meals.equals=" + UPDATED_MEALS);
    }

    @Test
    @Transactional
    public void getAllPointsByMealsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals not equals to DEFAULT_MEALS
        defaultPointsShouldNotBeFound("meals.notEquals=" + DEFAULT_MEALS);

        // Get all the pointsList where meals not equals to UPDATED_MEALS
        defaultPointsShouldBeFound("meals.notEquals=" + UPDATED_MEALS);
    }

    @Test
    @Transactional
    public void getAllPointsByMealsIsInShouldWork() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals in DEFAULT_MEALS or UPDATED_MEALS
        defaultPointsShouldBeFound("meals.in=" + DEFAULT_MEALS + "," + UPDATED_MEALS);

        // Get all the pointsList where meals equals to UPDATED_MEALS
        defaultPointsShouldNotBeFound("meals.in=" + UPDATED_MEALS);
    }

    @Test
    @Transactional
    public void getAllPointsByMealsIsNullOrNotNull() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals is not null
        defaultPointsShouldBeFound("meals.specified=true");

        // Get all the pointsList where meals is null
        defaultPointsShouldNotBeFound("meals.specified=false");
    }

    @Test
    @Transactional
    public void getAllPointsByMealsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals is greater than or equal to DEFAULT_MEALS
        defaultPointsShouldBeFound("meals.greaterThanOrEqual=" + DEFAULT_MEALS);

        // Get all the pointsList where meals is greater than or equal to UPDATED_MEALS
        defaultPointsShouldNotBeFound("meals.greaterThanOrEqual=" + UPDATED_MEALS);
    }

    @Test
    @Transactional
    public void getAllPointsByMealsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals is less than or equal to DEFAULT_MEALS
        defaultPointsShouldBeFound("meals.lessThanOrEqual=" + DEFAULT_MEALS);

        // Get all the pointsList where meals is less than or equal to SMALLER_MEALS
        defaultPointsShouldNotBeFound("meals.lessThanOrEqual=" + SMALLER_MEALS);
    }

    @Test
    @Transactional
    public void getAllPointsByMealsIsLessThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals is less than DEFAULT_MEALS
        defaultPointsShouldNotBeFound("meals.lessThan=" + DEFAULT_MEALS);

        // Get all the pointsList where meals is less than UPDATED_MEALS
        defaultPointsShouldBeFound("meals.lessThan=" + UPDATED_MEALS);
    }

    @Test
    @Transactional
    public void getAllPointsByMealsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where meals is greater than DEFAULT_MEALS
        defaultPointsShouldNotBeFound("meals.greaterThan=" + DEFAULT_MEALS);

        // Get all the pointsList where meals is greater than SMALLER_MEALS
        defaultPointsShouldBeFound("meals.greaterThan=" + SMALLER_MEALS);
    }


    @Test
    @Transactional
    public void getAllPointsByAlcoholIsEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol equals to DEFAULT_ALCOHOL
        defaultPointsShouldBeFound("alcohol.equals=" + DEFAULT_ALCOHOL);

        // Get all the pointsList where alcohol equals to UPDATED_ALCOHOL
        defaultPointsShouldNotBeFound("alcohol.equals=" + UPDATED_ALCOHOL);
    }

    @Test
    @Transactional
    public void getAllPointsByAlcoholIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol not equals to DEFAULT_ALCOHOL
        defaultPointsShouldNotBeFound("alcohol.notEquals=" + DEFAULT_ALCOHOL);

        // Get all the pointsList where alcohol not equals to UPDATED_ALCOHOL
        defaultPointsShouldBeFound("alcohol.notEquals=" + UPDATED_ALCOHOL);
    }

    @Test
    @Transactional
    public void getAllPointsByAlcoholIsInShouldWork() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol in DEFAULT_ALCOHOL or UPDATED_ALCOHOL
        defaultPointsShouldBeFound("alcohol.in=" + DEFAULT_ALCOHOL + "," + UPDATED_ALCOHOL);

        // Get all the pointsList where alcohol equals to UPDATED_ALCOHOL
        defaultPointsShouldNotBeFound("alcohol.in=" + UPDATED_ALCOHOL);
    }

    @Test
    @Transactional
    public void getAllPointsByAlcoholIsNullOrNotNull() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol is not null
        defaultPointsShouldBeFound("alcohol.specified=true");

        // Get all the pointsList where alcohol is null
        defaultPointsShouldNotBeFound("alcohol.specified=false");
    }

    @Test
    @Transactional
    public void getAllPointsByAlcoholIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol is greater than or equal to DEFAULT_ALCOHOL
        defaultPointsShouldBeFound("alcohol.greaterThanOrEqual=" + DEFAULT_ALCOHOL);

        // Get all the pointsList where alcohol is greater than or equal to UPDATED_ALCOHOL
        defaultPointsShouldNotBeFound("alcohol.greaterThanOrEqual=" + UPDATED_ALCOHOL);
    }

    @Test
    @Transactional
    public void getAllPointsByAlcoholIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol is less than or equal to DEFAULT_ALCOHOL
        defaultPointsShouldBeFound("alcohol.lessThanOrEqual=" + DEFAULT_ALCOHOL);

        // Get all the pointsList where alcohol is less than or equal to SMALLER_ALCOHOL
        defaultPointsShouldNotBeFound("alcohol.lessThanOrEqual=" + SMALLER_ALCOHOL);
    }

    @Test
    @Transactional
    public void getAllPointsByAlcoholIsLessThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol is less than DEFAULT_ALCOHOL
        defaultPointsShouldNotBeFound("alcohol.lessThan=" + DEFAULT_ALCOHOL);

        // Get all the pointsList where alcohol is less than UPDATED_ALCOHOL
        defaultPointsShouldBeFound("alcohol.lessThan=" + UPDATED_ALCOHOL);
    }

    @Test
    @Transactional
    public void getAllPointsByAlcoholIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where alcohol is greater than DEFAULT_ALCOHOL
        defaultPointsShouldNotBeFound("alcohol.greaterThan=" + DEFAULT_ALCOHOL);

        // Get all the pointsList where alcohol is greater than SMALLER_ALCOHOL
        defaultPointsShouldBeFound("alcohol.greaterThan=" + SMALLER_ALCOHOL);
    }


    @Test
    @Transactional
    public void getAllPointsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where notes equals to DEFAULT_NOTES
        defaultPointsShouldBeFound("notes.equals=" + DEFAULT_NOTES);

        // Get all the pointsList where notes equals to UPDATED_NOTES
        defaultPointsShouldNotBeFound("notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllPointsByNotesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where notes not equals to DEFAULT_NOTES
        defaultPointsShouldNotBeFound("notes.notEquals=" + DEFAULT_NOTES);

        // Get all the pointsList where notes not equals to UPDATED_NOTES
        defaultPointsShouldBeFound("notes.notEquals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllPointsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where notes in DEFAULT_NOTES or UPDATED_NOTES
        defaultPointsShouldBeFound("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES);

        // Get all the pointsList where notes equals to UPDATED_NOTES
        defaultPointsShouldNotBeFound("notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllPointsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where notes is not null
        defaultPointsShouldBeFound("notes.specified=true");

        // Get all the pointsList where notes is null
        defaultPointsShouldNotBeFound("notes.specified=false");
    }
    @Test
    @Transactional
    public void getAllPointsByNotesContainsSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where notes contains DEFAULT_NOTES
        defaultPointsShouldBeFound("notes.contains=" + DEFAULT_NOTES);

        // Get all the pointsList where notes contains UPDATED_NOTES
        defaultPointsShouldNotBeFound("notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllPointsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get all the pointsList where notes does not contain DEFAULT_NOTES
        defaultPointsShouldNotBeFound("notes.doesNotContain=" + DEFAULT_NOTES);

        // Get all the pointsList where notes does not contain UPDATED_NOTES
        defaultPointsShouldBeFound("notes.doesNotContain=" + UPDATED_NOTES);
    }


    @Test
    @Transactional
    public void getAllPointsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        points.setUser(user);
        pointsRepository.saveAndFlush(points);
        Long userId = user.getId();

        // Get all the pointsList where user equals to userId
        defaultPointsShouldBeFound("userId.equals=" + userId);

        // Get all the pointsList where user equals to userId + 1
        defaultPointsShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPointsShouldBeFound(String filter) throws Exception {
        restPointsMockMvc.perform(get("/api/points?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(points.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].exercise").value(hasItem(DEFAULT_EXERCISE)))
            .andExpect(jsonPath("$.[*].meals").value(hasItem(DEFAULT_MEALS)))
            .andExpect(jsonPath("$.[*].alcohol").value(hasItem(DEFAULT_ALCOHOL)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restPointsMockMvc.perform(get("/api/points/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPointsShouldNotBeFound(String filter) throws Exception {
        restPointsMockMvc.perform(get("/api/points?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPointsMockMvc.perform(get("/api/points/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPoints() throws Exception {
        // Get the points
        restPointsMockMvc.perform(get("/api/points/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePoints() throws Exception {
        // Initialize the database
        pointsService.save(points);

        int databaseSizeBeforeUpdate = pointsRepository.findAll().size();

        // Update the points
        Points updatedPoints = pointsRepository.findById(points.getId()).get();
        // Disconnect from session so that the updates on updatedPoints are not directly saved in db
        em.detach(updatedPoints);
        updatedPoints
            .date(UPDATED_DATE)
            .exercise(UPDATED_EXERCISE)
            .meals(UPDATED_MEALS)
            .alcohol(UPDATED_ALCOHOL)
            .notes(UPDATED_NOTES);

        restPointsMockMvc.perform(put("/api/points")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPoints)))
            .andExpect(status().isOk());

        // Validate the Points in the database
        List<Points> pointsList = pointsRepository.findAll();
        assertThat(pointsList).hasSize(databaseSizeBeforeUpdate);
        Points testPoints = pointsList.get(pointsList.size() - 1);
        assertThat(testPoints.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPoints.getExercise()).isEqualTo(UPDATED_EXERCISE);
        assertThat(testPoints.getMeals()).isEqualTo(UPDATED_MEALS);
        assertThat(testPoints.getAlcohol()).isEqualTo(UPDATED_ALCOHOL);
        assertThat(testPoints.getNotes()).isEqualTo(UPDATED_NOTES);

        // Validate the Points in Elasticsearch
        verify(mockPointsSearchRepository, times(2)).save(testPoints);
    }

    @Test
    @Transactional
    public void updateNonExistingPoints() throws Exception {
        int databaseSizeBeforeUpdate = pointsRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPointsMockMvc.perform(put("/api/points")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(points)))
            .andExpect(status().isBadRequest());

        // Validate the Points in the database
        List<Points> pointsList = pointsRepository.findAll();
        assertThat(pointsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Points in Elasticsearch
        verify(mockPointsSearchRepository, times(0)).save(points);
    }

    @Test
    @Transactional
    public void deletePoints() throws Exception {
        // Initialize the database
        pointsService.save(points);

        int databaseSizeBeforeDelete = pointsRepository.findAll().size();

        // Delete the points
        restPointsMockMvc.perform(delete("/api/points/{id}", points.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Points> pointsList = pointsRepository.findAll();
        assertThat(pointsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Points in Elasticsearch
        verify(mockPointsSearchRepository, times(1)).deleteById(points.getId());
    }

    @Test
    @Transactional
    public void searchPoints() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        pointsService.save(points);
        when(mockPointsSearchRepository.search(queryStringQuery("id:" + points.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(points), PageRequest.of(0, 1), 1));

        // Search the points
        restPointsMockMvc.perform(get("/api/_search/points?query=id:" + points.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(points.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].exercise").value(hasItem(DEFAULT_EXERCISE)))
            .andExpect(jsonPath("$.[*].meals").value(hasItem(DEFAULT_MEALS)))
            .andExpect(jsonPath("$.[*].alcohol").value(hasItem(DEFAULT_ALCOHOL)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    private void createPointsByWeek(LocalDate thisMonday, LocalDate lastMonday) {
        User user = userRepository.findOneByLogin("user").get();
// Create points in two separate weeks
        points = new Points(thisMonday.plusDays(2), 1, 1, 1, user);
        pointsRepository.saveAndFlush(points);
        points = new Points(thisMonday.plusDays(3), 1, 1, 0, user);
        pointsRepository.saveAndFlush(points);
        points = new Points(lastMonday.plusDays(3), 0, 0, 1, user);
        pointsRepository.saveAndFlush(points);
        points = new Points(lastMonday.plusDays(4), 1, 1, 0, user);
        pointsRepository.saveAndFlush(points);
    }
    @Test
    @Transactional
    public void getPointsThisWeek() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate thisMonday = today.with(DayOfWeek.MONDAY);
        LocalDate lastMonday = thisMonday.minusWeeks(1);
        createPointsByWeek(thisMonday, lastMonday);
// create security-aware mockMvc
        restPointsMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
// Get all the points
        restPointsMockMvc.perform(get("/api/points")
            .with(user("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(4)));
// Get the points for this week only
        restPointsMockMvc.perform(get("/api/points-this-week")
            .with(user("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.week").value(thisMonday.toString()))
            .andExpect(jsonPath("$.points").value(5));
    }
}
