package com.sap.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectUtils {

	public static final String EMPTY_STRING = "";

	public static <T> String stringifyObject(T obj) {
		return Optional.ofNullable(obj).map(String::valueOf).orElse(EMPTY_STRING);
	}

	public static <T> List<T> shallowClone(Collection<T> original) {
		return original.stream().collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public static <T, V> List<V> listCast(Collection<T> collection, Class<V> castClass) {
		return ExceptionUtils.tryOrFailWith(() -> collection.stream().map(t -> (V) t).collect(Collectors.toList()),
		        () -> new IllegalArgumentException("Could not cast collection to class" + castClass.getName()));
	}

	public static long convertToLongOrFailWith(String maybeLong, String message) {
		return ExceptionUtils.tryOrFailWith(() -> Long.parseLong(maybeLong),
		        () -> new IllegalArgumentException(message));
	}

	@SuppressWarnings("unchecked")
	public static <T, V> Set<V> toSet(Collection<T> list, Class<V> castClass) {
		return ExceptionUtils.tryOrFailWith(() -> list.stream().map(t -> (V) t).collect(Collectors.toSet()),
		        () -> new IllegalArgumentException("Could not cast collection to class" + castClass.getName()));
	}

	public static <T> boolean deepEqualsOrdered(List<T> first, List<T> second) {
		if (first.size() == second.size()) {
			// classic imperative iteration for faster ordered comparison
			for (int i = 0; i < first.size(); i++) {
				if (!first.get(i).equals(second.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static Field getDeclaredField(Class<?> type, String fieldName) {
		List<Field> allFields = getAllDeclaredFields(type);
		return allFields.stream().filter(field -> field.getName().equals(fieldName)).findFirst().get();
	}

	public static List<Field> getAllDeclaredFields(Class<?> type) {
		List<Field> result = new ArrayList<Field>();
		while (type != null && type != Object.class) {
			Collections.addAll(result, type.getDeclaredFields());
			type = type.getSuperclass();
		}
		return result;
	}

}
