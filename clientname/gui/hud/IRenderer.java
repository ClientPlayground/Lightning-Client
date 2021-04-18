package clientname.gui.hud;

public interface IRenderer extends IRenderConfig
{
    int getWidth();
    
    int getHeight();
    
    void render(final ScreenPosition p0);
    
    default void renderDummy(final ScreenPosition pos) {
        this.render(pos);
    }
    
    default boolean isEnabled() {
        return true;
    }
}
