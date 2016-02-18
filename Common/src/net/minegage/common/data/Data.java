package net.minegage.common.data;


public abstract class Data<T> {
	
	public static final DataNull NULL = new DataNull();
	public static final DataBoolean BOOLEAN = new DataBoolean();
	public static final DataByte BYTE = new DataByte();
	public static final DataCharacter CHARACTER = new DataCharacter();
	public static final DataShort SHORT = new DataShort();
	public static final DataInteger INTEGER = new DataInteger();
	public static final DataLong LONG = new DataLong();
	public static final DataFloat FLOAT = new DataFloat();
	public static final DataDouble DOUBLE = new DataDouble();
	public static final DataString STRING = new DataString();
	
	protected T data;
	
	public Data(T data) {
		this.data = data;
	}
	
	public Data() {
		
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public abstract T parse(String string) throws DataParseException;
	
}


