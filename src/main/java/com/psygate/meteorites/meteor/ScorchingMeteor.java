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

import com.psygate.meteorites.Meteors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.block.BlockFace;
import static com.psygate.meteorites.Meteors.rand;
import com.psygate.meteorites.math.Vector;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public abstract class ScorchingMeteor extends Meteor {

    private int scorchSphere;
    private int displacementSphere;
    private long displaced = 0;

    private Map<Material, Material> scorchMap = new HashMap<>();

    public ScorchingMeteor(Material mat, Location core, int radius, Vector trajectory, int scorchSphere, int displacementSphere, long moveticks) {

        super(mat, core, radius, trajectory, moveticks);
        this.scorchSphere = scorchSphere;
        this.displacementSphere = displacementSphere;
//        calcDisplacement();

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

    private int randInt(int bounds) {
        return rand.nextInt(bounds) * (rand.nextBoolean() ? -1 : 1);
    }

    private int randNegInt(int bounds) {
        return -rand.nextInt(bounds);
    }

    @Override
    public final void subTick() {
        int tgt = Math.max(scorchSphere, displacementSphere);
        for (int x = -tgt; x <= tgt; x++) {
            for (int y = -tgt; y <= tgt; y++) {
                for (int z = -tgt; z <= tgt; z++) {
                    Vector v = new Vector(x, y, z, getWorld());
                    if (Cache.isNoMod(v)) {
                        continue;
                    }

                    double length = v.length();
//                    System.out.println(length + "/" + displacementSphere);

                    if (length >= displacementSphere && length < scorchSphere) {
                        scorch(v);
                    } else if (length <= displacementSphere) {
                        if (length >= displacementSphere / 3 * 2) {
                            displaceOuter(v);
                        } else if (length >= displacementSphere / 3) {
                            middleDisplace(v);
                        } else if (length >= getRadius()) {
                            innerDisplace(v);
                        }
                    }
                }
            }
        }

        subsubTick();
    }

    public abstract void subsubTick();

    public int getScorchSphere() {
        return scorchSphere;
    }

    public void setScorchSphere(int scorchSphere) {
        this.scorchSphere = scorchSphere;
    }
//
//    public List<Vector> getScorchBlocks() {
//        return scorchBlocks;
//    }

    public long getDisplaced() {
        return displaced;
    }

    @Override
    public boolean isDead() {
//        System.out.println(displaced + " " + (getRadius() * 1000));
        return super.isDead() || displaced > getRadius() * 1000;
    }

    private void scorch(Vector vi) {
//        System.out.println("Scorching: " + b);
        Vector v = vi.add(getCore());
        SafeNotAirNotScorchedBlock block = new SafeNotAirNotScorchedBlock(v);
//            Block b = v.asLocation().getBlock();

        if (scorchMap.containsKey(block.getType())) {
            block.setType(scorchMap.get(block.getType()));
        }
    }

    private void displaceOuter(Vector vi) {
        Vector v = vi.add(getCore());
        Vector nv = v.add(getCore()).scale(5).add(0, 3, 0);

        SafeNotAirBlock source = new SafeNotAirBlock(v);
        SafeNotAirBlock target = new SafeNotAirBlock(nv);
        target.setType(source);
        if (source.setType(Material.AIR)) {
            displaced++;
        }
    }

    private void middleDisplace(Vector v) {

    }

    private void innerDisplace(Vector v) {

    }

    public int getDisplacementSphere() {
        return displacementSphere;
    }

    public int getExplosionRadius() {
        return displacementSphere * 4;
    }

    @Override
    public final void onDeath() {
        //Deposit a lot of energy on impact.

        int size = getExplosionRadius();
        Vector explsource = getCore().add(0, displacementSphere * 2, 0);

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    Vector v = new Vector(x, y, z, getWorld());

                    if (v.lengthSqr() > size * size) {
                        continue;
                    }

                    Vector displace = explsource.add(v);
                    SafeNotAirBlock block = new SafeNotAirBlock(displace);

                    if (displace.shorterThanOrEqual(getExplosionRadius() - 2)) {
                        block.setType(AIR);
                    } else {
                        block.setType(OBSIDIAN);
                    }
                }
            }
        }

        subDeath();
    }

    public abstract void subDeath();

}
