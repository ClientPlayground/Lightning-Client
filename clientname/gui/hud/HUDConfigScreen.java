package clientname.gui.hud;

import java.io.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import java.util.function.*;
import java.util.*;

public class HUDConfigScreen extends GuiScreen
{
    private final HashMap<IRenderer, ScreenPosition> renderers;
    private Optional<IRenderer> selectedRenderer;
    private int prevX;
    private int prevY;
    
    public HUDConfigScreen(final HUDManager api) {
        this.renderers = new HashMap<IRenderer, ScreenPosition>();
        this.selectedRenderer = Optional.empty();
        final Collection<IRenderer> registeredRenderers = api.getRegisteredRenderers();
        for (final IRenderer ren : registeredRenderers) {
            if (!ren.isEnabled()) {
                continue;
            }
            ScreenPosition pos = ren.load();
            if (pos == null) {
                pos = ScreenPosition.fromRelativePosition(0.5, 0.5);
            }
            this.adjustBounds(ren, pos);
            this.renderers.put(ren, pos);
        }
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 20, 100, 20, "Toggle Mods"));
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawDefaultBackground();
        final float zBackup = this.zLevel;
        this.zLevel = 200.0f;
        for (final IRenderer renderer : this.renderers.keySet()) {
            final ScreenPosition pos = this.renderers.get(renderer);
            renderer.renderDummy(pos);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.zLevel = zBackup;
    }
    
    private void drawHollowRect(final int x, final int y, final int w, final int h, final int color) {
        this.drawHorizontalLine(x, x + w, y, color);
        this.drawHorizontalLine(x, x + w, y + h, color);
        this.drawVerticalLine(x, y + h, y, color);
        this.drawVerticalLine(x + w, y + h, y, color);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.renderers.entrySet().forEach(entry -> entry.getKey().save((ScreenPosition)entry.getValue()));
            this.mc.displayGuiScreen(null);
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    @Override
    protected void mouseClickMove(final int x, final int y, final int button, final long time) {
        if (this.selectedRenderer.isPresent()) {
            this.moveSelectedRenderBy(x - this.prevX, y - this.prevY);
        }
        super.mouseClickMove(this.prevX = x, this.prevY = y, button, time);
    }
    
    private void moveSelectedRenderBy(final int offsetX, final int offsetY) {
        final IRenderer renderer = this.selectedRenderer.get();
        final ScreenPosition pos = this.renderers.get(renderer);
        pos.setAbsolute(pos.getAbsoluteX() + offsetX, pos.getAbsoluteY() + offsetY);
        this.adjustBounds(renderer, pos);
    }
    
    @Override
    public void onGuiClosed() {
        for (final IRenderer renderer : this.renderers.keySet()) {
            renderer.save(this.renderers.get(renderer));
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
    
    private void adjustBounds(final IRenderer renderer, final ScreenPosition pos) {
        final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        final int screenWidth = res.getScaledWidth();
        final int screenHeight = res.getScaledHeight();
        final int absoluteX = Math.max(0, Math.min(pos.getAbsoluteX(), Math.max(screenWidth - renderer.getWidth(), 0)));
        final int absoluteY = Math.max(0, Math.min(pos.getAbsoluteY(), Math.max(screenHeight - renderer.getHeight(), 0)));
        pos.setAbsolute(absoluteX, absoluteY);
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int mobuttonuseButton) throws IOException {
        this.loadMouseOver(this.prevX = x, this.prevY = y);
        super.mouseClicked(x, y, mobuttonuseButton);
    }
    
    private void loadMouseOver(final int x, final int y) {
        this.selectedRenderer = this.renderers.keySet().stream().filter(new MouseOverFinder(x, y)).findFirst();
    }
    
    private class MouseOverFinder implements Predicate<IRenderer>
    {
        private int mouseX;
        private int mouseY;
        
        public MouseOverFinder(final int x, final int y) {
            this.mouseX = x;
            this.mouseY = y;
        }
        
        @Override
        public boolean test(final IRenderer renderer) {
            final ScreenPosition pos = HUDConfigScreen.this.renderers.get(renderer);
            final int absoluteX = pos.getAbsoluteX();
            final int absoluteY = pos.getAbsoluteY();
            return this.mouseX >= absoluteX && this.mouseX <= absoluteX + renderer.getWidth() && this.mouseY >= absoluteY && this.mouseY <= absoluteY + renderer.getHeight();
        }
    }
}
