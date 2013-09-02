package de.kumpelblase2.dragonslair.map;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MinecraftFont;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;

public class DLMap
{
	private final String player;
	private int line = 0;
	private int objectiveid = -1;
	private int chapterid = -1;
	public final int lineHeight = MinecraftFont.Font.getHeight();
	public final int maxLinesPerMap = 120 / this.lineHeight;
	private boolean rendered = false;
	private String text;
	private String titleText;

	public DLMap(final Player p)
	{
		this.player = p.getName();
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(this.player);
		if(ad != null)
		{
			this.objectiveid = ad.getCurrentObjective().getID();
			this.chapterid = ad.getCurrentChapter().getID();
			this.titleText = "Chapter://" + ad.getCurrentChapter().getName();
			this.text = ad.getCurrentObjective().getDescription();
		}
	}

	public void setRendered(final boolean state)
	{
		this.rendered = state;
	}

	public boolean isRendered()
	{
		return this.rendered;
	}

	public int getCurrentLine()
	{
		return this.line;
	}

	public void scrollUp()
	{
		if(this.line >= 1)
		{
			this.line--;
			this.setRendered(false);
		}
	}

	public void scrollDown()
	{
		if(this.line > this.getSplittedText(this.getTitle() + "////" + this.getText()).length - 1)
			return;

		this.line++;
		this.setRendered(false);
	}

	public void checkUpdate()
	{
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(this.player);
		if(ad == null)
			return;

		if(this.chapterid != ad.getCurrentChapter().getID())
		{
			this.chapterid = ad.getCurrentChapter().getID();
			this.titleText = "Chapter://" + ad.getCurrentChapter().getName();
			this.setRendered(false);
		}

		if(this.objectiveid != ad.getCurrentObjective().getID())
		{
			this.objectiveid = ad.getCurrentObjective().getID();
			this.text = ad.getCurrentObjective().getDescription();
			this.setRendered(false);
		}
	}

	public String[] getSplittedText(final String in)
	{
		final List<String> mapLines = new ArrayList<String>();
		final String[] lines = in.split("//");
		final MinecraftFont f = MinecraftFont.Font;
		for(final String line : lines)
		{
			if(f.getWidth(line) <= 100)
				mapLines.add(line);
			else
			{
				final String[] chars = line.split("\\s+");
				String current = chars[0] + " ";
				int index = 1;
				while(index < chars.length)
				{
					if(f.getWidth(current + chars[index]) > 100)
					{
						mapLines.add(current);
						current = chars[index] + " ";
					}
					else
						current += chars[index] + " ";

					index++;
				}

				mapLines.add(current);
			}
		}

		return mapLines.toArray(new String[0]);
	}

	public String getText()
	{
		return this.text;
	}

	public String getTitle()
	{
		return this.titleText;
	}

	public Player getPlayer()
	{
		return Bukkit.getPlayer(this.player);
	}
}