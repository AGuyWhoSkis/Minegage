package net.minegage.build.command.rotation;


import net.minegage.common.command.Flags;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandRotationSync
		extends CommandModule<RotationManager> {
		
	private final String path = File.separator + "home" + File.separator + "minecraft" + File.separator + "lebronsync.sh";
	
	public CommandRotationSync(RotationManager manager) {
		super(manager, Rank.ADMIN, "sync", "synchronize", "push");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		File file = new File(path);
		if (!file.canExecute()) {
		
		}
		
		BufferedReader read = null;
		BufferedReader error = null;
		
		try {
			ProcessBuilder build = new ProcessBuilder(path);
			Process process = build.start();
			
			error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			while (( line = error.readLine() ) != null) {
				player.sendMessage(line);
			}
			
			read = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while (( line = read.readLine() ) != null) {
				player.sendMessage(C.cRed + line);
			}
			
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to run script");
		} finally {
			
			if (error != null) {
				try {
					error.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			if (read != null) {
				try {
					read.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
}
