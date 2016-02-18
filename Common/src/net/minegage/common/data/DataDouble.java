package net.minegage.common.data;


public class DataDouble
		extends Data<Double> {
	
	public DataDouble(Double data) {
		super(data);
	}
	
	public DataDouble() {
		
	}
	
	public Double parse(String string) throws DataParseException {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException ex) {
			throw new DataParseException("Unable to parse double; invalid value \"" + string + "\"");
		}	
	}
}
