/*

This file is part of EpicZones

Copyright (C) 2011 by Team ESO

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

/**
 * @author jblaske@gmail.com
 * @license MIT License
 */

package com.epicsagaonline.bukkit.EpicZones.objects;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.integration.PermissionsManager;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePermission.PermNode;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePermission.PermType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;

public class EpicZone
{

    public enum ZoneType
    {
        POLY, CIRCLE, GLOBAL
    }

    private String tag = "";
    private String name = "";
    private ZoneType type = ZoneType.POLY;
    private int floor = 0;
    private int ceiling = 128;
    private String world = "";
    private Polygon polygon = new Polygon();
    private Point center = new Point();
    private Rectangle boundingBox = new Rectangle();
    private String enterText = "";
    private String exitText = "";
    private EpicZone parent = null;
    private Map<String, EpicZone> children = new HashMap<String, EpicZone>();
    private boolean hasParentFlag = false;
    private boolean pvp = false;
    private boolean hasRegen = false;
    private Date lastRegen = new Date();
    private EpicZoneRegen regen = new EpicZoneRegen();
    private int radius = 0;
    private HashSet<String> mobs = new HashSet<String>();
    private EpicZoneFire fire = new EpicZoneFire();
    private EpicZoneExplode explode = new EpicZoneExplode();
    private ArrayList<String> owners = new ArrayList<String>();
    private boolean sanctuary = false;
    private boolean fireBurnsMobs = true;
    private Map<String, EpicZonePermission> permissions = new HashMap<String, EpicZonePermission>();
    private ArrayList<PillarBlock> pillarBlocks = new ArrayList<PillarBlock>();
    private Set<String> childrenTags = new HashSet<String>();

    public EpicZone()
    {
    }

    public EpicZone(EpicZone prime)
    {
        this.tag = prime.tag;
        this.name = prime.name;
        this.floor = prime.floor;
        this.ceiling = prime.ceiling;
        this.world = prime.world;
        this.polygon = prime.polygon;
        this.center = prime.center;
        this.boundingBox = prime.boundingBox;
        this.enterText = prime.enterText;
        this.exitText = prime.exitText;
        this.parent = prime.parent;
        this.children = prime.children;
        this.hasParentFlag = prime.hasParentFlag;
        this.pvp = prime.pvp;
        this.hasRegen = prime.hasRegen;
        this.lastRegen = prime.lastRegen;
        this.regen = prime.regen;
        this.radius = prime.radius;
        this.mobs = prime.mobs;
        this.fire = prime.fire;
        this.explode = prime.explode;
        this.owners = prime.owners;
        this.sanctuary = prime.sanctuary;
        this.permissions = prime.permissions;
    }

    public EpicZone(String zoneData)
    {

        String[] split = zoneData.split("\\|");

        if (split.length == 10)
        {
            this.tag = split[0].replaceAll("[^a-zA-Z0-9]", "");
            this.world = split[1];
            this.name = split[2];
            this.enterText = split[4];
            this.exitText = split[5];
            this.floor = Integer.valueOf(split[6]);
            this.ceiling = Integer.valueOf(split[7]);
            this.parent = null;
            this.children = new HashMap<String, EpicZone>();
            this.regen = new EpicZoneRegen();

            buildFlags(split[3]);
            buildChildren(split[8]);
            buildPolygon(split[9]);

            rebuildBoundingBox();

            //Log.Write("Created Zone [" + this.name + "]");
        }

    }

    public String getTag()
    {
        return tag;
    }

    public String getName()
    {
        return name;
    }

    public int getFloor()
    {
        return floor;
    }

    public int getCeiling()
    {
        return ceiling;
    }

    public Polygon getPolygon()
    {
        return polygon;
    }

    public String getEnterText()
    {
        return enterText;
    }

    public String getExitText()
    {
        return exitText;
    }

    public String getWorld()
    {
        return world;
    }

    public EpicZone getParent()
    {
        return parent;
    }

    public Map<String, EpicZone> getChildren()
    {
        return children;
    }

    public boolean hasChildren()
    {
        return (children.size() > 0 || childrenTags.size() > 0);
    }

    public boolean hasParent()
    {
        return hasParentFlag;
    }

