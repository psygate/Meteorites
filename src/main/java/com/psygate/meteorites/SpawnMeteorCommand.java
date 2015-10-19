/*
The MIT License (MIT)

Copyright (c) 2015 psygate (https://github.com/psygate)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.psygate.meteorites;

import com.psygate.meteorites.math.Vector;
import com.psygate.meteorites.meteor.ADisplacementMeteor;
import com.psygate.meteorites.meteor.AMeteor;
import com.psygate.meteorites.meteor.AScorchingMeteor;
import com.psygate.meteorites.meteor.ScorchingIceMeteor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class SpawnMeteorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] arguments) {
        if (sender instanceof Player) {
            try {
                Player p = (Player) sender;
                int type = parseType(sender, arguments);
                switch (type) {
                    case 0:
                        spawnAMeteor(p, arguments);
                        break;
                    case 1:
                        spawnAScorch(p, arguments);
                        break;
                    case 2:
                        spawnADisplace(p, arguments);
                        break;
                    default:
                        throw new IllegalStateException("Unknown type.");
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Command failed. " + e.getMessage());
            }
//            if (arg3.length < 1) {
//                p.sendMessage(ChatColor.RED + "Cannot spawn meteor without type argument.");
//            } else if (arg3[0].equals("meteor")) {
//                AMeteor met = new AMeteor(Material., null, null, radius)
//            }
//            Integer radius = Integer.parseInt(arg3[0]);
//            Integer scorch = Integer.parseInt(arg3[1]);
//            Integer displace = Integer.parseInt(arg3[2]);
//            Integer moveticks = Integer.parseInt(arg3[3]);
//
//            ScorchingIceMeteor met = new ScorchingIceMeteor(p.getLocation(), radius, new Vector(p.getEyeLocation().getDirection(), p.getWorld()), scorch, displace, moveticks);
//            Meteors.getPlugin(Meteors.class).forceAddMeteor(met);
        } else {
            sender.sendMessage(ChatColor.RED + " cannot spawn from console.");
        }

        return true;
    }

    private int parseType(CommandSender invoker, String[] vals) {
        if (vals.length == 0) {
            invoker.sendMessage(ChatColor.RED + "Cannot spawn meteor without type argument.");
            throw new IllegalArgumentException();
        } else {
            switch (vals[0].toLowerCase()) {
                case "meteor":
                    return 0;
                case "scorch":
                    return 1;
                case "displace":
                    return 2;
                default:
                    invoker.sendMessage(ChatColor.RED + "Unknown type: " + vals[0]);
                    throw new IllegalArgumentException("Unknown type.");
            }
        }
    }

    private void spawnAMeteor(Player p, String[] arguments) {
        Vector vec = new Vector(p.getEyeLocation().getDirection(), p.getWorld());
        AMeteor m = new AMeteor(Material.valueOf(arguments[1].toUpperCase()), p.getLocation(), vec, Double.parseDouble(arguments[2]));
        Meteors.getPlugin(Meteors.class).forceAddMeteor(m);
    }

    private void spawnAScorch(Player p, String[] arguments) {
        Vector vec = new Vector(p.getEyeLocation().getDirection(), p.getWorld());
        double radius = 0;
        try {
            radius = Double.parseDouble(arguments[2]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Radius missing.");
            throw e;
        }

        double scorchrad = 0;
        try {
            scorchrad = Double.parseDouble(arguments[3]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Scorch radius missing.");
            throw e;
        }

        AMeteor m = new AScorchingMeteor(Material.valueOf(arguments[1].toUpperCase()), p.getLocation(), vec, radius, scorchrad);
        Meteors.getPlugin(Meteors.class).forceAddMeteor(m);
    }

    private void spawnADisplace(Player p, String[] arguments) {
        Vector vec = new Vector(p.getEyeLocation().getDirection(), p.getWorld());
        double radius = 0;
        try {
            radius = Double.parseDouble(arguments[2]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Radius missing.");
            throw e;
        }

        double scorchrad = 0;
        try {
            scorchrad = Double.parseDouble(arguments[3]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Scorch radius missing.");
            throw e;
        }

        double displacerad = 0;
        try {
            displacerad = Double.parseDouble(arguments[4]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Displace radius missing.");
            throw e;
        }

        AMeteor m = new ADisplacementMeteor(Material.valueOf(arguments[1].toUpperCase()), p.getLocation(), vec, radius, scorchrad, displacerad);
        Meteors.getPlugin(Meteors.class).forceAddMeteor(m);
    }

}
