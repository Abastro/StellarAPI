package worldsets.api.provider;

import java.util.Comparator;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
import worldsets.api.provider.IProviderRegistry.AddCallback;
import worldsets.api.provider.IProviderRegistry.CreateCallback;
import worldsets.api.provider.IProviderRegistry.MissingCallback;
import worldsets.api.provider.IProviderRegistry.SubstitutionCallback;

public class ProviderRegistry {

	private static IInternalProviderHandler registry;

	public static <T extends IProvider> IProviderRegistry<T> findRegistry(Class<T> registryType) {
		return registry.findRegistryByType(registryType);
	}

	public static ImmutableMap<ResourceLocation, IProviderRegistry<?>> getProviderRegistryMap() {
		return registry.getProviderRegistryMap();
	}

	static <T extends IProvider> IProviderRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType,
			boolean addToFirst, Comparator<T> comparator, AddCallback<T> add, CreateCallback<T> create,
			SubstitutionCallback<T> substitution, MissingCallback<T> missing) {
		return registry.createRegistry(registryName, registryType, addToFirst, comparator, add, create, substitution, missing);
	}
}