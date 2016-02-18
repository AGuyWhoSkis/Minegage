package net.minegage.build;

import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EventManager
		extends PluginModule {

	public EventManager(JavaPlugin plugin) {
		super("Event Manager", plugin);
	}

	@EventHandler
	public void cancelBlockSpread(BlockSpreadEvent event) {
		L.d("block spread: source " + event.getSource().getType().name());
	}

	@EventHandler
	public void cancelBlockForm(BlockFormEvent event) {
		L.d("block form: source " + event.getBlock().getType().name());
	}

	@EventHandler
	public void cancelBlockGrow(BlockGrowEvent event) {
		L.d("block grow: source " + event.getBlock().getType().name());
	}



}
