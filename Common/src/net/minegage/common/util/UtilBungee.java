package net.minegage.common.util;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;


public class UtilBungee {
	
	public static ByteArrayDataOutput newOutput() {
		return ByteStreams.newDataOutput();
	}
	
	public static String getSubChannel(ByteArrayDataInput input) {
		return input.readUTF();
	}
	
	public static boolean isSubChannel(ByteArrayDataInput input, String subChannel) {
		return getSubChannel(input).equals(subChannel);
	}
	
}
