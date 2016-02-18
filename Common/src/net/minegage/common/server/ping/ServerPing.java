package net.minegage.common.server.ping;


import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;


public class ServerPing {

	private int timeout = 2000;

	public String ping(PingToken serverToken)
			throws IOException {

		Socket       socket      = null;
		OutputStream oStr        = null;
		InputStream  inputStream = null;
		String       response    = null;


		socket = new Socket();
		socket.setSoTimeout(timeout);
		socket.connect(serverToken.address, timeout);

		oStr = socket.getOutputStream();
		DataOutputStream dataOut = new DataOutputStream(oStr);

		inputStream = socket.getInputStream();
		DataInputStream dIn = new DataInputStream(inputStream);

		String thisIp   = Bukkit.getServer().getIp();
		int    thisPort = Bukkit.getServer().getPort();

		sendPacket(dataOut, prepareHandshake(thisIp, thisPort));
		sendPacket(dataOut, preparePing());

		response = receiveResponse(dIn);

		JSONParser parser = new JSONParser();
		JSONObject main;
		try {
			main = (JSONObject) parser.parse(response);

			String motd = (String) main.get("description");

			JSONObject players = (JSONObject) main.get("players");

			long maximumPlayers = (long) players.get("max");
			long onlinePlayers  = (long) players.get("online");

			serverToken.maxPlayers = maximumPlayers;
			serverToken.onlinePlayers = onlinePlayers;
			serverToken.motd = motd;

		} catch (ParseException e) {
			e.printStackTrace();
		}

		dIn.close();
		dataOut.close();

		if (oStr != null) {
			oStr.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
		if (socket != null) {
			socket.close();
		}


		return response;
	}

	private String receiveResponse(DataInputStream dIn)
			throws IOException {

		@SuppressWarnings ("unused")
		int size = readVarInt(dIn); // Packet size - not used
		int packetId = readVarInt(dIn);

		if (packetId != 0x00) {
			throw new IOException("Invalid packetId");
		}

		int stringLength = readVarInt(dIn);

		if (stringLength < 1) {
			throw new IOException("Invalid string length.");
		}

		byte[] responseData = new byte[stringLength];
		dIn.readFully(responseData);

		String jsonString = new String(responseData, Charset.forName("utf-8"));
		return jsonString;
	}

	/*
	 * New protocol expects the size of the data (length of byte array) to be
	 * sent prior to sending the byte array
	 */
	private void sendPacket(DataOutputStream out, byte[] data)
			throws IOException {
		writeVarInt(out, data.length);
		out.write(data);
	}

	private byte[] preparePing() {
		return new byte[] {0x00};
	}

	private byte[] prepareHandshake(String fromHost, int fromPort)
			throws IOException {
		ByteArrayOutputStream bOut      = new ByteArrayOutputStream();
		DataOutputStream      handshake = new DataOutputStream(bOut);

		bOut.write(0x00); // packet id
		writeVarInt(handshake, 47); // protocol version
		writeString(handshake, fromHost);
		handshake.writeShort(fromPort);
		writeVarInt(handshake, 1); // target state 1
		return bOut.toByteArray();
	}

	private void writeString(DataOutputStream out, String string)
			throws IOException {
		writeVarInt(out, string.length());
		out.write(string.getBytes(Charset.forName("UTF-8")));
	}

	private int readVarInt(DataInputStream in)
			throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((k & 0x80) != 128) {
				break;
			}
		}
		return i;
	}

	private void writeVarInt(DataOutputStream out, int paramInt)
			throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				out.write(paramInt);
				return;
			}

			out.write(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}

	private int getVarSize(int value) {
		int total = 0;
		while (true) {
			value >>>= 7;
			total++;
			if (value == 0) {
				break;
			}
		}
		return total;
	}

	@SuppressWarnings ("unused")
	private int getStringSize(String s)
			throws UnsupportedEncodingException {
		int total = 0;
		total += getVarSize(s.length());
		total += s.getBytes("UTF-8").length;
		return total;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