    public boolean hasRegen()
    {
        return hasRegen;
    }

    public EpicZoneRegen getRegen()
    {
        return regen;
    }

    public int getRadius()
    {
        return radius;
    }

    public Point getCenter()
    {
        return center;
    }

    public HashSet<String> getMobs()
    {
        return mobs;
    }

    public EpicZoneFire getFire()
    {
        return fire;
    }

    public EpicZoneExplode getExplode()
    {
        return explode;
    }

    public boolean getSanctuary()
    {
        return sanctuary;
    }

    public ArrayList<String> getOwners()
    {
        return owners;
    }

    public boolean getFireBurnsMobs()
    {
        return fireBurnsMobs;
    }

    public ZoneType getType()
    {
        return type;
    }

    public Map<String, EpicZonePermission> getPermissions()
    {
        return permissions;
    }

    public Set<String> getChildrenTags()
    {
        Set<String> result = new HashSet<String>();

        for (String zoneTag : this.children.keySet())
        {
            result.add(zoneTag);
        }
        for (String zoneTag : this.childrenTags)
        {
            if (!result.contains(zoneTag))
            {
                result.add(zoneTag);
            }
        }

        return result;
    }

    public String getPoints()
    {
        String result = "";
        Polygon poly = this.getPolygon();

        if (poly != null)
        {
            for (int i = 0; i < poly.npoints; i++)
            {
                result = result + poly.xpoints[i] + ":" + poly.ypoints[i] + " ";
            }
        }
        else
        {

            {
                result = this.center.x + ":" + this.center.y;
            }

        }

        return result;
    }

    public ArrayList<Point> getPointsArray()
    {
        ArrayList<Point> result = new ArrayList<Point>();
        Polygon poly = this.getPolygon();

        if (poly != null)
        {
            for (int i = 0; i < poly.npoints; i++)
            {
                result.add(new Point(poly.xpoints[i], poly.ypoints[i]));
            }
        }
        else
        {
            result = null; //Don't show borders for circle zones yet.
        }

        return result;
    }

    public void addChild(EpicZone childZone)
    {
        if (this.children == null)
        {
            this.children = new HashMap<String, EpicZone>();
        }
        if (childZone != null)
        {
            this.children.put(childZone.getTag(), childZone);
        }
    }

    public void setPolygon(String value)
    {
        if (this.type != ZoneType.GLOBAL)
        {
            buildPolygon(value);
        }
        else
        {
            this.polygon = null;
        }
    }

    public void setPermissions(Map<String, EpicZonePermission> value)
    {
        this.permissions = value;
    }

    public void addChildTag(String tag)
    {
        this.childrenTags.add(tag);
        if (General.myZones.get(tag) != null)
        {
            this.addChild(General.myZones.get(tag));
        }
    }

    public void addPermission(String member, String node, String permission)
    {
        if (this.permissions == null)
        {
            this.permissions = new HashMap<String, EpicZonePermission>();
        }
        if (member != null && node != null && permission != null)
        {
            if (this.permissions.get(member.toLowerCase() + node.toUpperCase()) == null)
            {
                EpicZonePermission newPerm = new EpicZonePermission();
                newPerm.setMember(member);
                newPerm.setNode(PermNode.valueOf(node.toUpperCase()));
                newPerm.setPermission(PermType.valueOf(permission.toUpperCase()));
                this.permissions.put(member.toLowerCase() + node.toUpperCase(), newPerm);
            }
            else
            {
                this.permissions.get(member.toLowerCase() + node.toUpperCase()).setPermission(PermType.valueOf(permission.toUpperCase()));
            }
        }
    }

    public void removePermission(String member, String node, String permission)
    {
        if (this.permissions == null)
        {
            this.permissions = new HashMap<String, EpicZonePermission>();
        }
        if (member != null && node != null && permission != null)
        {
            this.permissions.remove(member.toLowerCase() + node.toUpperCase());
        }
    }

    public void setType(String value)
    {
        this.type = ZoneType.valueOf(value.toUpperCase());
    }

    public void setFire(String value)
    {
        this.fire = new EpicZoneFire(value);
    }

    public void setFireBurnsMobs(Boolean value)
    {
        this.fireBurnsMobs = value;
    }

