package eu.eutampieri.catacombs.model;

import eu.eutampieri.catacombs.model.gen.MobFactory;
import eu.eutampieri.catacombs.model.gen.MobFactoryImpl;
import eu.eutampieri.catacombs.model.map.TileMap;
import eu.eutampieri.catacombs.ui.gamefx.AssetManagerProxy;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The game Boss.
 * @see Entity
 */
public final class Boss extends Entity {

    private static final int HEIGHT = 48;
    private static final int WIDTH = 48;
    private static final int MOVEMENT_SPEED = 4;
    private static final int HEALTH = 100;
    private static final int RADAR_BOX_POSITION_MODIFIER = 30 * AssetManagerProxy.getMapTileSize();
    private static final int RADAR_BOX_SIZE = 30 * 2 * AssetManagerProxy.getMapTileSize() + Math.max(WIDTH, HEIGHT);
    private static final String NAME = "Boss";
    private static final long MOVE_DELAY = 15L * 100;
    private static final long PAUSE_DELAY = 10L * 100;
    private static final long SPAWN_MOB_DELAY = 80L * 100;
    private static final int BASE_DAMAGE = 15;
    private static final int BASE_PROJECTILE_SPEED = 4;
    private static final int BASE_FIRE_RATE = 15;
    private static final int BULLET_SIZE = 28;
    private static final int MAX_CHANCE = 100;
    private static final int MOB_SPAWN_CHANCE = 60;
    private static final int SPAWN_RADIUS = 20;
    private final Weapon weapon;
    private boolean isMoving;
    private int delayCounter;
    private int pauseCounter;
    private final CollisionBox radarBox;
    private final Point shootingDirection;
    private boolean canSpawnMob;
    private int spawnMobCounter;
    private final MobFactory mf;

    /**
     * @param x       X spawn position
     * @param y       Y spawn position
     * @param tileMap Tile map in which Entity is spawned
     */
    public Boss(final int x, final int y, final TileMap tileMap) {
        super(x, y, WIDTH, HEIGHT, tileMap, GameObjectType.BOSS, GameObject.Team.ENEMY);
        setSpeed(MOVEMENT_SPEED);
        setHealth(HEALTH);
        face = Direction.RIGHT;
        radarBox = new CollisionBox(posX - RADAR_BOX_POSITION_MODIFIER, posY - RADAR_BOX_POSITION_MODIFIER, RADAR_BOX_SIZE,
                RADAR_BOX_SIZE);
        weapon = new Weapon(this, tileMap, this.getHitBox().getPosX(), this.getHitBox().getPosY(),
                BASE_DAMAGE, BASE_PROJECTILE_SPEED, BASE_FIRE_RATE, this.getTeam(), GameObjectType.BOSS_BULLET, BULLET_SIZE) { };
        shootingDirection = new Point(0, 0);
        this.delayCounter = 0;
        this.pauseCounter = 0;
        this.isMoving = true;
        this.canSpawnMob = false;
        this.spawnMobCounter = 0;
        this.mf = new MobFactoryImpl(tileMap);
    }

