package stellarapi.api;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import stellarapi.api.world.IWorldProviderReplacer;
import stellarapi.api.world.worldset.WorldSet;
import stellarapi.api.world.worldset.WorldSetFactory;

public interface IReference {
	public IPerWorldReference getPerWorldReference(World world);

	public World getDefaultWorld(boolean isRemote);


	public void registerFactory(WorldSetFactory factory);

	public ImmutableList<WorldSet> getAllWorldSets();
	public WorldSet[] getGeneratedWorldSets(ResourceLocation location);
	public WorldSet getPrimaryWorldSet(World world);
	public ImmutableList<WorldSet> appliedWorldSets(World world);

	public IWorldProviderReplacer getDefaultReplacer();

	public ICelestialScene getActivePack(World world);
}
