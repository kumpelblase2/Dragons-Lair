package de.kumpelblase2.dragonslair.api;

public class Option
{
	private String type;
	private String value;

	public Option(final String inType, final String inValue)
	{
		this.type = inType;
		this.value = inValue;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(final String inType)
	{
		this.type = inType;
	}

	public String getValue()
	{
		return this.value;
	}

	public void setValue(final String o)
	{
		this.value = o;
	}

	public String getAsString()
	{
		return this.type + ":" + this.value;
	}
}