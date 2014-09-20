/*
 *  Copyright (C) 2013 - 2014 Alexander "Evisceration" Martinz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.namelessrom.devicecontrol.objects;

import android.text.TextUtils;

import org.namelessrom.devicecontrol.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class which parses /proc/cpuinfo and prepares information ready for usage
 */
public class CpuInfo {
    public String processor;
    public String bogomips;
    public String features;
    public String hardware;

    public CpuInfo() {
        // initialize all with null
        processor = null;
        bogomips = null;
        features = null;
        hardware = null;
    }

    @Override public String toString() {
        return String.format("processor: %s, bogomips: %s, features: %s, hardware: %s",
                processor, bogomips, features, hardware);
    }

    public boolean feedWithInformation() {
        final File cpuInfoFile = new File("/proc/cpuinfo");
        if (!cpuInfoFile.exists()) {
            return false;
        }

        final ArrayList<String> list = new ArrayList<String>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(cpuInfoFile);
            br = new BufferedReader(fr);

            String aLine;
            while ((aLine = br.readLine()) != null) {
                aLine = aLine.trim();
                if (!TextUtils.isEmpty(aLine)) list.add(aLine);
            }
        } catch (IOException e) {
            Logger.e(this, "could not get cpu information", e);
            return false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) { }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception ignored) { }
            }
        }

        for (final String s : list) {
            Logger.i(this, s);
            if (s.contains("Processor")) {
                processor = getData(s);
            } else if (bogomips == null && s.contains("BogoMIPS")) {
                bogomips = getData(s);
            } else if (s.contains("Features")) {
                features = getData(s);
            } else if (s.contains("Hardware")) {
                hardware = getData(s);
            }
        }

        return true;
    }

    private String getData(final String data) {
        if (data == null) return "";

        final String[] splitted = data.split(":");
        if (splitted.length < 2) return "";

        return (splitted[1] != null ? splitted[1].trim() : "");
    }

}
