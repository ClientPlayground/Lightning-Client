package net.minecraft.network.play.client;

import net.minecraft.network.play.*;
import java.io.*;
import net.minecraft.network.*;

public class C11PacketEnchantItem implements Packet<INetHandlerPlayServer>
{
    private int windowId;
    private int button;
    
    public C11PacketEnchantItem() {
    }
    
    public C11PacketEnchantItem(final int windowId, final int button) {
        this.windowId = windowId;
        this.button = button;
    }
    
    @Override
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processEnchantItem(this);
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
        this.button = buf.readByte();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeByte(this.button);
    }
    
    public int getWindowId() {
        return this.windowId;
    }
    
    public int getButton() {
        return this.button;
    }
}
