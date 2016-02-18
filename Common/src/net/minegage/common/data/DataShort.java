package net.minegage.common.data;


public class DataShort
		extends Data<Short> {
	
	public DataShort(Short data) {
		super(data);
	}
	
	public DataShort() {
		
	}
	
	public Short parse(String string) throws DataParseException {
		try {	
			return Short.parseShort(string);
		} catch (NumberFormatException | NullPointerException ex) {
			throw new DataParseException("Unable to parse byte; invalid value \"" + string + "\"");
		}
	}
}
