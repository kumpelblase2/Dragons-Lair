package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.events.dungeon.ChapterChangeEvent;

public class ChapterCompleteEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final ActiveDungeon d = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
			if(d == null)
				return false;

			final Integer id = Integer.parseInt(e.getOption("chapter_id"));
			final Chapter next = DragonsLairMain.getSettings().getChapters().get(id);
			if(next == null)
				return false;

			final ChapterChangeEvent event = new ChapterChangeEvent(d, next);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled())
				d.setNextChapter(event.getNextChapter());
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Couldn't execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}

		return true;
	}
}