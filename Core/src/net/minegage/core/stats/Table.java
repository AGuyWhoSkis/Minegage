package net.minegage.core.stats;


import net.minegage.core.stats.column.Column;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Table {
	
	private static Connection connection;
	
	private String name;
	private List<Column<?>> primary = new ArrayList<>();
	private List<Column<?>> columns = new ArrayList<>();
	private Column<?> index;
	
	public Table(String name, List<Column<?>> columns, List<Column<?>> primary, Column<?> index) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			System.out.println("MySQL Driver not found");
			ex.printStackTrace();
			return;
		}
		
		this.columns = new ArrayList<>();
		this.columns = columns;
		this.primary = primary;
		this.index = index;
		this.name = name;
		
		if (!exists()) {
			create();
		}
	}
	
	private boolean create() {
		PreparedStatement statement = null;
		
		try {
			
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(StatManager.CONNECTION_STRING, StatManager.USERNAME,
						StatManager.PASSWORD);
			}
			
			String createString = buildCreateString();
			String primaryKeys = buildPrimaryKeyString();
			
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + name + " (" + createString + ", "
					+ primaryKeys + ", " + "INDEX (" + index.name + ")" + ");");
					
			statement.executeUpdate();
			return true;
			
		} catch (SQLException ex) {
			System.out.println("Unable to create table " + this.name);
			ex.printStackTrace();
		} finally {
			
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
		}
		return false;
	}
	
	private boolean exists() {
		PreparedStatement statement = null;
		try {
			
			if (( connection == null ) || ( connection.isClosed() )) {
				connection = DriverManager.getConnection(StatManager.CONNECTION_STRING, StatManager.USERNAME,
						StatManager.PASSWORD);
			}
			
			ResultSet r = connection.getMetaData()
					.getTables(null, null, name, null);
			return r.next();
			
		} catch (SQLException ex) {
			System.out.println("Unable to complete search for table " + this.name);
			ex.printStackTrace();
		} finally {
			
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
		}
		return false;
	}
	
	public Row fetch(List<Column<?>> columns) {
		/*
		 * SELECT column1,column2 FROM table WHERE column1 = VALUES('val1')
		 */
		
		StringBuilder selectBuilder = new StringBuilder();
		selectBuilder.append("SELECT ");
		
		Iterator<Column<?>> i = this.columns.iterator();
		while (i.hasNext()) {
			selectBuilder.append(i.next().name);
			if (i.hasNext()) {
				selectBuilder.append(",");
			}
		}
		
		List<Column<?>> where = new ArrayList<>();
		where.addAll(columns);
		
		selectBuilder.append(" FROM " + this.name + " " + buildWhereString(where));
		
		PreparedStatement statement = null;
		
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(StatManager.CONNECTION_STRING, StatManager.USERNAME,
						StatManager.PASSWORD);
			}
			
			statement = connection.prepareStatement(selectBuilder.toString());
			ResultSet resultSet = statement.executeQuery();
			Row row = new Row();
			
			while (resultSet.next()) {
				for (Column<?> column : this.columns) {
					Column<?> copy = column.clone();
					copy.name = column.name;
					copy.setValue(resultSet);
					
					row.columns.add(copy);
				}
			}
			
			return row;
			
		} catch (SQLException ex) {
			System.out.println("Unable to complete query from " + this.name);
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public boolean insert(Row row) {
		/*
		 * INSERT INTO table VALUES (val1,val2,val3,val4) ON DUPLICATE KEY UPDATE column1 =
		 * VALUES(val1), column2 = VALUES(val2);
		 */
		StringBuilder insertBuilder = new StringBuilder();
		StringBuilder duplicateBuilder = new StringBuilder();
		insertBuilder.append("INSERT INTO " + name + " VALUES(");
		
		Iterator<Column<?>> i = row.columns.iterator();
		while (i.hasNext()) {
			Column<?> next = i.next();
			
			insertBuilder.append("'" + next.value + "'");
			duplicateBuilder.append(next.name + " = VALUES(" + next.name + ")");
			
			if (i.hasNext()) {
				insertBuilder.append(", ");
				duplicateBuilder.append(", ");
			}
		}
		
		insertBuilder.append(") " + "ON DUPLICATE KEY " + "UPDATE " + duplicateBuilder.toString() + ";");
		
		PreparedStatement statement = null;
		
		try {
			
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(StatManager.CONNECTION_STRING, StatManager.USERNAME,
						StatManager.PASSWORD);
			}
			
			statement = connection.prepareStatement(insertBuilder.toString());
			
			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
			
		} catch (SQLException ex) {
			System.out.println("Unable to insert new row into table " + this.name);
			ex.printStackTrace();
		} finally {
			
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	// public boolean update(Row row, Column<?> where) {
	// List<Column<?>> whereList = new ArrayList<>();
	// whereList.add(where);
	// return update(row, whereList);
	// }
	
	// public boolean update(Row row, List<Column<?>> where) {
	// PreparedStatement statement = null;
	//
	// try {
	// if (connection == null || connection.isClosed()) {
	// connection = DriverManager.getConnection(connectionString, userName,
	// password);
	// }
	//
	// String updateStatement = buildUpdateString(row.columns, where);
	// statement = connection.prepareStatement(updateStatement);
	//
	// int rowsAffected = statement.executeUpdate();
	// return rowsAffected > 0;
	//
	// } catch (SQLException ex) {
	// System.out.println("Unable to update table " + this.name);
	// ex.printStackTrace();
	// } finally {
	//
	// if (statement != null) {
	// try {
	// statement.close();
	// } catch (SQLException ex) {
	// ex.printStackTrace();
	// }
	// }
	//
	// if (connection != null) {
	// try {
	// connection.close();
	// } catch (SQLException ex) {
	// ex.printStackTrace();
	// }
	// }
	// }
	//
	// return false;
	// }
	
	/*
	 * WHERE Country='Germany' AND City='Berlin';
	 */
	
	private String buildWhereString(List<Column<?>> whereColumns) {
		StringBuilder b = new StringBuilder();
		b.append("WHERE ");
		
		Iterator<Column<?>> i = whereColumns.iterator();
		Column<?> next = null;
		while (i.hasNext()) {
			if (next != null) {
				b.append("AND ");
			}
			
			next = i.next();
			b.append(next.name + " = '" + next.value + "'");
			
			if (i.hasNext()) {
				b.append(" ");
			}
		}
		
		return b.toString();
	}
	
	/*
	 * UPDATE table SET column1 = expression1, column2 = expression2 WHERE conditions;
	 */
	// private String buildUpdateString(List<Column<?>> columns, List<Column<?>>
	// where) {
	// StringBuilder b = new StringBuilder();
	// b.append("UPDATE " + name + " " + "SET ");
	//
	// Iterator<Column<?>> i = columns.iterator();
	// while (i.hasNext()) {
	// Column<?> next = i.next();
	// b.append(next.name + " = '" + next.value + "'");
	//
	// if (i.hasNext()) {
	// b.append(",");
	// }
	//
	// b.append(" ");
	// }
	//
	// b.append(buildWhereString(where));
	// b.append(";");
	//
	// return b.toString();
	// }
	
	private String buildPrimaryKeyString() {
		StringBuilder b = new StringBuilder();
		b.append("PRIMARY KEY (");
		Iterator<Column<?>> i = primary.iterator();
		while (i.hasNext()) {
			b.append(i.next().name);
			if (i.hasNext()) {
				b.append(", ");
			}
		}
		b.append(")");
		return b.toString();
	}
	
	private String buildCreateString() {
		StringBuilder b = new StringBuilder();
		Iterator<Column<?>> i = columns.iterator();
		while (i.hasNext()) {
			b.append(i.next()
					.getCreateString());
			if (i.hasNext()) {
				b.append(", ");
			}
		}
		return b.toString();
	}
	
	public List<Column<?>> getColumnsCopy() {
		List<Column<?>> copy = new ArrayList<>();
		for (Column<?> c : columns) {
			copy.add(c.clone());
		}
		return copy;
	}
	
}
