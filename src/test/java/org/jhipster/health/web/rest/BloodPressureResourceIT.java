package org.jhipster.health.web.rest;

import org.jhipster.health.TwentyOnePointsApp;
import org.jhipster.health.domain.BloodPressure;
import org.jhipster.health.domain.User;
import org.jhipster.health.repository.BloodPressureRepository;
import org.jhipster.health.service.BloodPressureService;
import org.jhipster.health.service.dto.BloodPressureCriteria;
import org.jhipster.health.service.BloodPressureQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BloodPressureResource} REST controller.
 */
@SpringBootTest(classes = TwentyOnePointsApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class BloodPressureResourceIT {

    private static final LocalDate DEFAULT_TIMESTAMP = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIMESTAMP = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TIMESTAMP = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_SYSTOLIC = 1;
    private static final Integer UPDATED_SYSTOLIC = 2;
    private static final Integer SMALLER_SYSTOLIC = 1 - 1;

    private static final Integer DEFAULT_DIASTOLIC = 1;
    private static final Integer UPDATED_DIASTOLIC = 2;
    private static final Integer SMALLER_DIASTOLIC = 1 - 1;

    @Autowired
    private BloodPressureRepository bloodPressureRepository;

    @Autowired
    private BloodPressureService bloodPressureService;

    @Autowired
    private BloodPressureQueryService bloodPressureQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBloodPressureMockMvc;

    private BloodPressure bloodPressure;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BloodPressure createEntity(EntityManager em) {
        BloodPressure bloodPressure = new BloodPressure()
            .timestamp(DEFAULT_TIMESTAMP)
            .systolic(DEFAULT_SYSTOLIC)
            .diastolic(DEFAULT_DIASTOLIC);
        return bloodPressure;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BloodPressure createUpdatedEntity(EntityManager em) {
        BloodPressure bloodPressure = new BloodPressure()
            .timestamp(UPDATED_TIMESTAMP)
            .systolic(UPDATED_SYSTOLIC)
            .diastolic(UPDATED_DIASTOLIC);
        return bloodPressure;
    }

    @BeforeEach
    public void initTest() {
        bloodPressure = createEntity(em);
    }

    @Test
    @Transactional
    public void createBloodPressure() throws Exception {
        int databaseSizeBeforeCreate = bloodPressureRepository.findAll().size();
        // Create the BloodPressure
        restBloodPressureMockMvc.perform(post("/api/blood-pressures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isCreated());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeCreate + 1);
        BloodPressure testBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        assertThat(testBloodPressure.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testBloodPressure.getSystolic()).isEqualTo(DEFAULT_SYSTOLIC);
        assertThat(testBloodPressure.getDiastolic()).isEqualTo(DEFAULT_DIASTOLIC);
    }

    @Test
    @Transactional
    public void createBloodPressureWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bloodPressureRepository.findAll().size();

        // Create the BloodPressure with an existing ID
        bloodPressure.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBloodPressureMockMvc.perform(post("/api/blood-pressures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodPressureRepository.findAll().size();
        // set the field null
        bloodPressure.setTimestamp(null);

        // Create the BloodPressure, which fails.


        restBloodPressureMockMvc.perform(post("/api/blood-pressures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSystolicIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodPressureRepository.findAll().size();
        // set the field null
        bloodPressure.setSystolic(null);

        // Create the BloodPressure, which fails.


        restBloodPressureMockMvc.perform(post("/api/blood-pressures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDiastolicIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodPressureRepository.findAll().size();
        // set the field null
        bloodPressure.setDiastolic(null);

        // Create the BloodPressure, which fails.


        restBloodPressureMockMvc.perform(post("/api/blood-pressures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBloodPressures() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList
        restBloodPressureMockMvc.perform(get("/api/blood-pressures?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bloodPressure.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].systolic").value(hasItem(DEFAULT_SYSTOLIC)))
            .andExpect(jsonPath("$.[*].diastolic").value(hasItem(DEFAULT_DIASTOLIC)));
    }
    
    @Test
    @Transactional
    public void getBloodPressure() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get the bloodPressure
        restBloodPressureMockMvc.perform(get("/api/blood-pressures/{id}", bloodPressure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bloodPressure.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.systolic").value(DEFAULT_SYSTOLIC))
            .andExpect(jsonPath("$.diastolic").value(DEFAULT_DIASTOLIC));
    }


    @Test
    @Transactional
    public void getBloodPressuresByIdFiltering() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        Long id = bloodPressure.getId();

        defaultBloodPressureShouldBeFound("id.equals=" + id);
        defaultBloodPressureShouldNotBeFound("id.notEquals=" + id);

        defaultBloodPressureShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBloodPressureShouldNotBeFound("id.greaterThan=" + id);

        defaultBloodPressureShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBloodPressureShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp equals to DEFAULT_TIMESTAMP
        defaultBloodPressureShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the bloodPressureList where timestamp equals to UPDATED_TIMESTAMP
        defaultBloodPressureShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp not equals to DEFAULT_TIMESTAMP
        defaultBloodPressureShouldNotBeFound("timestamp.notEquals=" + DEFAULT_TIMESTAMP);

        // Get all the bloodPressureList where timestamp not equals to UPDATED_TIMESTAMP
        defaultBloodPressureShouldBeFound("timestamp.notEquals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultBloodPressureShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the bloodPressureList where timestamp equals to UPDATED_TIMESTAMP
        defaultBloodPressureShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp is not null
        defaultBloodPressureShouldBeFound("timestamp.specified=true");

        // Get all the bloodPressureList where timestamp is null
        defaultBloodPressureShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp is greater than or equal to DEFAULT_TIMESTAMP
        defaultBloodPressureShouldBeFound("timestamp.greaterThanOrEqual=" + DEFAULT_TIMESTAMP);

        // Get all the bloodPressureList where timestamp is greater than or equal to UPDATED_TIMESTAMP
        defaultBloodPressureShouldNotBeFound("timestamp.greaterThanOrEqual=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp is less than or equal to DEFAULT_TIMESTAMP
        defaultBloodPressureShouldBeFound("timestamp.lessThanOrEqual=" + DEFAULT_TIMESTAMP);

        // Get all the bloodPressureList where timestamp is less than or equal to SMALLER_TIMESTAMP
        defaultBloodPressureShouldNotBeFound("timestamp.lessThanOrEqual=" + SMALLER_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp is less than DEFAULT_TIMESTAMP
        defaultBloodPressureShouldNotBeFound("timestamp.lessThan=" + DEFAULT_TIMESTAMP);

        // Get all the bloodPressureList where timestamp is less than UPDATED_TIMESTAMP
        defaultBloodPressureShouldBeFound("timestamp.lessThan=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where timestamp is greater than DEFAULT_TIMESTAMP
        defaultBloodPressureShouldNotBeFound("timestamp.greaterThan=" + DEFAULT_TIMESTAMP);

        // Get all the bloodPressureList where timestamp is greater than SMALLER_TIMESTAMP
        defaultBloodPressureShouldBeFound("timestamp.greaterThan=" + SMALLER_TIMESTAMP);
    }


    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic equals to DEFAULT_SYSTOLIC
        defaultBloodPressureShouldBeFound("systolic.equals=" + DEFAULT_SYSTOLIC);

        // Get all the bloodPressureList where systolic equals to UPDATED_SYSTOLIC
        defaultBloodPressureShouldNotBeFound("systolic.equals=" + UPDATED_SYSTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic not equals to DEFAULT_SYSTOLIC
        defaultBloodPressureShouldNotBeFound("systolic.notEquals=" + DEFAULT_SYSTOLIC);

        // Get all the bloodPressureList where systolic not equals to UPDATED_SYSTOLIC
        defaultBloodPressureShouldBeFound("systolic.notEquals=" + UPDATED_SYSTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsInShouldWork() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic in DEFAULT_SYSTOLIC or UPDATED_SYSTOLIC
        defaultBloodPressureShouldBeFound("systolic.in=" + DEFAULT_SYSTOLIC + "," + UPDATED_SYSTOLIC);

        // Get all the bloodPressureList where systolic equals to UPDATED_SYSTOLIC
        defaultBloodPressureShouldNotBeFound("systolic.in=" + UPDATED_SYSTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsNullOrNotNull() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic is not null
        defaultBloodPressureShouldBeFound("systolic.specified=true");

        // Get all the bloodPressureList where systolic is null
        defaultBloodPressureShouldNotBeFound("systolic.specified=false");
    }

    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic is greater than or equal to DEFAULT_SYSTOLIC
        defaultBloodPressureShouldBeFound("systolic.greaterThanOrEqual=" + DEFAULT_SYSTOLIC);

        // Get all the bloodPressureList where systolic is greater than or equal to UPDATED_SYSTOLIC
        defaultBloodPressureShouldNotBeFound("systolic.greaterThanOrEqual=" + UPDATED_SYSTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic is less than or equal to DEFAULT_SYSTOLIC
        defaultBloodPressureShouldBeFound("systolic.lessThanOrEqual=" + DEFAULT_SYSTOLIC);

        // Get all the bloodPressureList where systolic is less than or equal to SMALLER_SYSTOLIC
        defaultBloodPressureShouldNotBeFound("systolic.lessThanOrEqual=" + SMALLER_SYSTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsLessThanSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic is less than DEFAULT_SYSTOLIC
        defaultBloodPressureShouldNotBeFound("systolic.lessThan=" + DEFAULT_SYSTOLIC);

        // Get all the bloodPressureList where systolic is less than UPDATED_SYSTOLIC
        defaultBloodPressureShouldBeFound("systolic.lessThan=" + UPDATED_SYSTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresBySystolicIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where systolic is greater than DEFAULT_SYSTOLIC
        defaultBloodPressureShouldNotBeFound("systolic.greaterThan=" + DEFAULT_SYSTOLIC);

        // Get all the bloodPressureList where systolic is greater than SMALLER_SYSTOLIC
        defaultBloodPressureShouldBeFound("systolic.greaterThan=" + SMALLER_SYSTOLIC);
    }


    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic equals to DEFAULT_DIASTOLIC
        defaultBloodPressureShouldBeFound("diastolic.equals=" + DEFAULT_DIASTOLIC);

        // Get all the bloodPressureList where diastolic equals to UPDATED_DIASTOLIC
        defaultBloodPressureShouldNotBeFound("diastolic.equals=" + UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic not equals to DEFAULT_DIASTOLIC
        defaultBloodPressureShouldNotBeFound("diastolic.notEquals=" + DEFAULT_DIASTOLIC);

        // Get all the bloodPressureList where diastolic not equals to UPDATED_DIASTOLIC
        defaultBloodPressureShouldBeFound("diastolic.notEquals=" + UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsInShouldWork() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic in DEFAULT_DIASTOLIC or UPDATED_DIASTOLIC
        defaultBloodPressureShouldBeFound("diastolic.in=" + DEFAULT_DIASTOLIC + "," + UPDATED_DIASTOLIC);

        // Get all the bloodPressureList where diastolic equals to UPDATED_DIASTOLIC
        defaultBloodPressureShouldNotBeFound("diastolic.in=" + UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsNullOrNotNull() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic is not null
        defaultBloodPressureShouldBeFound("diastolic.specified=true");

        // Get all the bloodPressureList where diastolic is null
        defaultBloodPressureShouldNotBeFound("diastolic.specified=false");
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic is greater than or equal to DEFAULT_DIASTOLIC
        defaultBloodPressureShouldBeFound("diastolic.greaterThanOrEqual=" + DEFAULT_DIASTOLIC);

        // Get all the bloodPressureList where diastolic is greater than or equal to UPDATED_DIASTOLIC
        defaultBloodPressureShouldNotBeFound("diastolic.greaterThanOrEqual=" + UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic is less than or equal to DEFAULT_DIASTOLIC
        defaultBloodPressureShouldBeFound("diastolic.lessThanOrEqual=" + DEFAULT_DIASTOLIC);

        // Get all the bloodPressureList where diastolic is less than or equal to SMALLER_DIASTOLIC
        defaultBloodPressureShouldNotBeFound("diastolic.lessThanOrEqual=" + SMALLER_DIASTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsLessThanSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic is less than DEFAULT_DIASTOLIC
        defaultBloodPressureShouldNotBeFound("diastolic.lessThan=" + DEFAULT_DIASTOLIC);

        // Get all the bloodPressureList where diastolic is less than UPDATED_DIASTOLIC
        defaultBloodPressureShouldBeFound("diastolic.lessThan=" + UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    public void getAllBloodPressuresByDiastolicIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList where diastolic is greater than DEFAULT_DIASTOLIC
        defaultBloodPressureShouldNotBeFound("diastolic.greaterThan=" + DEFAULT_DIASTOLIC);

        // Get all the bloodPressureList where diastolic is greater than SMALLER_DIASTOLIC
        defaultBloodPressureShouldBeFound("diastolic.greaterThan=" + SMALLER_DIASTOLIC);
    }


    @Test
    @Transactional
    public void getAllBloodPressuresByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        bloodPressure.setUser(user);
        bloodPressureRepository.saveAndFlush(bloodPressure);
        Long userId = user.getId();

        // Get all the bloodPressureList where user equals to userId
        defaultBloodPressureShouldBeFound("userId.equals=" + userId);

        // Get all the bloodPressureList where user equals to userId + 1
        defaultBloodPressureShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBloodPressureShouldBeFound(String filter) throws Exception {
        restBloodPressureMockMvc.perform(get("/api/blood-pressures?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bloodPressure.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].systolic").value(hasItem(DEFAULT_SYSTOLIC)))
            .andExpect(jsonPath("$.[*].diastolic").value(hasItem(DEFAULT_DIASTOLIC)));

        // Check, that the count call also returns 1
        restBloodPressureMockMvc.perform(get("/api/blood-pressures/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBloodPressureShouldNotBeFound(String filter) throws Exception {
        restBloodPressureMockMvc.perform(get("/api/blood-pressures?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBloodPressureMockMvc.perform(get("/api/blood-pressures/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBloodPressure() throws Exception {
        // Get the bloodPressure
        restBloodPressureMockMvc.perform(get("/api/blood-pressures/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBloodPressure() throws Exception {
        // Initialize the database
        bloodPressureService.save(bloodPressure);

        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();

        // Update the bloodPressure
        BloodPressure updatedBloodPressure = bloodPressureRepository.findById(bloodPressure.getId()).get();
        // Disconnect from session so that the updates on updatedBloodPressure are not directly saved in db
        em.detach(updatedBloodPressure);
        updatedBloodPressure
            .timestamp(UPDATED_TIMESTAMP)
            .systolic(UPDATED_SYSTOLIC)
            .diastolic(UPDATED_DIASTOLIC);

        restBloodPressureMockMvc.perform(put("/api/blood-pressures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedBloodPressure)))
            .andExpect(status().isOk());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
        BloodPressure testBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        assertThat(testBloodPressure.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testBloodPressure.getSystolic()).isEqualTo(UPDATED_SYSTOLIC);
        assertThat(testBloodPressure.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    public void updateNonExistingBloodPressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBloodPressureMockMvc.perform(put("/api/blood-pressures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBloodPressure() throws Exception {
        // Initialize the database
        bloodPressureService.save(bloodPressure);

        int databaseSizeBeforeDelete = bloodPressureRepository.findAll().size();

        // Delete the bloodPressure
        restBloodPressureMockMvc.perform(delete("/api/blood-pressures/{id}", bloodPressure.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBloodPressure() throws Exception {
        // Initialize the database
        bloodPressureService.save(bloodPressure);

        // Search the bloodPressure
        restBloodPressureMockMvc.perform(get("/api/_search/blood-pressures?query=id:" + bloodPressure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bloodPressure.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].systolic").value(hasItem(DEFAULT_SYSTOLIC)))
            .andExpect(jsonPath("$.[*].diastolic").value(hasItem(DEFAULT_DIASTOLIC)));
    }
}
