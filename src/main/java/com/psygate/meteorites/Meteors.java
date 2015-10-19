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
import com.psygate.meteorites.meteor.Cache;
import com.psygate.meteorites.meteor.IMeteor;
import com.psygate.meteorites.meteor.Meteor;
import com.psygate.meteorites.meteor.ScorchingIceMeteor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class Meteors extends JavaPlugin {

    public static final Random rand = new Random(3487812891984950L);
    private long ticks = 0;
    private final List<IMeteor> meteors = new LinkedList<>();

    @Override
    public void onEnable() {
        super.onEnable(); //To change body of generated methods, choose Tools | Templates.
        saveConfig();
        Conf.init(getConfig());
        Conf.getMeteorSettings();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                if (meteors.isEmpty()) {
                    return;
                }
                ticks++;

                Iterator<IMeteor> it = meteors.iterator();
                while (it.hasNext()) {
                    IMeteor meteor = it.next();
                    if (meteor.isDead()) {
                        meteor.onDeath();
                        it.remove();
                    } else {
                        if (ticks % meteor.getTickDelay() == 0) {
                            meteor.tick();
//                        System.out.println(meteor.getCore());
                        }

                        if (ticks % 20 * 10 == 0) {
                            Cache.printStats();
                        }
                    }
                }
            }
        }, 1, 1);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MeteorSpawner(getDataFolder()), 200, 200);

        Bukkit.getPluginCommand("spawnmeteor").setExecutor(new SpawnMeteorCommand());
    }

    public void addMeteor(IMeteor meteor) {
        if (meteors.size() < Conf.getMaxMeteorites()) {
            meteors.add(meteor);
            getLogger().info("Meteor added.");
        }
    }

    public void forceAddMeteor(IMeteor meteor) {
        if (meteors.size() < Conf.getMaxMeteorites()) {
            meteors.add(meteor);
            getLogger().info("Meteor added.");
        } else {
            IMeteor m = meteors.remove(rand.nextInt(meteors.size()));
            m.cleanup();
            meteors.add(meteor);
            getLogger().info("Meteor force added.");
        }
    }
}
