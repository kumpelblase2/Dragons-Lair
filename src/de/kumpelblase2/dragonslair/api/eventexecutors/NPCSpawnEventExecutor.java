package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;

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
			NPC n = DragonsLairMain.getSettings().getNPCByName(npc);
			if(n == null)
			{
				int id = Integer.parseInt(npc);
				if(!DragonsLairMain.getSettings().getNPCs().containsKey(id))
					return false;
				
				DragonsLairMain.getDungeonManager().spawnNPC(DragonsLairMain.getSettings().getNPCs().get(id).getName());
			}
			else
			{
				DragonsLairMain.getDungeonManager().spawnNPC(n.getID());
			}
		}
		catch(Exception ex)
		{
			return false;
		}
		
		return true;
	}

}
