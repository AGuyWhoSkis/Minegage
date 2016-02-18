package net.minegage.minigame.kit;


import java.util.List;

import com.google.common.collect.Lists;


public class Descriptive {
	
	private String name;
	private List<String> description;
	
	public Descriptive(String name, List<String> description) {
		this.name = name;
		setDescription(description);
	}
	
	public Descriptive(String name, String... description) {
		this(name, Lists.newArrayList(description));
	}
	
	public Descriptive(String name) {
		this(name, new String[0]);
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getDescription() {
		return description;
	}
	
	public void setDescription(List<String> description) {
		if (description == null) {
			description = Lists.newArrayList();
		}
		
		this.description = description;
	}
	
	public boolean hasDescription() {
		return description.size() > 0;
	}
	
}
