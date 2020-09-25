package org.jhipster.health.repository;

import org.jhipster.health.domain.Points;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data  repository for the Points entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PointsRepository extends JpaRepository<Points, Long>, JpaSpecificationExecutor<Points> {

    @Query("select points from Points points where points.user.login = ?#{principal.username} " +
        "order by points.date desc")
    Page<Points> findByUserIsCurrentUser(Pageable pageable);

    Page<Points> findAllByOrderByDateDesc(Pageable pageable);

    List<Points> findAllByDateBetweenAndUserLoginOrderByDateDesc(LocalDate firstDate, LocalDate lastDate, String login);
}
