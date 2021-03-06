package stellarapi.example.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import stellarapi.api.world.ICelestialHelper;
import stellarapi.api.world.IWorldProviderReplacer;

public enum WorldReplacerEnd implements IWorldProviderReplacer {
	INSTANCE;

	@Override
	public boolean accept(World world, WorldProvider provider) {
		return provider instanceof WorldProviderEnd;
	}

	@Override
	public WorldProvider createWorldProvider(World world, WorldProvider originalProvider, ICelestialHelper helper) {
		return new WorldProviderRepEnd(world, (WorldProviderEnd)originalProvider, helper);
	}
}
