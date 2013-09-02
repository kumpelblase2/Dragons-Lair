package de.kumpelblase2.dragonslair.tasks;

import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.DungeonManager;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.Trigger;

public class CooldownCleanup implements Runnable
{
	@Override
	public void run()
	{
		final DungeonManager manager = DragonsLairMain.getDungeonManager();
		if(manager == null)
			return;

		for(final Trigger t : manager.getSettings().getTriggers().values())
		{
			t.clearCooldowns();
		}

		for(final Event e : manager.getSettings().getEvents().values())
		{
			e.clearCooldowns();
		}
	}
}