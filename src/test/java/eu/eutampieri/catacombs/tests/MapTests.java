package eu.eutampieri.catacombs.tests;

import eu.eutampieri.catacombs.model.map.TileMapImpl;
import org.junit.jupiter.api.Test;

import eu.eutampieri.catacombs.model.map.Tile;
import eu.eutampieri.catacombs.model.map.TileMap;
import eu.eutampieri.catacombs.model.map.TileMapFactoryImpl;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapTests {
    private void checkMap(final TileMap m) {
        for (int y = 0; y < m.height(); y++) {
            for (int x = 0; x < m.width(); x++) {
                assertTrue(m.at(x, y) == Tile.FLOOR || m.at(x, y) == Tile.WALL);
                if (y == 0 || x == 0 || y == m.height() - 1 || x == m.width() - 1) {
                    assertNotSame(m.at(x, y), Tile.FLOOR);
                }
            }
        }
    }

    @Test
    public void testMultipleMapWontFail() {
        final TileMapFactoryImpl mf = new TileMapFactoryImpl();
        for (int i = 0; i < 32; i++) {
            checkMap(mf.def());
        }
    }

    @Test
    public void testMapClone() {
        final TileMapImpl tm = (TileMapImpl) new TileMapFactoryImpl().def();
        final Tile[][] tiles = tm.getMap();
        assertEquals(tiles[0].length, tm.width());
        assertEquals(tiles.length, tm.height());
    }

    @Test
    public void testEmptyMap() {
        final TileMap tm = new TileMapFactoryImpl().empty(3, 3);
        assertEquals(Tile.FLOOR, tm.at(1, 1));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (x == y && x == 1) {
                    continue;
                } else {
                    assertEquals(Tile.WALL, tm.at(x, y));
                }
            }
        }
    }

    @Test
    public void testSpawnPoints() {
        final int mapSize = 8;
        final TileMap tm = new TileMapFactoryImpl().empty(mapSize, mapSize);
        assertEquals(Tile.FLOOR, tm.at(1, 1));
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < 8; x++) {
                assertEquals(tm.canSpawnAt(x, y), y > 1 && x > 1 && y < mapSize - 2 && x < mapSize - 2);
            }
        }
    }

    @Test
    public final void testOutOfBounds() {
        final int mapSize = 5;
        final TileMap tm = new TileMapFactoryImpl().empty(mapSize, mapSize);
        assertEquals(Tile.VOID, tm.at(-1, -1));
        assertEquals(Tile.VOID, tm.at(mapSize + 1, mapSize + 1));
        assertEquals(Tile.VOID, tm.at(-1, 0));
    }
}
