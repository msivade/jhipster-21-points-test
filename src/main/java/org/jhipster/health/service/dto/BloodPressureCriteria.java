package org.jhipster.health.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link org.jhipster.health.domain.BloodPressure} entity. This class is used
 * in {@link org.jhipster.health.web.rest.BloodPressureResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /blood-pressures?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BloodPressureCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter timestamp;

    private IntegerFilter systolic;

    private IntegerFilter diastolic;

    private LongFilter userId;

    public BloodPressureCriteria() {
    }

    public BloodPressureCriteria(BloodPressureCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.timestamp = other.timestamp == null ? null : other.timestamp.copy();
        this.systolic = other.systolic == null ? null : other.systolic.copy();
        this.diastolic = other.diastolic == null ? null : other.diastolic.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public BloodPressureCriteria copy() {
        return new BloodPressureCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateFilter timestamp) {
        this.timestamp = timestamp;
    }

    public IntegerFilter getSystolic() {
        return systolic;
    }

    public void setSystolic(IntegerFilter systolic) {
        this.systolic = systolic;
    }

    public IntegerFilter getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(IntegerFilter diastolic) {
        this.diastolic = diastolic;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BloodPressureCriteria that = (BloodPressureCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(systolic, that.systolic) &&
            Objects.equals(diastolic, that.diastolic) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        timestamp,
        systolic,
        diastolic,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BloodPressureCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (timestamp != null ? "timestamp=" + timestamp + ", " : "") +
                (systolic != null ? "systolic=" + systolic + ", " : "") +
                (diastolic != null ? "diastolic=" + diastolic + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
