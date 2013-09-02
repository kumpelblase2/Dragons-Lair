package de.kumpelblase2.dragonslair.api.eventexecutors;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;

public class ItemRemoveEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final String scope = e.getOption("scope");
			final String onFail = e.getOption("on_failure");
			final String onSuccess = e.getOption("on_success");
			final double amount = Double.parseDouble(e.getOption("amount"));
			boolean success = true;
			final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
			if(e.getOption("item_id").equalsIgnoreCase("money"))
			{
				if(DragonsLairMain.getInstance().isEconomyEnabled())
				{
					final Economy ec = DragonsLairMain.getInstance().getEconomy();
					if(scope == null || scope.equalsIgnoreCase("single"))
					{
						if(ec.getBalance(p.getName()) >= amount)
							ec.withdrawPlayer(p.getName(), amount);
						else
							success = false;
					}
					else if(ad != null)
					{
						for(final String member : ad.getCurrentParty().getMembers())
						{
							if(ec.getBalance(member) < amount)
								success = false;
						}

						if(success)
						{
							for(final String member : ad.getCurrentParty().getMembers())
							{
								ec.withdrawPlayer(member, amount);
							}
						}
					}
				}
			}
			else
			{
				Material itemMat = Material.AIR;
				if(Material.getMaterial(e.getOption("item_id").replace(" ", "_").toUpperCase()) == null)
					itemMat = Material.getMaterial(Integer.parseInt(e.getOption("item_id")));
				else
					itemMat = Material.getMaterial(e.getOption("item_id").replace(" ", "_").toUpperCase());

				final ItemStack item = new ItemStack(itemMat, (int)amount);
				if(scope == null || scope.equalsIgnoreCase("single"))
				{
					if(InventoryUtilities.isInventoryEmpty(p, true) || !InventoryUtilities.removeItem(p, item))
						success = false;
				}
				else if(ad != null)
				{
					for(final String member : ad.getCurrentParty().getMembers())
					{
						final Player pl = Bukkit.getPlayer(member);
						if(InventoryUtilities.isInventoryEmpty(pl, true) || !InventoryUtilities.removeItem(pl, item))
							success = false;
					}
				}
			}

			if(scope == null || scope.equalsIgnoreCase("single"))
			{
				if(!success)
				{
					if(onFail == null)
						return true;

					final int failid = Integer.parseInt(onFail);
					DragonsLairMain.getDungeonManager().executeEvent(DragonsLairMain.getSettings().getEvents().get(failid), p);
				}

				if(onSuccess != null)
				{
					final int successid = Integer.parseInt(onSuccess);
					DragonsLairMain.getDungeonManager().executeEvent(DragonsLairMain.getSettings().getEvents().get(successid), p);
				}
			}
			else
			{
				final ActiveDungeon d = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(d != null)
				{
					for(final String player : d.getCurrentParty().getMembers())
					{
						final Player pl = Bukkit.getPlayer(player);
						if(!success)
						{
							if(onFail == null)
								continue;

							final int failid = Integer.parseInt(onFail);
							DragonsLairMain.getDungeonManager().executeEvent(DragonsLairMain.getSettings().getEvents().get(failid), pl);
						}

						if(onSuccess != null)
						{
							final int successid = Integer.parseInt(onSuccess);
							DragonsLairMain.getDungeonManager().executeEvent(DragonsLairMain.getSettings().getEvents().get(successid), pl);
						}
					}
				}
			}
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