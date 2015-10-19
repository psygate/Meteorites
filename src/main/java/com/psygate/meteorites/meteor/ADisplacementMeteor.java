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
import com.psygate.meteorites.math.Vector;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class ADisplacementMeteor extends AScorchingMeteor {

    private final double displaceRadius;
    private final Vector[] displaceVecs;
    private long displaced = 0;

    public ADisplacementMeteor(Material meteorMaterial, Location spawn, Vector trajectory, double radius, double scorchRadius, double displaceRadius) {
        super(meteorMaterial, spawn, trajectory, radius, scorchRadius);

        if (displaceRadius > scorchRadius) {
            throw new IllegalArgumentException();
        }

        this.displaceRadius = displaceRadius;

        ArrayList<Vector> genVecs = new ArrayList<>();
        for (double x = -displaceRadius; x <= displaceRadius; x++) {
            for (double y = -displaceRadius; y <= displaceRadius; y++) {
                for (double z = -displaceRadius; z <= displaceRadius; z++) {
                    final Vector vec = new Vector(x, y, z, spawn.getWorld());

                    if (vec.isWithin(displaceRadius, 1)) {
                        genVecs.add(vec);
                    }
                }
            }
        }

        this.displaceVecs = genVecs.toArray(new Vector[0]);
    }

    @Override
    protected void subTick1() {
        for (Vector v : displaceVecs) {
            Vector q = v.add(getPosition());
            if (Cache.isDisplaced(q)) {
                continue;
            } else {
                Cache.setDisplaced(q);
            }

            SafeBlock selected = q.asSafeBlock();
            if (selected.getType() != Material.AIR) {
                displaced++;
            }
            selected.setType(Material.AIR);

        }
    }

    @Override
    public void onDeath() {
        super.onDeath();
        for (int x = (int) -displaceRadius * 3; x <= (int) displaceRadius * 3; x++) {
            for (int y = (int) -displaceRadius * 3; y <= (int) displaceRadius * 3; y++) {
                for (int z = (int) -displaceRadius * 3; z <= (int) displaceRadius * 3; z++) {
                    Vector v = new Vector(x, y, z, getPosition().world);
                    if (v.length() >= displaceRadius * 2 && v.shorterThanOrEqual(displaceRadius * 3)) {
                        if (Meteors.rand.nextInt(100) < 75) {
                            v.add(getPosition()).asSafeBlock().noAir().setType(Material.OBSIDIAN);
                        } else {
                            v.add(getPosition()).asSafeBlock().noAir().setType(Material.GLASS);
                        }
                    }
                }
            }
        }
        for (int x = (int) -displaceRadius * 2; x <= (int) displaceRadius * 2; x++) {
            for (int y = (int) -displaceRadius * 2; y <= (int) displaceRadius * 2; y++) {
                for (int z = (int) -displaceRadius * 2; z <= (int) displaceRadius * 2; z++) {
                    Vector v = new Vector(x, y, z, getPosition().world);
                    Vector q = v.add(getPosition());
                    if (!v.shorterThanOrEqual(displaceRadius * 2)) {
                        continue;
                    }

                    if (q.y <= getPosition().y) {
                        if (v.isWithin(displaceRadius * 2, 2)) {
                            if (q.asSafeBlock().getType() != Material.AIR) {
                                q.asSafeBlock().setType(Material.OBSIDIAN);
                            }
                        } else if (Cache.isDisplaced(q) || Cache.isScorched(q)) {
                            if (Meteors.rand.nextInt(100) < 75) {
                                v.add(getPosition()).asSafeBlock().setType(Material.STATIONARY_LAVA);
                            } else {
                                v.add(getPosition()).asSafeBlock().setType(getMaterial());
                            }
                        }
                    } else {
                        v.add(getPosition()).asSafeBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public boolean isDead() {
        return super.isDead() || displaced > radius * 1000;
    }

}
