package net.minecraft.client.renderer;

import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import java.nio.*;
import org.apache.logging.log4j.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import java.io.*;
import com.google.gson.*;
import net.minecraft.client.shader.*;
import net.minecraft.entity.boss.*;
import com.google.common.base.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import java.util.*;
import net.minecraft.client.entity.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import clientname.mods.*;
import net.minecraft.entity.passive.*;
import net.minecraft.block.state.*;
import org.lwjgl.util.glu.*;
import net.minecraft.potion.*;
import org.lwjgl.input.*;
import net.minecraft.client.gui.*;
import java.util.concurrent.*;
import clientname.event.impl.*;
import net.minecraft.crash.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.enchantment.*;
import org.lwjgl.opengl.*;

public class EntityRenderer implements IResourceManagerReloadListener
{
    private static final Logger logger;
    private static final ResourceLocation locationRainPng;
    private static final ResourceLocation locationSnowPng;
    public static boolean anaglyphEnable;
    public static int anaglyphField;
    private Minecraft mc;
    private final IResourceManager resourceManager;
    private Random random;
    private float farPlaneDistance;
    public final ItemRenderer itemRenderer;
    private final MapItemRenderer theMapItemRenderer;
    private int rendererUpdateCount;
    private Entity pointedEntity;
    private MouseFilter mouseFilterXAxis;
    private MouseFilter mouseFilterYAxis;
    private float thirdPersonDistance;
    private float thirdPersonDistanceTemp;
    private float smoothCamYaw;
    private float smoothCamPitch;
    private float smoothCamFilterX;
    private float smoothCamFilterY;
    private float smoothCamPartialTicks;
    private float fovModifierHand;
    private float fovModifierHandPrev;
    private float bossColorModifier;
    private float bossColorModifierPrev;
    private boolean cloudFog;
    private boolean renderHand;
    private boolean drawBlockOutline;
    private long prevFrameTime;
    private long renderEndNanoTime;
    private final DynamicTexture lightmapTexture;
    private final int[] lightmapColors;
    private final ResourceLocation locationLightMap;
    private boolean lightmapUpdateNeeded;
    private float torchFlickerX;
    private float torchFlickerDX;
    private int rainSoundCounter;
    private float[] rainXCoords;
    private float[] rainYCoords;
    private FloatBuffer fogColorBuffer;
    private float fogColorRed;
    private float fogColorGreen;
    private float fogColorBlue;
    private float fogColor2;
    private float fogColor1;
    private int debugViewDirection;
    private boolean debugView;
    private double cameraZoom;
    private double cameraYaw;
    private double cameraPitch;
    private ShaderGroup theShaderGroup;
    private static final ResourceLocation[] shaderResourceLocations;
    public static final int shaderCount;
    private int shaderIndex;
    private boolean useShader;
    private int frameCount;
    
