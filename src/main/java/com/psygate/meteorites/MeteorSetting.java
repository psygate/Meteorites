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
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class MeteorSetting {

    private final String name;
    private final int radius;
    private final Material material;
    private final boolean scorch;
    private final boolean displace;
    private final int displaceModifier;
    private final int scorchRadius;
    private final int displaceRadius;
    private final double spawnChance;

    public MeteorSetting(String name, int radius, Material material, boolean scorch, boolean displace, int displaceModifier, int scorchRadius, int displaceRadius, double spawnChance) {
        this.name = name;
        this.radius = radius;
        this.material = material;
        this.scorch = scorch;
        this.displace = displace;
        this.displaceModifier = displaceModifier;
        this.scorchRadius = scorchRadius;
        this.displaceRadius = displaceRadius;
        this.spawnChance = spawnChance;
    }

    public String getName() {
        return name;
    }

    public int getRadius() {
        return radius;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isScorch() {
        return scorch;
    }

    public boolean isDisplace() {
        return displace;
    }

    public int getDisplaceModifier() {
        return displaceModifier;
    }

    public int getScorchRadius() {
        return scorchRadius;
    }

    public int getDisplaceRadius() {
        return displaceRadius;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    public AMeteor asMeteor(final Location spawnLocation, final Vector trajectory) {
        if (displace) {
            return new ADisplacementMeteor(material, spawnLocation, trajectory, radius, scorchRadius, displaceRadius);
        } else if (scorch) {
            return new AScorchingMeteor(material, spawnLocation, trajectory, radius, scorchRadius);
        } else {
            return new AMeteor(material, spawnLocation, trajectory, radius);
        }
    }

    @Override
    public String toString() {
        return "MeteorSetting{" + "name=" + name + ", radius=" + radius + ", material=" + material + ", scorch=" + scorch + ", displace=" + displace + ", displaceModifier=" + displaceModifier + ", scorchRadius=" + scorchRadius + ", displaceRadius=" + displaceRadius + '}';
    }

}
