package com.example.realisticterrain;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class Network {
    private static final String PROTOCOL = "1";

    // acceptMissingOr: клієнт без моду теж може зайти на сервер
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RealisticTerrain.MOD_ID, "main"),
            () -> PROTOCOL,
            NetworkRegistry.acceptMissingOr(PROTOCOL),
            NetworkRegistry.acceptMissingOr(PROTOCOL));

    private Network() {}

    public static void init() {
        CHANNEL.registerMessage(0, TerrainStatePacket.class,
                TerrainStatePacket::encode, TerrainStatePacket::decode, TerrainStatePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendTo(ServerPlayer player, TerrainStatePacket packet) {
        if (!CHANNEL.isRemotePresent(player.connection.connection)) return; // у клієнта немає моду
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
