package com.sap.csc.timebackend.controller;

import com.sap.csc.timebackend.model.TaskType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasktype")
public class TaskTypeController {

    private final Predicate<TaskType> validTask = t -> !t.equals(TaskType.TRAV)
            && !t.equals(TaskType.MISC)
            && !t.equals(TaskType.DFLT);

    @GetMapping
    public List<TaskType> getTaskTypes() {

        return Arrays.stream(TaskType.values())
                .filter(validTask)
                .collect(Collectors.toList());
    }

}
