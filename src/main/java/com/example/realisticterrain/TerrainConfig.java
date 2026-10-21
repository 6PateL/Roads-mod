package com.example.realisticterrain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Конфіг: config/realistic_terrain.json. Значення штрафів — частка від 0 до 1 (0.30 = -30% швидкості). */
public final class TerrainConfig {
    public boolean enabled = true;
    public int checkIntervalTicks = 5;      // як часто перевіряти (20 тіків = 1 с)
    public double smoothing = 0.5;          // 0..1, плавність переходу між поверхнями
    public List<String> enabledDimensions = new ArrayList<>(List.of("minecraft:overworld"));

    public double offRoad = 0.20;           // трава/земля без дороги
    public double forest = 0.25;            // ліс/джунглі/тайга (трава/земля)
    public double sand = 0.25;
    public double snow = 0.30;
    public double mud = 0.40;
    public double rainExtra = 0.10;         // додатково на м'якому ґрунті під дощем
    public double maxPenalty = 0.50;        // стеля штрафу

    public boolean affectMounts = true;     // кінь/віслюк/мул/свиня (з морквою на палиці)/верблюд теж сповільнюються
    public boolean showHud = true;          // іконка поверхні справа знизу (клієнт)
    public int hudScale = 1;                // 1..3, множник розміру іконки
    public int hudOffsetX = 6;              // відступ від правого краю
    public int hudOffsetY = 6;              // відступ від нижнього краю

    private static TerrainConfig instance = new TerrainConfig();
    public static TerrainConfig get() { return instance; }

    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("realistic_terrain.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if (Files.exists(path)) {
                try (Reader r = Files.newBufferedReader(path)) {
                    TerrainConfig loaded = gson.fromJson(r, TerrainConfig.class);
                    if (loaded != null) instance = loaded;
                }
            }
            try (Writer w = Files.newBufferedWriter(path)) {
                gson.toJson(instance, w);
            }
        } catch (IOException | JsonParseException e) {
            RealisticTerrain.LOGGER.error("Не вдалося прочитати конфіг, використовую стандартний", e);
        }
    }
}
