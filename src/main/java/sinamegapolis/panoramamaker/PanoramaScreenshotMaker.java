/**
 * This class was created by <SinaMegapolis>. It's distributed as
 * part of the PanoramaMaker Mod. Get the Source Code in github:
 * https://github.com/SinaMegapolis/PanoramaMaker
 *
 * PanoramaMaker is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 */
package sinamegapolis.panoramamaker;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PanoramaScreenshotMaker {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    File panoramaDir;
    File currentDir;
    float rotationYaw, rotationPitch;
    int panoramaStep;
    boolean takingPanorama;
    int currentWidth, currentHeight;
    boolean overridenOnce;

    int panoramaSize = PanoramaMakerConfig.panoramaSize;
    boolean fullscreen = PanoramaMakerConfig.fullscreen;

    /*@SubscribeEvent
    public void loadMainMenu(GuiOpenEvent event) {
        if(PanoramaMakerConfig.overrideMainMenu && !overridenOnce && event.getGui() instanceof MainMenuScreen) {
            Minecraft mc = Minecraft.getInstance();
            File mcDir = mc.gameDir;
            File panoramasDir = new File(mcDir, "/screenshots/panoramas");

            List<File[]> validFiles = new ArrayList();

            ImmutableSet<String> set = ImmutableSet.of("panorama_0.png", "panorama_1.png", "panorama_2.png", "panorama_3.png", "panorama_4.png", "panorama_5.png");

            if(panoramasDir.exists()) {
                File[] subDirs;

                File mainMenu = new File(panoramasDir, "main_menu");
                if(mainMenu.exists())
                    subDirs = new File[] { mainMenu };
                else subDirs = panoramasDir.listFiles((File f) -> f.isDirectory() && !f.getName().endsWith("fullres"));

                for(File f : subDirs)
                    if(set.stream().allMatch((String s) -> new File(f, s).exists()))
                        validFiles.add(f.listFiles((File f1) -> set.contains(f1.getName())));
            }

            if(!validFiles.isEmpty()) {
                File[] files = validFiles.get(new Random().nextInt(validFiles.size()));
                Arrays.sort(files);

                ResourceLocation[] resources = new ResourceLocation[6];

                for(int i = 0; i < resources.length; i++) {
                    File f = files[i];
                    try {
                        DynamicTexture tex = new DynamicTexture(NativeImage.read(new FileInputStream(f)));
                        String name = PanoramaMaker.MODID + ":" + f.getName();

                        resources[i] = mc.getTextureManager().getDynamicTextureLocation(name, tex);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                try {
                    Field field = ObfuscationReflectionHelper.findField(MainMenuScreen.class, "field_213098_a");
                    field.setAccessible(true);

                    if(Modifier.isFinal(field.getModifiers())) {
                        Field modfield = Field.class.getDeclaredField("modifiers");
                        modfield.setAccessible(true);
                        modfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                    }

                    field.set(null, new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            overridenOnce = true;
        }
    }*/

    @SubscribeEvent
    public void takeScreenshot(ScreenshotEvent event) {
        if(takingPanorama)
            return;

        if(Screen.hasControlDown() && Screen.hasShiftDown() && Minecraft.getInstance().currentScreen == null) {
            takingPanorama = true;
            panoramaStep = 0;

            if(panoramaDir == null)
                panoramaDir = new File(event.getScreenshotFile().getParentFile(), "panoramas");
            if(!panoramaDir.exists())
                panoramaDir.mkdirs();

            int i = 0;
            String ts = getTimestamp();
            do {
                if(fullscreen) {
                    if(i == 0)
                        currentDir = new File(panoramaDir + "_fullres", ts);
                    else currentDir = new File(panoramaDir, ts + "_" + i + "_fullres");
                } else {
                    if(i == 0)
                        currentDir = new File(panoramaDir, ts);
                    else currentDir = new File(panoramaDir, ts + "_" + i);
                }
            } while(currentDir.exists());

            currentDir.mkdirs();

            event.setResultMessage(new StringTextComponent(""));;
            event.setCanceled(true);

            ITextComponent panoramaDirComponent = new StringTextComponent(currentDir.getName());
            panoramaDirComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, currentDir.getAbsolutePath())).setUnderlined(true);
            event.setResultMessage(new TranslationTextComponent("text.panoramamaker.panorama_saved", panoramaDirComponent));
        }
    }

    @SubscribeEvent
    public void renderTick(RenderTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if(takingPanorama) {
            if(event.phase == Phase.START) {
                if(panoramaStep == 0) {
                    mc.gameSettings.hideGUI = true;
                    currentWidth = mc.mainWindow.getWidth();
                    currentHeight = mc.mainWindow.getHeight();
                    rotationYaw = mc.player.rotationYaw;
                    rotationPitch = mc.player.rotationPitch;

                    if(!fullscreen)
                        resize(panoramaSize, panoramaSize);
                }

                switch(panoramaStep) {
                    case 1:
                        mc.player.rotationYaw = 180;
                        mc.player.rotationPitch = 0;
                        break;
                    case 2:
                        mc.player.rotationYaw = -90;
                        mc.player.rotationPitch = 0;
                        break;
                    case 3:
                        mc.player.rotationYaw = 0;
                        mc.player.rotationPitch = 0;
                        break;
                    case 4:
                        mc.player.rotationYaw = 90;
                        mc.player.rotationPitch = 0;
                        break;
                    case 5:
                        mc.player.rotationYaw = 180;
                        mc.player.rotationPitch = -90;
                        break;
                    case 6:
                        mc.player.rotationYaw = 180;
                        mc.player.rotationPitch = 90;
                        break;
                }
                mc.player.prevRotationYaw = mc.player.rotationYaw;
                mc.player.prevRotationPitch = mc.player.rotationPitch;
            } else {
                if(panoramaStep > 0)
                    saveScreenshot(currentDir, "panorama_" + (panoramaStep - 1) + ".png", mc.mainWindow.getWidth(), mc.mainWindow.getHeight(), mc.getFramebuffer());
                panoramaStep++;
                if(panoramaStep == 7) {
                    mc.gameSettings.hideGUI = false;
                    takingPanorama = false;

                    mc.player.rotationYaw = rotationYaw;
                    mc.player.rotationPitch = rotationPitch;
                    mc.player.prevRotationYaw = mc.player.rotationYaw;
                    mc.player.prevRotationPitch = mc.player.rotationPitch;

                    resize(currentWidth, currentHeight);
                }
            }
        }
    }
    
    public static void resize(int width, int height) {
    	GlStateManager.viewport(0, 0, width, height);
    }

	private static void saveScreenshot(File dir, String screenshotName, int width, int height, Framebuffer buffer) {
        try {
            NativeImage nativeImage = ScreenShotHelper.createScreenshot(width, height, buffer);
            File file2 = new File(dir, screenshotName);
            nativeImage.write(file2);

            net.minecraftforge.client.ForgeHooksClient.onScreenshot(nativeImage, file2);
        } catch(Exception exception) { }
    }

    private static String getTimestamp() {
        String s = DATE_FORMAT.format(new Date()).toString();
        return s;
    }

    @SubscribeEvent
    public void onEnteringWorld(EntityJoinWorldEvent event){
        if(event.getEntity()instanceof PlayerEntity && event.getWorld().isRemote){
            ((PlayerEntity) event.getEntity()).sendStatusMessage(new TranslationTextComponent("text.panoramamaker.enter_world"), false);
        }
    }
}
