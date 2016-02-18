package net.minegage.common.datafile;


import com.google.common.collect.Lists;
import net.minegage.common.java.SafeMap;
import net.minegage.common.util.UtilJava;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;


public class DataFile {



	public static final String FILE_NAME = "mapdata.dat";

	public static final String DELIM_INFO = "|";
	public static final String DELIM_DESC = ",";
	public static final String DELIM_LINE = ":";
	
	protected File file;
	
	private SafeMap<String, DataLine> information = new SafeMap<>();
	
	public DataFile(File file) {
		this.file = file;
	}
	
	public void loadFile() throws IOException {
		file.createNewFile();
		
		List<String> lines = FileUtils.readLines(file);
		
		for (String line : lines) {
			if (line.length() == 0) {
				continue;
			}
			
			String desc = "";
			String info = "";
			
			String[] lineSplit = line.split(DELIM_LINE, 2);
			
			desc = lineSplit[0];
			if (lineSplit.length > 1) {
				info = lineSplit[1];
			}
			
			information.put(desc, new DataLine(info));
			
		}
	}
	
	public void saveFile() throws IOException {
		List<String> lines = Lists.newArrayList();
		
		for (Entry<String, DataLine> entry : information.entrySet()) {
			String desc = entry.getKey();
			String info = entry.getValue()
					.toString();
					
			String line = desc;
			if (info.length() > 0) {
				line += DELIM_LINE + info;
			}
			
			lines.add(line);
		}
		
		FileUtils.writeLines(file, lines);
	}

	public boolean contains(String desc) {
		return information.containsKey(desc);
	}

	public DataLine read(String desc) {
		DataLine line = information.get(desc);
		if (line == null) {
			line = new DataLine();
			information.put(desc, line);
		}
		
		return line;
	}
	
	public void write(String desc, Object info) {
		information.put(desc, new DataLine(info.toString()));
	}
	
	public void append(String desc, Object... info) {
		DataLine line = read(desc);
		if (line == null) {
			line = new DataLine(info);
			information.put(desc, line);
		} else {
			line.append(info);
		}
	}
	
	public void remove(String desc, String... info) {
		DataLine line = read(desc);
		if (line == null) {
			return;
		}
		
		line.remove(info);
		
		if (line.asString()
				.length() == 0) {
			information.remove(desc);
		}
	}
	
	public void write(String desc, String... info) {
		String joined = UtilJava.joinArray(info, DELIM_INFO);
		write(desc, joined);
	}
	
	public DataLine delete(String desc) {
		return information.remove(desc);
	}
	
}
