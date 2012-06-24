package de.kumpelblase2.dragonslair;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.topcat.npclib.entity.HumanNPC;
import com.topcat.npclib.nms.*;
import com.topcat.npclib.nms.NpcEntityTargetEvent.NpcTargetReason;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.events.conversation.*;
import de.kumpelblase2.dragonslair.events.dungeon.*;
import de.kumpelblase2.dragonslair.logging.TNTList;
import de.kumpelblase2.dragonslair.logging.TNTList.TNTEntry;
import de.kumpelblase2.dragonslair.map.DLMap;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;

public class DLEventHandler implements Listener
{
	private Map<TriggerType, Set<Trigger>> triggers = new HashMap<TriggerType, Set<Trigger>>();
	private Map<Location, Trigger> locations = new HashMap<Location, Trigger>();
	private TNTList tntList = new TNTList();
	
	public void reloadTriggers()
	{
		this.triggers.clear();
		this.locations.clear();
		
		for(Trigger t : DragonsLairMain.getSettings().getTriggers().values())
		{
			if(t.getType() != TriggerType.MOVEMENT)
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
				String world = t.getOption("world");
				if(x == null || y == null || z == null)
					continue;
				
				if(x2 == null)
					x2 = x;
				if(y2 == null)
					y2 = y;
				if(z2 == null)
					z2 = z;
				
				int minx, maxx, miny, maxy, minz, maxz;
				minx = Integer.parseInt(x);
				maxx = Integer.parseInt(x2);
				miny = Integer.parseInt(y);
				maxy = Integer.parseInt(y2);
				minz = Integer.parseInt(z);
				maxz = Integer.parseInt(z2);
				
				for(int posx = minx; posx <= maxx; posx++ )
				{
					for(int posy = miny; posy <= maxy; posy++)
					{
						for(int posz = minz; posz <= maxz; posz++)
						{
							locations.put(new Location(Bukkit.getWorld(world), posx, posy, posz), t);
						}
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void onInteractNPC(EntityTargetEvent event)
	{
		if(!DragonsLairMain.isWorldEnabled(event.getEntity().getWorld().getName()))
			return;
		
		if(event instanceof NpcEntityTargetEvent)
		{
			if(((NpcEntityTargetEvent)event).getNpcReason() == NpcTargetReason.NPC_RIGHTCLICKED)
			{
				if(!this.triggers.containsKey(TriggerType.NPC_INTERACT))
					return;
				
				HumanNPC entitynpc = DragonsLairMain.getInstance().getDungeonManager().getNPCByEntity(event.getEntity());
				NPC npc = DragonsLairMain.getSettings().getNPCByName(entitynpc.getName());
				
				for(Trigger t : this.triggers.get(TriggerType.NPC_INTERACT))
				{
					String npcid = t.getOption("npc_id");
					if(npcid == null)
						continue;
					
					if(npcid.equals(npc.getID() + ""))
					{
						DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, (Player)event.getTarget());
					}
				}
			}
			else if(((NpcEntityTargetEvent)event).getNpcReason() == NpcTargetReason.NPC_BOUNCED)
			{
				if(!this.triggers.containsKey(TriggerType.NPC_TOUCH))
					return;
				
				HumanNPC entitynpc = DragonsLairMain.getInstance().getDungeonManager().getNPCByEntity(event.getEntity());
				NPC npc = DragonsLairMain.getSettings().getNPCByName(entitynpc.getName());
				
				for(Trigger t : this.triggers.get(TriggerType.NPC_TOUCH))
				{
					String npcid = t.getOption("npc_id");
					if(npcid == null)
						continue;
					
					if(npcid.equals(npc.getID() + ""))
					{
						DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, (Player)event.getTarget());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onNPCDamage(NpcDamageEvent event)
	{
		if(!(event.getDamager() instanceof Player))
			return;

		if(!this.triggers.containsKey(TriggerType.NPC_DAMAGE))
			return;
		
		if(!DragonsLairMain.isWorldEnabled(event.getDamager().getWorld().getName()))
			return;
		
		HumanNPC entitynpc = DragonsLairMain.getInstance().getDungeonManager().getNPCByEntity(event.getDamager());
		NPC npc = DragonsLairMain.getSettings().getNPCByName(entitynpc.getName());
		
		for(Trigger t : this.triggers.get(TriggerType.NPC_DAMAGE))
		{
			String npcid = t.getOption("npc_id");
			if(npcid == null)
				continue;
			
			if(npcid.equals(npc.getID() + ""))
			{
				DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, (Player)event.getDamager());
			}
		}
		
		if(npc.isInvincible())
			event.setDamage(0);
	}
	
	/*@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player p = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		if(to.getBlockX() - from.getBlockX() == 0 && to.getBlockY() - from.getBlockY() == 0 && to.getBlockZ() - from.getBlockZ() == 0)
			return;
		
		if(DragonsLairMain.getInstance().getConversationHandler().isInConversation(p))
		{
			NPCConversation conv = DragonsLairMain.getInstance().getConversationHandler().getConversations().get(p.getName());
			if(to.distanceSquared(conv.getNPCEntity().getBukkitEntity().getLocation()) > 100)
			{
				conv.getConversation().getForWhom().sendRawMessage("<" + conv.getNPC().getName() + ">" + "GET BACK HERE!");
				conv.adandon();
				DragonsLairMain.getInstance().getConversationHandler().getConversations().remove(p.getName());
			}
		}
	}*/
	
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{		
		Location from = event.getFrom();
		Location to = event.getTo();
		Player p = event.getPlayer();
		
		if(to.getBlockX() - from.getBlockX() == 0 && to.getBlockY() - from.getBlockY() == 0 && to.getBlockZ() - from.getBlockZ() == 0)
			return;
		
		if(!DragonsLairMain.isWorldEnabled(to.getWorld().getName()))
			return;
		
		if(this.locations.size() > 0)
		{
			Location newLoc = new Location(p.getWorld(), to.getBlockX(), to.getY(), to.getBlockZ());
			if(this.locations.containsKey(newLoc))
				DragonsLairMain.getInstance().getDungeonManager().callTrigger(this.locations.get(newLoc), p);
		}
	}
	
	@EventHandler
	public void onNextDialog(ConversationNextDialogEvent event)
	{
		if(!DragonsLairMain.isWorldEnabled(((Player)event.getConversation().getForWhom()).getWorld().getName()))
			return;
		
		if(this.triggers.containsKey(TriggerType.DIALOG_OCCUR))
		{
			for(Trigger t : this.triggers.get(TriggerType.DIALOG_OCCUR))
			{
				if(!t.getOption("npc_id").equals(event.getNPC().getID() + ""))
					continue;
				
				if(t.getOption("dialog_id") != null)
				{
					String id = t.getOption("dialog_id");
					if(id.equals(event.getNextDialogID() + ""))
					{
						DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, (Player)event.getConversation().getForWhom());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event)
	{
		Player p = event.getPlayer();
		if(!DragonsLairMain.isWorldEnabled(p.getWorld().getName()))
			return;
		
		ActiveDungeon d = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
		if(d == null)
			return;
		
		if(d.getInfo().getSafeWord().equalsIgnoreCase(event.getMessage()))
			DragonsLairMain.getInstance().getConversationHandler().startSafeWordConversation(p);	
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player p = event.getPlayer();
		Location placed = event.getBlock().getLocation();
		if(!DragonsLairMain.isWorldEnabled(placed.getWorld().getName()))
			return;
		
		ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad != null)
		{
			DragonsLairMain.getInstance().getLoggingManager().logBlockPlace(ad, event.getBlock().getState());
		}
		
		if(event.getBlock().getType() == Material.TNT)
			this.tntList.addEntry(ad.getInfo().getName(), placed);
		
		if(!this.triggers.containsKey(TriggerType.BLOCK_PLACE))
			return;
		
		for(Trigger t : this.triggers.get(TriggerType.BLOCK_PLACE))
		{
			String x, y, z, block_id;
			x = t.getOption("x");
			y = t.getOption("y");
			z = t.getOption("z");
			block_id = t.getOption("block_id");
			String world = t.getOption("world");
			if(x == null || y == null || z == null)
				continue;
			
			if(!world.equals(placed.getWorld().getName()))
				continue;
			
			if(block_id != null)
			{
				try
				{
					int id = Integer.parseInt(block_id);
					Material m = Material.getMaterial(id);
					if(m != null)
					{
						if(placed.getBlock().getType() != m)
							continue;
					}
				}
				catch(Exception e)
				{
					Material m = Material.getMaterial(block_id.replace(" ", "_").toUpperCase());
					if(m != null)
					{
						if(placed.getBlock().getType() != m)
							continue;
					}
				}
			}
			
			if(x.equals(placed.getBlockX() + "") && y.equals(placed.getBlockY() + "") && z.equals(placed.getBlockZ() + ""))
			{
				DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, p);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player p = event.getPlayer();
		Location placed = event.getBlock().getLocation();
		if(!DragonsLairMain.isWorldEnabled(placed.getWorld().getName()))
			return;
		
		ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad != null)
		{
			DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, event.getBlock().getState());
		}
		
		if(!this.triggers.containsKey(TriggerType.BLOCK_BREAK))
			return;
		
		for(Trigger t : this.triggers.get(TriggerType.BLOCK_BREAK))
		{
			String block_id;
			block_id = t.getOption("block_id");
			String world = t.getOption("world");
			if(t.getOption("x") == null || t.getOption("y") == null || t.getOption("z") == null)
				continue;
			
			int x, y, z;
			x = Integer.parseInt(t.getOption("x"));
			y = Integer.parseInt(t.getOption("y"));
			z = Integer.parseInt(t.getOption("z"));
			
			if(block_id != null)
			{
				try
				{
					int id = Integer.parseInt(block_id);
					Material m = Material.getMaterial(id);
					if(m != null)
					{
						if(placed.getBlock().getType() != m)
							continue;
					}
				}
				catch(Exception e)
				{
					Material m = Material.getMaterial(block_id.replace(" ", "_").toUpperCase());
					if(m != null)
					{
						if(placed.getBlock().getType() != m)
							continue;
					}
				}
			}
			
			if(!world.equals(placed.getWorld().getName()))
				continue;
			
			if(x == (placed.getBlockX() < 0 ? placed.getBlockX() + 1 : placed.getBlockX()) && y == placed.getBlockY() && z == placed.getBlockZ())
				DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, p);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onExplode(EntityExplodeEvent event)
	{
		TNTEntry e = this.tntList.getEntry(event.getLocation());
		if(e != null)
		{
			ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getActiveDungeonByName(e.getDungeon());
			if(ad == null)
				return;
			
			List<Block> blocks = event.blockList();
			for(Block b : blocks)
			{
				DragonsLairMain.getInstance().getLoggingManager().logBlockBreak(ad, b.getState());				
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		Action a = event.getAction();
		if(a != Action.LEFT_CLICK_BLOCK && a != Action.RIGHT_CLICK_BLOCK && a != Action.PHYSICAL)
			return;
		
		Block interactedBlock = event.getClickedBlock();
		Location interacted = interactedBlock.getLocation();
		if(!DragonsLairMain.isWorldEnabled(interacted.getWorld().getName()))
			return;
		
		if(!this.triggers.containsKey(TriggerType.BLOCK_INTERACT))
			return;
		
		for(Trigger t : this.triggers.get(TriggerType.BLOCK_INTERACT))
		{
			String x, y, z, block_id;
			x = t.getOption("x");
			y = t.getOption("y");
			z = t.getOption("z");
			block_id = t.getOption("block_id");
			String world = t.getOption("world");
			if(x == null || y == null || z == null)
				continue;
			
			if(block_id != null)
			{
				try
				{
					int id = Integer.parseInt(block_id);
					Material m = Material.getMaterial(id);
					if(m != null)
					{
						if(interactedBlock.getType() != m)
							continue;
					}
				}
				catch(Exception e)
				{
					Material m = Material.getMaterial(block_id.replace(" ", "_").toUpperCase());
					if(m != null)
					{
						if(interactedBlock.getType() != m)
							continue;
					}
				}
			}
			
			if(!world.equals(interacted.getWorld().getName()))
				continue;
			
			if(x.equals(interacted.getBlockX() + "") && y.equals(interacted.getBlockY() + "") && z.equals(interacted.getBlockZ() + ""))
			{
				DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, p);
			}
		}
	}
	
	@EventHandler
	public void onChapterChange(ChapterChangeEvent event)
	{		
		if(!this.triggers.containsKey(TriggerType.CHAPTER_CHANGE))
			return;
		
		if(!DragonsLairMain.isWorldEnabled(Bukkit.getPlayer(event.getActiveDungeon().getCurrentParty().getMembers()[0]).getWorld().getName()))
			return;
		
		int dungeon = event.getDungeon().getID();
		int chapterid = event.getNextChapter().getID();
		for(Trigger t : this.triggers.get(TriggerType.CHAPTER_CHANGE))
		{
			String d = t.getOption("dungeon_id");
			String chapter = t.getOption("chapter_id");
			if(d != null)
			{
				if(!d.equals(dungeon + ""))
					continue;
			}
			
			if(!chapter.equals(chapterid + ""))
				continue;
			
			DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, null);
		}
	}
	
	@EventHandler
	public void onObjectiveChange(ObjectiveChangeEvent event)
	{
		if(!this.triggers.containsKey(TriggerType.OBJECTIVE_CHANGE))
			return;
		
		if(!DragonsLairMain.isWorldEnabled(Bukkit.getPlayer(event.getActiveDungeon().getCurrentParty().getMembers()[0]).getWorld().getName()))
			return;
	
		int dungeon = event.getDungeon().getID();
		int objectiveid = event.getNextObjective().getID();
		for(Trigger t : this.triggers.get(TriggerType.OBJECTIVE_CHANGE))
		{
			String d = t.getOption("dungeon_id");
			String objective = t.getOption("objective_id");
			if(d != null)
			{
				if(!d.equals(dungeon + ""))
					continue;
			}
			
			if(!objective.equals(objectiveid + ""))
				continue;
			
			DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, null);
		}
	}
	
	@EventHandler
	public void onMapScroll(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		ItemStack current = p.getItemInHand();
		if(current == null || current.getType() != Material.MAP)
			return;

		if(DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName()) == null)
			return;
		
		DLMap map = DragonsLairMain.getInstance().getDungeonManager().getMapOfPlayer(p);
		if(map == null)
			return;
		
		Action a = event.getAction();
		if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK)
			map.scrollUp();
		else if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
			map.scrollDown();
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event)
	{
		Player p = event.getPlayer();
		ItemStack dropped = event.getItemDrop().getItemStack();
		if(DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName()) == null)
			return;
		
		if(dropped.getType() != Material.MAP)
			return;
		
		if(dropped.getEnchantments().containsKey(Enchantment.ARROW_INFINITE))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		Player p = (Player)event.getWhoClicked();
		if(!DragonsLairMain.isWorldEnabled(p.getWorld().getName()))
			return;
		
		ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
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
				Map<String, String> newItem = new HashMap<String, String>();
				Map<String, String> oldItem = new HashMap<String, String>();
				if(event.getCursor().getType() == Material.AIR)	
					newItem.put("slot" + event.getSlot(), InventoryUtilities.itemToString(null));
				else
					oldItem.put("slot" + event.getSlot(), InventoryUtilities.itemToString(event.getCursor()));
				
				DragonsLairMain.getInstance().getLoggingManager().logBlockChange(ad, holder, newItem, oldItem);
			}
		}
		else
		{
			if(event.isShiftClick())
				DragonsLairMain.getInstance().getLoggingManager().logBlockChange(ad, holder, InventoryUtilities.getChangedStates(event.getView().getTopInventory(), (event.getCursor().getType() == Material.AIR ? event.getCurrentItem() : event.getCursor())), new HashMap<String, String>());
		}
	}
	
	@EventHandler
	public void onPlayerExit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
		if(ad == null)
			return;
		
		DragonsLairMain.getInstance().getDungeonManager().stopDungeon(ad.getInfo().getName());
	}
	
	/*@EventHandler
	public void onBlockDamage(BlockDamageEvent event)
	{
		Player p = event.getPlayer();
		Block b = event.getBlock();
		
		if(!this.triggers.containsKey(TriggerType.BLOCK_DAMAGE))
			return;
		
		for(Trigger t : this.triggers.get(TriggerType.BLOCK_DAMAGE))
		{
			String x, y, z, block_id;
			x = t.getOption("x");
			y = t.getOption("y");
			z = t.getOption("z");
			block_id = t.getOption("block_id");
			String world = t.getOption("world");
			if(x == null || y == null || z == null)
				continue;
			
			if(!world.equals(b.getWorld().getName()))
				continue;
			
			if(block_id != null)
			{
				try
				{
					int id = Integer.parseInt(block_id);
					Material m = Material.getMaterial(id);
					if(m != null)
					{
						if(b.getType() != m)
							continue;
					}
				}
				catch(Exception e)
				{
					Material m = Material.getMaterial(block_id);
					if(m != null)
					{
						if(b.getType() != m)
							continue;
					}
				}
			}
			
			Location loc = b.getLocation();
			if(x.equals(loc.getBlockX() + "") && y.equals(loc.getBlockY() + "") && z.equals(loc.getBlockZ() + ""))
			{
				DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, p);
			}
		}
	}*/
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event)
	{
		Player p = event.getPlayer();
		if(!DragonsLairMain.isWorldEnabled(p.getWorld().getName()))
			return;
		
		if(!this.triggers.containsKey(TriggerType.GATHER_ITEM))
			return;
		
		Inventory i = p.getInventory();
		ItemStack pickedUp = event.getItem().getItemStack();
		
		for(Trigger t : this.triggers.get(TriggerType.GATHER_ITEM))
		{
			Material m;
			int amount;
			int dungeonid;
			try
			{
				m = Material.getMaterial(Integer.parseInt(t.getOption("item_id")));
			}
			catch(Exception e)
			{
				m = Material.getMaterial(t.getOption("item_id").replace(" ", "_").toUpperCase());
			}
			
			if(m == null)
				continue;
			
			try
			{
				amount = Integer.parseInt(t.getOption("amount"));
			}
			catch(Exception e)
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
				catch(Exception e)
				{
					continue;
				}
			}
			
			if(dungeonid != -1)
			{
				ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad == null)
					continue;
				
				if(ad.getInfo().getID() != dungeonid)
					continue;
			}
			
