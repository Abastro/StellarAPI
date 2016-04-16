package stellarapi.api.feature;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import stellarapi.api.perdimension.IPerDimensionResourceHandler;

/**
 * Registry for per-dimension resources.
 * */
public class PerDimensionResourceRegistry implements IPerDimensionResourceHandler {
	
	private static final PerDimensionResourceRegistry INSTANCE = new PerDimensionResourceRegistry();
	
	public static PerDimensionResourceRegistry getInstance() {
		return INSTANCE;
	}
	
	private Set<String> resourceIds;
	
	public void registerResourceId(String id)
	{
		resourceIds.add(id);
	}
	
	public ImmutableSet<String> getResourceIds() {
		return ImmutableSet.copyOf(this.resourceIds);
	}

	@Override
	public boolean accept(World world, String resourceId, ResourceLocation previous) {
		return PerDimensionResourceData.getData(world).getResourceMap().containsKey(resourceId);
	}

	@Override
	public ResourceLocation getLocation(World world, String resourceId, ResourceLocation previous) {
		return PerDimensionResourceData.getData(world).getResourceMap().get(resourceId);
	}

}
