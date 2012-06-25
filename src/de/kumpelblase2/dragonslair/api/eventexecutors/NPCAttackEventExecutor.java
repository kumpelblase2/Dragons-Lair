package de.kumpelblase2.dragonslair.api.eventexecutors;

import java.util.*;
import org.bukkit.entity.*;
import com.topcat.npclib.entity.HumanNPC;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class NPCAttackEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		String npcid = e.getOption("npc_id");
		try
		{
			int id = Integer.parseInt(npcid);
			NPC n = DragonsLairMain.getSettings().getNPCs().get(id);
			if(n == null)
				return false;
			
			String target = e.getOption("target");
			HumanNPC npc = DragonsLairMain.getDungeonManager().getNPCByName(n.getName());
			List<EntityType> types = new ArrayList<EntityType>();
			if(target == null || target.equals("enemy"))
			{
				types = Arrays.asList(new EntityType[] { EntityType.PLAYER });
			}
			else if(target.equals("player"))
			{
				types = new ArrayList<EntityType>(Arrays.asList(EntityType.values()));
				types.remove(EntityType.PLAYER);
			}
			
			LivingEntity nearest = WorldUtility.getNearestEntity(npc.getBukkitEntity().getLocation(), npc.getBukkitEntity().getNearbyEntities(10, 3, 10), types);
			if(nearest != null && npc != null)
			{
				npc.startAttacking(nearest);
			}
			
			return true;
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to let npc starting to attack from event: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}
		return false;
	}

}
