# Realistic Terrain (Forge 1.20.1) v1.1

Без доріг рух повільніший: земля/трава -20%, ліс -25%, пісок -25%, сніг -30%, бруд -40%,
дощ +10% на м'якому ґрунті. Максимум -50%.

- Іконка поточної поверхні справа знизу (дорога / бездоріжжя / ліс / пісок / сніг / бруд / розкисла земля) з відсотком.
- Верхи (кінь, віслюк, мул, верблюд, свиня з морквою на палиці) штраф діє на скакуна за поверхнею під ним, у підписі "(верхи)".

## Збірка
1. Залий вміст папки в репозиторій GitHub.
2. Actions -> build -> Artifacts -> realistic-terrain-forge -> .jar
3. Кинь jar у mods/ (Forge 47.x). Для іконки мод має стояти і на клієнті; без нього гра працює, просто без HUD.

## Налаштування (config/realistic_terrain.json)
Відсотки штрафів, плавність, виміри, affectMounts, showHud, hudScale (1..3), hudOffsetX/Y.
Теги блоків: data/realistic_terrain/tags/blocks/ (roads, soft_ground, mud, snow, sand).
Іконки: assets/realistic_terrain/textures/gui/state/*.png (16x16, можна замінити своїми).
