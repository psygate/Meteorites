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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import static com.psygate.meteorites.Meteors.rand;
import com.psygate.meteorites.math.Vector;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.logging.Level;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class ScorchingIceMeteor extends ScorchingMeteor {

    private final static Logger logger = Logger.getLogger(ScorchingIceMeteor.class.getName());

    public ScorchingIceMeteor(Location core, int radius, Vector trajectory, int scorchSphere, int displacementSphere, long moveticks) {
        super(Material.PACKED_ICE, core, radius, trajectory, scorchSphere, displacementSphere, moveticks);
//        logger.log(Level.INFO, "Ice Meteorite spawned {0}[{1}] {2}", new Object[]{core, radius, trajectory});
    }

    @Override
    public void display() {

    }

    @Override
    public void subsubTick() {

    }

    @Override
    public void subCleanup() {

    }

    @Override
    public void subDeath() {
        Vector core = getCore().sub(0, getRadius(), 0);
        if (core.getIntY() <= 0) {
            core = new Vector(core.x, 1, core.y, getWorld());
        }

        Location coreloc = core.sub(0, getRadius(), 0).asLocation();
        Block block = coreloc.getBlock();
        int search = 0;
        while (block.getType() == Material.AIR && search < 50) {
            search++;
            block = block.getRelative(BlockFace.DOWN);
        }

        block = block.getRelative(BlockFace.UP);
        Vector v = new Vector(block.getLocation());
        for (int y = 0; y < 2; y++) {
            for (int x = -getScorchSphere(); x <= getScorchSphere(); x++) {
                for (int z = -getScorchSphere(); z <= getScorchSphere(); z++) {
                    if (Cache.isAir(v.add(x, y, z))) {
                        Block rel = block.getRelative(x, y, z);
                        if (rel.getType() == Material.AIR) {
                            rel.setType(Material.LAVA);
                        }
                    }
                }
            }
        }
        //Cast deposit rays:

        for (int i = 0; i < 10; i++) {
            Vector ray = new Vector(nextDouble(), nextDouble(), nextDouble(), getWorld());

            for (int j = getExplosionRadius() - 10; j < getExplosionRadius() + 10; j++) {
                Vector buffer = ray.scale(j);
                if (!Cache.isNoMod(buffer) && !Cache.isAir(buffer)) {
                    Block tgt = block.getRelative((int) buffer.x, (int) buffer.y, (int) buffer.z);

                    for (int m = 0; m < 10; m++) {
                        tgt.getRelative(nextInt(5), nextInt(5), nextInt(5)).setType(getMaterial());
                    }
                }

            }
        }
    }

    private int nextInt(int bound) {
        return rand.nextInt(bound) * (rand.nextBoolean() ? -1 : 1);
    }

    private double nextDouble() {
        return rand.nextDouble() * (rand.nextBoolean() ? -1 : 1);
    }

    private static double randAngle() {
        return rand.nextDouble() * Math.PI * 2;
    }

    @Override
    public String stats() {
        return "Displaced: " + getDisplaced();
    }

    @Override
    protected void subOnDeath() {
        
    }

    @Override
    public void cleanup() {
        
    }

}
