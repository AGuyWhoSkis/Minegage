package net.minegage.common.data;

public class DataNull 
		extends Data<Object> {

	public DataNull() {
		super();
	}
	
	@Override
	public Object parse(String string) {
		return null;
	}
	
	@Override
	public void setData(Object data) {
		// Do nothing
	}
	
	@Override
	public Object getData() {
		return null;
	}
	
}
