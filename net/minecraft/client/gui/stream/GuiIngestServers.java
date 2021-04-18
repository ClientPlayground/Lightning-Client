package net.minecraft.client.gui.stream;

import net.minecraft.client.resources.*;
import java.io.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import tv.twitch.broadcast.*;
import net.minecraft.client.stream.*;

public class GuiIngestServers extends GuiScreen
{
    private final GuiScreen field_152309_a;
    private String field_152310_f;
    private ServerList field_152311_g;
    
    public GuiIngestServers(final GuiScreen p_i46312_1_) {
        this.field_152309_a = p_i46312_1_;
    }
    
    @Override
    public void initGui() {
        this.field_152310_f = I18n.format("options.stream.ingest.title", new Object[0]);
        this.field_152311_g = new ServerList(this.mc);
        if (!this.mc.getTwitchStream().func_152908_z()) {
            this.mc.getTwitchStream().func_152909_x();
        }
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height - 24 - 6, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height - 24 - 6, 150, 20, I18n.format("options.stream.ingest.reset", new Object[0])));
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.field_152311_g.handleMouseInput();
    }
    
    @Override
    public void onGuiClosed() {
        if (this.mc.getTwitchStream().func_152908_z()) {
            this.mc.getTwitchStream().func_152932_y().func_153039_l();
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.mc.displayGuiScreen(this.field_152309_a);
            }
            else {
                this.mc.gameSettings.streamPreferredServer = "";
                this.mc.gameSettings.saveOptions();
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.field_152311_g.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.field_152310_f, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    class ServerList extends GuiSlot
    {
        public ServerList(final Minecraft mcIn) {
            super(mcIn, GuiIngestServers.this.width, GuiIngestServers.this.height, 32, GuiIngestServers.this.height - 35, (int)(mcIn.fontRendererObj.FONT_HEIGHT * 3.5));
            this.setShowSelectionBox(false);
        }
        
        @Override
        protected int getSize() {
            return this.mc.getTwitchStream().func_152925_v().length;
        }
        
        @Override
        protected void elementClicked(final int slotIndex, final boolean isDoubleClick, final int mouseX, final int mouseY) {
            this.mc.gameSettings.streamPreferredServer = this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl;
            this.mc.gameSettings.saveOptions();
        }
        
        @Override
        protected boolean isSelected(final int slotIndex) {
            return this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl.equals(this.mc.gameSettings.streamPreferredServer);
        }
        
        @Override
        protected void drawBackground() {
        }
        
        @Override
        protected void drawSlot(final int entryID, int p_180791_2_, final int p_180791_3_, final int p_180791_4_, final int mouseXIn, final int mouseYIn) {
            final IngestServer ingestserver = this.mc.getTwitchStream().func_152925_v()[entryID];
            String s = ingestserver.serverUrl.replaceAll("\\{stream_key\\}", "");
            String s2 = String.valueOf((int)ingestserver.bitrateKbps) + " kbps";
            String s3 = null;
            final IngestServerTester ingestservertester = this.mc.getTwitchStream().func_152932_y();
            if (ingestservertester != null) {
                if (ingestserver == ingestservertester.func_153040_c()) {
                    s = EnumChatFormatting.GREEN + s;
                    s2 = String.valueOf((int)(ingestservertester.func_153030_h() * 100.0f)) + "%";
                }
                else if (entryID < ingestservertester.func_153028_p()) {
                    if (ingestserver.bitrateKbps == 0.0f) {
                        s2 = EnumChatFormatting.RED + "Down!";
                    }
                }
                else {
                    s2 = EnumChatFormatting.OBFUSCATED + "1234" + EnumChatFormatting.RESET + " kbps";
                }
            }
            else if (ingestserver.bitrateKbps == 0.0f) {
                s2 = EnumChatFormatting.RED + "Down!";
            }
            p_180791_2_ -= 15;
            if (this.isSelected(entryID)) {
                s3 = EnumChatFormatting.BLUE + "(Preferred)";
            }
            else if (ingestserver.defaultServer) {
                s3 = EnumChatFormatting.GREEN + "(Default)";
            }
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, ingestserver.serverName, p_180791_2_ + 2, p_180791_3_ + 5, 16777215);
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s, p_180791_2_ + 2, p_180791_3_ + GuiIngestServers.this.fontRendererObj.FONT_HEIGHT + 5 + 3, 3158064);
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s2, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(s2), p_180791_3_ + 5, 8421504);
            if (s3 != null) {
                GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s3, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(s3), p_180791_3_ + 5 + 3 + GuiIngestServers.this.fontRendererObj.FONT_HEIGHT, 8421504);
            }
        }
        
        @Override
        protected int getScrollBarX() {
            return super.getScrollBarX() + 15;
        }
    }
}
