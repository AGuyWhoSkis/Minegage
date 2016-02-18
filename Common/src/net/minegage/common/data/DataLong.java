package net.minegage.common.data;


public class DataLong
extends Data<Long> {
	
	public DataLong(Long data) {
		super(data);
	}
	
	public DataLong() {
		
	}
	
	public Long parse(String string) throws DataParseException {
		try {	
			return Long.parseLong(string);
		} catch (NumberFormatException | NullPointerException ex) {
			throw new DataParseException("Unable to parse long; invalid value \"" + string + "\"");
		}
	}
}
