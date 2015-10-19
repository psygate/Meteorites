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
package com.psygate.meteorites.math;

import com.psygate.meteorites.meteor.SafeBlock;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class Vector {

    public final double x, y, z;
    public final World world;

    public Vector(double x, double y, double z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public Vector(org.bukkit.util.Vector direction, World world) {
        this(direction.getX(), direction.getY(), direction.getZ(), world);
    }

    public Vector(Location direction) {
        this(direction.getX(), direction.getY(), direction.getZ(), direction.getWorld());
    }

    public Vector add(double x, double y, double z) {
        return new Vector(this.x + x, this.y + y, this.z + z, world);
    }

    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y, this.z + v.z, world);
    }

    public Vector sub(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z, world);
    }

    public Vector sub(double x, double y, double z) {
        return new Vector(this.x - x, this.y - y, this.z - z, world);
    }

    public Vector scale(double d) {
        return new Vector(this.x * d, this.y * d, this.z * d, world);
    }

    public Location asLocation() {
        return new Location(world, x, y, z);
    }

    public int getIntX() {
        return (int) x;
    }

    public int getIntY() {
        return (int) y;
    }

    public int getIntZ() {
        return (int) z;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public SafeBlock asSafeBlock() {
        return new SafeBlock(this);
    }

    public IntVector asIntVector() {
        return new IntVector(getIntX(), getIntY(), getIntZ(), world);
    }

    public double lengthSqr() {
        return x * x + y * y + z * z;
    }

    public boolean shorterThanOrEqual(double val) {
        return lengthSqr() <= val * val;
    }

    public Vector rotateX(double d) {
        // Rx(phi) = [1     0           0        ]
        //           [0     cos(phi)    -sin(phi)]
        //           [0     sin(phi)    cos(phi) ]

        return new Vector(this.x, y * cos(d) + z * -sin(d), y * sin(d) + z * cos(d), world);
    }

    public Vector rotateY(double d) {
        // Ry(phi) = [cos(phi)  0 sin(phi)  ]
        //           [0         1    0      ]
        //           [-sin(phi) 0 cos(phi)  ]

        return new Vector(x * cos(d) + z * sin(d), y, x * -sin(d) + z * cos(d), world);
    }

    public Vector rotateZ(double d) {
        // Rz(phi) = [cos(phi)  -sin(phi)   0]
        //           [sin(phi)  cos(phi)    0]
        //           [0         0           1]

        return new Vector(x * cos(d) + y * -sin(d), x * sin(d) + y * cos(d), z, world);
    }

    public Vector unit() {
        return new Vector(x / length(), y / length(), z / length(), world);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        hash = 61 * hash + Objects.hashCode(this.world);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector other = (Vector) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        if (!Objects.equals(this.world, other.world)) {
            return false;
        }
        return true;
    }

    public boolean isWithin(double radius, double i) {
        return lengthSqr() <= (radius + i) * (radius + i) && lengthSqr() >= (radius - i) * (radius - i);
    }

}
