package org.jhipster.health.web.rest;

import org.jhipster.health.TwentyOnePointsApp;
import org.jhipster.health.domain.Weight;
import org.jhipster.health.domain.User;
import org.jhipster.health.repository.WeightRepository;
import org.jhipster.health.repository.search.WeightSearchRepository;
import org.jhipster.health.service.WeightService;
import org.jhipster.health.service.dto.WeightCriteria;
import org.jhipster.health.service.WeightQueryService;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link WeightResource} REST controller.
 */
@SpringBootTest(classes = TwentyOnePointsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class WeightResourceIT {

    private static final LocalDate DEFAULT_TIMESTAMP = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIMESTAMP = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TIMESTAMP = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_WEIGHT = 1;
    private static final Integer UPDATED_WEIGHT = 2;
    private static final Integer SMALLER_WEIGHT = 1 - 1;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private WeightService weightService;

    /**
     * This repository is mocked in the org.jhipster.health.repository.search test package.
     *
     * @see org.jhipster.health.repository.search.WeightSearchRepositoryMockConfiguration
     */
    @Autowired
    private WeightSearchRepository mockWeightSearchRepository;

    @Autowired
    private WeightQueryService weightQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWeightMockMvc;

    @Autowired
    private WebApplicationContext context;

    private Weight weight;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Weight createEntity(EntityManager em) {
        Weight weight = new Weight()
            .timestamp(DEFAULT_TIMESTAMP)
            .weight(DEFAULT_WEIGHT);
        return weight;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Weight createUpdatedEntity(EntityManager em) {
        Weight weight = new Weight()
            .timestamp(UPDATED_TIMESTAMP)
            .weight(UPDATED_WEIGHT);
        return weight;
    }

    @BeforeEach
    public void initTest() {
        weight = createEntity(em);
    }

    @Test
    @Transactional
    public void createWeight() throws Exception {
        int databaseSizeBeforeCreate = weightRepository.findAll().size();

        // Create security-aware mockMvc
        restWeightMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

        // Create the Weight
        restWeightMockMvc.perform(post("/api/weights")
            .with(SecurityMockMvcRequestPostProcessors.user("user"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(weight)))
            .andExpect(status().isCreated());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeCreate + 1);
        Weight testWeight = weightList.get(weightList.size() - 1);
        assertThat(testWeight.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testWeight.getWeight()).isEqualTo(DEFAULT_WEIGHT);

        // Validate the Weight in Elasticsearch
        verify(mockWeightSearchRepository, times(1)).save(testWeight);
    }

    @Test
    @Transactional
    public void createWeightWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = weightRepository.findAll().size();

        // Create the Weight with an existing ID
        weight.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWeightMockMvc.perform(post("/api/weights")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(weight)))
            .andExpect(status().isBadRequest());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeCreate);

        // Validate the Weight in Elasticsearch
        verify(mockWeightSearchRepository, times(0)).save(weight);
    }


    @Test
    @Transactional
    public void checkTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = weightRepository.findAll().size();
        // set the field null
        weight.setTimestamp(null);

        // Create the Weight, which fails.


        restWeightMockMvc.perform(post("/api/weights")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(weight)))
            .andExpect(status().isBadRequest());

        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWeightIsRequired() throws Exception {
        int databaseSizeBeforeTest = weightRepository.findAll().size();
        // set the field null
        weight.setWeight(null);

        // Create the Weight, which fails.


        restWeightMockMvc.perform(post("/api/weights")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(weight)))
            .andExpect(status().isBadRequest());

        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWeights() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList
        restWeightMockMvc.perform(get("/api/weights?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weight.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT)));
    }

    @Test
    @Transactional
    public void getWeight() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get the weight
        restWeightMockMvc.perform(get("/api/weights/{id}", weight.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(weight.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.weight").value(DEFAULT_WEIGHT));
    }


    @Test
    @Transactional
    public void getWeightsByIdFiltering() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        Long id = weight.getId();

        defaultWeightShouldBeFound("id.equals=" + id);
        defaultWeightShouldNotBeFound("id.notEquals=" + id);

        defaultWeightShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWeightShouldNotBeFound("id.greaterThan=" + id);

        defaultWeightShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWeightShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllWeightsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp equals to DEFAULT_TIMESTAMP
        defaultWeightShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the weightList where timestamp equals to UPDATED_TIMESTAMP
        defaultWeightShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllWeightsByTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp not equals to DEFAULT_TIMESTAMP
        defaultWeightShouldNotBeFound("timestamp.notEquals=" + DEFAULT_TIMESTAMP);

        // Get all the weightList where timestamp not equals to UPDATED_TIMESTAMP
        defaultWeightShouldBeFound("timestamp.notEquals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllWeightsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultWeightShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the weightList where timestamp equals to UPDATED_TIMESTAMP
        defaultWeightShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllWeightsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp is not null
        defaultWeightShouldBeFound("timestamp.specified=true");

        // Get all the weightList where timestamp is null
        defaultWeightShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    public void getAllWeightsByTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp is greater than or equal to DEFAULT_TIMESTAMP
        defaultWeightShouldBeFound("timestamp.greaterThanOrEqual=" + DEFAULT_TIMESTAMP);

        // Get all the weightList where timestamp is greater than or equal to UPDATED_TIMESTAMP
        defaultWeightShouldNotBeFound("timestamp.greaterThanOrEqual=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllWeightsByTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp is less than or equal to DEFAULT_TIMESTAMP
        defaultWeightShouldBeFound("timestamp.lessThanOrEqual=" + DEFAULT_TIMESTAMP);

        // Get all the weightList where timestamp is less than or equal to SMALLER_TIMESTAMP
        defaultWeightShouldNotBeFound("timestamp.lessThanOrEqual=" + SMALLER_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllWeightsByTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp is less than DEFAULT_TIMESTAMP
        defaultWeightShouldNotBeFound("timestamp.lessThan=" + DEFAULT_TIMESTAMP);

        // Get all the weightList where timestamp is less than UPDATED_TIMESTAMP
        defaultWeightShouldBeFound("timestamp.lessThan=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    public void getAllWeightsByTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where timestamp is greater than DEFAULT_TIMESTAMP
        defaultWeightShouldNotBeFound("timestamp.greaterThan=" + DEFAULT_TIMESTAMP);

        // Get all the weightList where timestamp is greater than SMALLER_TIMESTAMP
        defaultWeightShouldBeFound("timestamp.greaterThan=" + SMALLER_TIMESTAMP);
    }


    @Test
    @Transactional
    public void getAllWeightsByWeightIsEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight equals to DEFAULT_WEIGHT
        defaultWeightShouldBeFound("weight.equals=" + DEFAULT_WEIGHT);

        // Get all the weightList where weight equals to UPDATED_WEIGHT
        defaultWeightShouldNotBeFound("weight.equals=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    public void getAllWeightsByWeightIsNotEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight not equals to DEFAULT_WEIGHT
        defaultWeightShouldNotBeFound("weight.notEquals=" + DEFAULT_WEIGHT);

        // Get all the weightList where weight not equals to UPDATED_WEIGHT
        defaultWeightShouldBeFound("weight.notEquals=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    public void getAllWeightsByWeightIsInShouldWork() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight in DEFAULT_WEIGHT or UPDATED_WEIGHT
        defaultWeightShouldBeFound("weight.in=" + DEFAULT_WEIGHT + "," + UPDATED_WEIGHT);

        // Get all the weightList where weight equals to UPDATED_WEIGHT
        defaultWeightShouldNotBeFound("weight.in=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    public void getAllWeightsByWeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight is not null
        defaultWeightShouldBeFound("weight.specified=true");

        // Get all the weightList where weight is null
        defaultWeightShouldNotBeFound("weight.specified=false");
    }

    @Test
    @Transactional
    public void getAllWeightsByWeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight is greater than or equal to DEFAULT_WEIGHT
        defaultWeightShouldBeFound("weight.greaterThanOrEqual=" + DEFAULT_WEIGHT);

        // Get all the weightList where weight is greater than or equal to UPDATED_WEIGHT
        defaultWeightShouldNotBeFound("weight.greaterThanOrEqual=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    public void getAllWeightsByWeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight is less than or equal to DEFAULT_WEIGHT
        defaultWeightShouldBeFound("weight.lessThanOrEqual=" + DEFAULT_WEIGHT);

        // Get all the weightList where weight is less than or equal to SMALLER_WEIGHT
        defaultWeightShouldNotBeFound("weight.lessThanOrEqual=" + SMALLER_WEIGHT);
    }

    @Test
    @Transactional
    public void getAllWeightsByWeightIsLessThanSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight is less than DEFAULT_WEIGHT
        defaultWeightShouldNotBeFound("weight.lessThan=" + DEFAULT_WEIGHT);

        // Get all the weightList where weight is less than UPDATED_WEIGHT
        defaultWeightShouldBeFound("weight.lessThan=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    public void getAllWeightsByWeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);

        // Get all the weightList where weight is greater than DEFAULT_WEIGHT
        defaultWeightShouldNotBeFound("weight.greaterThan=" + DEFAULT_WEIGHT);

        // Get all the weightList where weight is greater than SMALLER_WEIGHT
        defaultWeightShouldBeFound("weight.greaterThan=" + SMALLER_WEIGHT);
    }


    @Test
    @Transactional
    public void getAllWeightsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        weightRepository.saveAndFlush(weight);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        weight.setUser(user);
        weightRepository.saveAndFlush(weight);
        Long userId = user.getId();

        // Get all the weightList where user equals to userId
        defaultWeightShouldBeFound("userId.equals=" + userId);

        // Get all the weightList where user equals to userId + 1
        defaultWeightShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWeightShouldBeFound(String filter) throws Exception {
        restWeightMockMvc.perform(get("/api/weights?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weight.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT)));

        // Check, that the count call also returns 1
        restWeightMockMvc.perform(get("/api/weights/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWeightShouldNotBeFound(String filter) throws Exception {
        restWeightMockMvc.perform(get("/api/weights?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWeightMockMvc.perform(get("/api/weights/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingWeight() throws Exception {
        // Get the weight
        restWeightMockMvc.perform(get("/api/weights/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWeight() throws Exception {
        // Initialize the database
        weightService.save(weight);

        int databaseSizeBeforeUpdate = weightRepository.findAll().size();

        // Update the weight
        Weight updatedWeight = weightRepository.findById(weight.getId()).get();
        // Disconnect from session so that the updates on updatedWeight are not directly saved in db
        em.detach(updatedWeight);
        updatedWeight
            .timestamp(UPDATED_TIMESTAMP)
            .weight(UPDATED_WEIGHT);

        restWeightMockMvc.perform(put("/api/weights")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedWeight)))
            .andExpect(status().isOk());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);
        Weight testWeight = weightList.get(weightList.size() - 1);
        assertThat(testWeight.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testWeight.getWeight()).isEqualTo(UPDATED_WEIGHT);

        // Validate the Weight in Elasticsearch
        verify(mockWeightSearchRepository, times(2)).save(testWeight);
    }

    @Test
    @Transactional
    public void updateNonExistingWeight() throws Exception {
        int databaseSizeBeforeUpdate = weightRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWeightMockMvc.perform(put("/api/weights")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(weight)))
            .andExpect(status().isBadRequest());

        // Validate the Weight in the database
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Weight in Elasticsearch
        verify(mockWeightSearchRepository, times(0)).save(weight);
    }

    @Test
    @Transactional
    public void deleteWeight() throws Exception {
        // Initialize the database
        weightService.save(weight);

        int databaseSizeBeforeDelete = weightRepository.findAll().size();

        // Delete the weight
        restWeightMockMvc.perform(delete("/api/weights/{id}", weight.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Weight> weightList = weightRepository.findAll();
        assertThat(weightList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Weight in Elasticsearch
        verify(mockWeightSearchRepository, times(1)).deleteById(weight.getId());
    }

    @Test
    @Transactional
    public void searchWeight() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        weightService.save(weight);
        when(mockWeightSearchRepository.search(queryStringQuery("id:" + weight.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(weight), PageRequest.of(0, 1), 1));

        // Search the weight
        restWeightMockMvc.perform(get("/api/_search/weights?query=id:" + weight.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weight.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT)));
    }
}