    public void setSanctuary(Boolean value)
    {
        this.sanctuary = value;
    }

    public void setExplode(String value)
    {
        this.explode = new EpicZoneExplode(value);
    }

    public void removeChild(String tag)
    {
        if (this.childrenTags != null)
        {
            this.childrenTags.remove(tag);
        }
        if (this.children != null)
        {
            this.children.remove(tag);
        }
    }

    public void setWorld(String value)
    {
        this.world = value;
    }

    public void setTag(String value)
    {
        this.tag = value;
    }

    public void setName(String value)
    {
        this.name = value;
    }

    public void setFloor(int value)
    {
        this.floor = value;
    }

    public void setCeiling(int value)
    {
        this.ceiling = value;
    }

    public void setEnterText(String value)
    {
        this.enterText = value;
    }

    public void setExitText(String value)
    {
        this.exitText = value;
    }

    public void setRadius(int value)
    {
        this.radius = value;
        if (this.polygon.npoints == 1)
        {
            this.center.x = this.polygon.xpoints[0];
            this.center.y = this.polygon.ypoints[0];
        }
    }

    public boolean pointWithin(Point point)
    {
        boolean result = false;
        if (this.boundingBox != null)
        {
            if (this.boundingBox.contains(point))
            {
                if (this.polygon != null)
                {
                    if (this.polygon.contains(point))
                    {
                        result = true;
                    }
                }
            }
        }
        else if (this.center != null)
        {
            if (this.pointWithinCircle(point))
            {
                result = true;
            }
        }
        return result;
    }

    public void setParent(EpicZone parent)
    {
        this.parent = parent;
        this.parent.addChild(this);
        this.hasParentFlag = true;
    }

    private void buildFlags(String data)
    {

        mobs.add("ALL");

        if (data.length() > 0)
        {
            String[] dataList = data.split("\\s");

            for (String aDataList : dataList)
            {
                String[] split = aDataList.split(":");
                String flag = split[0].toLowerCase();

                if (flag.equals("pvp"))
                {
                    this.pvp = split[1].equalsIgnoreCase("true");
                }
                else if (flag.equals("regen"))
                {
                    this.regen.setAmount(Integer.parseInt(split[1].trim()));
                    if (split.length > 2)
                    {
                        this.regen.setInterval(Integer.parseInt(split[2].trim()));
                        if (split.length > 3)
                        {
                            this.regen.setDelay(Integer.parseInt(split[3].trim()));
                        }
                    }
                }
                else if (flag.equals("mobs"))
                {
                    BuildMobsFlag(split);
                }
                else if (flag.equals("fire"))
                {
                    boolean value = split[1].equalsIgnoreCase("true");
                    this.fire.setIgnite(value);
                    this.fire.setSpread(value);
                }
                else if (flag.equals("explode"))
                {
                    boolean value = split[1].equalsIgnoreCase("true");
                    this.explode.setTNT(value);
                    this.explode.setCreeper(value);
                    this.explode.setGhast(value);
                }
                else if (flag.equals("owners"))
                {
                    BuildOwnersFlag(split);
                }
                else if (flag.equals("sanctuary"))
                {
                    this.sanctuary = split[1].equalsIgnoreCase("true");
                }
            }
        }
    }

