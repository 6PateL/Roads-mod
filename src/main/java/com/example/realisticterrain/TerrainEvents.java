package com.example.realisticterrain;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealisticTerrain.MOD_ID)
public final class TerrainEvents {
    private TerrainEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        int interval = Math.max(1, TerrainConfig.get().checkIntervalTicks);
        if (player.tickCount % interval != 0) return;

        TerrainSlowdown.update(player);
    }

    /** Зіскочили зі скакуна — знімаємо з нього штраф, щоб кінь не лишався повільним. */
    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        if (!event.isDismounting()) return;
        if (event.getEntityMounting().level().isClientSide()) return;
        if (event.getEntityBeingMounted() instanceof LivingEntity mount) {
            TerrainSlowdown.clear(mount);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.getVehicle() instanceof LivingEntity mount) TerrainSlowdown.clear(mount);
            TerrainSlowdown.forget(player.getUUID());
        }
    }
}
