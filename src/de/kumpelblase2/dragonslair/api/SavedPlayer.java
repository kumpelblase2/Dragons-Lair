package de.kumpelblase2.dragonslair.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SavedPlayer
{
	private final Player player;
	private final int food;
	private final int health;
	private final ItemStack[] items;
	private final ItemStack[] armor;
	private final Location loc;

	public SavedPlayer(final Player p)
	{
		this.player = p;
		this.food = p.getFoodLevel();
		this.health = p.getHealth();
		this.items = p.getInventory().getContents();
		this.armor = p.getInventory().getArmorContents();
		this.loc = p.getLocation();
	}

	public void restore()
	{
		this.player.setHealth(this.health);
		this.player.setFoodLevel(this.food);
		this.player.getInventory().setContents(this.items);
		this.player.getInventory().setArmorContents(this.armor);
		this.player.teleport(this.loc);
	}
}
