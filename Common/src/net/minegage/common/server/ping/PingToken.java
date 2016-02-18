package net.minegage.common.server.ping;

import java.net.InetSocketAddress;

public class PingToken {

	public boolean offline = false;

	public InetSocketAddress address;
	public long onlinePlayers;
	public long maxPlayers;
	public String motd;


}
