package clientname.gui.hud;

import net.minecraft.client.*;
import com.google.common.collect.*;
import net.minecraft.client.gui.*;
import clientname.event.impl.*;
import net.minecraft.client.gui.inventory.*;
import java.util.*;
import clientname.event.*;

public class HUDManager
{
    private static HUDManager instance;
    private Set<IRenderer> registeredRenderers;
    private Minecraft mc;
    
    static {
        HUDManager.instance = null;
    }
    
    private HUDManager() {
        this.registeredRenderers = (Set<IRenderer>)Sets.newHashSet();
        this.mc = Minecraft.getMinecraft();
    }
    
    public static HUDManager getInstance() {
        if (HUDManager.instance != null) {
            return HUDManager.instance;
        }
        EventManager.register(HUDManager.instance = new HUDManager());
        return HUDManager.instance;
    }
    
    public void register(final IRenderer... renderers) {
        for (final IRenderer render : renderers) {
            this.registeredRenderers.add(render);
        }
    }
    
    public void unreister(final IRenderer... renderers) {
        for (final IRenderer render : renderers) {
            this.registeredRenderers.remove(render);
        }
    }
    
    public Collection<IRenderer> getRegisteredRenderers() {
        return (Collection<IRenderer>)Sets.newHashSet((Iterable)this.registeredRenderers);
    }
    
    public void openConfigScreen() {
        this.mc.displayGuiScreen(new HUDConfigScreen(this));
    }
    
    @EventTarget
    public void onRender(final RenderEvent e) {
        if (this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiContainer) {
            for (final IRenderer renderer : this.registeredRenderers) {
                this.callRenderer(renderer);
            }
        }
    }
    
    private void callRenderer(final IRenderer renderer) {
        if (!renderer.isEnabled()) {
            return;
        }
        ScreenPosition pos = renderer.load();
        if (pos == null) {
            pos = ScreenPosition.fromRelativePosition(0.5, 0.5);
        }
        renderer.render(pos);
    }
}
