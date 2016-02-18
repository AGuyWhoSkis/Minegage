package net.minegage.common.data;


public class DataByte
		extends Data<Byte> {
	
	public DataByte(Byte data) {
		super(data);
	}
	
	public DataByte() {
		
	}
	
	public Byte parse(String string) throws DataParseException {
		try {
			return Byte.parseByte(string);			
		} catch (NumberFormatException | NullPointerException ex) {
			throw new DataParseException("Unable to parse byte; invalid value \"" + string + "\"");
		}
	}
}
