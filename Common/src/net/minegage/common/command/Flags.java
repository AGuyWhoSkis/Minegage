package net.minegage.common.command;

import net.minegage.common.java.SafeMap;

import java.util.Iterator;
import java.util.Map.Entry;

public class Flags {
	
	protected SafeMap<String, String> flags;

	// When .has(flag) is called, the flag value is assigned to these strings if possible
	protected String valueBuffer = null;
	protected String keyBuffer = null;
	
	public Flags(SafeMap<String, String> flags) {
		this.flags = flags;
	}
	
	public boolean has(String flag) {
		Iterator<Entry<String, String>> flagIt = flags.entrySet().iterator();
		
		while(flagIt.hasNext()) {
			Entry<String, String> next = flagIt.next();
			
			String key = next.getKey();
			String value = next.getValue();
			
			if (flag.equals(key)) {
				keyBuffer = key;
				valueBuffer = value;
				flagIt.remove();
				return true;
			}
		}
		return false;
	}
	
	public String getString() {
		return valueBuffer;
	}
	
	public Integer getInt() {
		return Integer.valueOf(getString());
	}
	
	public Double getDouble() {
		return Double.valueOf(getString());
	}
	
	public Float getFloat() {
		return Float.valueOf(getString());
	}
	
	public Long getLong() {
		return Long.valueOf(getString());
	}
	
	public Boolean getBoolean() {
		return Boolean.valueOf(getString());
	}
	
}
