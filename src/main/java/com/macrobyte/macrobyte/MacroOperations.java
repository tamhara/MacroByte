package com.macrobyte.macrobyte;


import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.List;
import java.awt.event.KeyEvent;

public class MacroOperations {

    List<String> keys; // Save keys here
    List<String> actionOrder;
    int loopTime;
    int sleepTime;
    HashMap<String, Integer> coordinates;

    Robot robot;

    /**
     * Converts the text that represents the key ("A", "ENTER", "CTRL", etc) to the key code
     * (VK_*) that java.awt.Robot understands
     *
     * So when the user chooses "Simulate Key" in the macro, we can use Robot.keyPress()/keyRelease()
     * with the correct code
     * @param key String that represent the key we're going to map
     * @return {@link Integer} with the corresponding virtual key code
     *         (for example {@link java.awt.event.KeyEvent#VK_ENTER}),
     *         or {@code null} if there's not a valid map
     */
    private Integer mapKey(String key) {
        // If the String is blank or null, there's nothing to map
        if (key == null || key.isBlank()) return null;
        // Clear spaces and turn into uppercase so we don't have problems with the comparison
        String s = key.trim().toUpperCase();

        // Switch to compare the text with known names of keys and return the corresponding VK_* constant
        switch (s){
            case "ENTER":
            case "RETURN":
                return KeyEvent.VK_ENTER;
            case "BACKSPACE": return KeyEvent.VK_BACK_SPACE;
            case "TAB": return KeyEvent.VK_TAB;
            case "ESCAPE": return KeyEvent.VK_ESCAPE;
            case "SPACE":
            case "SPACEBAR":
                return KeyEvent.VK_SPACE;
            case "SHIFT":
                return KeyEvent.VK_SHIFT;
            case "CTRL":
                case "CONTROL":
                    return KeyEvent.VK_CONTROL;
                    case "ALT":
                        return KeyEvent.VK_ALT;
                        case "ALTGRAPH":
                            return KeyEvent.VK_ALT_GRAPH;
            case "META":
                case "WINDOWS":
            case "COMMAND":
                return KeyEvent.VK_META;

            case "UP":
            case "ARROW UP":
                return KeyEvent.VK_UP;
                case "DOWN":
                    case "ARROW DOWN":
                    return KeyEvent.VK_DOWN;

                    case "LEFT":
                        case "ARROW LEFT":
                            return KeyEvent.VK_LEFT;

                            case "RIGHT":
                                case "ARROW RIGHT":
                                    return KeyEvent.VK_RIGHT;

            case "DELETE":
                return KeyEvent.VK_DELETE;

            case "HOME":
                return KeyEvent.VK_HOME;

                case "END":
                    return KeyEvent.VK_END;

            case "PAGE UP":
            case "PAGE_UP":
                return KeyEvent.VK_PAGE_UP;

                case "PAGE DOWN":
                    case "PAGE_DOWN":
                        return KeyEvent.VK_PAGE_DOWN;

            case "INSERT":
                return KeyEvent.VK_INSERT;

                case "CAPS_LOCK":
            case "CAPSLOCK":
                return KeyEvent.VK_CAPS_LOCK;

            case "F1":
                return KeyEvent.VK_F1;
                case "F2":
                    return KeyEvent.VK_F2;
                    case "F3":
                        return KeyEvent.VK_F3;
                        case "F4":
                            return KeyEvent.VK_F4;
                            case "F5":
                                return KeyEvent.VK_F5;
                                case "F6":
                                    return KeyEvent.VK_F6;
                                    case "F7":
                                        return KeyEvent.VK_F7;
                                        case "F8":
                                            return KeyEvent.VK_F8;
                                            case "F9":
                                                return KeyEvent.VK_F9;
                                                case "F10":
                                                    return KeyEvent.VK_F10;
                                                    case "F11":
                                                        return KeyEvent.VK_F11;
                                                        case "F12":
                                                            return KeyEvent.VK_F12;
        }

        // If it's not any of those words
        // See if it's just one character (letter, number, symbol)
        if (s.length() == 1){
            // Get the VK code for that character
            int code = KeyEvent.getExtendedKeyCodeForChar(s.charAt(0));
            // If it's valid, return it, if not, return null
            return (code != KeyEvent.VK_UNDEFINED) ? code : null;
        }

        // If it's not any of those things, return null (it's not mapped)
        return null;

    }


    public MacroOperations() {


        try {
            robot = new Robot();
        } catch (AWTException a) {
            System.out.println("Something went wrong");
        }


    }

    /**
     * Executes the macro following the instructions and parameters configured in the interface
     * Repeats the sequence as many times as the user wants
     *
     * Inside the first loop, looks for every action and executes the consequent simulation using java.awt.Robot
     */
    public void runMacro() {
        this.coordinates = HelloApplication.controller.getCoordinates();
        actionOrder = HelloApplication.controller.getActions();
        loopTime = HelloApplication.controller.loopField();
        // Save key list that we'll use in the actions "Simulate Key"
        this.keys = HelloApplication.controller.getKeys();

        HelloApplication.controller.notifyUser();
        for (int i = 0; i < loopTime; i++) {
            int track = 0;
            int keyTrack = 0; // Index for keys
            for (String s : actionOrder) {
                String action = s.strip();
                if (action.equals("Left Click")) {
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                } else if (action.equals("Right Click")) {
                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);

                } else if (action.equals("Sleep")) {
                    sleepTime = HelloApplication.controller.getSleep();
                    try {
                        Thread.sleep(sleepTime * 1000L);
                    } catch (Exception e) {
                        System.out.println("Something went wrong.");
                    }
                } else if (action.equals("Move Cursor")) {
                    robot.mouseMove(coordinates.get("xCoordinate" + track), coordinates.get("yCoordinate" + track));
                    track++;

                } else if (action.equals("Simulate Key")) {
                    // If there's keys saved and we're not out of the list
                    if (keys != null && keyTrack < keys.size()) {
                        // Get next key to simulate and +1 the index
                        String keyStr = keys.get(keyTrack++);
                        // Convert key text to it's VK code
                        Integer vk = mapKey(keyStr);
                        // If we find the VK code
                        if (vk != null) {
                            // Press key
                            robot.keyPress(vk);
                            // Release key
                            robot.keyRelease(vk);
                            try {
                                Thread.sleep(30); // Short pause
                            } catch (Exception e) {
                                System.out.println("Something went wrong.");
                            }
                        } else {
                            System.out.println("Unmapped key: " + keyStr);
                        }
                    }
                } else {
                    System.out.println("No key provided for 'Simulate Key' action at index " + keyTrack)
                }

            }
        }

    }
}
