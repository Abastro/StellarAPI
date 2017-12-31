package worldsets.api;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import stellarapi.api.SAPIReferences;
import worldsets.api.worldset.WorldSet;

/**
 * Central reference for WorldSet API.
 * */
public final class WAPIReferences {

	// ********************************************* //
	// ************** Mod Information ************** //
	// ********************************************* //

	public static final String modid = "worldsetapi";
	public static final String version = "@WSVERSION@";

	// ********************************************* //
	// ************ WorldSet References ************ //
	// ********************************************* //

	public static final ResourceLocation WORLDSETS = new ResourceLocation(SAPIReferences.MODID, "worldsets");

	@ObjectHolder("minecraft:overworld")
	public static final WorldSet overworld = null;

	@ObjectHolder("minecraft:overworldtype")
	public static final WorldSet overworldTypeSet = null;

	@ObjectHolder("minecraft:endtype")
	public static final WorldSet endTypeSet = null;

	@ObjectHolder("minecraft:nethertype")
	public static final WorldSet NetherTypeSet = null;

	// ********************************************* //
	// ************* WorldSet API Calls ************ //
	// ********************************************* //
	private static final WAPIReferences INSTANCE = new WAPIReferences();

	/**
	 * Checks if this world is default.
	 * On server, this checks for overworld.
	 * On client, this checks for the main loaded world.
	 * */
	public static boolean isDefaultWorld(World world) {
		if(world.isRemote)
			return true;
		return world.provider.getDimension() == 0;
	}

	/**
	 * Get one of the default worlds.
	 * */
	public static World getDefaultWorld(boolean isRemote) {
		return INSTANCE.reference.getDefaultWorld(isRemote);
	}

	/**
	 * Gets the list of the world sets.
	 * */
	public static ImmutableList<WorldSet> getAllWorldSets() {
		return reference.getAllWorldSets();
	}

	/**
	 * Gets primary worldset for this world.
	 * */
	public static @Nullable WorldSet getPrimaryWorldSet(World world) {
		return INSTANCE.reference.getPrimaryWorldSet(world);
	}

	/**
	 * Gets applied worldsets for this world.
	 * */
	public static ImmutableList<WorldSet> appliedWorldSets(World world) {
		return INSTANCE.reference.appliedWorldSets(world);
	}

	// ********************************************* //
	// ****************** Internal ***************** //
	// ********************************************* //

	private static IReference reference;

	@Deprecated
	public static void putReference(IReference base) {
		reference = base;
	}
}
