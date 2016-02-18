package net.minegage.common.module;


import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;


/**
 Facilitates use of the Bukkit scheduler and keeps track of scheduled tasks
 */
public class LazyScheduler
		implements Runnable {

	private final Object asyncLock = new Object();

	/*
	Weak references are used to prevent memory leaks
	 */

	private Set<BukkitTask> syncTasks = Collections.newSetFromMap(
			new WeakHashMap<>());

	private Set<BukkitTask> asyncTasks = Collections.newSetFromMap(
			new WeakHashMap<>());

	protected JavaPlugin plugin;

	public LazyScheduler(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 Schedules a runnable to synchronously run once

	 @param delayTicks How many ticks before the runnable is called

	 @return The scheduled BukkitTask
	 */
	public BukkitTask runSyncDelayed(long delayTicks, Runnable runnable) {
		BukkitTask task = getScheduler().runTaskLater(plugin, runnable, delayTicks);
		syncTasks.add(task);

		return task;
	}

	/**
	 Schedules a runnable to synchronously run repeatedly

	 @param delayTicks    How many ticks before the runnable is first called
	 @param intervalTicks How many ticks in between runnable calls

	 @return The scheduled BukkitTask
	 */
	public BukkitTask runSyncTimer(long delayTicks, long intervalTicks, BukkitRunnable runnable) {
		BukkitTask task = runnable.runTaskTimer(plugin, delayTicks, intervalTicks);
		syncTasks.add(task);

		return task;
	}

	/**
	 Schedules a runnable to asynchronously run once

	 @param delayTicks How many ticks before the runnable is called

	 @return The bukkit task id
	 */
	public BukkitTask runAsyncDelayed(long delayTicks, Runnable runnable) {
		synchronized (asyncLock) {
			BukkitTask task = getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks);
			asyncTasks.add(task);
			return task;
		}
	}

	/**
	 Schedules a runnable to asynchronously run repeatedly

	 @param delayTicks    How many ticks before the runnable is first called
	 @param intervalTicks How many ticks in between runnable calls

	 @return The scheduled BukkitTask
	 */
	public BukkitTask runAsyncTimer(long delayTicks, long intervalTicks, BukkitRunnable runnable) {
		synchronized (asyncLock) {
			BukkitTask task = runnable.runTaskTimerAsynchronously(plugin, delayTicks, intervalTicks);
			asyncTasks.add(task);
			return task;
		}
	}

	public void cancelAllTasks() {
		cancelAllSyncTasks();
		cancelAllAsyncTasks();
	}

	public void cancelAllSyncTasks() {
		cancelTasks(syncTasks);
	}

	public void cancelAllAsyncTasks() {
		synchronized (asyncLock) {
			cancelTasks(asyncTasks);
		}
	}

	private void cancelTasks(Set<BukkitTask> tasks) {
		Iterator<BukkitTask> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			BukkitTask task = iterator.next();

			if (getScheduler().isQueued(task.getTaskId())) {
				task.cancel();
				iterator.remove();
			}
		}

		tasks.clear();
	}

	public Set<BukkitTask> getSyncTasks() {
		return syncTasks;
	}

	public Set<BukkitTask> getAsyncTasks() {
		synchronized (asyncLock) {
			return asyncTasks;
		}
	}

	protected BukkitScheduler getScheduler() {
		return plugin.getServer()
				.getScheduler();
	}

	@Override
	public void run() {
		// Optional override
	}

}
