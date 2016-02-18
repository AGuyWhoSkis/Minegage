package net.minegage.common.data;

import net.minegage.common.util.UtilJava;

public class DataEnum<T extends Enum<T>>
		extends Data<T> {

	private Class<T> clazz;

	public DataEnum(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T parse(String string)
			throws DataParseException {
		T data = UtilJava.parseEnum(clazz, string);

		if (data == null) {
			throw new DataParseException("Unable to parse enum; invalid enum constant \"" + string + "\"");
		}

		return data;
	}

}
