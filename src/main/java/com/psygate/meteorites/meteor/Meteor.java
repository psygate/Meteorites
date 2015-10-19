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
package com.psygate.meteorites.meteor;

import com.google.common.cache.LoadingCache;
import com.psygate.meteorites.Meteors;
import com.psygate.meteorites.math.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public abstract class Meteor implements IMeteor {

    private final static Logger logger = Logger.getLogger(Meteor.class.getName());
//    private List<Vector> blocks = new ArrayList<>();
    private Vector trajectory;
    private Vector core;
    private int radius;
    private long spawnTime;
    private long movementTicks;
    protected Material mat;
//    private World world;

    public Meteor(Material mat, Location core, int radius, Vector trajectory, long moveticks) {
        this.mat = mat;
        this.trajectory = trajectory;
//        this.world = core.getWorld();
        this.radius = radius;
        this.core = new Vector(core);
        spawnTime = System.currentTimeMillis();
        movementTicks = moveticks;

        //Use a shell to get faster updates.
//        for (int x = -radius; x <= radius; x++) {
//            for (int y = -radius; y <= radius; y++) {
//                for (int z = -radius; z <= radius; z++) {
//                    Vector v = new Vector(x, y, z, core.getWorld());
//
//                    if (v.lengthSqr() <= radius) {
//                        blocks.add(v);
//                    }
//                }
//            }
//        }
    }

    public Material getMaterial() {
        return mat;
    }

    public Vector getCore() {
        return core;
    }

    public int getRadius() {
        return radius;
    }

//    public List<Vector> getVectors() {
//        return blocks;
//    }
//
//    public List<Block> getBlocks() {
//        ArrayList<Block> list = new ArrayList<>(blocks.size());
//
//        for (Vector bv : getVectors()) {
//            Vector v = bv.add(core);
//            Block block = core.world.getBlockAt((int) v.x, (int) v.y, (int) v.z);
//            if (!Cache.isNoMod(v)) {
//                list.add(block);
//            }
//        }
//
//        return list;
//    }
    public World getWorld() {
        return core.world;
    }

    public Vector getTrajectory() {
        return trajectory;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public long getMovementTicks() {
        return movementTicks;
    }

    public static double distSqr(double x, double y, double z, double x1, double y1, double z1) {
        return (x - x1) * (x - x1) + (y - y1) * (y - y1) + (z - z1) * (z - z1);
    }

    public static double distSqr(double x, double y, double z, Location core) {
        return distSqr(x, y, z, core.getX(), core.getY(), core.getZ());
    }

    public static double distSqr(double x, double y, double z, Vector v) {
        return distSqr(x, y, z, v.x, v.y, v.z);
    }

    public static boolean isInRadius(double x, double y, double z, Location core, int radius) {
        return distSqr(x, y, z, core) <= radius * radius;
    }

    public static boolean isInRadius(Location loc, Vector core, double radius) {
        return distSqr(loc.getX(), loc.getY(), loc.getZ(), core) <= radius * radius;
    }

    public static boolean isInRadius(double x, double y, double z, Vector core, double radius) {
        return distSqr(x, y, z, core) <= radius * radius;
    }

    public static boolean isInRadius(Location loc, Location core, double i) {
        return loc.distanceSquared(core) < (i * i);
    }

    public void schedule() {
        Meteors.getPlugin(Meteors.class).addMeteor(this);
//        Meteorites.getPlugin(Meteorites.class).getServer().getScheduler().scheduleSyncRepeatingTask(Meteorites.getPlugin(Meteorites.class), new Runnable() {
//            
//            @Override
//            public void run() {
//                cleanup();
//                step();
//                display();
//            }
//        }, movementTicks, movementTicks);
    }

    public void tick() {
        meteorCleanup();
        core = core.add(trajectory);
        subTick();
        meteorDisplay();
    }

    private void meteorDisplay() {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Vector v = new Vector(x, y, z, core.world);

                    if (v.shorterThanOrEqual(radius)) {
                        SafeBlock block = new SafeBlock(new Vector(x, y, z, core.world).add(core));
                        block.setType(mat);
                    }
                }
            }
        }
//        display();
    }

    public void meteorCleanup() {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Vector v = new Vector(x, y, z, core.world);

                    if (v.shorterThanOrEqual(radius)) {
                        SafeBlock block = new SafeBlock(new Vector(x, y, z, core.world).add(core));
                        block.setType(Material.AIR);
                    }
                }
            }
        }
//        cleanup();
    }

    public void meteorDeath() {
        //Use a shell to get faster updates.
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Vector v = new Vector(x, y, z, core.world);

                    if (v.shorterThanOrEqual(radius)) {
                        SafeNotAirBlock block = new SafeNotAirBlock(v.add(core));
                        block.setType(Material.AIR);
                    }
                }
            }
        }

//        onDeath();
    }

//    public Block asBlock(Vector v) {
//        return world.getBlockAt((int) v.x, (int) v.y, (int) v.z);
//    }
    protected abstract void display();

    protected abstract void subCleanup();

    protected abstract void subTick();

    protected abstract void subOnDeath();

    @Override
    public final long getTickDelay() {
        return 1;
    }
    
    @Override
    public String toString() {
        return "Meteor{" + "trajectory=" + trajectory + ", core=" + core + ", radius=" + radius + '}';
    }

    @Override
    public boolean isDead() {
        return core.y < 1;
    }

    public abstract String stats();

}
