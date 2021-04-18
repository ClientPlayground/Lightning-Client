package clientname.cosmetics.util;

import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.*;

public class CosmeticModelBase extends ModelBase
{
    protected ModelBiped playerModel;
    
    public CosmeticModelBase(final RenderPlayer player) {
        this.playerModel = player.getMainModel();
    }
}
