package net.minegage.core.stats.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnInt extends Column<Integer> {
	
	public ColumnInt(String name) {
		super(name);
		this.value = 0;
	}

	public ColumnInt(String name, int value) {
		super(name, Integer.valueOf(value));
	}

	public String getCreateString() {
		return name + " INT";
	}

	public void setValue(ResultSet resultSet) throws SQLException {
		this.value = resultSet.getInt(name);
	}

	public ColumnInt clone() {
		return new ColumnInt(name, Integer.valueOf(value));
	}
}
