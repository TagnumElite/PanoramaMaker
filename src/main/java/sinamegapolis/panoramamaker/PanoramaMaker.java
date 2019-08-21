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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PanoramaMaker.MODID)
public class PanoramaMaker {
    public static final String MODID = "panoramamaker";
    
    public PanoramaMaker() {
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);
    	
    	MinecraftForge.EVENT_BUS.register(new PanoramaScreenshotMaker());
    	
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PanoramaMakerConfig.CLIENT_SPEC);
    }
    
    public void modConfig(ModConfig.ModConfigEvent event) {
    	ModConfig config = event.getConfig();
    	if (config.getSpec() == PanoramaMakerConfig.CLIENT_SPEC) {
    		PanoramaMakerConfig.refreshClient();
    	}
    }
}
