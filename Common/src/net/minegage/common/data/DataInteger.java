package net.minegage.common.data;


public class DataInteger
extends Data<Integer> {
	
	public DataInteger(Integer data) {
		super(data);
	}
	
	public DataInteger() {
		
	}
	
	public Integer parse(String string) throws DataParseException {
		try {			
			return Integer.parseInt(string);
		} catch (NumberFormatException | NullPointerException ex) {
			throw new DataParseException("Unable to parse integer; invalid value \"" + string + "\"");
		}
	}
}
