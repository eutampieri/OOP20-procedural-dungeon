package eu.eutampieri.catacombs.tests;

import eu.eutampieri.catacombs.model.Bat;
import eu.eutampieri.catacombs.model.Boss;
import eu.eutampieri.catacombs.model.GameObject;
import eu.eutampieri.catacombs.model.GameObjectType;
import eu.eutampieri.catacombs.model.Gun;
import eu.eutampieri.catacombs.model.HealthModifier;
import eu.eutampieri.catacombs.model.Player;
import eu.eutampieri.catacombs.model.Projectile;
import eu.eutampieri.catacombs.model.Slime;
import eu.eutampieri.catacombs.model.map.TileMap;
import eu.eutampieri.catacombs.model.map.TileMapFactoryImpl;
import eu.eutampieri.catacombs.ui.gamefx.AssetManagerProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CharactersTests {

     private static final TileMap TILE_MAP = new TileMapFactoryImpl().empty(20, 20);
     private static final Bat BAT = new Bat(1, 1, TILE_MAP);
     private static final Slime SLIME = new Slime(
             AssetManagerProxy.getMapTileSize() * 2,
             AssetManagerProxy.getMapTileSize() * 2,
             TILE_MAP
     );
     private static final Boss BOSS = new Boss(5, 5, TILE_MAP);
     private static final Gun GUN = new Gun(null, TILE_MAP, 0, 0, GameObject.Team.ENEMY);
     private static final HealthModifier GUN_HP_SUB = (Projectile) GUN.fire(0, 0).get(0);
     private static final Player PLAYER = new Player(0, 0, "John Appleseed", TILE_MAP);

    @Test
    void testPlayerName() {
        final String name = "John Appleseed";
        assertEquals(PLAYER.getName(), name);
    }

    @Test
    void testPlayerHealth() {
        assertEquals(PLAYER.getHealth(), 100);
        assertTrue(PLAYER.isAlive());
    }

    @Test
    void testEnemyNames() {
        assertEquals("Slime", SLIME.getName());
        assertEquals("Bat", BAT.getName());
        assertEquals("Boss", BOSS.getName());
    }

    @Test
    void testEnemyEntityKind() {
        assertEquals(GameObjectType.ENEMY, SLIME.getKind());
        assertEquals(GameObjectType.ENEMY, SLIME.getKind());
    }

    @Test
    void testBatGettersSetters() {
        final int initialHealth = BAT.getHealth();
        GUN_HP_SUB.useOn(BAT);
        assertEquals(initialHealth - GUN.getStrength(), BAT.getHealth());
    }

    @Test
    void testEntitiesSizesPowersOfTwo() {
        // Bat
        assertEquals(0, BAT.getWidth() & (BAT.getWidth() - 1));
        assertEquals(0, BAT.getHeight() & (BAT.getHeight() - 1));
        // Slime
        assertEquals(0, SLIME.getWidth() & (SLIME.getWidth() - 1));
        assertEquals(0, SLIME.getHeight() & (SLIME.getHeight() - 1));

    }

    @Test
    void testBoxSize() {
        assertEquals(BAT.getHitBox().getHeight(), BAT.getHeight());
        assertEquals(BAT.getHitBox().getWidth(), BAT.getWidth());
        assertEquals(SLIME.getHitBox().getHeight(), SLIME.getHeight());
        assertEquals(SLIME.getHitBox().getWidth(), SLIME.getWidth());
        assertEquals(BOSS.getHitBox().getHeight(), BOSS.getHeight());
        assertEquals(BOSS.getHitBox().getWidth(), BOSS.getWidth());
    }

    @Test
    void testSlimeFollowPlayer() {
        final Player player = new Player(3, 3, "player_testing", TILE_MAP);
        final int initialX = SLIME.getPosX();
        final int initialY = SLIME.getPosY();
        SLIME.setCharacterToFollow(player);
        assertEquals(SLIME.getCharacterToFollow(), player);
        SLIME.update(TimeUnit.SECONDS.toNanos(4), List.of(player));
        assertNotEquals(initialX, SLIME.getPosX());
        assertNotEquals(initialY, SLIME.getPosY());
    }

    @Test
    void testBatUpdate() {
        BAT.update(10, List.of(PLAYER));
        // TODO implement checks
    }

    @Test
    void testBossUpdate() {
        BOSS.update(10, List.of(PLAYER));
        // TODO implement checks
    }
}
