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

import com.psygate.meteorites.math.IntVector;
import com.psygate.meteorites.math.Vector;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class SafeBlock {

    protected final IntVector v;
    protected Block block;
    protected boolean loaded = false;

    public SafeBlock(IntVector v) {
        this.v = v;
    }

    public SafeBlock(Vector v) {
        this.v = v.asIntVector();
    }

    protected void load() {
        if (!loaded && block == null && !Cache.isNoMod(v) && !Cache.isReinforced(v)) {
            block = this.v.asLocation().getBlock();
        }
        loaded = true;
    }

    public SafeNotAirBlock noAir() {
        return new SafeNotAirBlock(v);
    }

    public boolean hasMaterial(Material m) {
        load();
        if (block == null) {
            return false;
        } else {
            return block.getType().equals(m);
        }
    }

    public boolean isValid() {
        load();
        return block != null;
    }

    public void setType(SafeNotAirBlock source) {
        if (source.isValid() && this.isValid()) {
            block.setType(source.block.getType());
        }
    }

    public Material getType() {
        load();
        return (block == null) ? Material.AIR : block.getType();
    }

    public boolean setType(Material m) {
        load();
        if (block != null) {
            block.setType(m);
            return true;
        } else {
            return false;
        }
    }

//    public Material getType() {
//        load();
//
//        if (block != null) {
//            return block.getType();
//        } else {
//            return Material.AIR;
//        }
//    }
}
