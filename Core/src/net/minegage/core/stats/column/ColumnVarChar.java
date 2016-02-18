package net.minegage.core.stats.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnVarChar extends Column<String> {
	
	public int length;

	public ColumnVarChar(String name, int length) {
		this(name, length, "");
	}

	public ColumnVarChar(String name, int length, String value) {
		super(name);
		this.length = length;
		this.value = value;
	}

	public String getCreateString() {
		return this.name + " VARCHAR(" + length + ")";
	}

	public void setValue(ResultSet resultSet) throws SQLException {
		this.value = resultSet.getString(name);
	}

	public ColumnVarChar clone() {
		return new ColumnVarChar(name, length, value);
	}
}
