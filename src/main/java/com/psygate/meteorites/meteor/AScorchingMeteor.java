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

import com.psygate.meteorites.Conf;
import com.psygate.meteorites.math.Vector;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class AScorchingMeteor extends AMeteor {

    private final double scorchRadius;
    private final Vector[] scorchVecs;

    public AScorchingMeteor(Material meteorMaterial, Location spawn, Vector trajectory, double radius, double scorchRadius) {
        super(meteorMaterial, spawn, trajectory, radius);
        this.scorchRadius = scorchRadius;

        ArrayList<Vector> genVecs = new ArrayList<>();
        for (double x = -scorchRadius; x <= scorchRadius; x++) {
            for (double y = -scorchRadius; y <= scorchRadius; y++) {
                for (double z = -scorchRadius; z <= scorchRadius; z++) {
                    final Vector vec = new Vector(x, y, z, spawn.getWorld());

                    if (vec.isWithin(scorchRadius, 1)) {
                        genVecs.add(vec);
                    }
                }
            }
        }

        this.scorchVecs = genVecs.toArray(new Vector[0]);
    }

    @Override
    protected final void subTick() {
        for (final Vector v : scorchVecs) {
            Vector q = v.add(getPosition());
            if (Cache.isScorched(q)) {
                continue;
            } else {
                Cache.setScorched(q);
            }
            SafeBlock b = q.asSafeBlock();
            if (Conf.getScorchMap().containsKey(b.getType())) {
                b.setType(Conf.getScorchMap().get(b.getType()));
            }
        }
        subTick1();
    }

    protected void subTick1() {

    }

}
