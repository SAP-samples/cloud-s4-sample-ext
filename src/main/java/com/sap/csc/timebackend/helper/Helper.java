package com.sap.csc.timebackend.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Helper {

    @SafeVarargs
    public static <T> void nullChecks(T... args) {
        IntStream.range(0, args.length).forEach(i -> Objects.requireNonNull(args[i], "Arg: " + i + " is null"));
    }

    @SafeVarargs
    public static <T> List<T> union(List<T>... args) {
        final List<T> result = new ArrayList<>();
        Arrays.stream(args).forEach(result::addAll);
        return result;
    }

}
