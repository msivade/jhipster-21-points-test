package org.jhipster.health.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import org.jhipster.health.domain.enumeration.Units;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link org.jhipster.health.domain.Preferences} entity. This class is used
 * in {@link org.jhipster.health.web.rest.PreferencesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /preferences?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PreferencesCriteria implements Serializable, Criteria {
    /**
     * Class for filtering Units
     */
    public static class UnitsFilter extends Filter<Units> {

        public UnitsFilter() {
        }

        public UnitsFilter(UnitsFilter filter) {
            super(filter);
        }

        @Override
        public UnitsFilter copy() {
            return new UnitsFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter weeklyGoal;

    private UnitsFilter weightUnits;

    private LongFilter userId;

    public PreferencesCriteria() {
    }

    public PreferencesCriteria(PreferencesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.weeklyGoal = other.weeklyGoal == null ? null : other.weeklyGoal.copy();
        this.weightUnits = other.weightUnits == null ? null : other.weightUnits.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public PreferencesCriteria copy() {
        return new PreferencesCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(IntegerFilter weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public UnitsFilter getWeightUnits() {
        return weightUnits;
    }

    public void setWeightUnits(UnitsFilter weightUnits) {
        this.weightUnits = weightUnits;
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
        final PreferencesCriteria that = (PreferencesCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(weeklyGoal, that.weeklyGoal) &&
            Objects.equals(weightUnits, that.weightUnits) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        weeklyGoal,
        weightUnits,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PreferencesCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (weeklyGoal != null ? "weeklyGoal=" + weeklyGoal + ", " : "") +
                (weightUnits != null ? "weightUnits=" + weightUnits + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