    public void addMob(String mobType)
    {
        boolean validType;

        if (this.mobs == null)
        {
            this.mobs = new HashSet<String>();
        }

        mobType = mobType.trim().toUpperCase();

        try
        {
            if (mobType.equals("NONE") ||
                    mobType.equals("MONSTERS") ||
                    mobType.equals("MONSTER") ||
                    mobType.equals("ANIMALS") ||
                    mobType.equals("ANIMAL") ||
                    mobType.equals("ALL"))
            {
                validType = true;
            }
            else
            {
                CreatureType.valueOf(mobType);
                validType = true;
            }
        } catch (Exception e)
        {
            validType = false;
        }

        if (validType)
        {
            if (mobType.equals("NONE"))
            {
                mobs.add("NONE");
                return;
            }
            if (mobType.equals("ANIMALS") || mobType.equals("ANIMAL"))
            {
                mobs.add(CreatureType.SQUID.toString());
                mobs.add(CreatureType.CHICKEN.toString());
                mobs.add(CreatureType.COW.toString());
                mobs.add(CreatureType.SHEEP.toString());
                mobs.add(CreatureType.PIG.toString());
                mobs.add(CreatureType.WOLF.toString());
            }
            else if (mobType.equals("MONSTERS") || mobType.equals("MONSTER"))
            {
                mobs.add(CreatureType.CREEPER.toString());
                mobs.add(CreatureType.ZOMBIE.toString());
                mobs.add(CreatureType.GHAST.toString());
                mobs.add(CreatureType.GIANT.toString());
                mobs.add(CreatureType.SKELETON.toString());
                mobs.add(CreatureType.SLIME.toString());
                mobs.add(CreatureType.SPIDER.toString());
                mobs.add(CreatureType.ENDERMAN.toString());
                mobs.add(CreatureType.CAVE_SPIDER.toString());
                mobs.add(CreatureType.SILVERFISH.toString());
            }
            else if (mobType.equals("ALL"))
            {
                mobs.add("ALL");
            }
            else
            {
                mobs.add(CreatureType.valueOf(mobType.toUpperCase()).toString());
            }
        }
    }

    public void setMobs(String data)
    {
        if (data.length() > 0)
        {
            String[] split = null;

            if (data.contains(":"))
            {
                split = (data + ":").split(":");
            }
            else if (data.contains(","))
            {
                split = (data + ",").split(",");
            }
            else if (data.contains(" "))
            {
                split = (data + " ").split(" ");
            }
            if (split != null)
            {
                BuildMobsFlag(split);
            }
        }
    }

    private void BuildMobsFlag(String[] split)
    {
        if (split.length > 0)
        {
            mobs = new HashSet<String>();
            for (String mobType : split)
            {
                addMob(mobType);
            }
        }
        else
        {
            mobs.add("ALL");
        }
    }

    public void BuildOwnersFlag(String[] split)
    {

        boolean skip = true;
        this.owners = new ArrayList<String>();

        if (split.length > 0)
        {
            this.owners = new ArrayList<String>();
            for (String owner : split)
            {
                if (!skip)
                {
                    owners.add(owner.trim());
                }
                else
                {
                    skip = false;
                }
            }
        }
    }

    public void setPVP(boolean value)
    {
        this.pvp = value;
    }

    public void setRegen(String value)
    {
        this.regen = new EpicZoneRegen(value);
        this.hasRegen = (this.regen.getAmount() != 0 || this.regen.getBedBonus() > 0);
    }

    private void buildChildren(String data)
    {

        if (data.length() > 0)
        {
            String[] dataList = data.split("\\s");

            Collections.addAll(this.childrenTags, dataList);
        }

    }

    private void buildPolygon(String data)
    {
        String[] dataList = data.split("\\s");

        if (dataList.length > 2)
        {
            this.polygon = new Polygon();
            this.center = null;
            for (String aDataList : dataList)
            {
                String[] split = aDataList.split(":");
                this.polygon.addPoint(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
            }
            this.type = ZoneType.POLY;
        }
        else if (dataList.length >= 1)
        {
            String[] split = dataList[0].split(":");
            this.polygon = null;
            this.center = new Point(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
            if (dataList.length > 1)
            {
                this.radius = Integer.valueOf(dataList[1]);
            }
            this.type = ZoneType.CIRCLE;
        }
    }

    public void addPoint(Point point)
    {
        if (this.polygon == null)
        {
            this.polygon = new Polygon();
        }
        this.polygon.addPoint(point.x, point.y);
    }

    public void clearPolyPoints()
    {
        this.polygon = new Polygon();
    }

    public void rebuildBoundingBox()
    {
        if (this.polygon != null)
        {
            this.boundingBox = this.polygon.getBounds();
        }
        else
        {
            this.boundingBox = null;
        }
    }

    public boolean getPVP()
    {
        return this.pvp;
    }

    public void Regen()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, this.regen.getInterval());
        this.lastRegen = cal.getTime();
    }

