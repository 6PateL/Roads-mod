package com.example.realisticterrain;

/** Останній стан, який прислав сервер (читає HUD). */
public final class ClientTerrainState {
    private static volatile TerrainState state = TerrainState.NONE;
    private static volatile int percent = 0;
    private static volatile boolean mounted = false;

    private ClientTerrainState() {}

    public static void set(TerrainStatePacket p) {
        state = TerrainState.byId(p.state());
        percent = p.percent();
        mounted = p.mounted();
    }

    public static void reset() {
        state = TerrainState.NONE;
        percent = 0;
        mounted = false;
    }

    public static TerrainState state() { return state; }
    public static int percent() { return percent; }
    public static boolean mounted() { return mounted; }
}
