package net.minegage.core.stats.column;


import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class Column<T> {
	
	public String name;
	public T value;
	
	public Column(String name) {
		this.name = name;
	}
	
	public Column(String name, T value) {
		this.name = name;
		this.value = value;
	}
	
	public abstract String getCreateString();
	
	public abstract void setValue(ResultSet paramResultSet) throws SQLException;
	
	public abstract Column<T> clone();
	
	@Override
	public String toString() {
		return name + " = " + value;
	}
}
