package com.sap.csc.timebackend.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.csc.timebackend.helper.Helper;
import com.sap.csc.timebackend.json.JsonLocalTimeDeserializer;
import com.sap.csc.timebackend.json.JsonLocalTimeSerializer;

import java.io.Serializable;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Break implements Serializable {
    private static final long serialVersionUID = -7877286159305246023L;
    private final TaskType taskType = TaskType.MISC;
    private Long duration;
    @JsonDeserialize(using = JsonLocalTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalTimeSerializer.class)
    private LocalTime startTime;
    @JsonDeserialize(using = JsonLocalTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalTimeSerializer.class)
    private LocalTime endTime;
    private String companyCode;
    private String recordNumber;

    private Break() {
        this.duration = 0L;
        this.companyCode = "";
        this.startTime = LocalTime.parse("00:00");
        this.endTime = LocalTime.parse("00:00");
        this.recordNumber = "";

    }

    public static Break getDefault() {
        return DefaultBreak.getInstance();
    }

    public static RequiredStartTime startTime(LocalTime startTime) {
        return new Builder(startTime);
    }

    public LocalTime getStartTime() {
        return startTime;
    }


    public LocalTime getEndTime() {
        return endTime;
    }


    public Long getDuration() {
        return duration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Break aBreak = (Break) o;

        return duration.equals(aBreak.duration);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Break{" +
                "duration=" + duration +
                '}';
    }

    public String getRecordNumber() {
        return recordNumber;
    }


    public String getCompanyCode() {
        return companyCode;
    }


    public TaskType getTaskType() {
        return taskType;
    }

    public interface RequiredStartTime {
        RequiredEndTime endTime(LocalTime endTime);
    }

    public interface RequiredEndTime {
        RequiredCompanyCode companyCode(String companyCode);
    }

    public interface RequiredCompanyCode {
        RequiredRecordNumber recordNumber(String recordNumber);
    }

    public interface RequiredRecordNumber {
        Break build();
    }

    public static class Builder implements RequiredStartTime, RequiredEndTime, RequiredCompanyCode, RequiredRecordNumber {
        private final Break instance = new Break();

        private Builder(LocalTime startTime) {
            instance.startTime = startTime;
        }

        @Override
        public RequiredEndTime endTime(LocalTime endTime) {
            instance.endTime = endTime;
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
        public Break build() {
            Helper.nullChecks(instance.startTime, instance.endTime, instance.companyCode, instance.recordNumber);
            instance.duration = instance.startTime.until(instance.endTime, MINUTES);
            return instance;
        }
    }

    private static class DefaultBreak {
        private static final Break instance = new Break();

        private static Break getInstance() {
            return instance;
        }
    }

}
