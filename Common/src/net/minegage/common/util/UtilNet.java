package net.minegage.common.util;


import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minegage.common.log.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class UtilNet {
	
	public static String getSafeUrl(String unsafeUrl) throws URISyntaxException {
		return new URI(unsafeUrl).toASCIIString();
	}
	
	public static String read(String url, int timeout) {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		
		try {
			URL urlConn = new URL(url);
			conn = (HttpURLConnection) urlConn.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-length", "0");
			
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.connect();
			
			int status;
			try {
				status = conn.getResponseCode();
			} catch (SocketTimeoutException ex) {
				L.warn("Unable to read content from " + url + "; timed out");
				return null;
			}
			
			if (status == HttpURLConnection.HTTP_ACCEPTED || status == HttpURLConnection.HTTP_CREATED
					|| status == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				
				String line;
				while (( line = reader.readLine() ) != null) {
					sb.append(line + "\n");
				}
				
				reader.close();
				return sb.toString();
			} else {
				L.warn("Unable to read content from " + url + "; response code " + status);
			}
			
		} catch (IOException ex) {
			L.error(ex, "Unable to read content from " + url);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			if (conn != null) {
				try {
					conn.disconnect();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public static JsonObject readJson(String url, int timeout) {
		String json = read(url, timeout);
		
		if (json == null) {
			return null;
		}
		
		JsonParser parser = new JsonParser();
		
		try {
			JsonObject object = (JsonObject) parser.parse(json);
			return object;
		} catch (JsonParseException ex) {
			L.error(ex, "Unable to parse Json object");
			return null;
		}
	}
	
}
