package stellarapi.impl;

import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.SAPIReferences;
import stellarapi.api.celestials.CelestialEffectors;
import stellarapi.api.daywake.EnumDaytimeDescriptor;
import stellarapi.api.daywake.IWakeHandler;

/**
 * Example of wake handler, as player gets up on certain amount of time after
 * midnight. Note that the day is checked as standard of primary light source.
 */
public class AlarmWakeHandler implements IWakeHandler {

	// Wake time from midnight
	private int wakeTime;

	@Override
	public boolean accept(World world, CelestialEffectors lightSources, ICelestialCoordinates coordinate) {
		return lightSources != null;
	}

	@Override
	public long getWakeTime(World world, CelestialEffectors lightSources, ICelestialCoordinates coordinate,
			long sleepTime) {
		long nextMidnight = SAPIReferences.getDaytimeChecker().timeForCertainDescriptor(world,
				EnumDaytimeDescriptor.MIDNIGHT, sleepTime);
		CelestialPeriod period = lightSources.getPrimarySource().getHorizontalPeriod();
		double currentOffset = period.getOffset(sleepTime, 0.0f);
		double midnightOffset = period.getOffset(nextMidnight, 0.0f);

		if (currentOffset < midnightOffset)
			return nextMidnight + this.wakeTime;
		else
			return nextMidnight - (long) period.getPeriodLength() + this.wakeTime;
	}

	@Override
	public SleepResult getSleepPossibility(World world, CelestialEffectors lightSources,
			ICelestialCoordinates coordinate, long sleepTime) {
		long nextMidnight = SAPIReferences.getDaytimeChecker().timeForCertainDescriptor(world,
				EnumDaytimeDescriptor.MIDNIGHT, sleepTime);
		CelestialPeriod period = lightSources.getPrimarySource().getHorizontalPeriod();
		double currentOffset = period.getOffset(sleepTime, 0.0f);
		double midnightOffset = period.getOffset(nextMidnight, 0.0f);
		double diff = (currentOffset - midnightOffset) % 1.0;

		return (!world.isDaytime() && (diff < 0.25 || diff > 0.75)) ? SleepResult.OK : SleepResult.NOT_POSSIBLE_NOW;
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryLanguageKey(category, "config.category.alarm");
		config.setCategoryComment(category, "Alarm type wake settings");

		Property pWakeTime = config.get(category, "Wake_Time_from_midnight", 6000);
		pWakeTime.setComment("Wake-up time from midnight, in tick.");
		pWakeTime.setRequiresWorldRestart(true);
		pWakeTime.setLanguageKey("config.property.waketime");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		ConfigCategory cfgCategory = config.getCategory(category);
		this.wakeTime = cfgCategory.get("Wake_Time_from_midnight").getInt();
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
	}

}
