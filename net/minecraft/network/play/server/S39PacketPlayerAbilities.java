package net.minecraft.network.play.server;

import net.minecraft.network.play.*;
import net.minecraft.entity.player.*;
import java.io.*;
import net.minecraft.network.*;

public class S39PacketPlayerAbilities implements Packet<INetHandlerPlayClient>
{
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;
    
    public S39PacketPlayerAbilities() {
    }
    
    public S39PacketPlayerAbilities(final PlayerCapabilities capabilities) {
        this.setInvulnerable(capabilities.disableDamage);
        this.setFlying(capabilities.isFlying);
        this.setAllowFlying(capabilities.allowFlying);
        this.setCreativeMode(capabilities.isCreativeMode);
        this.setFlySpeed(capabilities.getFlySpeed());
        this.setWalkSpeed(capabilities.getWalkSpeed());
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        final byte b0 = buf.readByte();
        this.setInvulnerable((b0 & 0x1) > 0);
        this.setFlying((b0 & 0x2) > 0);
        this.setAllowFlying((b0 & 0x4) > 0);
        this.setCreativeMode((b0 & 0x8) > 0);
        this.setFlySpeed(buf.readFloat());
        this.setWalkSpeed(buf.readFloat());
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        byte b0 = 0;
        if (this.isInvulnerable()) {
            b0 |= 0x1;
        }
        if (this.isFlying()) {
            b0 |= 0x2;
        }
        if (this.isAllowFlying()) {
            b0 |= 0x4;
        }
        if (this.isCreativeMode()) {
            b0 |= 0x8;
        }
        buf.writeByte(b0);
        buf.writeFloat(this.flySpeed);
        buf.writeFloat(this.walkSpeed);
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handlePlayerAbilities(this);
    }
    
    public boolean isInvulnerable() {
        return this.invulnerable;
    }
    
    public void setInvulnerable(final boolean isInvulnerable) {
        this.invulnerable = isInvulnerable;
    }
    
    public boolean isFlying() {
        return this.flying;
    }
    
    public void setFlying(final boolean isFlying) {
        this.flying = isFlying;
    }
    
    public boolean isAllowFlying() {
        return this.allowFlying;
    }
    
    public void setAllowFlying(final boolean isAllowFlying) {
        this.allowFlying = isAllowFlying;
    }
    
    public boolean isCreativeMode() {
        return this.creativeMode;
    }
    
    public void setCreativeMode(final boolean isCreativeMode) {
        this.creativeMode = isCreativeMode;
    }
    
    public float getFlySpeed() {
        return this.flySpeed;
    }
    
    public void setFlySpeed(final float flySpeedIn) {
        this.flySpeed = flySpeedIn;
    }
    
    public float getWalkSpeed() {
        return this.walkSpeed;
    }
    
    public void setWalkSpeed(final float walkSpeedIn) {
        this.walkSpeed = walkSpeedIn;
    }
}
