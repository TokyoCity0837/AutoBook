package com.autobook.util;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class demonstrating the purposeful use of Java Reflection.
 */
@Slf4j
public class ReflectionUtil {

    /**
     * Inspects any object using Reflection and returns a mapped representation of
     * its fields.
     * Use case: Deep auditing instances without knowing their class structure at
     * compile time.
     *
     * @param target the target object to inspect
     * @return a map of field names to their values
     */
    public static Map<String, Object> inspectObjectFields(Object target) {
        if (target == null)
            return Map.of();

        Map<String, Object> fieldData = new HashMap<>();
        Class<?> clazz = target.getClass();

        log.debug("Starting reflection scan for class: {}", clazz.getSimpleName());

        for (Field field : clazz.getDeclaredFields()) {
            boolean wasAccessible = field.canAccess(target);
            try {
                field.setAccessible(true);
                Object value = field.get(target);
                fieldData.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                log.error("Reflection access violation on field: {}", field.getName(), e);
            } finally {
                field.setAccessible(wasAccessible);
            }
        }

        return fieldData;
    }
}
