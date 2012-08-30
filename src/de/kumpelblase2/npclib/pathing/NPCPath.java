package de.kumpelblase2.npclib.pathing;

// original provided by Topcat, modified by kumpelblase2
import java.util.ArrayList;
import org.bukkit.Location;

public class NPCPath
{
	private final ArrayList<Node> path;
	private final NPCPathFinder pathFinder;
	private final Location end;

	public NPCPath(final NPCPathFinder npcPathFinder, final ArrayList<Node> path, final Location end)
	{
		this.path = path;
		this.end = end;
		this.pathFinder = npcPathFinder;
	}

	public Location getEnd()
	{
		return this.end;
	}

	public ArrayList<Node> getPath()
	{
		return this.path;
	}

	public boolean checkPath(final Node node, final Node parent, final boolean update)
	{
		return this.pathFinder.checkPath(node, parent, update);
	}
}