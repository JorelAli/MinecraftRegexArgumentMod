package dev.jorel.regexargmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexArgMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("regexargmod");

	@Override
	public void onInitialize() {
		LOGGER.info("Initialized regex argument mod!");
	}
}
