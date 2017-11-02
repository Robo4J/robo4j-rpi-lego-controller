/*
 * Copyright (C) 2017. Miroslav Kopecky
 * This LF710LegoController.java  is part of robo4j.
 * path: /Users/miroslavkopecky/GiTHub_MiroKopecky/robo4j-rpi-lego-controller/src/main/java/com/robo4j/rpi/lego/controller/controller/LF710LegoController.java
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
import com.robo4j.hw.rpi.pad.LF710JoystickButton;
import com.robo4j.hw.rpi.pad.LF710Message;
import com.robo4j.hw.rpi.pad.LF710State;
import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.units.rpi.lcd.AdafruitButtonEnum;

/**
 * controller receives messages from the Logitech GamePad
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class LF710LegoController extends RoboUnit<LF710Message> {

    private String target;
    private String targetWeapon;
    private String padInput;

    public LF710LegoController(RoboContext context, String id) {
        super(LF710Message.class, context, id);
    }

    @Override
    protected void onInitialization(Configuration configuration) throws ConfigurationException {
        padInput = configuration.getString("padInput", null);
        target = configuration.getString("target", null);
        targetWeapon = configuration.getString("targetWeapon", null);

        System.out.println(getClass() + " init targetWeapon: " + targetWeapon + " target: " + target);
        if (target == null) {
            throw ConfigurationException.createMissingConfigNameException("target");
        }

        if (padInput == null) {
            throw ConfigurationException.createMissingConfigNameException("padInput");
        }
    }

    @Override
    public void onMessage(LF710Message message) {
        processLF710Message(message);
    }


    //Privet Methods
    private void sendAdafruitLcdMessage(RoboContext ctx, AdafruitButtonEnum message) {
        ctx.getReference(target).sendMessage(message);
    }

    private void sendWeaponMessage(RoboContext ctx, LF710Button message){
        System.out.println(getClass().getSimpleName() + " targetWeapon: " + targetWeapon + " message: " + message);
        ctx.getReference(targetWeapon).sendMessage(message);
    }

    /**
     * process Gamepad message, convert to Adafruit Button message and send
     *
     * @param message
     *            padInput from Logitech F710 Gamepad
     */
    private void processLF710Message(LF710Message message) {
        switch (message.getPart()){
            case BUTTON:
                if(message.getInput().getName().equals("blue")){
                    System.out.println(getClass().getSimpleName() + " process BLUE");
                    sendWeaponMessage(getContext(), LF710Button.BLUE);
                } else {
                    SimpleLoggingUtil.print(getClass(), "Gamepad Buttons are not implemented: " + message);
                }
                break;
            case JOYSTICK:
                if(message.getInput() instanceof LF710JoystickButton){
                    LF710JoystickButton joystick = (LF710JoystickButton)message.getInput();
                    switch (joystick){
                        case PAD_X:
                            if(message.getState().equals(LF710State.PRESSED)){
                                if(message.getAmount() > 0){
                                    sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.LEFT);
                                } else {
                                    sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.RIGHT);
                                }
                            }
                            break;
                        case PAD_Y:
                            if(message.getState().equals(LF710State.PRESSED)){
                                if(message.getAmount() > 0){
                                    sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.DOWN);
                                } else {
                                    sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.UP);
                                }
                            }
                            break;
                        default:
                            SimpleLoggingUtil.print(getClass(), "joystick button is not implemented:" + message);
                            break;
                    }
                    if(message.getState() == LF710State.RELEASED){
                        //select currently represents stop!!
                        sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.SELECT);
                    }
                }
                break;
            default:
                SimpleLoggingUtil.error(getClass(), "unknonw Gamepad command: " + message);
        }
    }
}
