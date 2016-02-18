package net.minegage.common.data;


public class DataCharacter
		extends Data<Character> {
	
	public DataCharacter(Character data) {
		super(data);
	}
	
	public DataCharacter() {
		
	}
	
	public Character parse(String string) throws DataParseException {
		try {
			return string.charAt(0);
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
			throw new DataParseException("Unable to parse character; invalid value \"" + string + "\"");
		}
	}
}
