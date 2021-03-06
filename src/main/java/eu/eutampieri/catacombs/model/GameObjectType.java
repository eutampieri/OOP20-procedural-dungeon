package eu.eutampieri.catacombs.model;

/**
 * Useful enum to keep track of GameObjects types
 * @see GameObject
 */
public enum GameObjectType {
    /**
     * The player, i.e. the person who is playing the game.
     */
    PLAYER,
    /**
     * The enemy, i.e. a bad actor who interferes with the player.
     */
    ENEMY,
    /**
     * The boss, an enemy you have to defeat in order to clear the level.
     */
    BOSS,
    /**
     * An object that you pick up by stepping on it.
     */
    PICKUP,
    /**
     * A weapon, i.e. an object which, if used, reduces the health of the character
     * it's used against.
     */
    WEAPON,
    /**
     * A generic item.
     */
    ITEM,
    /**
     * A generic bullet.
     */
    BULLET,
    /**
     * The boss's special bullet.
     */
    BOSS_BULLET
}
