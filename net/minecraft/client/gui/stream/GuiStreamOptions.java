package net.minecraft.client.gui.stream;

import net.minecraft.client.settings.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import java.io.*;
import net.minecraft.util.*;

public class GuiStreamOptions extends GuiScreen
{
    private static final GameSettings.Options[] field_152312_a;
    private static final GameSettings.Options[] field_152316_f;
    private final GuiScreen parentScreen;
    private final GameSettings field_152318_h;
    private String field_152319_i;
    private String field_152313_r;
    private int field_152314_s;
    private boolean field_152315_t;
    
    static {
        field_152312_a = new GameSettings.Options[] { GameSettings.Options.STREAM_BYTES_PER_PIXEL, GameSettings.Options.STREAM_FPS, GameSettings.Options.STREAM_KBPS, GameSettings.Options.STREAM_SEND_METADATA, GameSettings.Options.STREAM_VOLUME_MIC, GameSettings.Options.STREAM_VOLUME_SYSTEM, GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR, GameSettings.Options.STREAM_COMPRESSION };
        field_152316_f = new GameSettings.Options[] { GameSettings.Options.STREAM_CHAT_ENABLED, GameSettings.Options.STREAM_CHAT_USER_FILTER };
    }
    
    public GuiStreamOptions(final GuiScreen parentScreenIn, final GameSettings p_i1073_2_) {
        this.field_152315_t = false;
        this.parentScreen = parentScreenIn;
        this.field_152318_h = p_i1073_2_;
    }
    
    @Override
    public void initGui() {
        int i = 0;
        this.field_152319_i = I18n.format("options.stream.title", new Object[0]);
        this.field_152313_r = I18n.format("options.stream.chat.title", new Object[0]);
        GameSettings.Options[] field_152312_a;
        for (int length = (field_152312_a = GuiStreamOptions.field_152312_a).length, j = 0; j < length; ++j) {
            final GameSettings.Options gamesettings$options = field_152312_a[j];
            if (gamesettings$options.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options));
            }
            else {
                this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options, this.field_152318_h.getKeyBinding(gamesettings$options)));
            }
            ++i;
        }
        if (i % 2 == 1) {
            ++i;
        }
        this.field_152314_s = this.height / 6 + 24 * (i >> 1) + 6;
        i += 2;
        GameSettings.Options[] field_152316_f;
        for (int length2 = (field_152316_f = GuiStreamOptions.field_152316_f).length, k = 0; k < length2; ++k) {
            final GameSettings.Options gamesettings$options2 = field_152316_f[k];
            if (gamesettings$options2.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options2.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options2));
            }
            else {
                this.buttonList.add(new GuiOptionButton(gamesettings$options2.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), gamesettings$options2, this.field_152318_h.getKeyBinding(gamesettings$options2)));
            }
            ++i;
        }
        this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height / 6 + 168, 150, 20, I18n.format("gui.done", new Object[0])));
        final GuiButton guibutton = new GuiButton(201, this.width / 2 + 5, this.height / 6 + 168, 150, 20, I18n.format("options.stream.ingestSelection", new Object[0]));
        guibutton.enabled = ((this.mc.getTwitchStream().isReadyToBroadcast() && this.mc.getTwitchStream().func_152925_v().length > 0) || this.mc.getTwitchStream().func_152908_z());
        this.buttonList.add(guibutton);
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id < 100 && button instanceof GuiOptionButton) {
                final GameSettings.Options gamesettings$options = ((GuiOptionButton)button).returnEnumOptions();
                this.field_152318_h.setOptionValue(gamesettings$options, 1);
                button.displayString = this.field_152318_h.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
                if (this.mc.getTwitchStream().isBroadcasting() && gamesettings$options != GameSettings.Options.STREAM_CHAT_ENABLED && gamesettings$options != GameSettings.Options.STREAM_CHAT_USER_FILTER) {
                    this.field_152315_t = true;
                }
            }
            else if (button instanceof GuiOptionSlider) {
                if (button.id == GameSettings.Options.STREAM_VOLUME_MIC.returnEnumOrdinal()) {
                    this.mc.getTwitchStream().updateStreamVolume();
                }
                else if (button.id == GameSettings.Options.STREAM_VOLUME_SYSTEM.returnEnumOrdinal()) {
                    this.mc.getTwitchStream().updateStreamVolume();
                }
                else if (this.mc.getTwitchStream().isBroadcasting()) {
                    this.field_152315_t = true;
                }
            }
            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 201) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiIngestServers(this));
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_152319_i, this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.field_152313_r, this.width / 2, this.field_152314_s, 16777215);
        if (this.field_152315_t) {
            this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + I18n.format("options.stream.changes", new Object[0]), this.width / 2, 20 + this.fontRendererObj.FONT_HEIGHT, 16777215);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
