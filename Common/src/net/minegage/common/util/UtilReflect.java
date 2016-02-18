package net.minegage.common.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


public class UtilReflect {

	public static Field getField(Class<?> clazz, String fieldName) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}

		return null;
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName) && Arrays.equals(paramTypes, method.getParameterTypes())) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	public static <T> Object call(Class<T> clazz, T instance, String methodName, Class<?>[] paramTypes,
	                              Object[] params) {
		Method method = getMethod(clazz, methodName, paramTypes);

		if (method == null) {
			throw new NullPointerException("Method '" + methodName + "' does not exist in " + clazz.getName());
		}

		try {
			return method.invoke(instance, params);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static <T> Object get(Class<T> clazz, T instance, String fieldName) {
		Field field = getField(clazz, fieldName);

		if (field == null) {
			throw new NullPointerException("Field '" + fieldName + "' does not exist in " + clazz.getName());
		}

		try {
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Object call(Method method, Object instance, Object[] params) {
		try {
			return method.invoke(instance, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Object get(Field field, Object instance) {
		try {
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
