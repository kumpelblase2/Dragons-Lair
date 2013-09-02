package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;

public class DungeonStartEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final String dungeon_id = e.getOption("dungeon_id");
			try
			{
				final int id = Integer.parseInt(dungeon_id);
				DragonsLairMain.getDungeonManager().startDungeon(id);
			}
			catch(final Exception ex2)
			{
				DragonsLairMain.getDungeonManager().startDungeon(dungeon_id);
			}
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Couldn't execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}

		return true;
	}
}