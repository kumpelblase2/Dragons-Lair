package de.kumpelblase2.dragonslair.utilities;

import java.util.*;
import org.bukkit.*;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Trigger;
import de.kumpelblase2.dragonslair.api.TriggerType;
import de.kumpelblase2.dragonslair.conversation.AnswerConverter;
import de.kumpelblase2.dragonslair.conversation.AnswerType;

public final class GeneralUtilities
{
	public static <T> List<T> getOrderedValues(final Map<Integer, T> map)
	{
		final List<T> values = new ArrayList<T>();
		final TreeSet<Integer> keys = new TreeSet<Integer>(map.keySet());
		for(final Integer i : keys)
		{
			values.add(map.get(i));
		}

		return values;
	}

	public static boolean isValidOptionInput(final ConversationContext arg0, final String arg1, final String option)
	{
		if(option.equalsIgnoreCase("npc_id"))
		{
			try
			{
				final int id = Integer.parseInt(arg1);
				if(DragonsLairMain.getSettings().getNPCs().get(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A npc with that id does not exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				if(DragonsLairMain.getSettings().getNPCByName(arg1) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A npc with that id does not exist.");
					return false;
				}

				return true;
			}
		}
		else if(option.equalsIgnoreCase("mob_id"))
		{
			try
			{
				final int id = Integer.parseInt(arg1);
				if(EntityType.fromId(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "The entity id doesn't exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				if(EntityType.fromName(arg1) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An entity with that name doesn't exist.");
					return false;
				}

				return true;
			}
		}
		else if(option.equalsIgnoreCase("world"))
		{
			if(Bukkit.getWorld(arg1) == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "The world does not exist.");
				return false;
			}

			return true;
		}
		else if(option.equalsIgnoreCase("x") || option.equalsIgnoreCase("y") || option.equalsIgnoreCase("z") || option.equalsIgnoreCase("x2") || option.equalsIgnoreCase("y2") || option.equalsIgnoreCase("z2") || option.equalsIgnoreCase("amount") || option.equalsIgnoreCase("damage") || option.equalsIgnoreCase("cooldown") || option.equalsIgnoreCase("delay") || option.equalsIgnoreCase("duration") || option.equalsIgnoreCase("amplifier"))
		{
			try
			{
				Double.parseDouble(arg1);
				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "The input is not a valid number.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("block_id") || option.equalsIgnoreCase("item_id"))
		{
			if(option.equalsIgnoreCase("item_id") && arg1.equalsIgnoreCase("money"))
				return true;

			try
			{
				final Material m = Material.getMaterial(Integer.parseInt(arg1));
				if(m == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "There's no such type with that id.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				if(Material.getMaterial(arg1.replace(" ", "_").toUpperCase()) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "There's no such type with that name.");
					return false;
				}

				return true;
			}
		}
		else if(option.equalsIgnoreCase("dialog_id"))
		{
			try
			{
				final int id = Integer.parseInt(arg1);
				if(DragonsLairMain.getSettings().getDialogs().get(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A dialog with that id does not exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "It's not a number.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("scope") || option.equalsIgnoreCase("send_to"))
		{
			if(arg1.equalsIgnoreCase("single") || arg1.equalsIgnoreCase("all") || arg1.equalsIgnoreCase("party") || arg1.equalsIgnoreCase("interactor"))
				return true;
			else
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid scope.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("target"))
		{
			if(arg1.equalsIgnoreCase("player") || arg1.equalsIgnoreCase("enemy"))
				return true;
			else
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid target.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("chapter_id") || option.equalsIgnoreCase("starting chapter")) try
		{
			final Integer id = Integer.parseInt(arg1);
			if(DragonsLairMain.getSettings().getChapters().get(id) == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "A chapter with that id doesn't exist.");
				return false;
			}

			return true;
		}
		catch(final Exception e)
		{
			arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
			return false;
		}
		else if(option.equalsIgnoreCase("objective_id") || option.equalsIgnoreCase("next_id") || option.equalsIgnoreCase("starting objective"))
		{
			try
			{
				final Integer id = Integer.parseInt(arg1);
				if(DragonsLairMain.getSettings().getObjectives().get(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An objective with that id doesn't exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("dungeon_id")) try
		{
			final int id = Integer.parseInt(arg1);
			if(DragonsLairMain.getSettings().getDungeons().get(id) == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "A dungeon with that id does not exist.");
				return false;
			}

			return true;
		}
		catch(final Exception e)
		{
			if(DragonsLairMain.getSettings().getDungeonByName(arg1) == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "A dungeon with that name doesn't exist.");
				return false;
			}

			return true;
		}
		else if(option.equalsIgnoreCase("on_success") || option.equalsIgnoreCase("on_failure"))
		{
			try
			{
				final int id = Integer.parseInt(arg1);
				if(DragonsLairMain.getSettings().getEvents().get(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "It's not a number.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("name"))
		{
			if(DragonsLairMain.getSettings().getDungeonByName(arg1) == null)
				return true;

			arg0.getForWhom().sendRawMessage(ChatColor.RED + "A dungeon with that name already exists.");
			return false;
		}
		else if(option.equalsIgnoreCase("starting pos"))
		{
			if(!arg1.equalsIgnoreCase("here"))
			{
				if(WorldUtility.stringToLocation(arg1) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid location.");
					return false;
				}

				return true;
			}
			else
				return true;
		}
		else if(option.equalsIgnoreCase("safe word") || option.equalsIgnoreCase("message") || option.equalsIgnoreCase("permission") || option.equalsIgnoreCase("command") || option.equals("ending message") || option.equals("starting message") || option.equals("ready message"))
			return true;
		else if(option.equalsIgnoreCase("min players"))
		{
			try
			{
				Integer.parseInt(arg1);
				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "It's not a number");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("max players"))
		{
			try
			{
				Integer.parseInt(arg1);
				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "It's not a number");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("warp_on_end") || option.equalsIgnoreCase("give_items"))
		{
			try
			{
				Boolean.parseBoolean(arg1);
				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid input.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("spawned_by"))
		{
			try
			{
				final int id = Integer.parseInt(arg1);
				if(!DragonsLairMain.getSettings().getEvents().containsKey(id))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "There's no event with that id.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage("Not a valid number.");
				return false;
			}
		}
		else if(option.equalsIgnoreCase("potiontype"))
		{
			for(final PotionEffectType t : PotionEffectType.values())
			{
				if(t.getName().toLowerCase().replace("_", " ").equalsIgnoreCase(arg1.replace("_", " ")))
					return true;
			}

			return false;
		}
		else if(option.equalsIgnoreCase("change_type"))
			return arg1.equals("add") || arg1.equals("remove") || arg1.equals("set");
		else if(option.equalsIgnoreCase("execute_as"))
			return arg1.equals("player") || arg1.equals("console");
		else if(option.equals("breakable blocks"))
		{
			final AnswerType answer = new AnswerConverter(arg1).convert();
			return answer != AnswerType.NOTHING;
		}
		else if(option.equals("event_id"))
		{
			try
			{
				final Integer id = Integer.parseInt(arg1);
				if(DragonsLairMain.getEventScheduler().getEventByID(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		else
			return false;
	}

	public static void recalculateOptions(final Trigger t)
	{
		if(t.getOption("x") == null || t.getOption("y") == null || t.getOption("z") == null)
			return;

		final int x = Integer.parseInt(t.getOption("x"));
		final int y = Integer.parseInt(t.getOption("y"));
		final int z = Integer.parseInt(t.getOption("z"));
		final int x2 = (t.getOption("x2") == null) ? x : Integer.parseInt(t.getOption("x2"));
		final int y2 = (t.getOption("y2") == null) ? y : Integer.parseInt(t.getOption("y2"));
		final int z2 = (t.getOption("z2") == null) ? z : Integer.parseInt(t.getOption("z2"));
		final int minx = (x > x2) ? x2 : x;
		final int maxx = (x < x2) ? x2 : x;
		final int miny = (y > y2) ? y2 : y;
		final int maxy = (y < y2) ? y2 : y;
		final int minz = (z > z2) ? z2 : z;
		final int maxz = (z < z2) ? z2 : z;
		t.setOption("x", minx + "");
		t.setOption("y", miny + "");
		t.setOption("z", minz + "");
		if(t.getOption("x2") != null)
			t.setOption("x2", "" + maxx);

		if(t.getOption("y2") != null)
			t.setOption("y2", maxy + "");

		if(t.getOption("z2") != null)
			t.setOption("z2", maxz + "");
	}

	public static int getDefaultCooldown(final TriggerType type)
	{
		switch(type)
		{
			case BLOCK_BREAK:
			case BLOCK_INTERACT:
			case BLOCK_PLACE:
			case GATHER_ITEM:
				return 30;
			case MOBS_KILLED:
				return 10;
			case MOVEMENT:
				return 3;
			default:
				return 0;
		}
	}

	public static String replaceColors(final String inString)
	{
		if(inString == null)
			return "";

		if(inString.length() == 0)
			return inString;

		return ChatColor.translateAlternateColorCodes('&', inString);
	}
}