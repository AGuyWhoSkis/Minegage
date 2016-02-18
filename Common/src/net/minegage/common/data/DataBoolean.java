package net.minegage.common.data;

public class DataBoolean extends Data<Boolean> {
	
	public DataBoolean(Boolean data) {
		super(data);
	}
	
	public DataBoolean() {
		
	}
	
	public Boolean parse(String string) throws DataParseException {
		if (string == null) {
			throw new DataParseException("Unable to parse boolean; null value");
		}
		
		if (string.equalsIgnoreCase("true")) {
			return true;
		} else if (string.equalsIgnoreCase("false")) {
			return false;
		}
		
		throw new DataParseException("Unable to parse boolean; invalid value \"" + string + "\"");
	}
}