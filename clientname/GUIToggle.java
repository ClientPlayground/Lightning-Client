package clientname;

import net.minecraft.client.resources.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.realms.*;
import net.minecraft.client.gui.achievement.*;
import net.minecraft.client.gui.*;
import java.io.*;

public class GUIToggle extends GuiScreen
{
    private final GuiScreen field_146598_a;
    private static String Cape;
    private static String Wings;
    private static String ToggleSprint;
    private static String Halo;
    private static String FPS;
    private static String Keystrokes;
    private static String Ping;
    private static String ArmorStatus;
    private static String Time;
    private static String PotionStatus;
    private static String ModPosition;
    private static boolean firstStart;
    private GuiButton field_146596_f;
    private GuiButton field_146597_g;
    private String field_146599_h;
    private boolean field_146600_i;
    
    static {
        GUIToggle.Cape = "Cape";
        GUIToggle.Wings = "Wings";
        GUIToggle.ToggleSprint = "Toggle Sprint";
        GUIToggle.Halo = "Halo";
        GUIToggle.FPS = "FPS";
        GUIToggle.Keystrokes = "Keystrokes";
        GUIToggle.Ping = "Ping";
        GUIToggle.ArmorStatus = "Armor Status";
        GUIToggle.Time = "Time";
        GUIToggle.PotionStatus = "Potion Status";
        GUIToggle.ModPosition = "Position";
        GUIToggle.firstStart = false;
    }
    
    public GUIToggle(final GuiScreen p_i1055_1_) {
        this.field_146599_h = "survival";
        this.field_146598_a = p_i1055_1_;
    }
    
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1003, 279, 70, 80, 20, I18n.format(GUIToggle.Cape, new Object[0])));
        this.buttonList.add(new GuiButton(1001, 279, 94, 80, 20, I18n.format(GUIToggle.Wings, new Object[0])));
        this.buttonList.add(new GuiButton(1002, 172, 70, 80, 20, I18n.format(GUIToggle.ToggleSprint, new Object[0])));
        this.buttonList.add(new GuiButton(1004, 279, 118, 80, 20, I18n.format(GUIToggle.Halo, new Object[0])));
        this.buttonList.add(new GuiButton(1005, 380, 70, 80, 20, I18n.format(GUIToggle.FPS, new Object[0])));
        this.buttonList.add(new GuiButton(1006, 380, 94, 80, 20, I18n.format(GUIToggle.Keystrokes, new Object[0])));
        this.buttonList.add(new GuiButton(1007, 380, 118, 80, 20, I18n.format(GUIToggle.Ping, new Object[0])));
        this.buttonList.add(new GuiButton(1008, 380, 142, 80, 20, I18n.format(GUIToggle.ArmorStatus, new Object[0])));
        this.buttonList.add(new GuiButton(1009, 380, 166, 80, 20, I18n.format(GUIToggle.Time, new Object[0])));
        this.buttonList.add(new GuiButton(1010, 380, 190, 80, 20, I18n.format(GUIToggle.PotionStatus, new Object[0])));
        this.buttonList.add(new GuiButton(1011, 380, 214, 80, 20, I18n.format(GUIToggle.ModPosition, new Object[0])));
    }
    
    private void func_146595_g() {
        this.field_146597_g.displayString = String.valueOf(I18n.format("selectWorld.gameMode", new Object[0])) + " " + I18n.format("selectWorld.gameMode." + this.field_146599_h, new Object[0]);
        this.field_146596_f.displayString = String.valueOf(I18n.format("selectWorld.allowCommands", new Object[0])) + " ";
        if (this.field_146600_i) {
            this.field_146596_f.displayString = String.valueOf(this.field_146596_f.displayString) + I18n.format("options.on", new Object[0]);
        }
        else {
            this.field_146596_f.displayString = String.valueOf(this.field_146596_f.displayString) + I18n.format("options.off", new Object[0]);
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            }
            case 1: {
                final boolean flag = this.mc.isIntegratedServerRunning();
                final boolean flag2 = this.mc.func_181540_al();
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                if (flag) {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                    break;
                }
                if (flag2) {
                    final RealmsBridge realmsbridge = new RealmsBridge();
                    realmsbridge.switchToRealms(new GuiMainMenu());
                    break;
                }
                this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                break;
            }
            case 4: {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;
            }
            case 5: {
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            }
            case 6: {
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            }
            case 7: {
                this.mc.displayGuiScreen(new GuiShareToLan(this));
                break;
            }
            case 1001: {
                Client.CosmeticWings = !Client.CosmeticWings;
                break;
            }
            case 1002: {
                Client.ToggleSprint = !Client.ToggleSprint;
                break;
            }
            case 1003: {
                Client.CosmeticCape = !Client.CosmeticCape;
                break;
            }
            case 1004: {
                Client.CosmeticHalo = !Client.CosmeticHalo;
                break;
            }
            case 8: {
                this.mc.displayGuiScreen(new GUIToggle(this));
                break;
            }
            case 1005: {
                Client.ModFPS = !Client.ModFPS;
                break;
            }
            case 1006: {
                Client.ModKeystrokes = !Client.ModKeystrokes;
                break;
            }
            case 1007: {
                Client.ModPing = !Client.ModPing;
                break;
            }
            case 1008: {
                Client.ModArmorStatus = !Client.ModArmorStatus;
                break;
            }
            case 1009: {
                Client.ModTimeShow = !Client.ModTimeShow;
                break;
            }
            case 1010: {
                Client.ModPotionstatus = !Client.ModPotionstatus;
                break;
            }
            case 1011: {
                Client.ModPosition = !Client.ModPosition;
                break;
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawString(this.fontRendererObj, String.valueOf(Client.ModFarbe) + Client.ClientName, 283, 20, -1);
        this.drawString(this.fontRendererObj, String.valueOf(Client.ModFarbe) + "Ingame Mods", 390, 54, -1);
        this.drawString(this.fontRendererObj, String.valueOf(Client.ModFarbe) + "Other Mods", 185, 54, -1);
        this.drawString(this.fontRendererObj, String.valueOf(Client.ModFarbe) + "Cosmetics", 294, 54, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
