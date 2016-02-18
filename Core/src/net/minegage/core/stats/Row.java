package net.minegage.core.stats;


import net.minegage.core.stats.column.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Row {
	
	public List<Column<?>> columns = new ArrayList<>();
	
	public Column<?> getColumn(String name) {
		for (Column<?> column : columns) {
			if (column.name.equals(name)) {
				return column;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		Iterator<Column<?>> i = columns.iterator();
		while (i.hasNext()) {
			Column<?> c = i.next();
			b.append(c.name + " = " + c.value);
			if (i.hasNext()) {
				b.append(", ");
			}
		}
		
		return b.toString();
	}
}
