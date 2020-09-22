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
 * Criteria class for the {@link org.jhipster.health.domain.Points} entity. This class is used
 * in {@link org.jhipster.health.web.rest.PointsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /points?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PointsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter date;

    private IntegerFilter exercise;

    private IntegerFilter meals;

    private IntegerFilter alcohol;

    private StringFilter notes;

    private LongFilter userId;

    public PointsCriteria() {
    }

    public PointsCriteria(PointsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.exercise = other.exercise == null ? null : other.exercise.copy();
        this.meals = other.meals == null ? null : other.meals.copy();
        this.alcohol = other.alcohol == null ? null : other.alcohol.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public PointsCriteria copy() {
        return new PointsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getDate() {
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public IntegerFilter getExercise() {
        return exercise;
    }

    public void setExercise(IntegerFilter exercise) {
        this.exercise = exercise;
    }

    public IntegerFilter getMeals() {
        return meals;
    }

    public void setMeals(IntegerFilter meals) {
        this.meals = meals;
    }

    public IntegerFilter getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(IntegerFilter alcohol) {
        this.alcohol = alcohol;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
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
        final PointsCriteria that = (PointsCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(exercise, that.exercise) &&
            Objects.equals(meals, that.meals) &&
            Objects.equals(alcohol, that.alcohol) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        date,
        exercise,
        meals,
        alcohol,
        notes,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PointsCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (exercise != null ? "exercise=" + exercise + ", " : "") +
                (meals != null ? "meals=" + meals + ", " : "") +
                (alcohol != null ? "alcohol=" + alcohol + ", " : "") +
                (notes != null ? "notes=" + notes + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
