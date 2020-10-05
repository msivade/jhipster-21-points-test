package org.jhipster.health.repository;

import org.jhipster.health.domain.BloodPressure;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data  repository for the BloodPressure entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BloodPressureRepository extends JpaRepository<BloodPressure, Long>, JpaSpecificationExecutor<BloodPressure> {

    @Query("select bloodPressure from BloodPressure bloodPressure where bloodPressure.user.login = ?#{principal.username}")
    List<BloodPressure> findByUserIsCurrentUser();

    List<BloodPressure> findAllByTimestampBetweenAndUserLoginOrderByTimestampDesc(LocalDate firstDate, LocalDate secondDate,
                                                                                  String login);
}
