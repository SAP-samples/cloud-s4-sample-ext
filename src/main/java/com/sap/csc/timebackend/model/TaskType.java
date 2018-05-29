package com.sap.csc.timebackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskType {
    ADMI("Administrative", "ADMI"),

    MISC("Miscellaneous", "MISC"),

    TRAV("Travel", "TRAV"),

    TRAI("Training", "TRAI"),

    DFLT("", "DFLT");


    private static Map<String, TaskType> FORMAT_MAP = Stream
            .of(TaskType.values())
            .collect(Collectors.toMap(TaskType::getFullName, Function.identity()));
    private final String fullName;
    private final String abbr;


    TaskType(String fullName, String abbr) {
        this.fullName = fullName;
        this.abbr = abbr;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAbbr() {
        return abbr;
    }

}
