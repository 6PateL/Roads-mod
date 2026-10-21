package com.example.realisticterrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TerrainSlowdown {
    private static final UUID MODIFIER_ID = UUID.fromString("7d4a1f0e-5b1c-4c1e-9a77-3f2f6a0c9b11");
    private static final Result NONE = new Result(TerrainState.NONE, 0.0);
    private static final Map<UUID, TerrainStatePacket> LAST_SENT = new HashMap<>();

    private record Result(TerrainState state, double penalty) {}

    private TerrainSlowdown() {}

    /** Викликається з серверного тіку гравця. */
    public static void update(ServerPlayer player) {
        TerrainConfig cfg = TerrainConfig.get();

        // Скакун, яким керує гравець (кінь, віслюк, мул, свиня з морквою на палиці, верблюд...)
        LivingEntity mount = null;
        if (cfg.affectMounts && player.getVehicle() instanceof LivingEntity v && v.getControllingPassenger() == player) {
            mount = v;
        }

        if (mount != null) {
            removeModifier(player);                 // штраф іде на скакуна, не на гравця
            Result r = evaluate(player, mount);
            if (r == null) return;
            smoothTo(mount, r.penalty());
            report(player, r, true);
        } else {
            Result r = evaluate(player, player);
            if (r == null) return;
            smoothTo(player, r.penalty());
            report(player, r, false);
        }
    }

    /** Прибрати штраф зі скакуна (при зіскакуванні/виході з гри). */
    public static void clear(LivingEntity entity) {
        removeModifier(entity);
    }

    public static void forget(UUID playerId) {
        LAST_SENT.remove(playerId);
    }

    // ---------------------------------------------------------------- логіка

    /** body — той, хто реально рухається по землі (гравець або скакун). null = у повітрі, нічого не міняти. */
    private static Result evaluate(ServerPlayer player, Entity body) {
        TerrainConfig cfg = TerrainConfig.get();
        Level level = body.level();

        if (!cfg.enabled) return NONE;
        if (!cfg.enabledDimensions.contains(level.dimension().location().toString())) return NONE;
        if (player.isCreative() || player.isSpectator()) return NONE;
        if (player.getAbilities().flying || player.isFallFlying()) return NONE;
        if (body == player && player.isPassenger()) return NONE;   // сидить у човні/вагонетці/на некерованому
        if (body.isInWater()) return NONE;
        if (!body.onGround()) return null;

        BlockPos feetPos = body.blockPosition();
        BlockPos groundPos = BlockPos.containing(body.getX(), body.getY() - 0.5000001, body.getZ());
        BlockState feet = level.getBlockState(feetPos);
        BlockState ground = level.getBlockState(groundPos);

        // Дорога (або рейки/килим під ногами) — штрафу немає
        if (ground.is(TerrainTags.ROADS) || feet.is(TerrainTags.ROADS)) return new Result(TerrainState.ROAD, 0.0);

        TerrainState state;
        double p;
        if (ground.is(TerrainTags.MUD)) {
            state = TerrainState.MUD;
            p = cfg.mud;
        } else if (feet.is(TerrainTags.SNOW) || ground.is(TerrainTags.SNOW)) {
            state = TerrainState.SNOW;
            p = cfg.snow;
        } else if (ground.is(TerrainTags.SAND)) {
            state = TerrainState.SAND;
            p = cfg.sand;
        } else if (ground.is(TerrainTags.SOFT_GROUND)) {
            state = TerrainState.GRASS;
            p = cfg.offRoad;
            var biome = level.getBiome(feetPos);
            if (biome.is(BiomeTags.IS_FOREST) || biome.is(BiomeTags.IS_JUNGLE) || biome.is(BiomeTags.IS_TAIGA)) {
                state = TerrainState.FOREST;
                p = Math.max(p, cfg.forest);
            }
            if (level.isRainingAt(feetPos)) {
                state = TerrainState.WET;    // розкисла земля
                p += cfg.rainExtra;
            }
        } else {
            return NONE;
        }
        return new Result(state, Math.min(p, cfg.maxPenalty));
    }

    // ---------------------------------------------------------------- атрибути

    private static void smoothTo(LivingEntity entity, double target) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;

        AttributeModifier current = attr.getModifier(MODIFIER_ID);
        double currentPenalty = current == null ? 0.0 : -current.getAmount();

        TerrainConfig cfg = TerrainConfig.get();
        double next = currentPenalty + (target - currentPenalty) * Math.min(1.0, Math.max(0.05, cfg.smoothing));
        if (Math.abs(target - next) < 0.01) next = target;
        if (Math.abs(next - currentPenalty) < 0.002) return;

        if (current != null) attr.removeModifier(MODIFIER_ID);
        if (next > 0.0) {
            attr.addTransientModifier(new AttributeModifier(
                    MODIFIER_ID, "realistic_terrain_slowdown", -next,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private static void removeModifier(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(MODIFIER_ID) != null) {
            attr.removeModifier(MODIFIER_ID);
        }
    }

    // ---------------------------------------------------------------- HUD

    private static void report(ServerPlayer player, Result r, boolean mounted) {
        int percent = (int) Math.round(r.penalty() * 100.0);
        TerrainStatePacket packet = new TerrainStatePacket(r.state().ordinal(), percent, mounted);
        if (packet.equals(LAST_SENT.get(player.getUUID()))) return; // не спамимо пакетами
        LAST_SENT.put(player.getUUID(), packet);
        Network.sendTo(player, packet);
    }
}
