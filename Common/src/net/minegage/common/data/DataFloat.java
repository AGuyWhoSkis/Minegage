package net.minegage.common.data;


public class DataFloat
extends Data<Float> {
	
	public DataFloat(Float data) {
		super(data);
	}
	
	public DataFloat() {
		
	}
	
	public Float parse(String string) throws DataParseException {
		try {
			return Float.parseFloat(string);			
		} catch (NumberFormatException | NullPointerException ex) {
			throw new DataParseException("Unable to parse float; invalid value \"" + string + "\"");
		}
	}
}
