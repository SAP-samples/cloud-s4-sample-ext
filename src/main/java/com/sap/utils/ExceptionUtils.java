package com.sap.utils;

import com.sap.exceptions.SAPBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ExceptionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

    public static <T, E> T tryOrFailWith(Supplier<T> runnable, Supplier<RuntimeException> exceptionSupplier) {
        try {
            return runnable.get();
        } catch (Throwable te) {
            return raiseException(exceptionSupplier, te);
        }
    }

    public static <T, E> void tryOrFailWith(Consumer<T> consumer, Supplier<RuntimeException> exceptionSupplier) {
        try {
            consumer.accept(null);
        } catch (Throwable te) {
            raiseException(exceptionSupplier, te);
        }
    }

    public static <T> T nullCheck(T object, String message) {
        if (Objects.isNull(object)) {
            throw new SAPBadRequestException(message);
        }
        return object;
    }

    private static <T> T raiseException(Supplier<RuntimeException> exceptionSupplier, Throwable te) {
        logger.error("Runtime exception raised: " + te.getMessage());
        logger.error("Runtime exception", te);
        RuntimeException raisedException = exceptionSupplier.get();
        raisedException.setStackTrace(te.getStackTrace());
        throw raisedException;
    }

}
	