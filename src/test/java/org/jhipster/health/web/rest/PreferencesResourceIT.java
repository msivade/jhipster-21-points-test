package org.jhipster.health.web.rest;

import org.jhipster.health.TwentyOnePointsApp;
import org.jhipster.health.domain.Preferences;
import org.jhipster.health.domain.User;
import org.jhipster.health.repository.PreferencesRepository;
import org.jhipster.health.service.PreferencesService;
import org.jhipster.health.service.dto.PreferencesCriteria;
import org.jhipster.health.service.PreferencesQueryService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.jhipster.health.domain.enumeration.Units;
/**
 * Integration tests for the {@link PreferencesResource} REST controller.
 */
@SpringBootTest(classes = TwentyOnePointsApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class PreferencesResourceIT {

    private static final Integer DEFAULT_WEEKLY_GOAL = 10;
    private static final Integer UPDATED_WEEKLY_GOAL = 11;
    private static final Integer SMALLER_WEEKLY_GOAL = 10 - 1;

    private static final Units DEFAULT_WEIGHT_UNITS = Units.KG;
    private static final Units UPDATED_WEIGHT_UNITS = Units.LB;

    @Autowired
    private PreferencesRepository preferencesRepository;

    @Autowired
    private PreferencesService preferencesService;

    @Autowired
    private PreferencesQueryService preferencesQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPreferencesMockMvc;

    private Preferences preferences;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Preferences createEntity(EntityManager em) {
        Preferences preferences = new Preferences()
            .weeklyGoal(DEFAULT_WEEKLY_GOAL)
            .weightUnits(DEFAULT_WEIGHT_UNITS);
        return preferences;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Preferences createUpdatedEntity(EntityManager em) {
        Preferences preferences = new Preferences()
            .weeklyGoal(UPDATED_WEEKLY_GOAL)
            .weightUnits(UPDATED_WEIGHT_UNITS);
        return preferences;
    }

    @BeforeEach
    public void initTest() {
        preferences = createEntity(em);
    }

    @Test
    @Transactional
    public void createPreferences() throws Exception {
        int databaseSizeBeforeCreate = preferencesRepository.findAll().size();
        // Create the Preferences
        restPreferencesMockMvc.perform(post("/api/preferences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(preferences)))
            .andExpect(status().isCreated());

        // Validate the Preferences in the database
        List<Preferences> preferencesList = preferencesRepository.findAll();
        assertThat(preferencesList).hasSize(databaseSizeBeforeCreate + 1);
        Preferences testPreferences = preferencesList.get(preferencesList.size() - 1);
        assertThat(testPreferences.getWeeklyGoal()).isEqualTo(DEFAULT_WEEKLY_GOAL);
        assertThat(testPreferences.getWeightUnits()).isEqualTo(DEFAULT_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void createPreferencesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = preferencesRepository.findAll().size();

        // Create the Preferences with an existing ID
        preferences.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPreferencesMockMvc.perform(post("/api/preferences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(preferences)))
            .andExpect(status().isBadRequest());

        // Validate the Preferences in the database
        List<Preferences> preferencesList = preferencesRepository.findAll();
        assertThat(preferencesList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkWeightUnitsIsRequired() throws Exception {
        int databaseSizeBeforeTest = preferencesRepository.findAll().size();
        // set the field null
        preferences.setWeightUnits(null);

        // Create the Preferences, which fails.


        restPreferencesMockMvc.perform(post("/api/preferences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(preferences)))
            .andExpect(status().isBadRequest());

        List<Preferences> preferencesList = preferencesRepository.findAll();
        assertThat(preferencesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList
        restPreferencesMockMvc.perform(get("/api/preferences?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(preferences.getId().intValue())))
            .andExpect(jsonPath("$.[*].weeklyGoal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
            .andExpect(jsonPath("$.[*].weightUnits").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));
    }
    
    @Test
    @Transactional
    public void getPreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get the preferences
        restPreferencesMockMvc.perform(get("/api/preferences/{id}", preferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(preferences.getId().intValue()))
            .andExpect(jsonPath("$.weeklyGoal").value(DEFAULT_WEEKLY_GOAL))
            .andExpect(jsonPath("$.weightUnits").value(DEFAULT_WEIGHT_UNITS.toString()));
    }


    @Test
    @Transactional
    public void getPreferencesByIdFiltering() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        Long id = preferences.getId();

        defaultPreferencesShouldBeFound("id.equals=" + id);
        defaultPreferencesShouldNotBeFound("id.notEquals=" + id);

        defaultPreferencesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPreferencesShouldNotBeFound("id.greaterThan=" + id);

        defaultPreferencesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPreferencesShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsEqualToSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal equals to DEFAULT_WEEKLY_GOAL
        defaultPreferencesShouldBeFound("weeklyGoal.equals=" + DEFAULT_WEEKLY_GOAL);

        // Get all the preferencesList where weeklyGoal equals to UPDATED_WEEKLY_GOAL
        defaultPreferencesShouldNotBeFound("weeklyGoal.equals=" + UPDATED_WEEKLY_GOAL);
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal not equals to DEFAULT_WEEKLY_GOAL
        defaultPreferencesShouldNotBeFound("weeklyGoal.notEquals=" + DEFAULT_WEEKLY_GOAL);

        // Get all the preferencesList where weeklyGoal not equals to UPDATED_WEEKLY_GOAL
        defaultPreferencesShouldBeFound("weeklyGoal.notEquals=" + UPDATED_WEEKLY_GOAL);
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsInShouldWork() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal in DEFAULT_WEEKLY_GOAL or UPDATED_WEEKLY_GOAL
        defaultPreferencesShouldBeFound("weeklyGoal.in=" + DEFAULT_WEEKLY_GOAL + "," + UPDATED_WEEKLY_GOAL);

        // Get all the preferencesList where weeklyGoal equals to UPDATED_WEEKLY_GOAL
        defaultPreferencesShouldNotBeFound("weeklyGoal.in=" + UPDATED_WEEKLY_GOAL);
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsNullOrNotNull() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal is not null
        defaultPreferencesShouldBeFound("weeklyGoal.specified=true");

        // Get all the preferencesList where weeklyGoal is null
        defaultPreferencesShouldNotBeFound("weeklyGoal.specified=false");
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal is greater than or equal to DEFAULT_WEEKLY_GOAL
        defaultPreferencesShouldBeFound("weeklyGoal.greaterThanOrEqual=" + DEFAULT_WEEKLY_GOAL);

        // Get all the preferencesList where weeklyGoal is greater than or equal to (DEFAULT_WEEKLY_GOAL + 1)
        defaultPreferencesShouldNotBeFound("weeklyGoal.greaterThanOrEqual=" + (DEFAULT_WEEKLY_GOAL + 1));
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal is less than or equal to DEFAULT_WEEKLY_GOAL
        defaultPreferencesShouldBeFound("weeklyGoal.lessThanOrEqual=" + DEFAULT_WEEKLY_GOAL);

        // Get all the preferencesList where weeklyGoal is less than or equal to SMALLER_WEEKLY_GOAL
        defaultPreferencesShouldNotBeFound("weeklyGoal.lessThanOrEqual=" + SMALLER_WEEKLY_GOAL);
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsLessThanSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal is less than DEFAULT_WEEKLY_GOAL
        defaultPreferencesShouldNotBeFound("weeklyGoal.lessThan=" + DEFAULT_WEEKLY_GOAL);

        // Get all the preferencesList where weeklyGoal is less than (DEFAULT_WEEKLY_GOAL + 1)
        defaultPreferencesShouldBeFound("weeklyGoal.lessThan=" + (DEFAULT_WEEKLY_GOAL + 1));
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeeklyGoalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weeklyGoal is greater than DEFAULT_WEEKLY_GOAL
        defaultPreferencesShouldNotBeFound("weeklyGoal.greaterThan=" + DEFAULT_WEEKLY_GOAL);

        // Get all the preferencesList where weeklyGoal is greater than SMALLER_WEEKLY_GOAL
        defaultPreferencesShouldBeFound("weeklyGoal.greaterThan=" + SMALLER_WEEKLY_GOAL);
    }


    @Test
    @Transactional
    public void getAllPreferencesByWeightUnitsIsEqualToSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weightUnits equals to DEFAULT_WEIGHT_UNITS
        defaultPreferencesShouldBeFound("weightUnits.equals=" + DEFAULT_WEIGHT_UNITS);

        // Get all the preferencesList where weightUnits equals to UPDATED_WEIGHT_UNITS
        defaultPreferencesShouldNotBeFound("weightUnits.equals=" + UPDATED_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeightUnitsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weightUnits not equals to DEFAULT_WEIGHT_UNITS
        defaultPreferencesShouldNotBeFound("weightUnits.notEquals=" + DEFAULT_WEIGHT_UNITS);

        // Get all the preferencesList where weightUnits not equals to UPDATED_WEIGHT_UNITS
        defaultPreferencesShouldBeFound("weightUnits.notEquals=" + UPDATED_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeightUnitsIsInShouldWork() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weightUnits in DEFAULT_WEIGHT_UNITS or UPDATED_WEIGHT_UNITS
        defaultPreferencesShouldBeFound("weightUnits.in=" + DEFAULT_WEIGHT_UNITS + "," + UPDATED_WEIGHT_UNITS);

        // Get all the preferencesList where weightUnits equals to UPDATED_WEIGHT_UNITS
        defaultPreferencesShouldNotBeFound("weightUnits.in=" + UPDATED_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void getAllPreferencesByWeightUnitsIsNullOrNotNull() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferencesList where weightUnits is not null
        defaultPreferencesShouldBeFound("weightUnits.specified=true");

        // Get all the preferencesList where weightUnits is null
        defaultPreferencesShouldNotBeFound("weightUnits.specified=false");
    }

    @Test
    @Transactional
    public void getAllPreferencesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        preferences.setUser(user);
        preferencesRepository.saveAndFlush(preferences);
        Long userId = user.getId();

        // Get all the preferencesList where user equals to userId
        defaultPreferencesShouldBeFound("userId.equals=" + userId);

        // Get all the preferencesList where user equals to userId + 1
        defaultPreferencesShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPreferencesShouldBeFound(String filter) throws Exception {
        restPreferencesMockMvc.perform(get("/api/preferences?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(preferences.getId().intValue())))
            .andExpect(jsonPath("$.[*].weeklyGoal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
            .andExpect(jsonPath("$.[*].weightUnits").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));

        // Check, that the count call also returns 1
        restPreferencesMockMvc.perform(get("/api/preferences/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPreferencesShouldNotBeFound(String filter) throws Exception {
        restPreferencesMockMvc.perform(get("/api/preferences?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPreferencesMockMvc.perform(get("/api/preferences/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPreferences() throws Exception {
        // Get the preferences
        restPreferencesMockMvc.perform(get("/api/preferences/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePreferences() throws Exception {
        // Initialize the database
        preferencesService.save(preferences);

        int databaseSizeBeforeUpdate = preferencesRepository.findAll().size();

        // Update the preferences
        Preferences updatedPreferences = preferencesRepository.findById(preferences.getId()).get();
        // Disconnect from session so that the updates on updatedPreferences are not directly saved in db
        em.detach(updatedPreferences);
        updatedPreferences
            .weeklyGoal(UPDATED_WEEKLY_GOAL)
            .weightUnits(UPDATED_WEIGHT_UNITS);

        restPreferencesMockMvc.perform(put("/api/preferences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPreferences)))
            .andExpect(status().isOk());

        // Validate the Preferences in the database
        List<Preferences> preferencesList = preferencesRepository.findAll();
        assertThat(preferencesList).hasSize(databaseSizeBeforeUpdate);
        Preferences testPreferences = preferencesList.get(preferencesList.size() - 1);
        assertThat(testPreferences.getWeeklyGoal()).isEqualTo(UPDATED_WEEKLY_GOAL);
        assertThat(testPreferences.getWeightUnits()).isEqualTo(UPDATED_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void updateNonExistingPreferences() throws Exception {
        int databaseSizeBeforeUpdate = preferencesRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPreferencesMockMvc.perform(put("/api/preferences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(preferences)))
            .andExpect(status().isBadRequest());

        // Validate the Preferences in the database
        List<Preferences> preferencesList = preferencesRepository.findAll();
        assertThat(preferencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePreferences() throws Exception {
        // Initialize the database
        preferencesService.save(preferences);

        int databaseSizeBeforeDelete = preferencesRepository.findAll().size();

        // Delete the preferences
        restPreferencesMockMvc.perform(delete("/api/preferences/{id}", preferences.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Preferences> preferencesList = preferencesRepository.findAll();
        assertThat(preferencesList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPreferences() throws Exception {
        // Initialize the database
        preferencesService.save(preferences);

        // Search the preferences
        restPreferencesMockMvc.perform(get("/api/_search/preferences?query=id:" + preferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(preferences.getId().intValue())))
            .andExpect(jsonPath("$.[*].weeklyGoal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
            .andExpect(jsonPath("$.[*].weightUnits").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));
    }
}
