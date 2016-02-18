package net.minegage.common.board;


import org.bukkit.entity.*;
import org.bukkit.event.*;


public class AssignBoardEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private Player player;
	private Board board;
	
	public AssignBoardEvent(Player player, Board board) {
		super();
		this.player = player;
		this.board = board;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	
	
}
