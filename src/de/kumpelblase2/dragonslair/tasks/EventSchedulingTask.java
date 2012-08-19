package de.kumpelblase2.dragonslair.tasks;

import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ScheduledEvent;

public class EventSchedulingTask implements Runnable
{
	private final ScheduledEvent event;
	
	public EventSchedulingTask(ScheduledEvent inEvent)
	{
		this.event = inEvent;
	}
	
	@Override
	public void run()
	{
		for(Integer id : this.event.getEventIDs())
		{
			if(id <= 0)
				continue;
			
			DragonsLairMain.getDungeonManager().executeEvent(DragonsLairMain.getSettings().getEvents().get(id), null);
		}
	}
}
