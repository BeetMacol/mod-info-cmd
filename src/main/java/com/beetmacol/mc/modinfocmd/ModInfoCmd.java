package com.beetmacol.mc.modinfocmd;

import com.beetmacol.mc.modinfocmd.command.ModCommand;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ModInfoCmd {
	public static final String MOD_ID = "mod-info-cmd";
	public static final Logger LOGGER = LogManager.getLogger("Mod Info Cmd");

	public static final Map<String, ModMetadata> MODS = new HashMap<>();

	public static final Map<String, String> OUTSTANDING_CONTACT_TYPES = ImmutableMap.of("homepage", "Homepage", "sources", "Sources", "issues", "Issues");

	public static void init() {
		loadModsList();
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(ModCommand.COMMAND));
	}

	public static void loadModsList() {
		MODS.clear();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			MODS.put(mod.getMetadata().getId(), mod.getMetadata());
		}
	}
}
