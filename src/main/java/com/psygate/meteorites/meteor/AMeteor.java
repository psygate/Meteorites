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

import com.psygate.meteorites.math.Vector;
import java.util.ArrayList;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class AMeteor implements IMeteor {

    private final Material meteorMaterial;
    private Vector position;
    private final Vector trajectory;
    protected final double radius;
    private final Vector[] vecs;

    public AMeteor(Material meteorMaterial, Location spawn, Vector trajectory, double radius) {
        this.meteorMaterial = Objects.requireNonNull(meteorMaterial);
        this.position = new Vector(Objects.requireNonNull(spawn));

        if (trajectory.y >= 0) {
            throw new IllegalArgumentException("Meteorites cannot fly up.");
        }

        this.trajectory = Objects.requireNonNull(trajectory);

        if (radius <= 0) {
            throw new IllegalArgumentException("Meteorites cannot have a 0 radius.");
        }

        this.radius = radius;

        ArrayList<Vector> genVecs = new ArrayList<>();
        for (double x = -radius; x <= radius; x++) {
            for (double y = -radius; y <= radius; y++) {
                for (double z = -radius; z <= radius; z++) {
                    final Vector vec = new Vector(x, y, z, spawn.getWorld());

                    if (vec.isWithin(radius, 1)) {
                        genVecs.add(vec);
                    }
                }
            }
        }

        this.vecs = genVecs.toArray(new Vector[0]);
    }

    @Override
    public final void tick() {
        cleanup();
        position = position.add(trajectory);
        subTick();
        display();
    }

    @Override
    public void cleanup() {
        for (final Vector v : vecs) {
//            System.out.println("Revmoving core. "+v.add(position));
            v.add(position).asSafeBlock().setType(Material.AIR);
        }
        subCleanup();
    }

    private void display() {
        subDisplay();
        for (final Vector v : vecs) {
            v.add(position).asSafeBlock().setType(meteorMaterial);
        }
    }

    protected void subTick() {

    }

    protected void subDisplay() {

    }

    protected void subCleanup() {

    }

    public Vector getPosition() {
        return position;
    }

    @Override
    public boolean isDead() {
        return position.y <= 10;
    }

    @Override
    public void onDeath() {
        cleanup();
    }

    @Override
    public final long getTickDelay() {
        return 1;
    }
    
    public Material getMaterial() {
        return meteorMaterial;
    }

}
