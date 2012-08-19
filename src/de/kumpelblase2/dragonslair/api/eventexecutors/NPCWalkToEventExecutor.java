package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.topcat.npclib.entity.HumanNPC;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCWalkToEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		String npcid = e.getOption("npc_id");
		try
		{
			NPC n = DragonsLairMain.getSettings().getNPCByName(npcid);
			if(n == null)
			{
				Integer id = Integer.parseInt(npcid);
				n = DragonsLairMain.getSettings().getNPCs().get(id);
				if(n == null)
					return false;
			}
			
			String x = e.getOption("x");
			String y = e.getOption("y");
			String z = e.getOption("z");
			
			if(x == null || y == null || z == null)
				return false;
			
			HumanNPC npc = DragonsLairMain.getDungeonManager().getNPCByID(n.getID());
			if(npc != null)
				npc.walkTo(new Location(npc.getBukkitEntity().getLocation().getWorld(), Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)));
			
			return true;
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to stop npc attack from event: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}
		return false;
	}

}
