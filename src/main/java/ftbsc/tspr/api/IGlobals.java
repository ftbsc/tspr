package ftbsc.tspr.api;

import org.slf4j.Logger;

import net.minecraft.client.Minecraft;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.helpers.Scheduler;

public interface IGlobals {
	public static final Minecraft MC = Minecraft.getInstance();
	public static final Scheduler SCHEDULER = Tiramisuper.SCHEDULER;
	public static final Logger LOGGER = Tiramisuper.LOGGER;
}
