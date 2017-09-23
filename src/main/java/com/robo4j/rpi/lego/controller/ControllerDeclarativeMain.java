/*
 * Copyright (C) 2017. Miroslav Kopecky
 * This ControllerDeclarativeMain.java  is part of robo4j.
 * path: /Users/miroslavkopecky/GiTHub_MiroKopecky/robo4j-rpi-lego-controller/src/main/java/com/robo4j/rpi/lego/controller/ControllerDeclarativeMain.java
 * module: robo4j-rpi-lego-controller_main
 *
 * robo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * robo4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.rpi.lego.controller;

import com.robo4j.RoboBuilder;
import com.robo4j.RoboContext;
import com.robo4j.util.SystemUtil;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class ControllerDeclarativeMain {
    private static final String ROBO4J_CONFIGURATION = "robo4j.xml";
    private static String configurationFileName;

    public static void main(String[] args) throws Exception {
        switch (args.length){
            case 1:
                configurationFileName = args[0];
                break;
            default:
                System.out.println("default configuration");
                break;
        }

        InputStream isConfig = configurationFileName == null ?
                Thread.currentThread().getContextClassLoader().getResourceAsStream(ROBO4J_CONFIGURATION) :
                Files.newInputStream(Paths.get(configurationFileName));

        RoboBuilder builder = new RoboBuilder()
                .add(isConfig);
        RoboContext system = builder.build();
        system.start();
        System.out.println("RoboSystem after start:");
        System.out.println(SystemUtil.printStateReport(system));

        System.out.println("Press enter to quit!");
        System.in.read();
        system.shutdown();

    }
}
