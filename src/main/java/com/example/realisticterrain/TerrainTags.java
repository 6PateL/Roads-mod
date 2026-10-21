package com.example.realisticterrain;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class TerrainTags {
    public static final TagKey<Block> ROADS = of("roads");
    public static final TagKey<Block> SOFT_GROUND = of("soft_ground");
    public static final TagKey<Block> MUD = of("mud");
    public static final TagKey<Block> SNOW = of("snow");
    public static final TagKey<Block> SAND = of("sand");

    private static TagKey<Block> of(String path) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(RealisticTerrain.MOD_ID, path));
    }

    private TerrainTags() {}
}
