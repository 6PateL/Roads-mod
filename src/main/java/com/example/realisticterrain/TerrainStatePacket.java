package com.example.realisticterrain;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Сервер -> клієнт: на чому стоїмо, який штраф і чи їдемо верхи. */
public record TerrainStatePacket(int state, int percent, boolean mounted) {

    public static void encode(TerrainStatePacket p, FriendlyByteBuf buf) {
        buf.writeByte(p.state);
        buf.writeByte(p.percent);
        buf.writeBoolean(p.mounted);
    }

    public static TerrainStatePacket decode(FriendlyByteBuf buf) {
        return new TerrainStatePacket(buf.readByte(), buf.readByte(), buf.readBoolean());
    }

    public static void handle(TerrainStatePacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientTerrainState.set(p)));
        ctx.get().setPacketHandled(true);
    }
}
