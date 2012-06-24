package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;

public class NPCSpawnEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		String npc = e.getOption("npc_id");
		if(npc == null)
			return false;
		
		try
		{
			int id = Integer.parseInt(npc);
			if(!DragonsLairMain.getSettings().getNPCs().containsKey(id))
				return false;
			
			DragonsLairMain.getInstance().getDungeonManager().spawnNPC(DragonsLairMain.getSettings().getNPCs().get(id).getName());
		}
		catch(Exception ex)
		{
			return false;
		}
		
		return true;
	}

}
