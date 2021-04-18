package net.minecraft.client.renderer.texture;

import net.minecraft.client.resources.*;
import org.apache.logging.log4j.*;
import com.google.common.collect.*;
import java.io.*;
import java.util.concurrent.*;
import net.minecraft.util.*;
import net.minecraft.crash.*;
import java.util.*;

public class TextureManager implements ITickable, IResourceManagerReloadListener
{
    private static final Logger logger;
    private final Map<ResourceLocation, ITextureObject> mapTextureObjects;
    private final List<ITickable> listTickables;
    private final Map<String, Integer> mapTextureCounters;
    private IResourceManager theResourceManager;
    
    static {
        logger = LogManager.getLogger();
    }
    
    public TextureManager(final IResourceManager resourceManager) {
        this.mapTextureObjects = (Map<ResourceLocation, ITextureObject>)Maps.newHashMap();
        this.listTickables = (List<ITickable>)Lists.newArrayList();
        this.mapTextureCounters = (Map<String, Integer>)Maps.newHashMap();
        this.theResourceManager = resourceManager;
    }
    
    public void bindTexture(final ResourceLocation resource) {
        ITextureObject itextureobject = this.mapTextureObjects.get(resource);
        if (itextureobject == null) {
            itextureobject = new SimpleTexture(resource);
            this.loadTexture(resource, itextureobject);
        }
        TextureUtil.bindTexture(itextureobject.getGlTextureId());
    }
    
    public boolean loadTickableTexture(final ResourceLocation textureLocation, final ITickableTextureObject textureObj) {
        if (this.loadTexture(textureLocation, textureObj)) {
            this.listTickables.add(textureObj);
            return true;
        }
        return false;
    }
    
    public boolean loadTexture(final ResourceLocation textureLocation, ITextureObject textureObj) {
        boolean flag = true;
        try {
            textureObj.loadTexture(this.theResourceManager);
        }
        catch (IOException ioexception) {
            TextureManager.logger.warn("Failed to load texture: " + textureLocation, (Throwable)ioexception);
            textureObj = TextureUtil.missingTexture;
            this.mapTextureObjects.put(textureLocation, textureObj);
            flag = false;
        }
        catch (Throwable throwable) {
            final ITextureObject textureObjf = textureObj;
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addCrashSection("Resource location", textureLocation);
            crashreportcategory.addCrashSectionCallable("Texture object class", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return textureObjf.getClass().getName();
                }
            });
            throw new ReportedException(crashreport);
        }
        this.mapTextureObjects.put(textureLocation, textureObj);
        return flag;
    }
    
    public ITextureObject getTexture(final ResourceLocation textureLocation) {
        return this.mapTextureObjects.get(textureLocation);
    }
    
    public ResourceLocation getDynamicTextureLocation(final String name, final DynamicTexture texture) {
        Integer integer = this.mapTextureCounters.get(name);
        if (integer == null) {
            integer = 1;
        }
        else {
            ++integer;
        }
        this.mapTextureCounters.put(name, integer);
        final ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
        this.loadTexture(resourcelocation, texture);
        return resourcelocation;
    }
    
    @Override
    public void tick() {
        for (final ITickable itickable : this.listTickables) {
            itickable.tick();
        }
    }
    
    public void deleteTexture(final ResourceLocation textureLocation) {
        final ITextureObject itextureobject = this.getTexture(textureLocation);
        if (itextureobject != null) {
            TextureUtil.deleteTexture(itextureobject.getGlTextureId());
        }
    }
    
    @Override
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        for (final Map.Entry<ResourceLocation, ITextureObject> entry : this.mapTextureObjects.entrySet()) {
            this.loadTexture(entry.getKey(), entry.getValue());
        }
    }
}
