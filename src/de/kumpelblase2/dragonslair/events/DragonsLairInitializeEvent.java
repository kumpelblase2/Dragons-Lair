package de.kumpelblase2.dragonslair.events;

import de.kumpelblase2.dragonslair.DragonsLairMain;

public class DragonsLairInitializeEvent extends BaseEvent
{
	private final DragonsLairMain instance;
	
	public DragonsLairInitializeEvent(DragonsLairMain plugin)
	{
		this.instance = plugin;
	}
	
	public DragonsLairMain getInstance()
	{
		return this.instance;
	}
}
