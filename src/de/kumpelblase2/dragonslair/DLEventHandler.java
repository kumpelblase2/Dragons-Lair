package de.kumpelblase2.dragonslair;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.conversation.ConversationHandler;
import de.kumpelblase2.dragonslair.events.conversation.ConversationNextDialogEvent;
import de.kumpelblase2.dragonslair.events.dungeon.*;
import de.kumpelblase2.dragonslair.logging.TNTList;
import de.kumpelblase2.dragonslair.logging.TNTList.TNTEntry;
import de.kumpelblase2.dragonslair.map.DLMap;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;
import de.kumpelblase2.remoteentities.api.events.RemoteEntityInteractEvent;

public class DLEventHandler implements Listener
{
	private final Map<TriggerType, Set<Trigger>> triggers = new HashMap<TriggerType, Set<Trigger>>();
	private final Set<TriggerLocationEntry> locations = new HashSet<TriggerLocationEntry>();
	private final TNTList tntList = new TNTList();
	private final Set<String> deadPlayers = new HashSet<String>();

	public void reloadTriggers()
	{
		this.triggers.clear();
		this.locations.clear();
		for(final Trigger t : DragonsLairMain.getSettings().getTriggers().values())
		{
			if(t.getOption("x") == null || t.getOption("y") == null || t.getOption("z") == null)
			{
				if(!this.triggers.containsKey(t.getType()))
					this.triggers.put(t.getType(), new HashSet<Trigger>());

				this.triggers.get(t.getType()).add(t);
			}
			else
			{
				String x, y, z, x2, y2, z2;
				x = t.getOption("x");
				y = t.getOption("y");
				z = t.getOption("z");
				x2 = t.getOption("x2");
				y2 = t.getOption("y2");
				z2 = t.getOption("z2");
				final String world = t.getOption("world");
				if(x2 == null) x2 = x;
				if(y2 == null) y2 = y;
				if(z2 == null) z2 = z;
				int minx = 0, maxx = 0, miny = 0, maxy = 0, minz = 0, maxz = 0;
				try
				{
					minx = Integer.parseInt(x);
					maxx = Integer.parseInt(x2);
					miny = Integer.parseInt(y);
					maxy = Integer.parseInt(y2);
					minz = Integer.parseInt(z);
					maxz = Integer.parseInt(z2);
				}
				catch(final Exception e)
				{
					DragonsLairMain.Log.warning("The was an error parsing the location of trigger " + t.getID() + ".");
					continue;
				}

				for(int posx = minx; posx <= maxx; posx++)
				{
					for(int posy = miny; posy <= maxy; posy++)
					{
						for(int posz = minz; posz <= maxz; posz++)
						{
							final Location loc = new Location(Bukkit.getWorld(world), posx, posy, posz);
							boolean added = false;
							for(final TriggerLocationEntry entry : this.locations)
							{
								if(entry.equals(loc))
								{
									entry.addTrigger(t);
									added = true;
								}
							}

							if(!added)
							{
								final TriggerLocationEntry entry = new TriggerLocationEntry(loc);
								entry.addTrigger(t);
								this.locations.add(entry);
							}
						}
					}
				}
			}
		}
	}

	public void removePlayerFromDeathObserving(final String player)
	{
		this.deadPlayers.remove(player);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInteractNPC(final RemoteEntityInteractEvent event)
	{
		if(!DragonsLairMain.isWorldEnabled(event.getRemoteEntity().getBukkitEntity().getWorld().getName()))
			return;

		if(!this.triggers.containsKey(TriggerType.NPC_INTERACT))
			return;

		final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getInteractor().getName());
		final int databaseid = DragonsLairMain.getDungeonManager().getNPCManager().getDatabaseIDFromEntity(event.getRemoteEntity());
		for(final Trigger t : this.triggers.get(TriggerType.NPC_INTERACT))
		{
			final String npcid = t.getOption("npc_id");
			if(npcid == null)
				continue;

			final String dungeonID = t.getOption("dungeon_id");
			if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
				continue;

			if(npcid.equals(databaseid + ""))
				DragonsLairMain.getDungeonManager().callTrigger(t, event.getInteractor());
		}
		/*else if(((NpcEntityTargetEvent)event).getNpcReason() == NpcTargetReason.NPC_BOUNCED)
		{
			if(!this.triggers.containsKey(TriggerType.NPC_TOUCH))
				return;
			final NPC npc = DragonsLairMain.getDungeonManager().getNPCByNPCEntity(event.getEntity());
			final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(((Player)event.getTarget()).getName());
			for(final Trigger t : this.triggers.get(TriggerType.NPC_TOUCH))
			{
				final String npcid = t.getOption("npc_id");
				if(npcid == null)
					continue;

				final String dungeonID = t.getOption("dungeon_id");
				if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
					continue;

				if(npcid.equals(npc.getID() + ""))
					DragonsLairMain.getDungeonManager().callTrigger(t, (Player)event.getTarget());
			}
		}*/
	}

