package net.minecraft.network;

public enum EnumPacketDirection
{
    SERVERBOUND("SERVERBOUND", 0), 
    CLIENTBOUND("CLIENTBOUND", 1);
    
    private EnumPacketDirection(final String s, final int n) {
    }
}
