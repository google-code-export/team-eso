/*

        This file is part of EpicGates

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

package commands;

import objects.EpicGate;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicGates.EpicGates;
import com.epicsagaonline.bukkit.EpicGates.General;

public class EGGate implements CommandHandler {

	@Override
	public boolean onCommand(String command, CommandSender sender, String[] args) {
		if ((sender instanceof Player && EpicGates.permissions.hasPermission(
				(Player) sender, "epicgates.admin"))) {
			Player player = (Player) sender;

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("create")) {
					Create(args, sender, player);
				} else if (args[0].equalsIgnoreCase("link")) {
					Link(args, sender, player);
				} else if (args[0].equalsIgnoreCase("unlink")) {
					Unlink(args, sender, player);
				} else if (args[0].equalsIgnoreCase("move")) {
					Move(args, sender, player);
				} else if (args[0].equalsIgnoreCase("direction")) {
					Direction(args, sender, player);
				} else if (args[0].equalsIgnoreCase("delete")) {
					Delete(args, sender, player);
				} else if (args[0].equalsIgnoreCase("info")) {
					Info(args, sender, player);
				} else if (args[0].equalsIgnoreCase("list")) {
					List(args, sender, player);
				} else {
					Help(sender, "");
				}
			} else {
				Help(sender, "");
			}
			return true;
		}
		return false;
	}

	private void Create(String[] args, CommandSender sender, Player player) {
		if (args.length > 1 && !args[1].equalsIgnoreCase("?")) {

			String tag = args[1];

			EpicGate gate = new EpicGate(tag, player.getLocation().clone(),
					player.getLocation().getYaw());

			buildGate(gate, player.getWorld());
			buildLanding(gate, player.getWorld());

			General.myGateTags.add(tag);
			General.myGates.put(tag, gate);

			General.saveGates();
			General.loadGates();

		} else {
			Help(sender, "create");
		}

	}

	private void Link(String[] args, CommandSender sender, Player player) {
		if (args.length > 1 && !args[1].equalsIgnoreCase("?")) {

			if (args.length > 2) {

				EpicGate gate = General.myGates.get(args[1]);
				if (gate != null) {
					EpicGate target = General.myGates.get(args[2]);
					if (target != null) {
						gate.setTarget(target);
						General.saveGates();
						General.loadGates();
						sender.sendMessage("[" + args[1]
								+ "] Now teleports to [" + args[2] + "]");
					} else {
						sender.sendMessage("The target gate [" + args[2]
								+ "] does not exist.");
					}
				} else {
					sender.sendMessage("The source gate [" + args[1]
							+ "] does not exist.");
				}
			} else {
				Help(sender, "link");
			}

		} else {
			Help(sender, "link");
		}

	}

	private void Unlink(String[] args, CommandSender sender, Player player) {
		if (args.length > 1 && !args[1].equalsIgnoreCase("?")) {
			EpicGate gate = General.myGates.get(args[1]);
			if (gate != null) {
				gate.setTarget(null);
				General.saveGates();
				General.loadGates();
				sender.sendMessage("[" + args[1] + "] no longer has a target.");
			} else {
				sender.sendMessage("The gate [" + args[1] + "] does not exist.");
			}
		} else {
			Help(sender, "unlink");
		}
	}

	private void Move(String[] args, CommandSender sender, Player player) {
		if (args.length > 1 && !args[1].equalsIgnoreCase("?")) {

			EpicGate gate = General.myGates.get(args[1]);
			if (gate != null) {

				clearGate(gate);
				clearLanding(gate);

				gate.setLocation(player.getLocation().clone());

				buildGate(gate, player.getWorld());
				buildLanding(gate, player.getWorld());

				General.myGates.put(args[1], gate);
				General.saveGates();
				General.loadGates();

			} else {
				sender.sendMessage("The gate [" + args[1] + "] does not exist.");
			}

		} else {
			Help(sender, "move");
		}

	}

	private void Delete(String[] args, CommandSender sender, Player player) {
		if (args.length > 1 && !args[1].equalsIgnoreCase("?")) {
			EpicGate gate = General.myGates.get(args[1]);
			if (gate != null) {
				clearGate(gate);
				clearLanding(gate);
				General.myGates.remove(args[1]);
				General.myGateTags.remove(args[1]);
				General.saveGates();
				General.loadGates();
				sender.sendMessage("[" + args[1] + "] has been deleted.");
			} else {
				sender.sendMessage("The gate [" + args[1] + "] does not exist.");
			}
		} else {
			Help(sender, "delete");
		}

	}

	private void Info(String[] args, CommandSender sender, Player player) {
		if (args.length > 1 && !args[1].equalsIgnoreCase("?")) {
			Help(sender, "info");
		} else {
			EpicGate gate = GetGateByLandingForLocation(player.getLocation());
			if (gate != null) {
				String message = "";
				message = ChatColor.GOLD + "[" + gate.getTag() + "]"
						+ ChatColor.GREEN + " in " + ChatColor.GOLD + "["
						+ gate.getLocation().getWorld().getName() + "]"
						+ ChatColor.GREEN + " at " + ChatColor.GOLD + "["
						+ gate.getLocation().getBlockX() + ","
						+ gate.getLocation().getBlockY() + ","
						+ gate.getLocation().getBlockZ() + "]";
				if (gate.getTarget() != null) {
					message = message + ChatColor.GREEN + " linked to "
							+ ChatColor.GOLD + "[" + gate.getTargetTag() + "].";
				} else {
					message = message + ChatColor.GREEN + ".";
				}
				sender.sendMessage(message);
			} else {
				sender.sendMessage("You are not standing on the landing of a valid gate.");
			}
		}
	}

	private void List(String[] args, CommandSender sender, Player player) {

		for (String gateTag : General.myGateTags) {
			String message = "";
			EpicGate gate = General.myGates.get(gateTag);
			if (gate != null) {
				message = ChatColor.GOLD + "[" + gate.getTag() + "]"
						+ ChatColor.GREEN + " in " + ChatColor.GOLD + "["
						+ gate.getLocation().getWorld().getName() + "]"
						+ ChatColor.GREEN + " at " + ChatColor.GOLD + "["
						+ gate.getLocation().getBlockX() + ","
						+ gate.getLocation().getBlockY() + ","
						+ gate.getLocation().getBlockZ() + "]";
				if (gate.getTarget() != null) {
					message = message + ChatColor.GREEN + " linked to "
							+ ChatColor.GOLD + "[" + gate.getTargetTag() + "].";
				} else {
					message = message + ChatColor.GREEN + ".";
				}
				sender.sendMessage(message);
			}
		}
	}

	private void Direction(String[] args, CommandSender sender, Player player) {
		if (args.length > 2 && !args[1].equalsIgnoreCase("?")) {

			EpicGate gate = General.myGates.get(args[1]);
			if (gate != null) {
				clearLanding(gate);
				gate.setDirection(args[2]);
				buildLanding(gate, player.getWorld());
				General.saveGates();
				General.loadGates();
				sender.sendMessage("The direction of [" + args[1]
						+ "] has been set to [" + gate.getDirection() + "]");
			} else {
				sender.sendMessage("The gate [" + args[1] + "] does not exist.");
			}
		} else {
			Help(sender, "direction");
		}

	}

	private void Help(CommandSender sender, String mode) {

		System.out.println(mode);
		if (mode.equalsIgnoreCase("create")) {
			sender.sendMessage(ChatColor.GOLD + "/gate create "
					+ ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN
					+ " = New Gate Name.");
			sender.sendMessage(ChatColor.GREEN + "Used to create new gates.");
		} else if (mode.equalsIgnoreCase("link")) {
			sender.sendMessage(ChatColor.GOLD + "/gate link " + ChatColor.AQUA
					+ "[1] [2] " + ChatColor.WHITE + "| " + ChatColor.AQUA
					+ "[1]" + ChatColor.GREEN + " = Source Gate, "
					+ ChatColor.AQUA + "[2]" + ChatColor.GREEN
					+ " = Target Gate");
			sender.sendMessage(ChatColor.GREEN + "Used to link two gates.");
		} else if (mode.equalsIgnoreCase("unlink")) {
			sender.sendMessage(ChatColor.GOLD + "/gate unlink "
					+ ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Gate Tag.");
			sender.sendMessage(ChatColor.GREEN
					+ "Used to remove the supplied gate's target.)");
		} else if (mode.equalsIgnoreCase("direction")) {
			sender.sendMessage(ChatColor.GOLD + "/gate direction "
					+ ChatColor.AQUA + "[1] [2] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Gate Tag."
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Direction");
			sender.sendMessage(ChatColor.GREEN
					+ "Used to change the direction of the landing pad for a gate.");
		} else if (mode.equalsIgnoreCase("move")) {
			sender.sendMessage(ChatColor.GOLD + "/gate move " + ChatColor.AQUA
					+ "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]"
					+ ChatColor.GREEN + " = Gate Tag.");
			sender.sendMessage(ChatColor.GREEN
					+ "Used to change the location of a gate to where you are currently standing. (Works across worlds)");
		} else if (mode.equalsIgnoreCase("delete")) {
			sender.sendMessage(ChatColor.GOLD + "/gate delete "
					+ ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Gate Tag.");
			sender.sendMessage(ChatColor.GREEN + "Used to remove a gate.");
		} else if (mode.equalsIgnoreCase("info")) {
			sender.sendMessage(ChatColor.GOLD + "/gate info " + ChatColor.WHITE
					+ "| " + ChatColor.GREEN + " Get gate info.");
			sender.sendMessage(ChatColor.GREEN
					+ "Stand on a gate's landing pad and issue this command for info on that gate.");
		} else {
			sender.sendMessage(ChatColor.GOLD + "EpicGates help");
			sender.sendMessage(ChatColor.GOLD + "/gate create "
					+ ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN
					+ " = New Gate Name.");
			sender.sendMessage(ChatColor.GOLD + "/gate link " + ChatColor.AQUA
					+ "[1] [2] " + ChatColor.WHITE + "| " + ChatColor.AQUA
					+ "[1]" + ChatColor.GREEN + " = Source Gate, "
					+ ChatColor.AQUA + "[2]" + ChatColor.GREEN
					+ " = Target Gate");
			sender.sendMessage(ChatColor.GOLD + "/gate unlink "
					+ ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Gate Tag.");
			sender.sendMessage(ChatColor.GOLD + "/gate direction "
					+ ChatColor.AQUA + "[1] [2] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Gate Tag."
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Direction");
			sender.sendMessage(ChatColor.GOLD + "/gate move " + ChatColor.AQUA
					+ "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]"
					+ ChatColor.GREEN + " = Gate Tag.");
			sender.sendMessage(ChatColor.GOLD + "/gate delete "
					+ ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| "
					+ ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Gate Tag.");
		}
	}

	private void buildGate(EpicGate gate, World world) {

		Location floor = gate.getLocation().clone();
		Location bottom = gate.getLocation().clone();
		Location top = gate.getLocation().clone();

		floor.setY(bottom.getBlockY() - 1);
		top.setY(bottom.getBlockY() + 1);

		world.getBlockAt(floor).setType(Material.OBSIDIAN);
		world.getBlockAt(bottom).setType(Material.AIR);
		world.getBlockAt(top).setType(Material.AIR);

	}

	private void buildLanding(EpicGate gate, World world) {

		Location floor = gate.getLanding().clone();
		Location bottom = gate.getLanding().clone();
		Location top = gate.getLanding().clone();

		floor.setY(bottom.getBlockY() - 1);
		top.setY(bottom.getBlockY() + 1);

		world.getBlockAt(floor).setType(Material.WOOD);
		world.getBlockAt(bottom).setType(Material.AIR);
		world.getBlockAt(top).setType(Material.AIR);

	}

	private void clearGate(EpicGate gate) {

		World world = gate.getLocation().getWorld();
		Location floor = gate.getLocation().clone();
		Location bottom = gate.getLocation().clone();
		Location top = gate.getLocation().clone();

		floor.setY(bottom.getBlockY() - 1);
		top.setY(bottom.getBlockY() + 1);

		world.getBlockAt(floor).setType(Material.DIRT);
		world.getBlockAt(bottom).setType(Material.AIR);
		world.getBlockAt(top).setType(Material.AIR);

	}

	private void clearLanding(EpicGate gate) {

		World world = gate.getLocation().getWorld();
		Location floor = gate.getLanding().clone();
		Location bottom = gate.getLanding().clone();
		Location top = gate.getLanding().clone();

		floor.setY(bottom.getBlockY() - 1);
		top.setY(bottom.getBlockY() + 1);

		world.getBlockAt(floor).setType(Material.DIRT);
		world.getBlockAt(bottom).setType(Material.AIR);
		world.getBlockAt(top).setType(Material.AIR);

	}

	private EpicGate GetGateByLandingForLocation(Location loc) {
		for (String gateTag : General.myGateTags) {
			EpicGate gate = General.myGates.get(gateTag);
			if (gate != null) {
				if (gate.getLocation().getWorld().getName()
						.equalsIgnoreCase(loc.getWorld().getName())) {
					if (gate.getTargetTag().length() > 0) {
						if (PlayerWithinGateLanding(gate, loc)) {
							return gate;
						}
					}
				}
			}
		}
		return null;
	}

	private boolean PlayerWithinGateLanding(EpicGate gate, Location playerLoc) {

		boolean result = false;

		if ((int) gate.getLanding().getBlockY() == (int) playerLoc.getBlockY()) {
			if (playerLoc.getX() <= Math.ceil(gate.getLanding().getX())
					&& playerLoc.getX() >= Math.floor(gate.getLanding().getX())) {
				if (playerLoc.getZ() <= Math.ceil(gate.getLanding().getZ())
						&& playerLoc.getZ() >= Math.floor(gate.getLanding()
								.getZ())) {
					result = true;
				}
			}
		}

		return result;
	}

}