    @Override
    public List<GameObject> update(final long delta, final List<GameObject> others) {
        final List<GameObject> objs = new ArrayList<>();
        final Random rand = new Random();
        resetShootingDirection();
        if (isMoving) {
            delayCounter += delta;
            if (delayCounter >= MOVE_DELAY) {
                delayCounter = 0;
                isMoving = false;
                resetMovement();
            }
        } else {
            pauseCounter += delta;
            if (pauseCounter >= PAUSE_DELAY) {
                pauseCounter = 0;
                isMoving = true;
                changeDirection();
            }
        }
        others.stream().filter((x) -> x instanceof Player)
                .filter((x) -> x.getHitBox().overlaps(this.radarBox)).findFirst()
                .ifPresentOrElse((x) -> {
                    if (this.weapon.canFire()) {
                        setShootingDirection(x);
                    }
                }, () -> this.weapon.setCanFire(false));

        super.update(delta, others);
        updateRadarBoxLocation();
        weapon.update(delta, others);
        if (canSpawnMob) {
            canSpawnMob = false;
            if (rand.nextInt(MAX_CHANCE) + 1 <= MOB_SPAWN_CHANCE) {
                System.out.println("spawn");
                if (rand.nextBoolean()) {
                    objs.addAll(this.mf.spawnNear(SPAWN_RADIUS, this, Slime::new));
                } else {
                    objs.addAll(this.mf.spawnNear(SPAWN_RADIUS, this, Bat::new));
                }
                return objs;
            }
        } else {
            spawnMobCounter += delta;
            if (spawnMobCounter >= SPAWN_MOB_DELAY) {
                canSpawnMob = true;
                spawnMobCounter = 0;
            }
        }
        if (this.weapon.canFire && this.getShootingDirection().getX() != 0 && this.getShootingDirection().getY() != 0) {
            objs.addAll(weapon.fire((int) getShootingDirection().getX() * weapon.ps, (int) getShootingDirection().getY() * weapon.ps));
            this.weapon.setCanFire(true);
            objs.addAll(weapon.fire((int) getShootingDirection().getX() * -weapon.ps, (int) getShootingDirection().getY() * weapon.ps));
            this.weapon.setCanFire(true);
            objs.addAll(weapon.fire((int) getShootingDirection().getX() * weapon.ps, (int) getShootingDirection().getY() * -weapon.ps));
            this.weapon.setCanFire(true);
            objs.addAll(weapon.fire((int) getShootingDirection().getX() * -weapon.ps, (int) getShootingDirection().getY() * -weapon.ps));
            this.weapon.setCanFire(true);
            objs.addAll(weapon.fire(0, (int) getShootingDirection().getY() * weapon.ps));
            this.weapon.setCanFire(true);
            objs.addAll(weapon.fire(0, (int) getShootingDirection().getY() * -weapon.ps));
            this.weapon.setCanFire(true);
            objs.addAll(weapon.fire((int) getShootingDirection().getX() * weapon.ps, 0));
            this.weapon.setCanFire(true);
            objs.addAll(weapon.fire((int) getShootingDirection().getX() * -weapon.ps, 0));
            return objs;
        }
        return List.of();
    }

    @Override
    public Pair<Action, Direction> getActionWithDirection() {
        if (this.face == Direction.UP || this.face == Direction.DOWN) {
            return Pair.of(Action.IDLE, Direction.RIGHT);
        }
        return Pair.of(this.isMoving() ? Action.MOVE : Action.IDLE, this.face);
    }

    @Override
    public boolean canPerform(final Action action) {
        switch (action) {
        case IDLE:
        case MOVE:
            return true;
        default:
            return false;
        }
    }

    /**
     * Makes the boss change facing direction.
     */
    private void changeDirection() {
        final Random rand = new Random();
        final int c = rand.nextInt(8);
        switch (Math.floorDiv(c, 2)) {
            case 0:
                face = Direction.UP;
                up = true;
            break;
            case 1:
                face = Direction.DOWN;
                down = true;
            break;
            case 2:
                face = Direction.LEFT;
                left = true;
            break;
            case 3:
                face = Direction.RIGHT;
                right = true;
            break;
            default:
                face = Direction.LEFT;
                resetMovement();
            break;
        }

    }

    /**
     * Updates the aggro radar's Boss box.
     */
    private void updateRadarBoxLocation() {
        radarBox.setLocation(posX - RADAR_BOX_POSITION_MODIFIER, posY - RADAR_BOX_POSITION_MODIFIER);
    }

    @Override
    public int getHealth() {
        return this.hp;
    }

    @Override
    public void setHealth(final int health) {
        this.hp = health;
    }

    /**
     *
     * @return Boss name
     */
    public String getName() {
        return Boss.NAME;
    }

    /**
     *
     * @return Boss shooting direction
     */
    public Point getShootingDirection() {
        return this.shootingDirection;
    }

    /**
     * Resets the shooting direction of the Boss.
     */
    public void resetShootingDirection() {
        this.shootingDirection.setLocation(0, 0);
    }

    /**
     * Sets the shooting direction of the Boss.
     * @param e GameObject to aim
     */
    public void setShootingDirection(final GameObject e) {
        if (e == null) {
            return;
        }
        final int x = Integer.compare(e.getHitBox().getPosX(), this.getHitBox().getPosX());
        final int y = Integer.compare(e.getHitBox().getPosY(), this.getHitBox().getPosY());
        this.shootingDirection.setLocation(x, y);
    }


}