    static {
        logger = LogManager.getLogger();
        locationRainPng = new ResourceLocation("textures/environment/rain.png");
        locationSnowPng = new ResourceLocation("textures/environment/snow.png");
        shaderResourceLocations = new ResourceLocation[] { new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json") };
        shaderCount = EntityRenderer.shaderResourceLocations.length;
    }
    
    public EntityRenderer(final Minecraft mcIn, final IResourceManager resourceManagerIn) {
        this.random = new Random();
        this.mouseFilterXAxis = new MouseFilter();
        this.mouseFilterYAxis = new MouseFilter();
        this.thirdPersonDistance = 4.0f;
        this.thirdPersonDistanceTemp = 4.0f;
        this.renderHand = true;
        this.drawBlockOutline = true;
        this.prevFrameTime = Minecraft.getSystemTime();
        this.rainXCoords = new float[1024];
        this.rainYCoords = new float[1024];
        this.fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
        this.debugViewDirection = 0;
        this.debugView = false;
        this.cameraZoom = 1.0;
        this.shaderIndex = EntityRenderer.shaderCount;
        this.useShader = false;
        this.frameCount = 0;
        this.mc = mcIn;
        this.resourceManager = resourceManagerIn;
        this.itemRenderer = mcIn.getItemRenderer();
        this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
        this.lightmapTexture = new DynamicTexture(16, 16);
        this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
        this.lightmapColors = this.lightmapTexture.getTextureData();
        this.theShaderGroup = null;
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                final float f = (float)(j - 16);
                final float f2 = (float)(i - 16);
                final float f3 = MathHelper.sqrt_float(f * f + f2 * f2);
                this.rainXCoords[i << 5 | j] = -f2 / f3;
                this.rainYCoords[i << 5 | j] = f / f3;
            }
        }
    }
    
    public boolean isShaderActive() {
        return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
    }
    
    public void func_181022_b() {
        if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
        }
        this.theShaderGroup = null;
        this.shaderIndex = EntityRenderer.shaderCount;
    }
    
    public void switchUseShader() {
        this.useShader = !this.useShader;
    }
    
    public void loadEntityShader(final Entity entityIn) {
        if (OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.deleteShaderGroup();
            }
            this.theShaderGroup = null;
            if (entityIn instanceof EntityCreeper) {
                this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
            }
            else if (entityIn instanceof EntitySpider) {
                this.loadShader(new ResourceLocation("shaders/post/spider.json"));
            }
            else if (entityIn instanceof EntityEnderman) {
                this.loadShader(new ResourceLocation("shaders/post/invert.json"));
            }
        }
    }
    
    public void activateNextShader() {
        if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.deleteShaderGroup();
            }
            this.shaderIndex = (this.shaderIndex + 1) % (EntityRenderer.shaderResourceLocations.length + 1);
            if (this.shaderIndex != EntityRenderer.shaderCount) {
                this.loadShader(EntityRenderer.shaderResourceLocations[this.shaderIndex]);
            }
            else {
                this.theShaderGroup = null;
            }
        }
    }
    
    private void loadShader(final ResourceLocation resourceLocationIn) {
        try {
            (this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn)).createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.useShader = true;
        }
        catch (IOException ioexception) {
            EntityRenderer.logger.warn("Failed to load shader: " + resourceLocationIn, (Throwable)ioexception);
            this.shaderIndex = EntityRenderer.shaderCount;
            this.useShader = false;
        }
        catch (JsonSyntaxException jsonsyntaxexception) {
            EntityRenderer.logger.warn("Failed to load shader: " + resourceLocationIn, (Throwable)jsonsyntaxexception);
            this.shaderIndex = EntityRenderer.shaderCount;
            this.useShader = false;
        }
    }
    
    @Override
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
        }
        this.theShaderGroup = null;
        if (this.shaderIndex != EntityRenderer.shaderCount) {
            this.loadShader(EntityRenderer.shaderResourceLocations[this.shaderIndex]);
        }
        else {
            this.loadEntityShader(this.mc.getRenderViewEntity());
        }
    }
    
    public void updateRenderer() {
        if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
        }
        this.updateFovModifierHand();
        this.updateTorchFlicker();
        this.fogColor2 = this.fogColor1;
        this.thirdPersonDistanceTemp = this.thirdPersonDistance;
        if (this.mc.gameSettings.smoothCamera) {
            final float f = this.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float f2 = f * f * f * 8.0f;
            this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05f * f2);
            this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05f * f2);
            this.smoothCamPartialTicks = 0.0f;
            this.smoothCamYaw = 0.0f;
            this.smoothCamPitch = 0.0f;
        }
        else {
            this.smoothCamFilterX = 0.0f;
            this.smoothCamFilterY = 0.0f;
            this.mouseFilterXAxis.reset();
            this.mouseFilterYAxis.reset();
        }
        if (this.mc.getRenderViewEntity() == null) {
            this.mc.setRenderViewEntity(this.mc.thePlayer);
        }
        final float f3 = this.mc.theWorld.getLightBrightness(new BlockPos(this.mc.getRenderViewEntity()));
        final float f4 = this.mc.gameSettings.renderDistanceChunks / 32.0f;
        final float f5 = f3 * (1.0f - f4) + f4;
        this.fogColor1 += (f5 - this.fogColor1) * 0.1f;
        ++this.rendererUpdateCount;
        this.itemRenderer.updateEquippedItem();
        this.addRainParticles();
        this.bossColorModifierPrev = this.bossColorModifier;
        if (BossStatus.hasColorModifier) {
            this.bossColorModifier += 0.05f;
            if (this.bossColorModifier > 1.0f) {
                this.bossColorModifier = 1.0f;
            }
            BossStatus.hasColorModifier = false;
        }
        else if (this.bossColorModifier > 0.0f) {
            this.bossColorModifier -= 0.0125f;
        }
    }
    
    public ShaderGroup getShaderGroup() {
        return this.theShaderGroup;
    }
    
    public void updateShaderGroupSize(final int width, final int height) {
        if (OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.createBindFramebuffers(width, height);
            }
            this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
        }
    }
    
    public void getMouseOver(final float partialTicks) {
        final Entity entity = this.mc.getRenderViewEntity();
        if (entity != null && this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("pick");
            this.mc.pointedEntity = null;
            double d0 = this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d2 = d0;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            final int i = 3;
            if (this.mc.playerController.extendedReach()) {
                d0 = 6.0;
                d2 = 6.0;
            }
            else if (d0 > 3.0) {
                flag = true;
            }
            if (this.mc.objectMouseOver != null) {
                d2 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
            }
            final Vec3 vec4 = entity.getLook(partialTicks);
            final Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
            this.pointedEntity = null;
            Vec3 vec6 = null;
            final float f = 1.0f;
            final List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand(f, f, f), (Predicate<? super Entity>)Predicates.and((Predicate)EntitySelectors.NOT_SPECTATING, (Predicate)new Predicate<Entity>() {
                public boolean apply(final Entity p_apply_1_) {
                    return p_apply_1_.canBeCollidedWith();
                }
            }));
            double d3 = d2;
            for (int j = 0; j < list.size(); ++j) {
                final Entity entity2 = list.get(j);
                final float f2 = entity2.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity2.getEntityBoundingBox().expand(f2, f2, f2);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec5);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d3 >= 0.0) {
                        this.pointedEntity = entity2;
                        vec6 = ((movingobjectposition == null) ? vec3 : movingobjectposition.hitVec);
                        d3 = 0.0;
                    }
                }
                else if (movingobjectposition != null) {
                    final double d4 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d4 < d3 || d3 == 0.0) {
                        if (entity2 == entity.ridingEntity) {
                            if (d3 == 0.0) {
                                this.pointedEntity = entity2;
                                vec6 = movingobjectposition.hitVec;
                            }
                        }
                        else {
                            this.pointedEntity = entity2;
                            vec6 = movingobjectposition.hitVec;
                            d3 = d4;
                        }
                    }
                }
            }
            if (this.pointedEntity != null && flag && vec3.distanceTo(vec6) > 3.0) {
                this.pointedEntity = null;
                this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec6, null, new BlockPos(vec6));
            }
            if (this.pointedEntity != null && (d3 < d2 || this.mc.objectMouseOver == null)) {
                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec6);
                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    this.mc.pointedEntity = this.pointedEntity;
                }
            }
            this.mc.mcProfiler.endSection();
        }
    }
    
    private void updateFovModifierHand() {
        float f = 1.0f;
        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
            final AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.mc.getRenderViewEntity();
            f = abstractclientplayer.getFovModifier();
        }
        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (f - this.fovModifierHand) * 0.5f;
        if (this.fovModifierHand > 1.5f) {
            this.fovModifierHand = 1.5f;
        }
        if (this.fovModifierHand < 0.1f) {
            this.fovModifierHand = 0.1f;
        }
    }
    
    private float getFOVModifier(final float partialTicks, final boolean p_78481_2_) {
        if (this.debugView) {
            return 90.0f;
        }
        final Entity entity = this.mc.getRenderViewEntity();
        float f = 70.0f;
        if (p_78481_2_) {
            f = this.mc.gameSettings.fovSetting;
            f *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks;
        }
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0f) {
            final float f2 = ((EntityLivingBase)entity).deathTime + partialTicks;
            f /= (1.0f - 500.0f / (f2 + 500.0f)) * 2.0f + 1.0f;
        }
        final Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
        if (block.getMaterial() == Material.water) {
            f = f * 60.0f / 70.0f;
        }
        return f;
    }
    
    private void hurtCameraEffect(final float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
            final EntityLivingBase entitylivingbase = (EntityLivingBase)this.mc.getRenderViewEntity();
            float f = entitylivingbase.hurtTime - partialTicks;
            if (entitylivingbase.getHealth() <= 0.0f) {
                final float f2 = entitylivingbase.deathTime + partialTicks;
                GlStateManager.rotate(40.0f - 8000.0f / (f2 + 200.0f), 0.0f, 0.0f, 1.0f);
            }
            if (f < 0.0f) {
                return;
            }
            f /= entitylivingbase.maxHurtTime;
            f = MathHelper.sin(f * f * f * f * 3.1415927f);
            final float f3 = entitylivingbase.attackedAtYaw;
            GlStateManager.rotate(-f3, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-f * 14.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotate(f3, 0.0f, 1.0f, 0.0f);
        }
    }
    
    private void setupViewBobbing(final float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            final EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            final float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            final float f2 = -(entityplayer.distanceWalkedModified + f * partialTicks);
            final float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
            final float f4 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
            GlStateManager.translate(MathHelper.sin(f2 * 3.1415927f) * f3 * 0.5f, -Math.abs(MathHelper.cos(f2 * 3.1415927f) * f3), 0.0f);
            GlStateManager.rotate(MathHelper.sin(f2 * 3.1415927f) * f3 * 3.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotate(Math.abs(MathHelper.cos(f2 * 3.1415927f - 0.2f) * f3) * 5.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(f4, 1.0f, 0.0f, 0.0f);
        }
    }
    
    private void orientCamera(final float partialTicks) {
        final Entity entity = this.mc.getRenderViewEntity();
        float f = entity.getEyeHeight();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
        double d2 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + f;
        double d3 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
            ++f;
            GlStateManager.translate(0.0f, 0.3f, 0.0f);
            if (!this.mc.gameSettings.debugCamEnable) {
                final BlockPos blockpos = new BlockPos(entity);
                final IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
                final Block block = iblockstate.getBlock();
                if (block == Blocks.bed) {
                    final int j = iblockstate.getValue((IProperty<EnumFacing>)BlockBed.FACING).getHorizontalIndex();
                    GlStateManager.rotate((float)(j * 90), 0.0f, 1.0f, 0.0f);
                }
                GlStateManager.rotate(ModInstances.getModPerspective().getCameraYaw() + (ModInstances.getModPerspective().getCameraYaw() - ModInstances.getModPerspective().getCameraYaw()) * partialTicks + 180.0f, 0.0f, -1.0f, 0.0f);
                GlStateManager.rotate(ModInstances.getModPerspective().getCameraPitch() + (ModInstances.getModPerspective().getCameraPitch() - ModInstances.getModPerspective().getCameraPitch()) * partialTicks, -1.0f, 0.0f, 0.0f);
            }
        }
        else if (this.mc.gameSettings.thirdPersonView > 0) {
            double d4 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks;
            if (this.mc.gameSettings.debugCamEnable) {
                GlStateManager.translate(0.0f, 0.0f, (float)(-d4));
            }
            else {
                final float f2 = entity.rotationYaw;
                float f3 = entity.rotationPitch;
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    f3 += 180.0f;
                }
                final double d5 = -MathHelper.sin(f2 / 180.0f * 3.1415927f) * MathHelper.cos(f3 / 180.0f * 3.1415927f) * d4;
                final double d6 = MathHelper.cos(f2 / 180.0f * 3.1415927f) * MathHelper.cos(f3 / 180.0f * 3.1415927f) * d4;
                final double d7 = -MathHelper.sin(f3 / 180.0f * 3.1415927f) * d4;
                for (int i = 0; i < 8; ++i) {
                    float f4 = (float)((i & 0x1) * 2 - 1);
                    float f5 = (float)((i >> 1 & 0x1) * 2 - 1);
                    float f6 = (float)((i >> 2 & 0x1) * 2 - 1);
                    f4 *= 0.1f;
                    f5 *= 0.1f;
                    f6 *= 0.1f;
                    final MovingObjectPosition movingobjectposition = this.mc.theWorld.rayTraceBlocks(new Vec3(d0 + f4, d2 + f5, d3 + f6), new Vec3(d0 - d5 + f4 + f6, d2 - d7 + f5, d3 - d6 + f6));
                    if (movingobjectposition != null) {
                        final double d8 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d2, d3));
                        if (d8 < d4) {
                            d4 = d8;
                        }
                    }
                }
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                }
                GlStateManager.rotate(entity.rotationPitch - f3, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(entity.rotationYaw - f2, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate(0.0f, 0.0f, (float)(-d4));
                GlStateManager.rotate(f2 - entity.rotationYaw, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(f3 - entity.rotationPitch, 1.0f, 0.0f, 0.0f);
            }
        }
        else {
            GlStateManager.translate(0.0f, 0.0f, -0.1f);
        }
        if (!this.mc.gameSettings.debugCamEnable) {
            GlStateManager.rotate(ModInstances.getModPerspective().getCameraPitch() + (ModInstances.getModPerspective().getCameraPitch() - ModInstances.getModPerspective().getCameraPitch()) * partialTicks, 1.0f, 0.0f, 0.0f);
            if (entity instanceof EntityAnimal) {
                final EntityAnimal entityanimal = (EntityAnimal)entity;
                GlStateManager.rotate(entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0f, 0.0f, 1.0f, 0.0f);
            }
            else {
                GlStateManager.rotate(ModInstances.getModPerspective().getCameraYaw() + (ModInstances.getModPerspective().getCameraYaw() - ModInstances.getModPerspective().getCameraYaw()) * partialTicks + 180.0f, 0.0f, 1.0f, 0.0f);
            }
        }
        GlStateManager.translate(0.0f, -f, 0.0f);
        d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
        d2 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + f;
        d3 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d2, d3, partialTicks);
    }
    
    private void setupCameraTransform(final float partialTicks, final int pass) {
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        final float f = 0.07f;
        if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate(-(pass * 2 - 1) * f, 0.0f, 0.0f);
        }
        if (this.cameraZoom != 1.0) {
            GlStateManager.translate((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0f);
            GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0);
        }
        Project.gluPerspective(this.getFOVModifier(partialTicks, true), this.mc.displayWidth / (float)this.mc.displayHeight, 0.05f, this.farPlaneDistance * MathHelper.SQRT_2);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((pass * 2 - 1) * 0.1f, 0.0f, 0.0f);
        }
        this.hurtCameraEffect(partialTicks);
        if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(partialTicks);
        }
        final float f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
        if (f2 > 0.0f) {
            int i = 20;
            if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
                i = 7;
            }
            float f3 = 5.0f / (f2 * f2 + 5.0f) - f2 * 0.04f;
            f3 *= f3;
            GlStateManager.rotate((this.rendererUpdateCount + partialTicks) * i, 0.0f, 1.0f, 1.0f);
            GlStateManager.scale(1.0f / f3, 1.0f, 1.0f);
            GlStateManager.rotate(-(this.rendererUpdateCount + partialTicks) * i, 0.0f, 1.0f, 1.0f);
        }
        this.orientCamera(partialTicks);
        if (this.debugView) {
            switch (this.debugViewDirection) {
                case 0: {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 1: {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 2: {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 3: {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                    break;
                }
                case 4: {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                    break;
                }
            }
        }
    }
    
    private void renderHand(final float partialTicks, final int xOffset) {
        if (!this.debugView) {
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            final float f = 0.07f;
            if (this.mc.gameSettings.anaglyph) {
                GlStateManager.translate(-(xOffset * 2 - 1) * f, 0.0f, 0.0f);
            }
            Project.gluPerspective(this.getFOVModifier(partialTicks, false), this.mc.displayWidth / (float)this.mc.displayHeight, 0.05f, this.farPlaneDistance * 2.0f);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            if (this.mc.gameSettings.anaglyph) {
                GlStateManager.translate((xOffset * 2 - 1) * 0.1f, 0.0f, 0.0f);
            }
            GlStateManager.pushMatrix();
            this.hurtCameraEffect(partialTicks);
            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(partialTicks);
            }
            final boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
            if (this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectator()) {
                this.enableLightmap();
                this.itemRenderer.renderItemInFirstPerson(partialTicks);
                this.disableLightmap();
            }
            GlStateManager.popMatrix();
            if (this.mc.gameSettings.thirdPersonView == 0 && !flag) {
                this.itemRenderer.renderOverlays(partialTicks);
                this.hurtCameraEffect(partialTicks);
            }
            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(partialTicks);
            }
        }
    }
    
    public void disableLightmap() {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    public void enableLightmap() {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float f = 0.00390625f;
        GlStateManager.scale(f, f, f);
        GlStateManager.translate(8.0f, 8.0f, 8.0f);
        GlStateManager.matrixMode(5888);
        this.mc.getTextureManager().bindTexture(this.locationLightMap);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 10496);
        GL11.glTexParameteri(3553, 10243, 10496);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    private void updateTorchFlicker() {
        this.torchFlickerDX += (float)((Math.random() - Math.random()) * Math.random() * Math.random());
        this.torchFlickerDX *= (float)0.9;
        this.torchFlickerX += (this.torchFlickerDX - this.torchFlickerX) * 1.0f;
        this.lightmapUpdateNeeded = true;
    }
    
    private void updateLightmap(final float partialTicks) {
        if (this.lightmapUpdateNeeded) {
            this.mc.mcProfiler.startSection("lightTex");
            final World world = this.mc.theWorld;
            if (world != null) {
                final float f = world.getSunBrightness(1.0f);
                final float f2 = f * 0.95f + 0.05f;
                for (int i = 0; i < 256; ++i) {
                    float f3 = world.provider.getLightBrightnessTable()[i / 16] * f2;
                    final float f4 = world.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1f + 1.5f);
                    if (world.getLastLightningBolt() > 0) {
                        f3 = world.provider.getLightBrightnessTable()[i / 16];
                    }
                    final float f5 = f3 * (f * 0.65f + 0.35f);
                    final float f6 = f3 * (f * 0.65f + 0.35f);
                    final float f7 = f4 * ((f4 * 0.6f + 0.4f) * 0.6f + 0.4f);
                    final float f8 = f4 * (f4 * f4 * 0.6f + 0.4f);
                    float f9 = f5 + f4;
                    float f10 = f6 + f7;
                    float f11 = f3 + f8;
                    f9 = f9 * 0.96f + 0.03f;
                    f10 = f10 * 0.96f + 0.03f;
                    f11 = f11 * 0.96f + 0.03f;
                    if (this.bossColorModifier > 0.0f) {
                        final float f12 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
                        f9 = f9 * (1.0f - f12) + f9 * 0.7f * f12;
                        f10 = f10 * (1.0f - f12) + f10 * 0.6f * f12;
                        f11 = f11 * (1.0f - f12) + f11 * 0.6f * f12;
                    }
                    if (world.provider.getDimensionId() == 1) {
                        f9 = 0.22f + f4 * 0.75f;
                        f10 = 0.28f + f7 * 0.75f;
                        f11 = 0.25f + f8 * 0.75f;
                    }
                    if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                        final float f13 = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                        float f14 = 1.0f / f9;
                        if (f14 > 1.0f / f10) {
                            f14 = 1.0f / f10;
                        }
                        if (f14 > 1.0f / f11) {
                            f14 = 1.0f / f11;
                        }
                        f9 = f9 * (1.0f - f13) + f9 * f14 * f13;
                        f10 = f10 * (1.0f - f13) + f10 * f14 * f13;
                        f11 = f11 * (1.0f - f13) + f11 * f14 * f13;
                    }
                    if (f9 > 1.0f) {
                        f9 = 1.0f;
                    }
                    if (f10 > 1.0f) {
                        f10 = 1.0f;
                    }
                    if (f11 > 1.0f) {
                        f11 = 1.0f;
                    }
                    final float f15 = this.mc.gameSettings.gammaSetting;
                    float f16 = 1.0f - f9;
                    float f17 = 1.0f - f10;
                    float f18 = 1.0f - f11;
                    f16 = 1.0f - f16 * f16 * f16 * f16;
                    f17 = 1.0f - f17 * f17 * f17 * f17;
                    f18 = 1.0f - f18 * f18 * f18 * f18;
                    f9 = f9 * (1.0f - f15) + f16 * f15;
                    f10 = f10 * (1.0f - f15) + f17 * f15;
                    f11 = f11 * (1.0f - f15) + f18 * f15;
                    f9 = f9 * 0.96f + 0.03f;
                    f10 = f10 * 0.96f + 0.03f;
                    f11 = f11 * 0.96f + 0.03f;
                    if (f9 > 1.0f) {
                        f9 = 1.0f;
                    }
                    if (f10 > 1.0f) {
                        f10 = 1.0f;
                    }
                    if (f11 > 1.0f) {
                        f11 = 1.0f;
                    }
                    if (f9 < 0.0f) {
                        f9 = 0.0f;
                    }
                    if (f10 < 0.0f) {
                        f10 = 0.0f;
                    }
                    if (f11 < 0.0f) {
                        f11 = 0.0f;
                    }
                    final int j = 255;
                    final int k = (int)(f9 * 255.0f);
                    final int l = (int)(f10 * 255.0f);
                    final int i2 = (int)(f11 * 255.0f);
                    this.lightmapColors[i] = (j << 24 | k << 16 | l << 8 | i2);
                }
                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                this.mc.mcProfiler.endSection();
            }
        }
    }
    
    private float getNightVisionBrightness(final EntityLivingBase entitylivingbaseIn, final float partialTicks) {
        final int i = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
        return (i > 200) ? 1.0f : (0.7f + MathHelper.sin((i - partialTicks) * 3.1415927f * 0.2f) * 0.3f);
    }
    
    public void func_181560_a(final float p_181560_1_, final long p_181560_2_) {
        final boolean flag = Display.isActive();
        if (!flag && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
            if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
                this.mc.displayInGameMenu();
            }
        }
        else {
            this.prevFrameTime = Minecraft.getSystemTime();
        }
        this.mc.mcProfiler.startSection("mouse");
        if (flag && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
            Mouse.setGrabbed(false);
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
            Mouse.setGrabbed(true);
        }
        if (this.mc.inGameHasFocus && flag && ModInstances.getModPerspective().overrideMouse()) {
            this.mc.mouseHelper.mouseXYChange();
            final float f = this.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float f2 = f * f * f * 8.0f;
            float f3 = this.mc.mouseHelper.deltaX * f2;
            float f4 = this.mc.mouseHelper.deltaY * f2;
            int i = 1;
            if (this.mc.gameSettings.invertMouse) {
                i = -1;
            }
            if (this.mc.gameSettings.smoothCamera) {
                this.smoothCamYaw += f3;
                this.smoothCamPitch += f4;
                final float f5 = p_181560_1_ - this.smoothCamPartialTicks;
                this.smoothCamPartialTicks = p_181560_1_;
                f3 = this.smoothCamFilterX * f5;
                f4 = this.smoothCamFilterY * f5;
                this.mc.thePlayer.setAngles(f3, f4 * i);
            }
            else {
                this.smoothCamYaw = 0.0f;
                this.smoothCamPitch = 0.0f;
                this.mc.thePlayer.setAngles(f3, f4 * i);
            }
        }
        this.mc.mcProfiler.endSection();
        if (!this.mc.skipRenderWorld) {
            EntityRenderer.anaglyphEnable = this.mc.gameSettings.anaglyph;
            final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            final int i2 = scaledresolution.getScaledWidth();
            final int j1 = scaledresolution.getScaledHeight();
            final int k1 = Mouse.getX() * i2 / this.mc.displayWidth;
            final int l1 = j1 - Mouse.getY() * j1 / this.mc.displayHeight - 1;
            final int i3 = this.mc.gameSettings.limitFramerate;
            if (this.mc.theWorld != null) {
                this.mc.mcProfiler.startSection("level");
                int m = Math.min(Minecraft.getDebugFPS(), i3);
                m = Math.max(m, 60);
                final long k2 = System.nanoTime() - p_181560_2_;
                final long l2 = Math.max(1000000000 / m / 4 - k2, 0L);
                this.renderWorld(p_181560_1_, System.nanoTime() + l2);
                if (OpenGlHelper.shadersSupported) {
                    this.mc.renderGlobal.renderEntityOutlineFramebuffer();
                    if (this.theShaderGroup != null && this.useShader) {
                        GlStateManager.matrixMode(5890);
                        GlStateManager.pushMatrix();
                        GlStateManager.loadIdentity();
                        this.theShaderGroup.loadShaderGroup(p_181560_1_);
                        GlStateManager.popMatrix();
                    }
                    this.mc.getFramebuffer().bindFramebuffer(true);
                }
                this.renderEndNanoTime = System.nanoTime();
                this.mc.mcProfiler.endStartSection("gui");
                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
                    GlStateManager.alphaFunc(516, 0.1f);
                    this.mc.ingameGUI.renderGameOverlay(p_181560_1_);
                }
                this.mc.mcProfiler.endSection();
            }
            else {
                GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                this.setupOverlayRendering();
                this.renderEndNanoTime = System.nanoTime();
            }
            if (this.mc.currentScreen != null) {
                GlStateManager.clear(256);
                try {
                    this.mc.currentScreen.drawScreen(k1, l1, p_181560_1_);
                }
                catch (Throwable throwable) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
                    crashreportcategory.addCrashSectionCallable("Screen name", new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    crashreportcategory.addCrashSectionCallable("Mouse location", new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", k1, l1, Mouse.getX(), Mouse.getY());
                        }
                    });
                    crashreportcategory.addCrashSectionCallable("Screen size", new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), EntityRenderer.this.mc.displayWidth, EntityRenderer.this.mc.displayHeight, scaledresolution.getScaleFactor());
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }
        new RenderEvent().call();
    }
    
    public void renderStreamIndicator(final float partialTicks) {
        this.setupOverlayRendering();
        this.mc.ingameGUI.renderStreamIndicator(new ScaledResolution(this.mc));
    }
    
    private boolean isDrawBlockOutline() {
        if (!this.drawBlockOutline) {
            return false;
        }
        final Entity entity = this.mc.getRenderViewEntity();
        boolean flag = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;
        if (flag && !((EntityPlayer)entity).capabilities.allowEdit) {
            final ItemStack itemstack = ((EntityPlayer)entity).getCurrentEquippedItem();
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                final BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                final Block block = this.mc.theWorld.getBlockState(blockpos).getBlock();
                if (this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
                    flag = (block.hasTileEntity() && this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory);
                }
                else {
                    flag = (itemstack != null && (itemstack.canDestroy(block) || itemstack.canPlaceOn(block)));
                }
            }
        }
        return flag;
    }
    
    private void renderWorldDirections(final float partialTicks) {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.gameSettings.hideGUI && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
            final Entity entity = this.mc.getRenderViewEntity();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GL11.glLineWidth(1.0f);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.orientCamera(partialTicks);
            GlStateManager.translate(0.0f, entity.getEyeHeight(), 0.0f);
            RenderGlobal.func_181563_a(new AxisAlignedBB(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), 255, 0, 0, 255);
            RenderGlobal.func_181563_a(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255, 255);
            RenderGlobal.func_181563_a(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), 0, 255, 0, 255);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
    
    public void renderWorld(final float partialTicks, final long finishTimeNano) {
        this.updateLightmap(partialTicks);
        if (this.mc.getRenderViewEntity() == null) {
            this.mc.setRenderViewEntity(this.mc.thePlayer);
        }
        this.getMouseOver(partialTicks);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.5f);
        this.mc.mcProfiler.startSection("center");
        if (this.mc.gameSettings.anaglyph) {
            EntityRenderer.anaglyphField = 0;
            GlStateManager.colorMask(false, true, true, false);
            this.renderWorldPass(0, partialTicks, finishTimeNano);
            EntityRenderer.anaglyphField = 1;
            GlStateManager.colorMask(true, false, false, false);
            this.renderWorldPass(1, partialTicks, finishTimeNano);
            GlStateManager.colorMask(true, true, true, false);
        }
        else {
            this.renderWorldPass(2, partialTicks, finishTimeNano);
        }
        this.mc.mcProfiler.endSection();
    }
    
    private void renderWorldPass(final int pass, final float partialTicks, final long finishTimeNano) {
        final RenderGlobal renderglobal = this.mc.renderGlobal;
        final EffectRenderer effectrenderer = this.mc.effectRenderer;
        final boolean flag = this.isDrawBlockOutline();
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("clear");
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        this.updateFogColor(partialTicks);
        GlStateManager.clear(16640);
        this.mc.mcProfiler.endStartSection("camera");
        this.setupCameraTransform(partialTicks, pass);
        ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
        this.mc.mcProfiler.endStartSection("frustum");
        ClippingHelperImpl.getInstance();
        this.mc.mcProfiler.endStartSection("culling");
        final ICamera icamera = new Frustum();
        final Entity entity = this.mc.getRenderViewEntity();
        final double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        icamera.setPosition(d0, d2, d3);
        if (this.mc.gameSettings.renderDistanceChunks >= 4) {
            this.setupFog(-1, partialTicks);
            this.mc.mcProfiler.endStartSection("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), this.mc.displayWidth / (float)this.mc.displayHeight, 0.05f, this.farPlaneDistance * 2.0f);
            GlStateManager.matrixMode(5888);
            renderglobal.renderSky(partialTicks, pass);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), this.mc.displayWidth / (float)this.mc.displayHeight, 0.05f, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }
        this.setupFog(0, partialTicks);
        GlStateManager.shadeModel(7425);
        if (entity.posY + entity.getEyeHeight() < 128.0) {
            this.renderCloudsCheck(renderglobal, partialTicks, pass);
        }
        this.mc.mcProfiler.endStartSection("prepareterrain");
        this.setupFog(0, partialTicks);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        this.mc.mcProfiler.endStartSection("terrain_setup");
        renderglobal.setupTerrain(entity, partialTicks, icamera, this.frameCount++, this.mc.thePlayer.isSpectator());
        if (pass == 0 || pass == 2) {
            this.mc.mcProfiler.endStartSection("updatechunks");
            this.mc.renderGlobal.updateChunks(finishTimeNano);
        }
        this.mc.mcProfiler.endStartSection("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, partialTicks, pass, entity);
        GlStateManager.enableAlpha();
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, pass, entity);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, partialTicks, pass, entity);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1f);
        if (!this.debugView) {
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            renderglobal.renderEntities(entity, icamera, partialTicks);
            RenderHelper.disableStandardItemLighting();
            this.disableLightmap();
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            if (this.mc.objectMouseOver != null && entity.isInsideOfMaterial(Material.water) && flag) {
                final EntityPlayer entityplayer = (EntityPlayer)entity;
                GlStateManager.disableAlpha();
                this.mc.mcProfiler.endStartSection("outline");
                renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, partialTicks);
                GlStateManager.enableAlpha();
            }
        }
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        if (flag && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.water)) {
            final EntityPlayer entityplayer2 = (EntityPlayer)entity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            renderglobal.drawSelectionBox(entityplayer2, this.mc.objectMouseOver, 0, partialTicks);
            GlStateManager.enableAlpha();
        }
        this.mc.mcProfiler.endStartSection("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), entity, partialTicks);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        GlStateManager.disableBlend();
        if (!this.debugView) {
            this.enableLightmap();
            this.mc.mcProfiler.endStartSection("litParticles");
            effectrenderer.renderLitParticles(entity, partialTicks);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, partialTicks);
            this.mc.mcProfiler.endStartSection("particles");
            effectrenderer.renderParticles(entity, partialTicks);
            this.disableLightmap();
        }
        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("weather");
        this.renderRainSnow(partialTicks);
        GlStateManager.depthMask(true);
        renderglobal.renderWorldBorder(entity, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.alphaFunc(516, 0.1f);
        this.setupFog(0, partialTicks);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.shadeModel(7425);
        this.mc.mcProfiler.endStartSection("translucent");
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, pass, entity);
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();
        if (entity.posY + entity.getEyeHeight() >= 128.0) {
            this.mc.mcProfiler.endStartSection("aboveClouds");
            this.renderCloudsCheck(renderglobal, partialTicks, pass);
        }
        this.mc.mcProfiler.endStartSection("hand");
        if (this.renderHand) {
            GlStateManager.clear(256);
            this.renderHand(partialTicks, pass);
            this.renderWorldDirections(partialTicks);
        }
    }
    
    private void renderCloudsCheck(final RenderGlobal renderGlobalIn, final float partialTicks, final int pass) {
        if (this.mc.gameSettings.func_181147_e() != 0) {
            this.mc.mcProfiler.endStartSection("clouds");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), this.mc.displayWidth / (float)this.mc.displayHeight, 0.05f, this.farPlaneDistance * 4.0f);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            this.setupFog(0, partialTicks);
            renderGlobalIn.renderClouds(partialTicks, pass);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), this.mc.displayWidth / (float)this.mc.displayHeight, 0.05f, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }
    }
    
    private void addRainParticles() {
        float f = this.mc.theWorld.getRainStrength(1.0f);
        if (!this.mc.gameSettings.fancyGraphics) {
            f /= 2.0f;
        }
        if (f != 0.0f) {
            this.random.setSeed(this.rendererUpdateCount * 312987231L);
            final Entity entity = this.mc.getRenderViewEntity();
            final World world = this.mc.theWorld;
            final BlockPos blockpos = new BlockPos(entity);
            final int i = 10;
            double d0 = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            int j = 0;
            int k = (int)(100.0f * f * f);
            if (this.mc.gameSettings.particleSetting == 1) {
                k >>= 1;
            }
            else if (this.mc.gameSettings.particleSetting == 2) {
                k = 0;
            }
            for (int l = 0; l < k; ++l) {
                final BlockPos blockpos2 = world.getPrecipitationHeight(blockpos.add(this.random.nextInt(i) - this.random.nextInt(i), 0, this.random.nextInt(i) - this.random.nextInt(i)));
                final BiomeGenBase biomegenbase = world.getBiomeGenForCoords(blockpos2);
                final BlockPos blockpos3 = blockpos2.down();
                final Block block = world.getBlockState(blockpos3).getBlock();
                if (blockpos2.getY() <= blockpos.getY() + i && blockpos2.getY() >= blockpos.getY() - i && biomegenbase.canSpawnLightningBolt() && biomegenbase.getFloatTemperature(blockpos2) >= 0.15f) {
                    final double d4 = this.random.nextDouble();
                    final double d5 = this.random.nextDouble();
                    if (block.getMaterial() == Material.lava) {
                        this.mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, blockpos2.getX() + d4, blockpos2.getY() + 0.1f - block.getBlockBoundsMinY(), blockpos2.getZ() + d5, 0.0, 0.0, 0.0, new int[0]);
                    }
                    else if (block.getMaterial() != Material.air) {
                        block.setBlockBoundsBasedOnState(world, blockpos3);
                        ++j;
                        if (this.random.nextInt(j) == 0) {
                            d0 = blockpos3.getX() + d4;
                            d2 = blockpos3.getY() + 0.1f + block.getBlockBoundsMaxY() - 1.0;
                            d3 = blockpos3.getZ() + d5;
                        }
                        this.mc.theWorld.spawnParticle(EnumParticleTypes.WATER_DROP, blockpos3.getX() + d4, blockpos3.getY() + 0.1f + block.getBlockBoundsMaxY(), blockpos3.getZ() + d5, 0.0, 0.0, 0.0, new int[0]);
                    }
                }
            }
            if (j > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
                this.rainSoundCounter = 0;
                if (d2 > blockpos.getY() + 1 && world.getPrecipitationHeight(blockpos).getY() > MathHelper.floor_float((float)blockpos.getY())) {
                    this.mc.theWorld.playSound(d0, d2, d3, "ambient.weather.rain", 0.1f, 0.5f, false);
                }
                else {
                    this.mc.theWorld.playSound(d0, d2, d3, "ambient.weather.rain", 0.2f, 1.0f, false);
                }
            }
        }
    }
    
    protected void renderRainSnow(final float partialTicks) {
        final float f = this.mc.theWorld.getRainStrength(partialTicks);
        if (f > 0.0f) {
            this.enableLightmap();
            final Entity entity = this.mc.getRenderViewEntity();
            final World world = this.mc.theWorld;
            final int i = MathHelper.floor_double(entity.posX);
            final int j = MathHelper.floor_double(entity.posY);
            final int k = MathHelper.floor_double(entity.posZ);
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GlStateManager.disableCull();
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.alphaFunc(516, 0.1f);
            final double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            final double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            final double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            final int l = MathHelper.floor_double(d2);
            int i2 = 5;
            if (this.mc.gameSettings.fancyGraphics) {
                i2 = 10;
            }
            int j2 = -1;
            final float f2 = this.rendererUpdateCount + partialTicks;
            worldrenderer.setTranslation(-d0, -d2, -d3);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            for (int k2 = k - i2; k2 <= k + i2; ++k2) {
                for (int l2 = i - i2; l2 <= i + i2; ++l2) {
                    final int i3 = (k2 - k + 16) * 32 + l2 - i + 16;
                    final double d4 = this.rainXCoords[i3] * 0.5;
                    final double d5 = this.rainYCoords[i3] * 0.5;
                    blockpos$mutableblockpos.func_181079_c(l2, 0, k2);
                    final BiomeGenBase biomegenbase = world.getBiomeGenForCoords(blockpos$mutableblockpos);
                    if (biomegenbase.canSpawnLightningBolt() || biomegenbase.getEnableSnow()) {
                        final int j3 = world.getPrecipitationHeight(blockpos$mutableblockpos).getY();
                        int k3 = j - i2;
                        int l3 = j + i2;
                        if (k3 < j3) {
                            k3 = j3;
                        }
                        if (l3 < j3) {
                            l3 = j3;
                        }
                        int i4;
                        if ((i4 = j3) < l) {
                            i4 = l;
                        }
                        if (k3 != l3) {
                            this.random.setSeed(l2 * l2 * 3121 + l2 * 45238971 ^ k2 * k2 * 418711 + k2 * 13761);
                            blockpos$mutableblockpos.func_181079_c(l2, k3, k2);
                            final float f3 = biomegenbase.getFloatTemperature(blockpos$mutableblockpos);
                            if (world.getWorldChunkManager().getTemperatureAtHeight(f3, j3) >= 0.15f) {
                                if (j2 != 0) {
                                    if (j2 >= 0) {
                                        tessellator.draw();
                                    }
                                    j2 = 0;
                                    this.mc.getTextureManager().bindTexture(EntityRenderer.locationRainPng);
                                    worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }
                                final double d6 = ((this.rendererUpdateCount + l2 * l2 * 3121 + l2 * 45238971 + k2 * k2 * 418711 + k2 * 13761 & 0x1F) + (double)partialTicks) / 32.0 * (3.0 + this.random.nextDouble());
                                final double d7 = l2 + 0.5f - entity.posX;
                                final double d8 = k2 + 0.5f - entity.posZ;
                                final float f4 = MathHelper.sqrt_double(d7 * d7 + d8 * d8) / i2;
                                final float f5 = ((1.0f - f4 * f4) * 0.5f + 0.5f) * f;
                                blockpos$mutableblockpos.func_181079_c(l2, i4, k2);
                                final int j4 = world.getCombinedLight(blockpos$mutableblockpos, 0);
                                final int k4 = j4 >> 16 & 0xFFFF;
                                final int l4 = j4 & 0xFFFF;
                                worldrenderer.pos(l2 - d4 + 0.5, k3, k2 - d5 + 0.5).tex(0.0, k3 * 0.25 + d6).color(1.0f, 1.0f, 1.0f, f5).lightmap(k4, l4).endVertex();
                                worldrenderer.pos(l2 + d4 + 0.5, k3, k2 + d5 + 0.5).tex(1.0, k3 * 0.25 + d6).color(1.0f, 1.0f, 1.0f, f5).lightmap(k4, l4).endVertex();
                                worldrenderer.pos(l2 + d4 + 0.5, l3, k2 + d5 + 0.5).tex(1.0, l3 * 0.25 + d6).color(1.0f, 1.0f, 1.0f, f5).lightmap(k4, l4).endVertex();
                                worldrenderer.pos(l2 - d4 + 0.5, l3, k2 - d5 + 0.5).tex(0.0, l3 * 0.25 + d6).color(1.0f, 1.0f, 1.0f, f5).lightmap(k4, l4).endVertex();
                            }
                            else {
                                if (j2 != 1) {
                                    if (j2 >= 0) {
                                        tessellator.draw();
                                    }
                                    j2 = 1;
                                    this.mc.getTextureManager().bindTexture(EntityRenderer.locationSnowPng);
                                    worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }
                                final double d9 = ((this.rendererUpdateCount & 0x1FF) + partialTicks) / 512.0f;
                                final double d10 = this.random.nextDouble() + f2 * 0.01 * (float)this.random.nextGaussian();
                                final double d11 = this.random.nextDouble() + f2 * (float)this.random.nextGaussian() * 0.001;
                                final double d12 = l2 + 0.5f - entity.posX;
                                final double d13 = k2 + 0.5f - entity.posZ;
                                final float f6 = MathHelper.sqrt_double(d12 * d12 + d13 * d13) / i2;
                                final float f7 = ((1.0f - f6 * f6) * 0.3f + 0.5f) * f;
                                blockpos$mutableblockpos.func_181079_c(l2, i4, k2);
                                final int i5 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
                                final int j5 = i5 >> 16 & 0xFFFF;
                                final int k5 = i5 & 0xFFFF;
                                worldrenderer.pos(l2 - d4 + 0.5, k3, k2 - d5 + 0.5).tex(0.0 + d10, k3 * 0.25 + d9 + d11).color(1.0f, 1.0f, 1.0f, f7).lightmap(j5, k5).endVertex();
                                worldrenderer.pos(l2 + d4 + 0.5, k3, k2 + d5 + 0.5).tex(1.0 + d10, k3 * 0.25 + d9 + d11).color(1.0f, 1.0f, 1.0f, f7).lightmap(j5, k5).endVertex();
                                worldrenderer.pos(l2 + d4 + 0.5, l3, k2 + d5 + 0.5).tex(1.0 + d10, l3 * 0.25 + d9 + d11).color(1.0f, 1.0f, 1.0f, f7).lightmap(j5, k5).endVertex();
                                worldrenderer.pos(l2 - d4 + 0.5, l3, k2 - d5 + 0.5).tex(0.0 + d10, l3 * 0.25 + d9 + d11).color(1.0f, 1.0f, 1.0f, f7).lightmap(j5, k5).endVertex();
                            }
                        }
                    }
                }
            }
            if (j2 >= 0) {
                tessellator.draw();
            }
            worldrenderer.setTranslation(0.0, 0.0, 0.0);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1f);
            this.disableLightmap();
        }
    }
    
    public void setupOverlayRendering() {
        final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
    }
    
    private void updateFogColor(final float partialTicks) {
        final World world = this.mc.theWorld;
        final Entity entity = this.mc.getRenderViewEntity();
        float f = 0.25f + 0.75f * this.mc.gameSettings.renderDistanceChunks / 32.0f;
        f = 1.0f - (float)Math.pow(f, 0.25);
        final Vec3 vec3 = world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
        final float f2 = (float)vec3.xCoord;
        final float f3 = (float)vec3.yCoord;
        final float f4 = (float)vec3.zCoord;
        final Vec3 vec4 = world.getFogColor(partialTicks);
        this.fogColorRed = (float)vec4.xCoord;
        this.fogColorGreen = (float)vec4.yCoord;
        this.fogColorBlue = (float)vec4.zCoord;
        if (this.mc.gameSettings.renderDistanceChunks >= 4) {
            final double d0 = -1.0;
            final Vec3 vec5 = (MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) > 0.0f) ? new Vec3(d0, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
            float f5 = (float)entity.getLook(partialTicks).dotProduct(vec5);
            if (f5 < 0.0f) {
                f5 = 0.0f;
            }
            if (f5 > 0.0f) {
                final float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
                if (afloat != null) {
                    f5 *= afloat[3];
                    this.fogColorRed = this.fogColorRed * (1.0f - f5) + afloat[0] * f5;
                    this.fogColorGreen = this.fogColorGreen * (1.0f - f5) + afloat[1] * f5;
                    this.fogColorBlue = this.fogColorBlue * (1.0f - f5) + afloat[2] * f5;
                }
            }
        }
        this.fogColorRed += (f2 - this.fogColorRed) * f;
        this.fogColorGreen += (f3 - this.fogColorGreen) * f;
        this.fogColorBlue += (f4 - this.fogColorBlue) * f;
        final float f6 = world.getRainStrength(partialTicks);
        if (f6 > 0.0f) {
            final float f7 = 1.0f - f6 * 0.5f;
            final float f8 = 1.0f - f6 * 0.4f;
            this.fogColorRed *= f7;
            this.fogColorGreen *= f7;
            this.fogColorBlue *= f8;
        }
        final float f9 = world.getThunderStrength(partialTicks);
        if (f9 > 0.0f) {
            final float f10 = 1.0f - f9 * 0.5f;
            this.fogColorRed *= f10;
            this.fogColorGreen *= f10;
            this.fogColorBlue *= f10;
        }
        final Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
        if (this.cloudFog) {
            final Vec3 vec6 = world.getCloudColour(partialTicks);
            this.fogColorRed = (float)vec6.xCoord;
            this.fogColorGreen = (float)vec6.yCoord;
            this.fogColorBlue = (float)vec6.zCoord;
        }
        else if (block.getMaterial() == Material.water) {
            float f11 = EnchantmentHelper.getRespiration(entity) * 0.2f;
            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.waterBreathing)) {
                f11 = f11 * 0.3f + 0.6f;
            }
            this.fogColorRed = 0.02f + f11;
            this.fogColorGreen = 0.02f + f11;
            this.fogColorBlue = 0.2f + f11;
        }
        else if (block.getMaterial() == Material.lava) {
            this.fogColorRed = 0.6f;
            this.fogColorGreen = 0.1f;
            this.fogColorBlue = 0.0f;
        }
        final float f12 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
        this.fogColorRed *= f12;
        this.fogColorGreen *= f12;
        this.fogColorBlue *= f12;
        double d2 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) * world.provider.getVoidFogYFactor();
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.blindness)) {
            final int i = ((EntityLivingBase)entity).getActivePotionEffect(Potion.blindness).getDuration();
            if (i < 20) {
                d2 *= 1.0f - i / 20.0f;
            }
            else {
                d2 = 0.0;
            }
        }
        if (d2 < 1.0) {
            if (d2 < 0.0) {
                d2 = 0.0;
            }
            d2 *= d2;
            this.fogColorRed *= (float)d2;
            this.fogColorGreen *= (float)d2;
            this.fogColorBlue *= (float)d2;
        }
        if (this.bossColorModifier > 0.0f) {
            final float f13 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
            this.fogColorRed = this.fogColorRed * (1.0f - f13) + this.fogColorRed * 0.7f * f13;
            this.fogColorGreen = this.fogColorGreen * (1.0f - f13) + this.fogColorGreen * 0.6f * f13;
            this.fogColorBlue = this.fogColorBlue * (1.0f - f13) + this.fogColorBlue * 0.6f * f13;
        }
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.nightVision)) {
            final float f14 = this.getNightVisionBrightness((EntityLivingBase)entity, partialTicks);
            float f15 = 1.0f / this.fogColorRed;
            if (f15 > 1.0f / this.fogColorGreen) {
                f15 = 1.0f / this.fogColorGreen;
            }
            if (f15 > 1.0f / this.fogColorBlue) {
                f15 = 1.0f / this.fogColorBlue;
            }
            this.fogColorRed = this.fogColorRed * (1.0f - f14) + this.fogColorRed * f15 * f14;
            this.fogColorGreen = this.fogColorGreen * (1.0f - f14) + this.fogColorGreen * f15 * f14;
            this.fogColorBlue = this.fogColorBlue * (1.0f - f14) + this.fogColorBlue * f15 * f14;
        }
        if (this.mc.gameSettings.anaglyph) {
            final float f16 = (this.fogColorRed * 30.0f + this.fogColorGreen * 59.0f + this.fogColorBlue * 11.0f) / 100.0f;
            final float f17 = (this.fogColorRed * 30.0f + this.fogColorGreen * 70.0f) / 100.0f;
            final float f18 = (this.fogColorRed * 30.0f + this.fogColorBlue * 70.0f) / 100.0f;
            this.fogColorRed = f16;
            this.fogColorGreen = f17;
            this.fogColorBlue = f18;
        }
        GlStateManager.clearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0f);
    }
    
    private void setupFog(final int p_78468_1_, final float partialTicks) {
        final Entity entity = this.mc.getRenderViewEntity();
        boolean flag = false;
        if (entity instanceof EntityPlayer) {
            flag = ((EntityPlayer)entity).capabilities.isCreativeMode;
        }
        GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0f));
        GL11.glNormal3f(0.0f, -1.0f, 0.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        final Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.blindness)) {
            float f1 = 5.0f;
            final int i = ((EntityLivingBase)entity).getActivePotionEffect(Potion.blindness).getDuration();
            if (i < 20) {
                f1 = 5.0f + (this.farPlaneDistance - 5.0f) * (1.0f - i / 20.0f);
            }
            GlStateManager.setFog(9729);
            if (p_78468_1_ == -1) {
                GlStateManager.setFogStart(0.0f);
                GlStateManager.setFogEnd(f1 * 0.8f);
            }
            else {
                GlStateManager.setFogStart(f1 * 0.25f);
                GlStateManager.setFogEnd(f1);
            }
            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                GL11.glFogi(34138, 34139);
            }
        }
        else if (this.cloudFog) {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(0.1f);
        }
        else if (block.getMaterial() == Material.water) {
            GlStateManager.setFog(2048);
            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.waterBreathing)) {
                GlStateManager.setFogDensity(0.01f);
            }
            else {
                GlStateManager.setFogDensity(0.1f - EnchantmentHelper.getRespiration(entity) * 0.03f);
            }
        }
        else if (block.getMaterial() == Material.lava) {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(2.0f);
        }
        else {
            final float f2 = this.farPlaneDistance;
            GlStateManager.setFog(9729);
            if (p_78468_1_ == -1) {
                GlStateManager.setFogStart(0.0f);
                GlStateManager.setFogEnd(f2);
            }
            else {
                GlStateManager.setFogStart(f2 * 0.75f);
                GlStateManager.setFogEnd(f2);
            }
            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                GL11.glFogi(34138, 34139);
            }
            if (this.mc.theWorld.provider.doesXZShowFog((int)entity.posX, (int)entity.posZ)) {
                GlStateManager.setFogStart(f2 * 0.05f);
                GlStateManager.setFogEnd(Math.min(f2, 192.0f) * 0.5f);
            }
        }
        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }
    
    private FloatBuffer setFogColorBuffer(final float red, final float green, final float blue, final float alpha) {
        this.fogColorBuffer.clear();
        this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
        this.fogColorBuffer.flip();
        return this.fogColorBuffer;
    }
    
    public MapItemRenderer getMapItemRenderer() {
        return this.theMapItemRenderer;
    }
}
