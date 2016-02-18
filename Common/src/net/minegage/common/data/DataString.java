package net.minegage.common.data;


public class DataString
		extends Data<String> {
	public DataString(String data) {
		super(data);
	}
	
	public DataString() {
		
	}
	
	public String parse(String string) {
		return string;
	}
}
