package com.epicsagaonline.bukkit.EpicZones.objects;

public class EpicZonePermission 
{

	public enum PermNode { BUILD, DESTROY, ENTRY }
	public enum PermType { ALLOW, DENY }
	
	private String member; //User or Group name
	private PermNode node;
	private PermType permission;
	
	public void setMember(String value)
	{
		this.member = value;	
	}
	public String getMember()
	{
		return this.member;
	}
	
	public void setNode(PermNode value)
	{
		this.node = value;	
	}
	public PermNode getNode()
	{
		return this.node;
	}
	
	public void setPermission(PermType value)
	{
		this.permission = value;	
	}
	public PermType getPermission()
	{
		return this.permission;
	}

}
