package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;

public class DungeonStartEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			String dungeon_id = e.getOption("dungeon_id");
			try
			{
				int id = Integer.parseInt(dungeon_id);
				DragonsLairMain.getDungeonManager().startDungeon(id);
			}
			catch(Exception ex2)
			{
				DragonsLairMain.getDungeonManager().startDungeon(dungeon_id);
			}
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Couldn't execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}

}
