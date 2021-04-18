package clientname.mods.impl;

import clientname.mods.*;
import java.util.*;
import clientname.gui.hud.*;
import org.lwjgl.opengl.*;
import clientname.*;
import org.lwjgl.input.*;
import java.awt.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import net.minecraft.client.settings.*;

public class ModKeystrokes extends ModDraggable
{
    private KeystrokesMode mode;
    private List<Long> clicksLMB;
    private boolean wasPressedLMB;
    private long lastPressedLMB;
    private List<Long> clicksRMB;
    private boolean wasPressedRMB;
    private long lastPressedRMB;
    
    public ModKeystrokes() {
        this.mode = KeystrokesMode.WASD_SPRINT_MOUSE;
        this.clicksLMB = new ArrayList<Long>();
        this.clicksRMB = new ArrayList<Long>();
    }
    
    public void setMode(final KeystrokesMode mode) {
        this.mode = mode;
    }
    
    @Override
    public int getWidth() {
        return this.mode.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.mode.getHeight();
    }
    
    private int getCPSLMB() {
        final long time = System.currentTimeMillis();
        this.clicksLMB.removeIf(aLong -> aLong + 1000L < time);
        return this.clicksLMB.size();
    }
    
    private int getCPSRMB() {
        final long time = System.currentTimeMillis();
        this.clicksRMB.removeIf(aLong -> aLong + 1000L < time);
        return this.clicksRMB.size();
    }
    
