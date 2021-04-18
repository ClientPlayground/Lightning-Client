package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.*;
import org.apache.logging.log4j.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.network.handshake.client.*;
import net.minecraft.network.*;
import net.minecraft.network.login.client.*;
import clientname.*;
import java.net.*;
import java.io.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import net.minecraft.util.*;

public class GuiConnecting extends GuiScreen
{
    private static final AtomicInteger CONNECTION_ID;
    private static final Logger logger;
    private NetworkManager networkManager;
    private boolean cancel;
    private final GuiScreen previousGuiScreen;
    
    static {
        CONNECTION_ID = new AtomicInteger(0);
        logger = LogManager.getLogger();
    }
    
    public GuiConnecting(final GuiScreen p_i1181_1_, final Minecraft mcIn, final ServerData p_i1181_3_) {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1181_1_;
        final ServerAddress serveraddress = ServerAddress.func_78860_a(p_i1181_3_.serverIP);
        mcIn.loadWorld(null);
        mcIn.setServerData(p_i1181_3_);
        this.connect(serveraddress.getIP(), serveraddress.getPort());
    }
    
    public GuiConnecting(final GuiScreen p_i1182_1_, final Minecraft mcIn, final String hostName, final int port) {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1182_1_;
        mcIn.loadWorld(null);
        this.connect(hostName, port);
    }
    
    private void connect(final String ip, final int port) {
        GuiConnecting.logger.info("Connecting to " + ip + ", " + port);
        new Thread("Server Connector #" + GuiConnecting.CONNECTION_ID.incrementAndGet()) {
            @Override
            public void run() {
                InetAddress inetaddress = null;
                try {
                    if (GuiConnecting.this.cancel) {
                        return;
                    }
                    inetaddress = InetAddress.getByName(ip);
                    GuiConnecting.access$2(GuiConnecting.this, NetworkManager.func_181124_a(inetaddress, port, GuiConnecting.this.mc.gameSettings.func_181148_f()));
                    GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
                    GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                    GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
                    String ip1 = ip.substring(0, ip.length() - 1);
                    final int index = ip1.indexOf(".");
                    ip1 = ip1.substring(index + 1);
                    final char[] cha = ip1.toCharArray();
                    final String[] numbersSTRING = new String[cha.length];
                    for (int i = 0; i < cha.length; ++i) {
                        numbersSTRING[i] = String.valueOf(cha[i]);
                    }
                    numbersSTRING[0] = numbersSTRING[0].toUpperCase();
                    for (int i = 0; i < numbersSTRING.length; ++i) {
                        if (numbersSTRING[i].toLowerCase().contains("h".toLowerCase()) && numbersSTRING.length > i + 1 && numbersSTRING[i + 1].toLowerCase().contains("d".toLowerCase())) {
                            numbersSTRING[i] = numbersSTRING[i].toUpperCase();
                            numbersSTRING[i + 1] = numbersSTRING[i + 1].toUpperCase();
                        }
                        if (numbersSTRING[i].toLowerCase().contains("m".toLowerCase()) && numbersSTRING.length > i + 1 && numbersSTRING[i + 1].toLowerCase().contains("c".toLowerCase())) {
                            numbersSTRING[i] = numbersSTRING[i].toUpperCase();
                            numbersSTRING[i + 1] = numbersSTRING[i + 1].toUpperCase();
                        }
                    }
                    for (int i = 0; i < numbersSTRING.length; ++i) {
                        if (numbersSTRING[i].toLowerCase().contains(".".toLowerCase()) && numbersSTRING.length > i + 3 && numbersSTRING[i + 1].toLowerCase().contains("c".toLowerCase()) && numbersSTRING[i + 2].toLowerCase().contains("o".toLowerCase()) && numbersSTRING[i + 3].toLowerCase().contains("m".toLowerCase())) {
                            numbersSTRING[i + 0] = numbersSTRING[i + 0].replace(".", ".");
                            numbersSTRING[i + 1] = numbersSTRING[i + 1].replace("c", "n");
                            numbersSTRING[i + 2] = numbersSTRING[i + 2].replace("o", "e");
                            numbersSTRING[i + 3] = numbersSTRING[i + 3].replace("m", "t");
                        }
                    }
                    String newip = "";
                    for (int j = 0; j < numbersSTRING.length; ++j) {
                        newip = String.valueOf(newip) + numbersSTRING[j];
                    }
                    Client.getInstance().getDiscordRP().update("Play's " + newip, "In Game");
                }
                catch (UnknownHostException unknownhostexception) {
                    if (GuiConnecting.this.cancel) {
                        return;
                    }
                    GuiConnecting.logger.error("Couldn't connect to server", (Throwable)unknownhostexception);
                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] { "Unknown host" })));
                }
                catch (Exception exception) {
                    if (GuiConnecting.this.cancel) {
                        return;
                    }
                    GuiConnecting.logger.error("Couldn't connect to server", (Throwable)exception);
                    String s = exception.toString();
                    if (inetaddress != null) {
                        final String s2 = String.valueOf(inetaddress.toString()) + ":" + port;
                        s = s.replaceAll(s2, "");
                    }
                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] { s })));
                }
            }
        }.start();
    }
    
    @Override
    public void updateScreen() {
        if (this.networkManager != null) {
            if (this.networkManager.isChannelOpen()) {
                this.networkManager.processReceivedPackets();
            }
            else {
                this.networkManager.checkDisconnected();
            }
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel", new Object[0])));
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.cancel = true;
            if (this.networkManager != null) {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }
            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        if (this.networkManager == null) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
        }
        else {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    static /* synthetic */ void access$2(final GuiConnecting guiConnecting, final NetworkManager networkManager) {
        guiConnecting.networkManager = networkManager;
    }
}
