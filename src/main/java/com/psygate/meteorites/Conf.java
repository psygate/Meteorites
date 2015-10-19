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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bukkit.Material;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.CARROT;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.FIRE;
import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.GRAVEL;
import static org.bukkit.Material.OBSIDIAN;
import static org.bukkit.Material.POTATO;
import static org.bukkit.Material.SAND;
import static org.bukkit.Material.SANDSTONE;
import static org.bukkit.Material.SEEDS;
import static org.bukkit.Material.STATIONARY_WATER;
import static org.bukkit.Material.STONE;
import static org.bukkit.Material.WATER;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class Conf {

    private static FileConfiguration conf;
    private static Map<Material, Material> scorchMap = new HashMap<>();

    public static void init(FileConfiguration config) {
        conf = config;

        scorchMap.put(Material.LEAVES, Material.FIRE);
        scorchMap.put(Material.LEAVES_2, Material.FIRE);
        scorchMap.put(Material.DIRT, Material.FIRE);
        scorchMap.put(Material.GRASS, Material.FIRE);
        scorchMap.put(Material.LOG, Material.FIRE);
        scorchMap.put(Material.LOG_2, Material.FIRE);
        scorchMap.put(SAND, GLASS);
        scorchMap.put(SANDSTONE, GLASS);
        scorchMap.put(GRAVEL, COBBLESTONE);
        scorchMap.put(SEEDS, FIRE);
        scorchMap.put(POTATO, FIRE);
        scorchMap.put(CARROT, FIRE);

        scorchMap.put(STONE, OBSIDIAN);
        scorchMap.put(Material.COBBLESTONE, Material.AIR);
        scorchMap.put(WATER, AIR);
        scorchMap.put(STATIONARY_WATER, AIR);
    }

    public static List<World> getEnabledWorlds() {
        final List<World> worlds = new ArrayList<>();
        for (String name : conf.getStringList("worlds")) {
            World world = Meteors.getPlugin(Meteors.class).getServer().getWorld(name);
            if (world != null) {
                worlds.add(world);
            }
        }

        return worlds;
    }

    public static int getMaxMeteorites() {
        return conf.getInt("performance.max_meteorites");
    }

    public static Map<Material, Material> getScorchMap() {
        return scorchMap;
    }

    public static List<MeteorSetting> getMeteorSettings() {
        /*
         meteors:
         small:
         ice:
         radius: 1
         material: PACKED_ICE
         scorch: true
         scorch_radius: 3
         displace: true
         displace_radius: 5
         displace_modifier: 500
         */
        ArrayList<MeteorSetting> settings = new ArrayList<>();
        MemorySection map = (MemorySection) conf.get("meteors");
        String[] keys = new String[]{"small", "medium", "large"};

        for (String key : keys) {
            MemorySection mem = (MemorySection) map.get(key);
            for (String name : mem.getKeys(false)) {
                int radius = mem.getInt(name + ".radius");
                Material mat = Material.valueOf(mem.getString(name + ".material").toUpperCase());
                boolean scorch = mem.getBoolean(name + ".scorch");
                int scorchrad = 0;
                if (scorch) {
                    scorchrad = mem.getInt(name + ".scorch_radius");
                }

                boolean displace = mem.getBoolean(name + ".displace");
                int disprad = 0;
                int displacemod = 0;
                if (displace) {
                    disprad = mem.getInt(name + ".displace_radius");
                    displacemod = mem.getInt(name + ".displace_modifier");
                }

                double spawnChance = mem.getDouble(name + ".spawn_probability");
                settings.add(new MeteorSetting(name, radius, mat, scorch, displace, displacemod, scorchrad, disprad, spawnChance));
            }
        }

        return settings;
    }

    public static long spawnInterval() {
        return TimeUnit.MINUTES.toMillis(conf.getLong("spawn_interval"));
    }
}
