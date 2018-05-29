package com.sap.csc.timebackend.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.csc.timebackend.exceptions.InvalidTimeException;
import com.sap.csc.timebackend.helper.Helper;
import com.sap.csc.timebackend.json.JsonLocalTimeDeserializer;
import com.sap.csc.timebackend.json.JsonLocalTimeSerializer;

import java.io.Serializable;
import java.time.LocalTime;

public class Task implements Serializable {

    private static final long serialVersionUID = 4434369000233620414L;

    @JsonDeserialize(using = JsonLocalTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalTimeSerializer.class)
    private LocalTime startTime;

    @JsonDeserialize(using = JsonLocalTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalTimeSerializer.class)

    private LocalTime endTime;
    private TaskType taskType;
    private String companyCode;
    private String recordNumber;


    private Task() {
        this.startTime = LocalTime.parse("00:00");
        this.endTime = LocalTime.parse("00:00");
        this.taskType = TaskType.DFLT;
        this.companyCode = "";
        this.recordNumber = "";
    }

    public static Task getDefault() {
        return DefaultTask.getInstance();
    }


    public static RequiredStartTime startTime(LocalTime startTime) {
        return new Task.Builder(startTime);
    }

    public LocalTime getStartTime() {
        return startTime;
    }


    public LocalTime getEndTime() {
        return endTime;
    }


    public TaskType getTaskType() {
        return taskType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return (startTime != null ? startTime.equals(task.startTime)
                : task.startTime == null) && (endTime != null ? endTime.equals(task.endTime)
                : task.endTime == null) && taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (taskType != null ? taskType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", taskType=" + taskType +
                '}';
    }

    public String getCompanyCode() {
        return companyCode;
    }


    public String getRecordNumber() {
        return recordNumber;
    }


    public interface RequiredStartTime {
        RequiredEndTime endTime(LocalTime endTime);
    }

    public interface RequiredEndTime {
        RequiredTaskType taskType(TaskType taskType);
    }


    public interface RequiredTaskType {
        RequiredCompanyCode companyCode(String companyCode);

    }

    public interface RequiredCompanyCode {
        RequiredRecordNumber recordNumber(String recordNumber);
    }

    public interface RequiredRecordNumber {
        Task build();
    }

    public static class Builder implements RequiredStartTime, RequiredEndTime, RequiredTaskType, RequiredCompanyCode, RequiredRecordNumber {
        private final Task instance = new Task();

        private Builder(LocalTime startTime) {
            instance.startTime = startTime;
        }

        @Override
        public RequiredEndTime endTime(LocalTime endTime) {
            instance.endTime = endTime;
            return this;
        }

        @Override
        public RequiredTaskType taskType(TaskType taskType) {
            instance.taskType = taskType;
            return this;
        }

        @Override
        public RequiredCompanyCode companyCode(String companyCode) {
            instance.companyCode = companyCode;
            return this;
        }


        @Override
        public RequiredRecordNumber recordNumber(String recordNumber) {
            instance.recordNumber = recordNumber;
            return this;
        }

        @Override
        public Task build() {
            Helper.nullChecks(instance.startTime, instance.endTime, instance.taskType, instance.companyCode, instance.recordNumber);
            if (instance.startTime.isAfter(instance.endTime)) {
                throw InvalidTimeException.create("Start time is after end time!");
            }
            return instance;
        }
    }

    private static class DefaultTask {
        private static final Task instance = new Task();

        private static Task getInstance() {
            return instance;
        }
    }


}
