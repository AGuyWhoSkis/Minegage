package net.minegage.common.datafile;


import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DataLine {

	private List<String> info = new ArrayList<>();

	private List<String> defaultInfo = new ArrayList<>();

	public DataLine(String dataLine) {
		this((Object[]) dataLine.split("\\|"));
	}

	public DataLine(Object... data) {
		for (Object obj : data) {
			info.add(obj.toString());
		}
	}

	public DataLine defaults(Object... defaults) {
		for (Object obj : defaults) {
			defaultInfo.add(obj.toString());
		}
		return this;
	}

	public void append(Object... append) {
		for (Object obj : append) {
			info.add(obj.toString());
		}
	}

	public void remove(String... remove) {
		for (String str : remove) {
			info.remove(str);
		}
	}

	@Override
	public String toString() {
		return UtilJava.joinList(info, "|");
	}

	public String asString() {
		List<String> strings = asStrings();

		if (strings.size() > 0) {
			return strings.get(0);
		} else {
			return "";
		}
	}

	public List<String> asStrings() {
		if (info.size() == 0 && defaultInfo.size() > 0) {
			return defaultInfo;
		} else {
			return info;
		}
	}

	public Integer asInt() {
		return Integer.parseInt(asString());
	}

	public List<Integer> asInts() {
		return asStrings().stream()
				.map(str -> Integer.parseInt(str))
				.collect(Collectors.toList());
	}

	public Double asDouble() {
		return Double.parseDouble(asString());
	}

	public List<Double> asDoubles() {
		return asStrings().stream()
				.map(str -> Double.parseDouble(str))
				.collect(Collectors.toList());
	}

	public Long asLong() {
		return Long.parseLong(asString());
	}

	public List<Long> asLongs() {
		return asStrings().stream()
				.map(str -> Long.parseLong(str))
				.collect(Collectors.toList());
	}

	public Location asLocation(World world) {
		return UtilPos.deserializeLocation(asString(), world);
	}

	public List<Location> asLocations(World world) {
		return asStrings().stream()
				.map(str -> UtilPos.deserializeLocation(str, world))
				.collect(Collectors.toList());
	}

	public Vector asVector() {
		return UtilPos.deserializeVector(asString());
	}

	public List<Vector> asVectors() {
		return asStrings().stream()
				.map(str -> UtilPos.deserializeVector(str))
				.collect(Collectors.toList());
	}

	public <T extends Enum<T>> T asEnum(Class<T> enumClass) {
		return UtilJava.parseEnum(enumClass, asString());

	}

	public <T extends Enum<T>> List<T> asEnums(Class<T> enumClass) {
		return asStrings().stream()
				.map(str -> UtilJava.parseEnum(enumClass, str))
				.collect(Collectors.toList());
	}

	public MaterialData asMaterialData() {
		return parse(asString());
	}

	public List<MaterialData> asMaterialDatas() {
		return asStrings().stream()
				.map(str -> parse(str))
				.collect(Collectors.toList());
	}

	@SuppressWarnings ("deprecated")
	private MaterialData parse(String info) {
		String[] split = info.split(":", 2);

		int  type = Integer.parseInt(split[0]);
		byte data = (byte) 0;
		if (split.length > 1) {
			data = Byte.parseByte(split[1]);
		}

		return new MaterialData(type, data);
	}


}