    public Date getAdjustedRegenDelay()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -this.regen.getDelay());
        return cal.getTime();
    }

    public Date getAdjustedRestDelay()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -this.regen.getRestDelay());
        return cal.getTime();
    }

    public boolean timeToRegen()
    {
        if (this.hasRegen)
        {
            if (this.lastRegen.before(new Date()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean pointWithinCircle(Point test)
    {
        double x = test.x - this.center.x;
        double y = test.y - this.center.y;
        double xsquared = x * x;
        double ysquared = y * y;
        double distanceFromCenter = Math.sqrt(xsquared + ysquared);
        return distanceFromCenter <= this.radius;

    }

    public boolean isOwner(String playerName)
    {
        return owners.contains(playerName);
    }

    public void addOwner(String playerName)
    {
        owners.add(playerName);
    }

    public void removeOwner(String playerName)
    {
        owners.remove(playerName);
    }

    public boolean hasPermission(Player player, String node, PermType permission)
    {
        EpicZonePermission perm = permissions.get(player.getName().toLowerCase() + node.toUpperCase());
        if (perm != null)
        {
            if (perm.getPermission() == permission)
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermissionFromGroup(Player player, String node, PermType permission)
    {
        for (String groupName : PermissionsManager.getGroupNames(player))
        {
            EpicZonePermission perm = permissions.get(groupName.toLowerCase() + node.toUpperCase());
            if (perm != null)
            {
                if (perm.getPermission() == permission)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void addPillar(Block blk)
    {
        if (this.pillarBlocks == null)
        {
            this.pillarBlocks = new ArrayList<PillarBlock>();
        }
        this.pillarBlocks.add(new PillarBlock(blk.getLocation(), blk.getType()));
    }

    public void ShowPillars()
    {
        ArrayList<Point> points = this.getPointsArray();
        this.pillarBlocks = new ArrayList<PillarBlock>();
        if (points != null)
        {
            for (Point pnt : points)
            {
                ShowPillar(pnt);
            }
        }
    }

    public void ShowPillar(Point pnt)
    {

        World world = General.plugin.getServer().getWorld(this.world);
        Block blk = world.getBlockAt(pnt.x, world.getHighestBlockYAt(pnt.x, pnt.y), pnt.y);

        Location low = blk.getLocation().clone();
        Location mid = blk.getLocation().clone();
        Location high = blk.getLocation().clone();

        low.setY(low.getBlockY());
        mid.setY(mid.getBlockY() + 1);
        high.setY(high.getBlockY() + 2);

        this.pillarBlocks.add(new PillarBlock(world.getBlockAt(low).getLocation(), world.getBlockAt(low).getType()));
        this.pillarBlocks.add(new PillarBlock(world.getBlockAt(mid).getLocation(), world.getBlockAt(mid).getType()));
        this.pillarBlocks.add(new PillarBlock(world.getBlockAt(high).getLocation(), world.getBlockAt(high).getType()));

        world.getBlockAt(low).setType(Material.BEDROCK);
        world.getBlockAt(mid).setType(Material.BEDROCK);
        world.getBlockAt(high).setType(Material.BEDROCK);
    }


    public void HidePillars()
    {
        if (this.pillarBlocks != null)
        {
            org.bukkit.World world = General.plugin.getServer().getWorld(this.world);
            for (PillarBlock blk : this.pillarBlocks)
            {
                world.getBlockAt(blk.location).setType(blk.material);
            }
        }
        this.pillarBlocks = null;
    }

    public boolean IsPointWithin(String worldName, Integer height, Point point)
    {
        boolean result = false;
        if (worldName.equalsIgnoreCase(this.getWorld()))
        {
            if (this.type == ZoneType.GLOBAL)
            {
                result = true;
            }
            else
            {
                if (height >= this.getFloor() && height <= this.getCeiling())
                {
                    if (this.pointWithin(point))
                    {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public void setDefaults(EpicZone parentZone)
    {
        this.ceiling = parentZone.ceiling;
        this.explode = parentZone.explode;
        this.fire = parentZone.fire;
        this.fireBurnsMobs = parentZone.fireBurnsMobs;
        this.floor = parentZone.floor;
        this.mobs = parentZone.mobs;
        this.pvp = parentZone.pvp;
        this.regen = parentZone.regen;
        this.sanctuary = parentZone.sanctuary;
    }
}

