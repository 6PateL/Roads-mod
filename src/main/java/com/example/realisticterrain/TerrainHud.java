package com.example.realisticterrain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Map;

/** Іконка поверхні справа знизу + підпис («Бруд -40%»). */
@Mod.EventBusSubscriber(modid = RealisticTerrain.MOD_ID, value = Dist.CLIENT)
public final class TerrainHud {
    private static final int TEX = 16;
    private static final Map<TerrainState, ResourceLocation> ICONS = new EnumMap<>(TerrainState.class);

    static {
        for (TerrainState s : TerrainState.values()) {
            if (s != TerrainState.NONE) {
                ICONS.put(s, new ResourceLocation(RealisticTerrain.MOD_ID, "textures/gui/state/" + s.key + ".png"));
            }
        }
    }

    private TerrainHud() {}

    @SubscribeEvent
    public static void onRender(RenderGuiEvent.Post event) {
        TerrainConfig cfg = TerrainConfig.get();
        Minecraft mc = Minecraft.getInstance();
        if (!cfg.showHud || mc.player == null || mc.options.hideGui) return;

        TerrainState state = ClientTerrainState.state();
        ResourceLocation icon = ICONS.get(state);
        if (icon == null) return;

        GuiGraphics g = event.getGuiGraphics();
        int scale = Math.max(1, Math.min(3, cfg.hudScale));
        int size = TEX * scale;
        int x = event.getWindow().getGuiScaledWidth() - size - cfg.hudOffsetX;
        int y = event.getWindow().getGuiScaledHeight() - size - cfg.hudOffsetY;

        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1f);
        g.blit(icon, 0, 0, 0, 0, TEX, TEX, TEX, TEX);
        g.pose().popPose();

        MutableComponent label = Component.translatable("realistic_terrain.state." + state.key);
        int percent = ClientTerrainState.percent();
        if (percent > 0) label.append(" -" + percent + "%");
        if (ClientTerrainState.mounted()) {
            label.append(" (").append(Component.translatable("realistic_terrain.mounted")).append(")");
        }
        int textX = x - 4 - mc.font.width(label);
        int textY = y + (size - mc.font.lineHeight) / 2;
        g.drawString(mc.font, label, textX, textY, state.color, true);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientTerrainState.reset();
    }
}
