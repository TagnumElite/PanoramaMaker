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

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class PanoramaMakerConfig {
	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	
	static {
		final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = specPair.getLeft();
		CLIENT_SPEC = specPair.getRight();
	}
	
	public static boolean overrideMainMenu = true;
	public static boolean fullscreen = false;
	public static int panoramaSize = 256;
	
	public static class ClientConfig {
		public final ForgeConfigSpec.BooleanValue overrideMainMenu;
		public final ForgeConfigSpec.BooleanValue fullscreen;
		public final ForgeConfigSpec.ConfigValue<Integer> panoramaSize;

		ClientConfig(ForgeConfigSpec.Builder builder) {
			builder.comment("Options for the panorama maker.")
			.push("panorama");
			
			overrideMainMenu = builder
					.comment("Use panorama screenshots in the main menu")
					.translation("text.panormamamaker.config.override_main_menu")
					.define("overrideMainMenu", true);
			
			fullscreen = builder
					.comment("Fullres screenshots: Take panorama screenshots without changing the render size")
					.translation("text.panoramamaker.config.fullscreen")
					.define("fullscreen", false);
			panoramaSize = builder
					.comment("Panorama Picture Resolution")
					.translation("text.panoramamaker.config.panorama_size")
					.define("panoramaSize", 256);
			
			builder.pop();
		}
	}
	
	public static void refreshClient() {
		overrideMainMenu = CLIENT.overrideMainMenu.get();
		fullscreen = CLIENT.fullscreen.get();
		panoramaSize = CLIENT.panoramaSize.get();
	}
}
