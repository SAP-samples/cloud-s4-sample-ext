package com.sap.csc.timebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.csc.timebackend.helper.Helper;
import com.sap.csc.timebackend.helper.Tuple;
import com.sap.csc.timebackend.json.JsonDateDeserializer;
import com.sap.csc.timebackend.json.JsonDateSerializer;

import java.io.Serializable;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Day implements Serializable {

    private static final long serialVersionUID = 3625258786793405209L;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDate date;

    private Tuple<Task, Task> taskTime;
    private Break breakTime;
    private Tuple<Travel, Travel> travelTime;
    private String user;
    private Long totalTravelTime;

    private Day(LocalDate date) {
        this.date = date;
        this.taskTime = Tuple.create(Task.getDefault(), Task.getDefault());
        this.breakTime = Break.getDefault();
        this.travelTime = Tuple.create(Travel.getDefault(), Travel.getDefault());
        this.user = "";
        this.totalTravelTime = travelTime.getFirst().getDuration() + travelTime.getSecond().getDuration();
    }

    private Day() {
        this.date = LocalDate.now();
        this.taskTime = Tuple.create(Task.getDefault(), Task.getDefault());
        this.breakTime = Break.getDefault();
        this.travelTime = Tuple.create(Travel.getDefault(), Travel.getDefault());
        this.user = "";
        this.totalTravelTime = travelTime.getFirst().getDuration() + travelTime.getSecond().getDuration();
    }

    public static Day create(LocalDate date) {
        return new Day(date);
    }

    public static RequiredDate date(LocalDate date) {
        return new Day.Builder(date);
    }

    public String getUser() {
        return user;
    }


    public LocalDate getDate() {
        return date;
    }


    public Tuple<Task, Task> getTaskTime() {
        return taskTime;
    }


    public Break getBreakTime() {
        return breakTime;
    }


    public Tuple<Travel, Travel> getTravelTime() {
        return travelTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Day day = (Day) o;

        return date.equals(day.date)
                && taskTime.equals(day.taskTime)
                && breakTime.equals(day.breakTime)
                && travelTime.equals(day.travelTime);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + taskTime.hashCode();
        result = 31 * result + breakTime.hashCode();
        result = 31 * result + travelTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Day{" +
                "date=" + date +
                ", taskTime=" + taskTime +
                ", breakTime=" + breakTime +
                ", travelTime=" + travelTime +
                '}';
    }

    public Long getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(Long totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
    }

    public interface RequiredDate {
        RequiredTaskTime taskTime(Tuple<Task, Task> taskTime);
    }

    public interface RequiredTaskTime {
        RequiredBreakTime breakTime(Break breakTime);
    }

    public interface RequiredBreakTime {
        RequiredTravelTime travelTime(Tuple<Travel, Travel> travel);
    }

    public interface RequiredTravelTime {
        RequiredUser user(String user);
    }

    public interface RequiredUser {
        Day build();
    }

    public static class Builder implements RequiredDate, RequiredTaskTime, RequiredBreakTime, RequiredTravelTime, RequiredUser {

        private final Day instance = new Day();

        private Builder(LocalDate date) {
            instance.date = date;
        }

        @Override
        public RequiredTaskTime taskTime(Tuple<Task, Task> taskTime) {
            instance.taskTime = taskTime;
            return this;
        }

        @Override
        public RequiredBreakTime breakTime(Break breakTime) {
            instance.breakTime = breakTime;
            return this;
        }

        @Override
        public RequiredTravelTime travelTime(Tuple<Travel, Travel> travelTime) {
            instance.totalTravelTime = travelTime.getFirst().getDuration() + travelTime.getSecond().getDuration();
            instance.travelTime = travelTime;
            return this;
        }


        @Override
        public Day build() {
            Helper.nullChecks(instance.date, instance.taskTime, instance.breakTime, instance.travelTime, instance.user);
            return instance;
        }

        @Override
        public RequiredUser user(String user) {
            instance.user = user;
            return this;
        }
    }
}
