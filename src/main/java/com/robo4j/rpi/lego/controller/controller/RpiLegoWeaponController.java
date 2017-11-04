/*
 * Copyright (C) 2017. Miroslav Kopecky
 * This RpiLegoWeaponController.java  is part of robo4j.
 * path: /Users/miroslavkopecky/GiTHub_MiroKopecky/robo4j-rpi-lego-controller/src/main/java/com/robo4j/rpi/lego/controller/controller/RpiLegoWeaponController.java
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

package com.robo4j.rpi.lego.controller.controller;

import com.robo4j.ConfigurationException;
import com.robo4j.RoboContext;
import com.robo4j.RoboUnit;
import com.robo4j.configuration.Configuration;
import com.robo4j.hw.rpi.pad.LF710Button;
import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.codec.SimpleCommand;
import com.robo4j.socket.http.codec.SimpleCommandCodec;
import com.robo4j.socket.http.util.RoboHttpUtils;
import com.robo4j.util.StringConstants;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class RpiLegoWeaponController extends RoboUnit<LF710Button> {

    private SimpleCommandCodec codec = new SimpleCommandCodec();
    private String targetOut;
    private String client;
    private String clientUri;
    private boolean pressed = false;

    public RpiLegoWeaponController(RoboContext context, String id) {
        super(LF710Button.class, context, id);
    }

    @Override
    protected void onInitialization(Configuration configuration) throws ConfigurationException {
        targetOut = configuration.getString("targetOut", null);
        String tmpClient = configuration.getString("client", null);

        if (tmpClient == null || targetOut == null) {
            throw ConfigurationException.createMissingConfigNameException("target, client");
        }
        String clientPort = configuration.getString("clientPort", null);
        client = tmpClient.concat(":").concat(clientPort);
        clientUri = configuration.getString("clientUri", StringConstants.EMPTY);
    }

    @Override
    public void onMessage(LF710Button message) {
        pressed = !pressed;
        processLegoWeaponMessage(getContext(), pressed);
    }


    //Private Methods
    private void processLegoWeaponMessage(RoboContext ctx, boolean pressed) {

        String command = pressed ? "forward" : "stop";
        String message = RoboHttpUtils.createRequest(HttpMethod.POST, client, clientUri, codec.encode(new SimpleCommand(command)));
        ctx.getReference(targetOut).sendMessage(message);
    }
}