	@EventHandler(ignoreCancelled = true)
	public void onNPCDamage(final EntityDamageByEntityEvent event)
	{
		if(!DragonsLairMain.getDungeonManager().getNPCManager().isRemoteEntity((LivingEntity)event.getEntity()))
			return;

		if(!(event.getDamager() instanceof Player))
			return;

		if(!this.triggers.containsKey(TriggerType.NPC_DAMAGE))
			return;

		if(!DragonsLairMain.isWorldEnabled(event.getDamager().getWorld().getName()))
			return;

		final NPC npc = DragonsLairMain.getDungeonManager().getNPCManager().getNPCFromEntity(DragonsLairMain.getDungeonManager().getNPCByEntity((LivingEntity)event.getEntity()));
		final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(((Player)event.getDamager()).getName());
		for(final Trigger t : this.triggers.get(TriggerType.NPC_DAMAGE))
		{
			final String npcid = t.getOption("npc_id");
			if(npcid == null)
				continue;

			try
			{
				final Integer id = Integer.parseInt(npcid);
				if(id != npc.getID())
					continue;

				final String dungeonID = t.getOption("dungeon_id");
				if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
					continue;

				DragonsLairMain.getDungeonManager().callTrigger(t, (Player)event.getDamager());
			}
			catch(final Exception e)
			{
				if(npc.getName().equals(npcid))
					DragonsLairMain.getDungeonManager().callTrigger(t, (Player)event.getDamager());
			}
		}

		if(npc.isInvincible())
			event.setDamage(0);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onMove(final PlayerMoveEvent event)
	{
		final Location from = event.getFrom();
		final Location to = event.getTo();
		final Player p = event.getPlayer();
		if(to.getBlockX() - from.getBlockX() == 0 && to.getBlockY() - from.getBlockY() == 0 && to.getBlockZ() - from.getBlockZ() == 0)
			return;

		if(!DragonsLairMain.isWorldEnabled(to.getWorld().getName()))
			return;

		if(this.locations.size() > 0)
		{
			final Location newLoc = new Location(p.getWorld(), to.getBlockX(), to.getY(), to.getBlockZ());
			final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getPlayer().getName());
			for(final TriggerLocationEntry entry : this.locations)
			{
				if(entry.equals(newLoc))
				{
					for(final Trigger t : entry.getTriggersForType(TriggerType.MOVEMENT))
					{
						final String dungeonID = t.getOption("dungeon_id");
						if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
							continue;

						DragonsLairMain.getDungeonManager().callTrigger(t, p);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onNextDialog(final ConversationNextDialogEvent event)
	{
		if(!DragonsLairMain.isWorldEnabled(((Player)event.getConversation().getForWhom()).getWorld().getName()))
			return;

		if(this.triggers.containsKey(TriggerType.DIALOG_OCCUR))
		{
			final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getPlayer().getName());
			for(final Trigger t : this.triggers.get(TriggerType.DIALOG_OCCUR))
			{
				if(!t.getOption("npc_id").equals(event.getNPC().getID() + ""))
					continue;

				final String dungeonID = t.getOption("dungeon_id");
				if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
					continue;

				if(t.getOption("dialog_id") != null)
				{
					final String id = t.getOption("dialog_id");
					if(id.equals(event.getNextDialogID() + ""))
						DragonsLairMain.getDungeonManager().callTrigger(t, (Player)event.getConversation().getForWhom());
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerChat(final AsyncPlayerChatEvent event)
	{
		final Player p = event.getPlayer();
		if(!DragonsLairMain.isWorldEnabled(p.getWorld().getName()))
			return;

		final ActiveDungeon d = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		if(d == null)
			return;

		if(d.getInfo().getSafeWord().equalsIgnoreCase(event.getMessage()))
			DragonsLairMain.getInstance().getConversationHandler().startSafeWordConversation(p);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		final Player p = event.getPlayer();
		final Location placed = event.getBlock().getLocation();
		if(!DragonsLairMain.isWorldEnabled(placed.getWorld().getName()))
			return;

		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad != null)
			DragonsLairMain.getInstance().getLoggingManager().logBlockPlace(ad, event.getBlock().getState());

		if(event.getBlock().getType() == Material.TNT)
			this.tntList.addEntry(ad.getInfo().getName(), placed);

		final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getPlayer().getName());
		for(final TriggerLocationEntry entry : this.locations)
		{
			if(entry.equals(event.getBlock().getLocation()))
			{
				for(final Trigger t : entry.getTriggersForType(TriggerType.BLOCK_PLACE))
				{
					final String dungeonID = t.getOption("dungeon_id");
					if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
						continue;

					final String block_id = t.getOption("block_id");
					if(block_id != null)
					{
						try
						{
							final int id = Integer.parseInt(block_id);
							final Material m = Material.getMaterial(id);
							if(m != null)
							{
								if(placed.getBlock().getType() != m)
									continue;
							}
						}
						catch(final Exception e)
						{
							final Material m = Material.getMaterial(block_id.replace(" ", "_").toUpperCase());
							if(m != null)
							{
								if(placed.getBlock().getType() != m)
									continue;
							}
						}
					}

					DragonsLairMain.getDungeonManager().callTrigger(t, p);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(final BlockBreakEvent event)
	{
		final Player p = event.getPlayer();
		final Block placed = event.getBlock();
		if(!DragonsLairMain.isWorldEnabled(placed.getWorld().getName()))
			return;

		if(this.deadPlayers.contains(p.getName()))
		{
			event.setCancelled(true);
			return;
		}

		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad != null)
		{
			if(!ad.getInfo().areBlocksBreakable())
			{
				event.setCancelled(true);
				return;
			}

			DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, placed.getState());
			switch(placed.getType())
			{
				case BED_BLOCK:
					final Bed b = (Bed)placed.getState().getData();
					if(b.isHeadOfBed())
						DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, placed.getRelative(b.getFacing().getOppositeFace()).getState());
					else
						DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, placed.getRelative(b.getFacing()).getState());

					break;
				case WOODEN_DOOR:
				case IRON_DOOR_BLOCK:
					final Door d = (Door)placed.getState().getData();
					if(d.isTopHalf())
						DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, placed.getRelative(BlockFace.DOWN).getState());
					else
						DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, placed.getRelative(BlockFace.UP).getState());

					break;
				default:
					break;
			}
		}

		for(final TriggerLocationEntry entry : this.locations)
		{
			if(entry.equals(placed.getLocation()))
			{
				for(final Trigger t : entry.getTriggersForType(TriggerType.BLOCK_BREAK))
				{
					final String dungeonID = t.getOption("dungeon_id");
					if(ad != null && dungeonID != null && (!dungeonID.equals("" + ad.getInfo().getID()) || !dungeonID.equals("" + ad.getInfo().getName())))
						continue;

					final String block_id = t.getOption("block_id");
					if(block_id != null)
					{
						try
						{
							final int id = Integer.parseInt(block_id);
							final Material m = Material.getMaterial(id);
							if(m != null)
							{
								if(placed.getType() != m)
									continue;
							}
						}
						catch(final Exception e)
						{
							final Material m = Material.getMaterial(block_id.replace(" ", "_").toUpperCase());
							if(m != null)
							{
								if(placed.getType() != m)
									continue;
							}
						}
					}

					DragonsLairMain.getDungeonManager().callTrigger(t, p);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onExplode(final EntityExplodeEvent event)
	{
		final TNTEntry e = this.tntList.getEntry(event.getLocation());
		if(e != null && event.getEntityType() == EntityType.PRIMED_TNT)
		{
			final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getActiveDungeonByName(e.getDungeon());
			if(ad == null)
				return;

			final List<Block> blocks = event.blockList();
			for(final Block b : blocks)
			{
				DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, b.getState());
			}
		}

		if(event.getEntityType() == EntityType.CREEPER)
		{
			final EventMonster en = DragonsLairMain.getDungeonManager().getEventMonsterByEntity((LivingEntity)event.getEntity());
			if(en == null)
				return;

			final ActiveDungeon ad = en.getDungeon();
			if(!ad.getInfo().areBlocksBreakable())
				event.blockList().clear();
		}
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event)
	{
		final Player p = event.getPlayer();
		final Action a = event.getAction();
		if(a != Action.LEFT_CLICK_BLOCK && a != Action.RIGHT_CLICK_BLOCK && a != Action.PHYSICAL)
			return;

		if(this.deadPlayers.contains(p.getName()))
		{
			event.setCancelled(true);
			return;
		}

		final Block interactedBlock = event.getClickedBlock();
		final Location interacted = interactedBlock.getLocation();
		if(!DragonsLairMain.isWorldEnabled(interacted.getWorld().getName()))
			return;

		final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getPlayer().getName());
		for(final TriggerLocationEntry entry : this.locations)
		{
			if(entry.equals(interactedBlock.getLocation()))
			{
				for(final Trigger t : entry.getTriggersForType(TriggerType.BLOCK_INTERACT))
				{
					final String dungeonID = t.getOption("dungeon_id");
					if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
						continue;

					final String block_id = t.getOption("block_id");
					if(block_id != null)
					{
						try
						{
							final int id = Integer.parseInt(block_id);
							final Material m = Material.getMaterial(id);
							if(m != null)
							{
								if(interactedBlock.getType() != m)
									continue;
							}
						}
						catch(final Exception e)
						{
							final Material m = Material.getMaterial(block_id.replace(" ", "_").toUpperCase());
							if(m != null)
							{
								if(interactedBlock.getType() != m)
									continue;
							}
						}
					}

					DragonsLairMain.getDungeonManager().callTrigger(t, p);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onChapterChange(final ChapterChangeEvent event)
	{
		if(!this.triggers.containsKey(TriggerType.CHAPTER_CHANGE))
			return;

		if(!DragonsLairMain.isWorldEnabled(Bukkit.getPlayer(event.getActiveDungeon().getCurrentParty().getMembers()[0]).getWorld().getName()))
			return;

		final int dungeon = event.getDungeon().getID();
		final int chapterid = event.getNextChapter().getID();
		for(final Trigger t : this.triggers.get(TriggerType.CHAPTER_CHANGE))
		{
			final String d = t.getOption("dungeon_id");
			final String chapter = t.getOption("chapter_id");
			if(d != null)
			{
				if(!d.equals(dungeon + ""))
					continue;
			}

			if(!chapter.equals(chapterid + ""))
				continue;

			DragonsLairMain.getDungeonManager().callTrigger(t, null);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onObjectiveChange(final ObjectiveChangeEvent event)
	{
		if(!this.triggers.containsKey(TriggerType.OBJECTIVE_CHANGE))
			return;

		if(!DragonsLairMain.isWorldEnabled(Bukkit.getPlayer(event.getActiveDungeon().getCurrentParty().getMembers()[0]).getWorld().getName()))
			return;

		final int dungeon = event.getDungeon().getID();
		final int objectiveid = event.getNextObjective().getID();
		for(final Trigger t : this.triggers.get(TriggerType.OBJECTIVE_CHANGE))
		{
			final String d = t.getOption("dungeon_id");
			final String objective = t.getOption("objective_id");
			if(d != null)
			{
				if(!d.equals(dungeon + ""))
					continue;
			}

			if(!objective.equals(objectiveid + ""))
				continue;

			DragonsLairMain.getDungeonManager().callTrigger(t, null);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onMapScroll(final PlayerInteractEvent event)
	{
		final Player p = event.getPlayer();
		final ItemStack current = p.getItemInHand();
		if(current == null || current.getType() != Material.MAP)
			return;

		if(DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName()) == null)
			return;

		final DLMap map = DragonsLairMain.getDungeonManager().getMapOfPlayer(p);
		if(map == null)
			return;

		final Action a = event.getAction();
		if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK)
			map.scrollUp();
		else if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
			map.scrollDown();

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemDrop(final PlayerDropItemEvent event)
	{
		final Player p = event.getPlayer();
		final ItemStack dropped = event.getItemDrop().getItemStack();
		if(DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName()) == null)
			return;

		if(dropped.getType() != Material.MAP)
			return;

		if(dropped.getEnchantments().containsKey(Enchantment.ARROW_INFINITE))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInventoryClick(final InventoryClickEvent event)
	{
		final Player p = (Player)event.getWhoClicked();
		if(!DragonsLairMain.isWorldEnabled(p.getWorld().getName()))
			return;

		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad == null)
			return;

		if(event.getInventory().getType() == InventoryType.PLAYER || event.getInventory().getType() == InventoryType.ENCHANTING || event.getInventory().getType() == InventoryType.CRAFTING)
			return;

		if(event.getRawSlot() == -999)
			return;

		final BlockState holder = (BlockState)event.getInventory().getHolder();
		if(event.getView().getTopInventory().getSize() > event.getRawSlot())
		{
			if(event.getCursor().getType() != Material.AIR || event.getCurrentItem().getType() != Material.AIR)
			{
				final Map<String, String> newItem = new HashMap<String, String>();
				final Map<String, String> oldItem = new HashMap<String, String>();
				if(event.getCursor().getType() == Material.AIR)
					newItem.put("slot" + event.getSlot(), InventoryUtilities.itemToString(null));
				else
					oldItem.put("slot" + event.getSlot(), InventoryUtilities.itemToString(event.getCursor()));

				DragonsLairMain.getInstance().getLoggingManager().logBlockContentChange(ad, holder, newItem, oldItem);
			}
		}
		else if(event.isShiftClick())
			DragonsLairMain.getInstance().getLoggingManager().logBlockContentChange(ad, holder, InventoryUtilities.getChangedStates(event.getView().getTopInventory(), (event.getCursor().getType() == Material.AIR ? event.getCurrentItem() : event.getCursor())), new HashMap<String, String>());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerExit(final PlayerQuitEvent event)
	{
		final Player p = event.getPlayer();
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad == null)
			return;

		DragonsLairMain.getDungeonManager().stopDungeon(ad.getInfo().getName());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPickup(final PlayerPickupItemEvent event)
	{
		final Player p = event.getPlayer();
		if(!DragonsLairMain.isWorldEnabled(p.getWorld().getName()))
			return;

		if(!this.triggers.containsKey(TriggerType.GATHER_ITEM))
			return;

		final Inventory i = p.getInventory();
		final ItemStack pickedUp = event.getItem().getItemStack();
		for(final Trigger t : this.triggers.get(TriggerType.GATHER_ITEM))
		{
			Material m;
			int amount;
			int dungeonid;
			try
			{
				m = Material.getMaterial(Integer.parseInt(t.getOption("item_id")));
			}
			catch(final Exception e)
			{
				m = Material.getMaterial(t.getOption("item_id").replace(" ", "_").toUpperCase());
			}

			if(m == null)
				continue;

			try
			{
				amount = Integer.parseInt(t.getOption("amount"));
			}
			catch(final Exception e)
			{
				continue;
			}

			if(t.getOption("dungeon_id") == null)
				dungeonid = -1;
			else
			{
				try
				{
					dungeonid = Integer.parseInt(t.getOption("dungeon_id"));
				}
				catch(final Exception e)
				{
					continue;
				}
			}

			if(dungeonid != -1)
			{
				final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad == null)
					continue;

				if(ad.getInfo().getID() != dungeonid)
					continue;
			}

			if(pickedUp.getType() == m)
			{
				if(pickedUp.getAmount() >= amount)
					DragonsLairMain.getDungeonManager().callTrigger(t, p);
				else
				{
					amount -= pickedUp.getAmount();
					if(i.contains(m))
					{
						final HashMap<Integer, ? extends ItemStack> items = i.all(m);
						for(final ItemStack item : items.values())
						{
							if(item.getAmount() >= amount)
								DragonsLairMain.getDungeonManager().callTrigger(t, p);
							else
								amount -= item.getAmount();
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(final EntityDeathEvent event)
	{
		if(!this.triggers.containsKey(TriggerType.MOBS_KILLED) && !this.triggers.containsKey(TriggerType.NPC_DEATH))
			return;

		final Player killer = event.getEntity().getKiller();
		if(killer == null)
			return;

		if(!DragonsLairMain.isWorldEnabled(killer.getWorld().getName()))
			return;

		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(killer.getName());
		if(ad == null)
			return;

		LivingEntity e = event.getEntity();
		EntityType type = e.getType();
		if(type == EntityType.COMPLEX_PART)
		{
			type = EntityType.ENDER_DRAGON;
			e = ((EnderDragonPart)e).getParent();
		}

		if(type == EntityType.PLAYER)
		{
			if(!this.triggers.containsKey(TriggerType.NPC_DEATH))
				return;

			final NPC n = DragonsLairMain.getDungeonManager().getNPCManager().getNPCFromEntity(DragonsLairMain.getDungeonManager().getNPCByEntity(e));
			if(n != null)
			{
				for(final Trigger t : this.triggers.get(TriggerType.NPC_DEATH))
				{
					if(Integer.parseInt(t.getOption("dungeon_id")) != ad.getInfo().getID())
						continue;

					if(!(n.getID() + "").equals(t.getOption("npc_id")))
						continue;

					DragonsLairMain.getDungeonManager().callTrigger(t, killer);
				}
			}
		}
		else
		{
			if(!this.triggers.containsKey(TriggerType.MOBS_KILLED))
				return;

			final Event spawnEvent = DragonsLairMain.getDungeonManager().getEventFromMob(e);
			final int amount = DragonsLairMain.getDungeonManager().addMobKill(ad, e, spawnEvent);
			for(final Trigger t : this.triggers.get(TriggerType.MOBS_KILLED))
			{
				if(Integer.parseInt(t.getOption("amount")) > amount)
					continue;

				if(Integer.parseInt(t.getOption("dungeon_id")) != ad.getInfo().getID())
					continue;

				if(t.getOption("spawned_by") != null && Integer.parseInt(t.getOption("spawned_by")) != spawnEvent.getID())
					continue;

				if(t.getOption("mob_id") != null)
				{
					try
					{
						final int id = Integer.parseInt(t.getOption("mob_id"));
						final EntityType searchedType = EntityType.fromId(id);
						if(searchedType != type)
							continue;
					}
					catch(final Exception ex)
					{
						final EntityType searchedType = EntityType.fromName(t.getOption("mob_id"));
						if(searchedType != type)
							continue;
					}
				}

				DragonsLairMain.getDungeonManager().callTrigger(t, killer);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLevelChange(final PlayerLevelChangeEvent event)
	{
		if(!this.triggers.containsKey(TriggerType.LEVEL_ACHIEVE))
			return;

		final Player p = event.getPlayer();
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		for(final Trigger t : this.triggers.get(TriggerType.LEVEL_ACHIEVE))
		{
			final int level = Integer.parseInt(t.getOption("amount"));
			if(level == event.getNewLevel())
			{
				final String dungeonid = t.getOption("dungeon_id");
				if(dungeonid == null)
					DragonsLairMain.getDungeonManager().callTrigger(t, p);
				else
				{
					if(ad == null)
						continue;

					try
					{
						final int id = Integer.parseInt(dungeonid);
						if(ad.getInfo().getID() == id)
							DragonsLairMain.getDungeonManager().callTrigger(t, p);
					}
					catch(final Exception e)
					{
						if(ad.getInfo().getName().equals(dungeonid))
							DragonsLairMain.getDungeonManager().callTrigger(t, p);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInteractLog(final PlayerInteractEvent event)
	{
		final Player p = event.getPlayer();
		final Action a = event.getAction();
		if(a == Action.LEFT_CLICK_AIR || a == Action.RIGHT_CLICK_AIR)
			return;

		if(a == Action.LEFT_CLICK_BLOCK && p.getGameMode() == GameMode.CREATIVE)
			return;

		final Block clicked = event.getClickedBlock();
		if(!DragonsLairMain.isWorldEnabled(clicked.getWorld().getName()))
			return;

		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad == null)
			return;

		DragonsLairMain.getInstance().getLoggingManager().logBlockDataChange(ad, clicked.getState());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerCraft(final CraftItemEvent event)
	{
		final Material outcome = event.getRecipe().getResult().getType();
		if(!this.triggers.containsKey(TriggerType.ITEM_CRAFT))
			return;

		final ActiveDungeon dungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getView().getPlayer().getName());
		for(final Trigger t : this.triggers.get(TriggerType.ITEM_CRAFT))
		{
			final String dungeonID = t.getOption("dungeon_id");
			if(dungeon != null && dungeonID != null && (!dungeonID.equals("" + dungeon.getInfo().getID()) || !dungeonID.equals("" + dungeon.getInfo().getName())))
				continue;

			Material m;
			try
			{
				m = Material.getMaterial(Integer.parseInt(t.getOption("item_id")));
			}
			catch(final Exception e)
			{
				m = Material.getMaterial(t.getOption("item_id").replace(" ", "_").toUpperCase());
			}

			if(m == outcome)
				DragonsLairMain.getDungeonManager().callTrigger(t, (Player)event.getWhoClicked());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onRespawn(final PlayerRespawnEvent event)
	{
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getPlayer().getName());
		if(ad != null)
			event.setRespawnLocation(ad.getInfo().getStartingPosition());
	}

	@EventHandler
	public void onPlayerDeath(final EntityDeathEvent event)
	{
		if(!(event.getEntity() instanceof Player))
			return;

		final Player p = (Player)event.getEntity();
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad == null)
			return;

		if(DragonsLairMain.getDungeonManager().getMapOfPlayer(p) != null)
			DragonsLairMain.getDungeonManager().removeMapHolder(p);

		event.getDrops().clear();
		event.setDroppedExp(0);
		ad.playerDies(p.getName());
		this.deadPlayers.add(p.getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDungeonEnd(final DungeonEndEvent event)
	{
		for(final String member : event.getDungeon().getCurrentParty().getMembers())
		{
			this.deadPlayers.remove(member);
		}

		final Set<String> playerSet = new HashSet<String>(Arrays.asList(event.getDungeon().getCurrentParty().getMembers()));
		for(final String member : event.getDungeon().getCurrentParty().getMembers())
		{
			final Player player = Bukkit.getPlayer(member);
			for(final Player pl : Bukkit.getOnlinePlayers())
			{
				if(pl.getName().equals(member) || playerSet.contains(pl.getName()) || (DragonsLairMain.getDungeonManager().getDungeonOfPlayer(pl.getName()) != null && !DragonsLairMain.canPlayersInteract()))
					continue;

				player.showPlayer(pl);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDeadPlayerMove(final PlayerMoveEvent event)
	{
		final Player p = event.getPlayer();
		if(!this.deadPlayers.contains(p.getName()))
			return;

		final Location from = event.getFrom();
		final Location to = event.getTo();
		if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY())
			return;

		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		final Location deathLoc = ad.getDeathLocationForPlayer(p.getName()).getDeathLocation();
		final ConversationHandler h = DragonsLairMain.getInstance().getConversationHandler();
		if(to.distanceSquared(deathLoc) <= 100 && !h.isInRespawnConversation(p))
			h.startRespawnConversation(p);
	}

	@EventHandler
	public void onDeadPickup(final PlayerPickupItemEvent event)
	{
		final Player p = event.getPlayer();
		if(!this.deadPlayers.contains(p.getName()))
			return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDungeonPlayerDropItem(final PlayerDropItemEvent event)
	{
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(event.getPlayer().getName());
		final String dungeonName = (ad == null ? "_GENERAL_" : ad.getInfo().getName());
		DragonsLairMain.getItemTracker().addItem(event.getItemDrop(), dungeonName);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDungeonPlayerPickup(final PlayerPickupItemEvent event)
	{
		if(!DragonsLairMain.getItemTracker().canCollect(event.getItem(), event.getPlayer()))
			event.setCancelled(true);
		else
			DragonsLairMain.getItemTracker().removeItem(event.getItem());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDungeonItemDespawn(final ItemDespawnEvent event)
	{
		DragonsLairMain.getItemTracker().removeItem(event.getEntity());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		for(final Player p : Bukkit.getOnlinePlayers())
		{
			if(DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName()) != null && !DragonsLairMain.canPlayersInteract())
				event.getPlayer().hidePlayer(p);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDungeonPlayerDamageEvent(final EntityDamageByEntityEvent event)
	{
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
			return;

		final Player p = (Player)event.getEntity();
		final Player damager = (Player)event.getDamager();
		final ActiveDungeon pDungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		final ActiveDungeon damagerDungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(damager.getName());
		if(pDungeon == null)
		{
			if(damagerDungeon != null && DragonsLairMain.canPlayersInteract())
				event.setCancelled(true);
		}
		else
		{
			if(damagerDungeon == null && DragonsLairMain.canPlayersInteract())
				event.setCancelled(true);
			else if(!pDungeon.getInfo().getName().equals(damagerDungeon.getInfo().getName()) && DragonsLairMain.canPlayersInteract())
				event.setCancelled(true);
		}
	}
}