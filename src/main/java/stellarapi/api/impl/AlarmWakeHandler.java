package stellarapi.api.impl;

import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import stellarapi.api.CelestialLightSources;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.mc.EnumDaytimeDescriptor;
import stellarapi.api.mc.IWakeHandler;
import stellarapi.util.math.Spmath;

/**
 * Example of wake handler,
 * as player gets up on certain amount of time after midnight.
 * Note that the day is checked as standard of primary light source.
 * */
public class AlarmWakeHandler implements IWakeHandler {

	//Wake time from midnight
	private int wakeTime;

	@Override
	public boolean accept(World world, CelestialLightSources lightSource, ICelestialCoordinate coordinate) {
		return true;
	}

	@Override
	public long getWakeTime(World world, CelestialLightSources lightSources, ICelestialCoordinate coordinate,
			long sleepTime) {
		long nextMidnight = StellarAPIReference.getDaytimeChecker().timeForCertainDescriptor(world,
				EnumDaytimeDescriptor.MIDNIGHT, sleepTime);
		CelestialPeriod period = lightSources.getPrimarySource().getHorizontalPeriod();
		double currentOffset = period.getOffset(sleepTime, 0.0f);
		double midnightOffset = period.getOffset(nextMidnight, 0.0f);

		if(currentOffset < midnightOffset)
			return nextMidnight + this.wakeTime;
		else return nextMidnight - (long)period.getPeriodLength() + this.wakeTime;
	}
	
	@Override
	public EnumStatus getSleepPossibility(World world, CelestialLightSources lightSources,
			ICelestialCoordinate coordinate, long sleepTime) {
		long nextMidnight = StellarAPIReference.getDaytimeChecker().timeForCertainDescriptor(world,
				EnumDaytimeDescriptor.MIDNIGHT, sleepTime);
		CelestialPeriod period = lightSources.getPrimarySource().getHorizontalPeriod();
		double currentOffset = period.getOffset(sleepTime, 0.0f);
		double midnightOffset = period.getOffset(nextMidnight, 0.0f);
		double diff = Spmath.fmod(currentOffset - midnightOffset, 1.0);
		
		return (!world.isDaytime() && (diff < 0.25 || diff > 0.75))? EnumStatus.OK : EnumStatus.NOT_POSSIBLE_NOW;
	}


	@Override
	public void setupConfig(Configuration config, String category) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		// TODO Auto-generated method stub
		
	}
	
}
