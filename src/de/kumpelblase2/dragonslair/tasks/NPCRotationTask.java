package de.kumpelblase2.dragonslair.tasks;

import java.util.*;
import org.bukkit.entity.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;
import de.kumpelblase2.npclib.entity.HumanNPC;

public class NPCRotationTask implements Runnable
{
	private final Integer npcID;
	private boolean looked = false;
	
	public NPCRotationTask(Integer id)
	{
		this.npcID = id;
	}
	
	@Override
	public void run()
	{
		DragonsLairMain main = DragonsLairMain.getInstance();
		if(main != null)
		{
			HumanNPC npc = DragonsLairMain.getDungeonManager().getNPCByID(this.npcID);
			if(npc != null)
			{
				List<Entity> entities = npc.getBukkitEntity().getNearbyEntities(10, 5, 10);				
				List<EntityType> types = new ArrayList<EntityType>(Arrays.asList(EntityType.values()));
				types.remove(EntityType.PLAYER);
				LivingEntity lookat = WorldUtility.getNearestEntity(npc.getBukkitEntity().getLocation(), entities, types);
				if(!npc.isWalking())
				{
					if(lookat != null)
					{
						npc.lookAtEntity(lookat);
					}
					else
					{
						if(!looked)
						{
							Random r = new Random();
							float yaw = r.nextFloat() * 180;
							float pitch = r.nextFloat() * 30;
							boolean inverseYaw = r.nextBoolean();
							boolean inversePitch = r.nextBoolean();
							if(inverseYaw)
								npc.setYaw(-yaw);
							else
								npc.setYaw(yaw);
							
							if(inversePitch)
								npc.setPitch(-pitch);
							else
								npc.setPitch(pitch);
							
							looked = true;
						}
						else
						{
							looked = false;
						}
					}
				}
			}
		}
	}

}
