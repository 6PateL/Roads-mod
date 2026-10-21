package com.example.realisticterrain;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RealisticTerrain.MOD_ID)
public class RealisticTerrain {
    public static final String MOD_ID = "realistic_terrain";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealisticTerrain() {
        TerrainConfig.load();
        Network.init();
        LOGGER.info("Realistic Terrain loaded");
    }
}
