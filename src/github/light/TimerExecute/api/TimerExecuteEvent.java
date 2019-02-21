package github.light.TimerExecute.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 当一个计时器被触发时,触发此事件
 */
public class TimerExecuteEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancel = false;
	
	private Timer timer;
	
	TimerExecuteEvent(Timer timer) {
		
		this.timer = timer;
		
	}
	
	public Timer getTimer() {
		return timer;
	}

	public HandlerList getHandlers() {
		
	    return handlers;
	    
	}
	
	public static HandlerList getHandlerList() {
		
	    return handlers;
	    
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean append) {
		cancel = append;
	}

}
