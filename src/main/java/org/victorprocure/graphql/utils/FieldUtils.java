package org.victorprocure.graphql.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by victo on 7/1/2017.
 */
public final class FieldUtils {
    public static Field getFieldByName(Object instance, String fieldName) {
        Class<?> clazz = instance.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        throw new IllegalStateException();
    }

    public static Object getFieldValueByFieldName(Object instance, String fieldName) {
        try {
            Field field = getFieldByName(instance, fieldName);
            return field.get(instance);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setFieldValueByFieldName(Object instance, String fieldName, Object value) {
        try {
            Field field = getFieldByName(instance, fieldName);
            field.set(instance, value);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean fieldHasAnnotation(Class annotation, Field field) {
        Annotation anno = field.getAnnotation(annotation);

        return anno != null;
    }
}
