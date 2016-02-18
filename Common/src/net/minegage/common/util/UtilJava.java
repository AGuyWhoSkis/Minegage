package net.minegage.common.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class UtilJava {

	public static boolean equals(float f1, float f2, float epsilon) {
		return Math.abs(f1 - f2) < epsilon;
	}

	public static <T> boolean contains(T[] array, T elem) {
		for (T t : array) {
			if (t.equals(elem)) {
				return true;
			}
		}

		return false;
	}

	public static <T> List<T> join(Iterator<T> iterator) {
		List<T> list = new ArrayList<>();
		
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		
		return list;
	}
	
	public static int getWrappedIndex(int index, int size) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(index + "");
		}
		
		return index % size;
	}
	
	public static <T> T getWrappedIndex(List<T> list, int index) {
		int wrappedIndex = getWrappedIndex(index, list.size());
		return list.get(wrappedIndex);
	}
	
	public static <T> Iterator<T> wrappedIterator(final List<T> c, final int startIndex) {
		return new Iterator<T>() {
			int index = 0;
			int wrapIndex = startIndex;
			int stopIndex = c.size();
			
			@Override
			public boolean hasNext() {
				return index < stopIndex;
			}
			
			@Override
			public T next() {
				index++;
				return getWrappedIndex(c, wrapIndex++);
			}
			
		};
	}
	
	public static boolean hasNull(Collection<?> objects) {
		for (Object o : objects) {
			if (o == null) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasNull(Object... objects) {
		for (Object o : objects) {
			if (o == null) {
				return true;
			}
		}
		return false;
	}
	
	public static String getEnumName(String unsafe) {
		return unsafe.toUpperCase()
				.replaceAll(" ", "_");
	}
	
	public static <E extends Enum<E>> boolean isEnum(Class<E> enumClass, String name) {
		return parseEnum(enumClass, name) != null;
	}
	
	public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String name) {
		String enumName = getEnumName(name);
		
		for (E enumerator : enumClass.getEnumConstants()) {
			if (enumName.equals(enumerator.name())) {
				return enumerator;
			}
		}
		
		return null;
	}
	
	public static <T> boolean containsAll(Collection<T> c, Collection<T> elements) {
		for (T e : elements) {
			if (!c.contains(e)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static <T> Iterator<T> arrayIterator(final T[] array) {
		return new Iterator<T>() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < array.length;
			}
			
			@Override
			public T next() {
				return array[index++];
			}
		};
	}
	
	public static <T> T getRandIndex(Collection<T> c) {
		if (c == null || c.isEmpty()) {
			return null;
		}
		
		int randIndex = Rand.rInt(c.size());
		int index = 0;
		
		Iterator<T> i = c.iterator();
		while (index++ < randIndex) {
			i.next();
		}
		
		return i.next();
	}
	
	public static <T> T getRandIndex(T[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		
		return array[Rand.rInt(array.length)];
	}
	
	public static boolean isSafe(String string, int maxLength) {
		return string.length() <= maxLength;
	}
	
	public static String getSafe(String string, int maxLength) {
		if (!isSafe(string, maxLength)) {
			return string.substring(0, maxLength - 1);
		}
		return string;
	}

	@Deprecated
	public static int getSafeIncrement(int currentValue, int unsafeIncrement, int minValue, int maxValue) {
		if (currentValue + unsafeIncrement > maxValue) {
			return maxValue - currentValue;
		} else if (currentValue + unsafeIncrement < minValue) {
			return minValue - currentValue;
		} else {
			return unsafeIncrement;
		}
	}
	@Deprecated
	public static double getSafeIncrement(double currentValue, double unsafeIncrement, double minValue, double maxValue) {
		if (currentValue + unsafeIncrement > maxValue) {
			return maxValue - currentValue;
		} else if (currentValue + unsafeIncrement < minValue) {
			return minValue - currentValue;
		} else {
			return unsafeIncrement;
		}
	}

	@Deprecated
	public static float getSafeIncrement(float currentValue, float unsafeIncrement, float minValue, float maxValue) {
		if (currentValue + unsafeIncrement > maxValue) {
			return maxValue - currentValue;
		} else if (currentValue + unsafeIncrement < minValue) {
			return minValue - currentValue;
		} else {
			return unsafeIncrement;
		}
	}


	/**
	 * Creates a string from all elements of the specified array
	 *
	 * @param array
	 *        The array to join together
	 * @param spacer
	 *        The string separating each element of the array in the returning string
	 * @return The combined string
	 */
	public static <T> String joinArray(T[] array, String spacer) {
		return joinArray(array, spacer, 0, array.length - 1);
	}

	/**
	 * Creates a string from a range of elements of the specified array
	 *
	 * @param spacer
	 *        The String separating each element of the array in the returning string
	 * @param startingIndex
	 *        The minimum index to be included
	 * @param finalIndex
	 *        The maximum index to be included
	 * @return The combined string, containing all elements of the array
	 */
	public static <T> String joinArray(T[] array, String spacer, int startingIndex, int finalIndex) {
		if (array.length == 0 || array == null) {
			return "";
		}

		String ret = "";
		for (int i = startingIndex; i <= finalIndex; i++) {
			ret = ret + spacer + array[i];
		}
		return ret.replaceFirst(spacer, "");
	}

	public static <T> String joinArray(T[] array, String delim, int startingIndex) {
		return joinArray(array, delim, startingIndex, array.length - 1);
	}

	public static <T> String joinList(List<T> list, String delim, int startingIndex, int finalIndex) {
		String ret = "";
		for (int i = startingIndex; i <= finalIndex; i++) {
			ret = ret + delim + list.get(i);
		}

		ret = UtilString.removeFirst(ret, delim);

		return ret;
	}

	public static <T> String joinList(List<T> list, String delim, int startingIndex) {
		return joinList(list, delim, startingIndex, list.size() - 1);
	}

	public static <T> String joinList(List<T> list, String delim) {
		return joinList(list, delim, 0);
	}

	public static <T> String joinList(List<T> e, String delim, Function<? super T, String> stringMap) {
		List<String> strings = e.stream()
				.map(stringMap)
				.collect(Collectors.toList());
		String joined = joinList(strings, delim);
		return joined;
	}

	public static <T> List<T> split(String string, String delim, Function<String, ? extends T> map) {
		return new ArrayList<>(UtilString.split(string, delim)).stream()
				.map(map)
				.collect(Collectors.toList());
	}
}
