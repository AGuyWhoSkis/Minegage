package net.minegage.core.db;

import net.minegage.common.log.L;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.UUID;

public abstract class DataSet {
	protected String name;
	
	public DataSet(String name) {
		this.name = name;
	}
	
	/**
	 * Performs an action when a player file is created.
	 */
	protected abstract void onPlayerFileCreate(UUID id);

	/**
	 * Returns true if a player has a file for this DataSet.
	 * @return
	 */
	public boolean isPlayerOnFile(UUID id) {
		return this.getPlayerFile(id).exists();
	}
	
	/**
	 * Gets a player file for a player.
	 * @return
	 */
	public File getPlayerFile(UUID id) {
		File file = new File(DBManager.getPlayerDir(id), this.name);
		DBManager.getPlayerDir(id).mkdirs();
		
		return file;
	}
	
	/**
	 * Writes data to a player file. True on success. False on error.
	 * @param string
	 * @return
	 */
	public boolean writeToFile(UUID id, String string) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(getPlayerFile(id), false));
			out.write(string);
			out.flush();
			out.close();
			return true;
		}
		catch(Exception err) {
			L.error(err, "Unable to write to file for " + id.toString().substring(0, 8));
		}
		
		return false;
	}
	
	public String readFromFile(UUID id) {
		try {
			Scanner scan = new Scanner(getPlayerFile(id));
			String output = new String();
			
			while(scan.hasNextLine())
				output += scan.nextLine();
			
			scan.close();
			
			return output;
		}
		catch(Exception err) {
			L.error(err, "Unable to read from file for " + id.toString().substring(0, 8));
		}
		
		return "";
	}
}
