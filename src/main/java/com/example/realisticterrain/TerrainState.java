package com.example.realisticterrain;

/** Стан поверхні під гравцем (або під скакуном). Порядок = id у мережевому пакеті. */
public enum TerrainState {
    NONE("none", 0xFFFFFF),
    ROAD("road", 0x55FF55),
    GRASS("grass", 0xFFFF55),
    FOREST("forest", 0xFFAA00),
    SAND("sand", 0xFFAA00),
    SNOW("snow", 0xFF7755),
    MUD("mud", 0xFF5555),
    WET("wet", 0xFF5555);

    public final String key;
    public final int color;

    TerrainState(String key, int color) {
        this.key = key;
        this.color = color;
    }

    public static TerrainState byId(int id) {
        TerrainState[] v = values();
        return id >= 0 && id < v.length ? v[id] : NONE;
    }
}
