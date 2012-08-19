package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;

public class ExecuteCommandEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			String command = e.getOption("command");
			String as = e.getOption("execute_as");
			if(command.startsWith("/"))
				command = command.substring(1);
			
			if(as == null || as.equals("player"))
			{
				Bukkit.dispatchCommand(p, command);
			}
			else
			{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to execute event with id " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}
}
