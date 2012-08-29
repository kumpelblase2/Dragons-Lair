package de.kumpelblase2.dragonslair.api;

public class Cooldown
{
	private final String dungeon;
	private final long endTime;
	
	public Cooldown(ActiveDungeon ad, int cd)
	{
		this(ad.getInfo().getName(), cd);
	}
	
	public Cooldown(String name, int cd)
	{
		this.dungeon = name;
		this.endTime = System.currentTimeMillis() + cd * 1000;
	}
	
	public String getDungeonName()
	{
		return this.dungeon;
	}
	
	public int getRemainingTime()
	{
		return (int)((this.endTime - System.currentTimeMillis()) / 1000);
	}
	
	public boolean isOnCooldown()
	{
		return this.endTime > System.currentTimeMillis();
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof String)
			return this.dungeon.equals(object);
		else if(object instanceof Cooldown)
			return this.dungeon.equals(((Cooldown)object).getDungeonName());
		else
			return false;
	}
}