			if(pickedUp.getType() == m)
			{
				if(pickedUp.getAmount() >= amount)
					DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, p);
				else
				{
					amount -= pickedUp.getAmount();
					
					if(i.contains(m))
					{
						HashMap<Integer, ? extends ItemStack> items = i.all(m);
						for(ItemStack item : items.values())
						{
							if(item.getAmount() >= amount)
								DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, p);
							else
								amount -= item.getAmount();
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(!this.triggers.containsKey(TriggerType.MOBS_KILLED) && !this.triggers.containsKey(TriggerType.NPC_DEATH))
			return;
		
		Player killer = event.getEntity().getKiller();
		if(killer == null)
			return;
		
		if(!DragonsLairMain.isWorldEnabled(killer.getWorld().getName()))
			return;
		
		ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(killer.getName());
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
			
			NPC n = DragonsLairMain.getSettings().getNPCByName(DragonsLairMain.getInstance().getDungeonManager().getNPCByEntity(e).getName());
			if(n != null)
			{
				for(Trigger t : this.triggers.get(TriggerType.NPC_DEATH))
				{
					if(Integer.parseInt(t.getOption("dungeon_id")) != ad.getInfo().getID())
						continue;
					
					
					if(!(n.getID() + "").equals(t.getOption("npc_id")))
						continue;
					
					DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, killer);
				}
			}
		}
		else
		{
			if(!this.triggers.containsKey(TriggerType.MOBS_KILLED))
				return;
			
			Event spawnEvent = DragonsLairMain.getInstance().getDungeonManager().getEventFromMob(e);
			int amount = DragonsLairMain.getInstance().getDungeonManager().addMobKill(ad, e, spawnEvent);
			for(Trigger t : this.triggers.get(TriggerType.MOBS_KILLED))
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
						int id = Integer.parseInt(t.getOption("mob_id"));
						EntityType searchedType = EntityType.fromId(id);
						if(searchedType != type)
							continue;
					}
					catch(Exception ex)
					{
						EntityType searchedType = EntityType.fromName(t.getOption("mob_id"));
						if(searchedType != type)
							continue;
					}
				}
				
				DragonsLairMain.getInstance().getDungeonManager().callTrigger(t, killer);
			}
		}
	}
}
