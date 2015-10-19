/*
The MIT License (MIT)

Copyright (c) 2015 Florian

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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.psygate.meteorites.Meteors;
import com.psygate.meteorites.math.IntVector;
import com.psygate.meteorites.math.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import vg.civcraft.mc.citadel.Citadel;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class Cache {

    private static Cache instance;
    private final LoadingCache<IntVector, Boolean> nomodcache = CacheBuilder.newBuilder()
            .initialCapacity(5000)
            .maximumSize(400000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<IntVector, Boolean>() {

                @Override
                public Boolean load(IntVector key) throws Exception {
                    return key.asLocation().getBlock() == null
                    //                    || key.asLocation().getBlock().getType() == Material.AIR
                    || key.asLocation().getBlock().getType() == Material.BEDROCK;
                }
            });

    private final LoadingCache<IntVector, Boolean> aircache = CacheBuilder.newBuilder()
            .initialCapacity(5000)
            .maximumSize(400000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<IntVector, Boolean>() {

                @Override
                public Boolean load(IntVector key) throws Exception {
                    return key.asLocation().getBlock() == null
                    || key.asLocation().getBlock().getType() == Material.AIR; //                    || key.asLocation().getBlock().getType() == Material.BEDROCK;
                }
            });

    private final com.google.common.cache.Cache<IntVector, Boolean> scorchedcache = CacheBuilder.newBuilder()
            .initialCapacity(5000)
            .maximumSize(400000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();

    private final com.google.common.cache.Cache<IntVector, Boolean> displacecache = CacheBuilder.newBuilder()
            .initialCapacity(5000)
            .maximumSize(400000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();

    private final com.google.common.cache.LoadingCache<IntVector, Boolean> reinforcedcache = CacheBuilder.newBuilder()
            .initialCapacity(5000)
            .maximumSize(400000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<IntVector, Boolean>() {
                PluginManager pm = Meteors.getPlugin(Meteors.class).getServer().getPluginManager();

                @Override
                public Boolean load(IntVector key) throws Exception {
                    if (pm.getPlugin("Citadel") == null) {
                        return false;
                    } else {
                        boolean out = Citadel.getReinforcementManager().isReinforced(key.asLocation());

                        return out;

                    }
                }
            });

    private Cache() {

    }

    public static Cache cache() {
        if (instance == null) {
            instance = new Cache();
        }

        return instance;
    }

    public static boolean isNoMod(Vector v) {

        try {
            return cache().nomodcache.get(v.asIntVector());
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean isAir(Vector v) {
        try {
            return cache().aircache.get(v.asIntVector());
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean isScorched(Vector v) {
        try {
            return cache().scorchedcache.get(v.asIntVector(), new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    return Boolean.FALSE;
                }
            });
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static void setScorched(Vector v) {
        cache().scorchedcache.put(v.asIntVector(), Boolean.TRUE);
    }

    public static boolean isDisplaced(Vector v) {
        try {
            return cache().displacecache.get(v.asIntVector(), new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    return Boolean.FALSE;
                }
            });
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static void setDisplaced(Vector v) {
        cache().displacecache.put(v.asIntVector(), Boolean.TRUE);
    }

    public static boolean isReinforced(Vector v) {
        try {
            return instance.reinforcedcache.get(v.asIntVector());
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    public static boolean isNoMod(IntVector v) {

        try {
            return cache().nomodcache.get(v);
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean isAir(IntVector v) {
        try {
            return cache().aircache.get(v);
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean isScorched(IntVector v) {
        try {
            return cache().scorchedcache.get(v, new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    return Boolean.FALSE;
                }
            });
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static void setScorched(IntVector v) {
        cache().scorchedcache.put(v, Boolean.TRUE);
    }

    public static boolean isDisplaced(IntVector v) {
        try {
            return cache().displacecache.get(v, new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    return Boolean.FALSE;
                }
            });
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static void setDisplaced(IntVector v) {
        cache().displacecache.put(v, Boolean.TRUE);
    }

    public static boolean isReinforced(IntVector v) {
        try {
            return instance.reinforcedcache.get(v);
        } catch (ExecutionException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    public static void printStats() {
        System.out.print(cache().nomodcache.stats());
        System.out.print(cache().aircache.stats());
    }
}
