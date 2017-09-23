/*
 * Copyright (C) 2017. Miroslav Kopecky
 * This RpiLegoController.java  is part of robo4j.
 * path: /Users/miroslavkopecky/GiTHub_MiroKopecky/robo4j-rpi-lego-controller/src/main/java/com/robo4j/rpi/lego/controller/controller/RpiLegoController.java
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
import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.rpi.lego.controller.codec.LegoButtonPlateCodec;
import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.util.RoboHttpUtils;
import com.robo4j.units.rpi.lcd.AdafruitButtonEnum;
import com.robo4j.util.StringConstants;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class RpiLegoController extends RoboUnit<AdafruitButtonEnum> {
    private final LegoButtonPlateCodec codec = new LegoButtonPlateCodec();
    private String targetOut;
    private String client;
    private String clientUri;

    public RpiLegoController(RoboContext context, String id) {
        super(AdafruitButtonEnum.class, context, id);
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
    public void onMessage(AdafruitButtonEnum message) {
        processAdaruitMessage(message);
    }

    // Private Methods
    private void sendClientMessage(RoboContext ctx, String message) {
        ctx.getReference(targetOut).sendMessage(message);
    }

    private void processAdaruitMessage(AdafruitButtonEnum message) {
        switch (message) {
            case RIGHT:
                sendClientMessage(getContext(), RoboHttpUtils.createRequest(HttpMethod.POST, client, clientUri, codec.encode("left")));
                break;
            case LEFT:
                sendClientMessage(getContext(), RoboHttpUtils.createRequest(HttpMethod.POST,client, clientUri, codec.encode("right")));
                break;
            case UP:
                sendClientMessage(getContext(), RoboHttpUtils.createRequest(HttpMethod.POST,client, clientUri, codec.encode("move")));
                break;
            case DOWN:
                sendClientMessage(getContext(), RoboHttpUtils.createRequest(HttpMethod.POST,client, clientUri, codec.encode("back")));
                break;
            case SELECT:
                sendClientMessage(getContext(), RoboHttpUtils.createRequest(HttpMethod.POST,client, clientUri, codec.encode("stop")));
                break;
            default:
                SimpleLoggingUtil.error(getClass(), "no such message: " + message);
                break;
        }
    }
}
