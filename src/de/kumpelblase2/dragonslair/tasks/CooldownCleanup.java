package de.kumpelblase2.dragonslair.tasks;

import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.DungeonManager;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.Trigger;

public class CooldownCleanup implements Runnable
{
	public void run()
	{
		DungeonManager manager = DragonsLairMain.getInstance().getDungeonManager();
		if(manager == null)
			return;
		
		for(Trigger t : manager.getSettings().getTriggers().values())
		{
			t.clearCooldowns();
		}
		
		for(Event e : manager.getSettings().getEvents().values())
		{
			e.clearCooldowns();
		}
	}

}