    @Override
    public void render(final ScreenPosition pos) {
        GL11.glPushMatrix();
        if (Client.ModKeystrokes) {
            final boolean pressedLMB = Mouse.isButtonDown(0);
            if (pressedLMB != this.wasPressedLMB) {
                this.lastPressedLMB = System.currentTimeMillis();
                if (this.wasPressedLMB = pressedLMB) {
                    this.clicksLMB.add(this.lastPressedLMB);
                }
            }
            Gui.drawRect(pos.getAbsoluteX() + Key.LMBCPS.getX(), pos.getAbsoluteY() + Key.LMBCPS.getY(), pos.getAbsoluteX() + Key.LMBCPS.getX() + Key.LMBCPS.getWidth(), pos.getAbsoluteY() + Key.LMBCPS.getY() + Key.LMBCPS.getHeight(), new Color(0, 0, 0, 142).getRGB());
            ModKeystrokes.font.drawString(new StringBuilder(String.valueOf(this.getCPSLMB())).toString(), pos.getAbsoluteX() + Key.LMBCPS.getX() + Key.LMBCPS.getWidth() / 2 - ModKeystrokes.font.getStringWidth("000") / 2, pos.getAbsoluteY() + Key.LMBCPS.getY() + Key.LMBCPS.getHeight() / 2 - 4, -1);
            final boolean pressedRMB = Mouse.isButtonDown(1);
            if (pressedRMB != this.wasPressedRMB) {
                this.lastPressedRMB = System.currentTimeMillis();
                if (this.wasPressedRMB = pressedRMB) {
                    this.clicksRMB.add(this.lastPressedRMB);
                }
            }
            Gui.drawRect(pos.getAbsoluteX() + Key.RMBCPS.getX(), pos.getAbsoluteY() + Key.RMBCPS.getY(), pos.getAbsoluteX() + Key.RMBCPS.getX() + Key.RMBCPS.getWidth(), pos.getAbsoluteY() + Key.RMBCPS.getY() + Key.RMBCPS.getHeight(), new Color(0, 0, 0, 142).getRGB());
            ModKeystrokes.font.drawString(new StringBuilder(String.valueOf(this.getCPSRMB())).toString(), pos.getAbsoluteX() + Key.RMBCPS.getX() + Key.RMBCPS.getWidth() / 2 - ModKeystrokes.font.getStringWidth("000") / 2, pos.getAbsoluteY() + Key.RMBCPS.getY() + Key.RMBCPS.getHeight() / 2 - 4, -1);
            Key[] keys;
            for (int length = (keys = this.mode.getKeys()).length, i = 0; i < length; ++i) {
                final Key key = keys[i];
                if (key != Key.LMBCPS && key != Key.RMBCPS) {
                    final int textWidth = ModKeystrokes.font.getStringWidth(key.getName());
                    Gui.drawRect(pos.getAbsoluteX() + key.getX(), pos.getAbsoluteY() + key.getY(), pos.getAbsoluteX() + key.getX() + key.getWidth(), pos.getAbsoluteY() + key.getY() + key.getHeight(), key.isDown() ? new Color(255, 255, 255, 102).getRGB() : new Color(0, 0, 0, 102).getRGB());
                    ModKeystrokes.font.drawString(key.getName(), pos.getAbsoluteX() + key.getX() + key.getWidth() / 2 - textWidth / 2, pos.getAbsoluteY() + key.getY() + key.getHeight() / 2 - 4, key.isDown() ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    public enum KeystrokesMode
    {
        WASD("WASD", 0, new Key[] { Key.W, Key.A, Key.S, Key.D }), 
        WASD_Mouse("WASD_Mouse", 1, new Key[] { Key.W, Key.A, Key.S, Key.D, Key.LMB, Key.RMB }), 
        WASD_SPRINT("WASD_SPRINT", 2, new Key[] { Key.W, Key.A, Key.S, Key.D, new Key("Sprint", Minecraft.getMinecraft().gameSettings.keyBindSprint, 1, 41, 58, 18) }), 
        WASD_SPRINT_MOUSE("WASD_SPRINT_MOUSE", 3, new Key[] { Key.W, Key.A, Key.S, Key.D, Key.LMB, Key.RMB, Key.RMBCPS, Key.LMBCPS });
        
        private final Key[] keys;
        private int width;
        private int height;
        
        private KeystrokesMode(final String s, final int n, final Key... keysIn) {
            this.width = 0;
            this.height = 0;
            this.keys = keysIn;
            Key[] keys;
            for (int length = (keys = this.keys).length, i = 0; i < length; ++i) {
                final Key key = keys[i];
                this.width = Math.max(this.width, key.getX() + key.getWidth());
                this.height = Math.max(this.height, key.getY() + key.getHeight());
            }
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public Key[] getKeys() {
            return this.keys;
        }
    }
    
    private static class Key
    {
        private static final Key W;
        private static final Key A;
        private static final Key S;
        private static final Key D;
        private static final Key LMB;
        private static final Key RMB;
        private static final Key LMBCPS;
        private static final Key RMBCPS;
        private final String name;
        private final KeyBinding keyBind;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        
        static {
            W = new Key("W", Minecraft.getMinecraft().gameSettings.keyBindForward, 21, 1, 18, 18);
            A = new Key("A", Minecraft.getMinecraft().gameSettings.keyBindLeft, 1, 21, 18, 18);
            S = new Key("S", Minecraft.getMinecraft().gameSettings.keyBindBack, 21, 21, 18, 18);
            D = new Key("D", Minecraft.getMinecraft().gameSettings.keyBindRight, 41, 21, 18, 18);
            LMB = new Key("LMB", Minecraft.getMinecraft().gameSettings.keyBindAttack, 1, 41, 28, 18);
            RMB = new Key("RMB", Minecraft.getMinecraft().gameSettings.keyBindUseItem, 31, 41, 28, 18);
            LMBCPS = new Key("0", Minecraft.getMinecraft().gameSettings.keyBindAttack, 1, 61, 28, 18);
            RMBCPS = new Key("0", Minecraft.getMinecraft().gameSettings.keyBindUseItem, 31, 61, 28, 18);
        }
        
        public Key(final String name, final KeyBinding keyBind, final int x, final int y, final int width, final int height) {
            this.name = name;
            this.keyBind = keyBind;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public boolean isDown() {
            return this.keyBind.isKeyDown();
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public String getName() {
            return this.name;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
    }
}
