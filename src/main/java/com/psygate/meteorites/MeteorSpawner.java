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

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import static com.psygate.meteorites.Conf.getEnabledWorlds;
import com.psygate.meteorites.math.Vector;
import com.psygate.meteorites.meteor.AMeteor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author psygate (https://github.com/psygate)
 */
public class MeteorSpawner implements Runnable {

    private Gson gson = new Gson();
    private MeteorData md = new MeteorData();
    private File data;

    public MeteorSpawner(File datafolder) {
        data = new File(datafolder, "meteor_spawn_data.json");
        if (data.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(data))) {
                md = gson.fromJson(reader, MeteorData.class);
            } catch (IOException ex) {
                Logger.getLogger(MeteorSpawner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() - md.lastSpawn > Conf.spawnInterval()) {
            List<MeteorSetting> settings = Conf.getMeteorSettings();
            ArrayList<SpawnChance> chances = new ArrayList<>(settings.size());
            double startprob = 0;
            for (MeteorSetting setting : settings) {
                double endprob = startprob + setting.getSpawnChance();
                chances.add(new SpawnChance(startprob, endprob, setting));
                startprob = endprob;
            }

            double select = Meteors.rand.nextDouble();

            for (SpawnChance chance : chances) {
                if (chance.lower <= select && chance.upper > select) {
                    Meteors.getPlugin(Meteors.class).getLogger().log(Level.INFO, "Spawning meteor. ({0})", chance.attachment.getName());
                    List<World> worlds = getEnabledWorlds();
                    World w = worlds.get(Meteors.rand.nextInt(worlds.size()));
                    Meteors.getPlugin(Meteors.class).getLogger().log(Level.INFO, "Spawning meteor @{0}", w);
                    int y = w.getMaxHeight();
                    int x = w.getWorldBorder().getCenter().getBlockX() + Meteors.rand.nextInt((int) w.getWorldBorder().getSize()) * (Meteors.rand.nextBoolean() ? -1 : 1);
                    int z = w.getWorldBorder().getCenter().getBlockZ() + Meteors.rand.nextInt((int) w.getWorldBorder().getSize()) * (Meteors.rand.nextBoolean() ? -1 : 1);

                    Location loc = new Location(w, x, y, z);
                    double vy = -1 * Meteors.rand.nextDouble() * 2 + 0.1;

                    if (vy > 0) {
                        vy *= -1;
                    }
                    Vector vec = new Vector(Meteors.rand.nextDouble() * 2 + 0.5, vy, Meteors.rand.nextDouble() * 2 + 0.5, w);
                    Meteors.getPlugin(Meteors.class).getLogger().log(Level.INFO, "Spawning meteor @{0} Trajectory: {1}", new Object[]{loc, vec});
                    AMeteor meteor = chance.attachment.asMeteor(loc, vec);
                    Meteors.getPlugin(Meteors.class).addMeteor(meteor);
                    md.lastSpawn = System.currentTimeMillis();
                    md.lastSpawnName = chance.attachment.getName();

                    try (FileWriter out = new FileWriter(data)) {
                        String json = gson.toJson(md);
                        out.write(json);
                    } catch (IOException ex) {
                        Logger.getLogger(MeteorSpawner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public static class MeteorData {

        private long lastSpawn = 0;
        private String lastSpawnName = "";

        public MeteorData() {
        }

        public long getLastSpawn() {
            return lastSpawn;
        }

        public void setLastSpawn(long lastSpawn) {
            this.lastSpawn = lastSpawn;
        }

        public String getLastSpawnName() {
            return lastSpawnName;
        }

        public void setLastSpawnName(String lastSpawnName) {
            this.lastSpawnName = lastSpawnName;
        }
    }

    private static class SpawnChance {

        private double lower;
        private double upper;
        private MeteorSetting attachment;

        public SpawnChance(double lower, double upper, MeteorSetting attachment) {
            this.lower = lower;
            this.upper = upper;
            this.attachment = attachment;
        }
    }

}
