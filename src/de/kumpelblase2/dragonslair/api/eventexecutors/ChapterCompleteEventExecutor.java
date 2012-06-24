package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.events.dungeon.ChapterChangeEvent;

public class ChapterCompleteEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			ActiveDungeon d = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
			if(d == null)
				return false;
			
			Integer id = Integer.parseInt(e.getOption("chapter_id"));
			
			Chapter next = DragonsLairMain.getSettings().getChapters().get(id);
			if(next == null)
				return false;
			ChapterChangeEvent event = new ChapterChangeEvent(d, next);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled())
				d.setNextChapter(event.getNextChapter());
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Couldn't execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}

}
