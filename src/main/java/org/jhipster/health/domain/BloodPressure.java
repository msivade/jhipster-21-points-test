package org.jhipster.health.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * A BloodPressure.
 */
@Entity
@Table(name = "blood_pressure")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bloodpressure")
public class BloodPressure implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDate timestamp;

    @NotNull
    @Column(name = "systolic", nullable = false)
    private Integer systolic;

    @NotNull
    @Column(name = "diastolic", nullable = false)
    private Integer diastolic;

    @ManyToOne
    @JsonIgnoreProperties(value = "bloodPressures", allowSetters = true)
    private User user;

    public BloodPressure(){};

    public BloodPressure(ZonedDateTime date, Integer systolic, Integer diastolic, User user){
        this.timestamp = date.toLocalDate();
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.user = user;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public BloodPressure timestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public BloodPressure systolic(Integer systolic) {
        this.systolic = systolic;
        return this;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public BloodPressure diastolic(Integer diastolic) {
        this.diastolic = diastolic;
        return this;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    public User getUser() {
        return user;
    }

    public BloodPressure user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BloodPressure)) {
            return false;
        }
        return id != null && id.equals(((BloodPressure) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BloodPressure{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", systolic=" + getSystolic() +
            ", diastolic=" + getDiastolic() +
            "}";
    }
}
