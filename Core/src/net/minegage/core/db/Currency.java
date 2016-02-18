package net.minegage.core.db;

import net.minegage.common.log.L;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.UUID;

public class Currency extends DataSet {
	public enum CurrencyType {
		AESTHETIC;
		
		public String toString() {
			return name().toLowerCase();
	    }
	};
	
	public Currency() {
		super("currency");
	}
	
	public double getCurrency(UUID id, CurrencyType type) {
		if(!isPlayerOnFile(id)) {
			onPlayerFileCreate(id);
			return 0D;
		}
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(readFromFile(id));
			return (double)obj.get(type.toString());
		}
		catch(Exception err) {
			L.error(err, "Unable to parse file for " + id.toString().substring(0, 8));
		}
		
		return 0D;
	}
	
	public double getCurrency(Player player, CurrencyType type) {
		return this.getCurrency(player.getUniqueId(), type);
	}
	
	public void creditCurrency(UUID id, CurrencyType type, double amount) {
		if(!isPlayerOnFile(id))
			onPlayerFileCreate(id);
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(readFromFile(id));
			obj.put(type.toString(), amount);
		}
		catch(Exception err) {
			L.error(err, "Unable to credit currency in file for " + id.toString().substring(0, 8));
		}
	}
	
	public void creditCurrency(Player player, CurrencyType type, double amount) {
		this.creditCurrency(player.getUniqueId(), type, amount);
	}
	
	//////////////////////////////////////////////
	
	@Override
	protected void onPlayerFileCreate(UUID id) {
		try {
			this.getPlayerFile(id).createNewFile();
		}
		catch (IOException err) {
			L.error(err, err.getMessage());
		}
		
		JSONObject obj = new JSONObject();
		
		for(CurrencyType ct : CurrencyType.values()) {
			obj.put(ct.toString(), 0D);				
		}
		
		writeToFile(id, obj.toJSONString());
		
		L.info("No such record exists for " + id.toString().substring(0, 8) + "::" + this.name +". Creating...");
	}
}
