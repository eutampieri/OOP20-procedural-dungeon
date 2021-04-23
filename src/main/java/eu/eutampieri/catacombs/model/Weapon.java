package eu.eutampieri.catacombs.model;

import eu.eutampieri.catacombs.model.map.TileMap;

import java.util.List;

public abstract class Weapon extends GameObject {

    private static final int BULLET_DEFAULT_SIZE = 8;

    /**
     * Projectile strength.
     */
    protected int strength;
    /**
     * projectile speed.
     */
    protected int ps;
    /**
     * weapon fire rate as bullets fired per minute.
     */
    protected long fireRate;
    /**
     * Stores if the weapon can fire.
     */
    protected boolean canFire;
    /**
     * utility to count delay between shots.
     */
    protected long fireDelay, fireDelayCount;

    /**
     * Map in which resides the gun.
     */
    private TileMap tileMap;

    /**
     * Entity using the weapon.
     */
    private Entity user;

    /**
     * Bullet type.
     */
    private GameObjectType bulletKind;

    /**
     * Bullet size.
     */
    private int bulletSize;

    /**
     *
     * @param e        Entity using weapon
     * @param tm       Tile map
     * @param x        X position
     * @param y        Y position
     * @param strength Bullet strength
     * @param ps       Bullet speed
     * @param fr       Weapon fire rate
     * @param team     Shooting entity team
     */
    public Weapon(final Entity e, final TileMap tm, final int x, final int y, final int strength, final int ps,
            final int fr, final Team team) {
        super(x, y, GameObjectType.WEAPON, new CollisionBox(x, y, 0, 0), team);
        this.user = e;
        setTileMap(tm);
        setStrength(strength);
        setProjectileSpeed(ps);
        setFireRate(fr);
        setFireDelay(Math.round(60_000f / fireRate));
        setCanFire(true);
        this.bulletSize = BULLET_DEFAULT_SIZE;
        this.fireDelayCount = 0;
    }

    public Weapon(final Entity e, final TileMap tm, final int x, final int y, final int strength, final int ps,
                  final int fr, final Team team, final GameObjectType kind, final int size) {
        super(x, y, GameObjectType.WEAPON, new CollisionBox(x, y, 0, 0), team);
        this.user = e;
        setTileMap(tm);
        setStrength(strength);
        setProjectileSpeed(ps);
        setFireRate(fr);
        setFireDelay(Math.round(60_000f / fireRate));
        setCanFire(true);
        this.bulletKind = kind;
        this.bulletSize = size;
        this.fireDelayCount = 0;
    }

    @Override
    public List<GameObject> update(final long delta, final List<GameObject> others) {
        if (!canFire) {
            fireDelayCount += delta;
            if (fireDelayCount >= fireDelay) {
                fireDelayCount = 0;
                setCanFire(true);
            }
        }
        if (this.user != null) {
            this.hitBox.setPosX(this.user.getHitBox().getPosX() + this.user.getSize()/2 - 1);
            this.hitBox.setPosY(this.user.getHitBox().getPosY() + this.user.getSize()/2 - 1);
        }
        return List.of();
    }

    public final void setTileMap(final TileMap tm) {
        this.tileMap = tm;
    }

    public final void setStrength(final int str) {
        this.strength = str;
    }

    public final void setProjectileSpeed(final int ps) {
        this.ps = ps;
    }

    public final void setFireRate(final long fr) {
        this.fireRate = fr;

    }

    public final void setFireDelay(final long fd) {
        this.fireDelay = fd;
    }

    public final void setCanFire(final boolean cf) {
        this.canFire = cf;
    }

    public final boolean canFire() {
        return this.canFire;
    }

    public final List<GameObject> fire(final int psx, final int psy) {
       final Projectile p;
       if (this.bulletKind != null && bulletSize > 8) {
           p = new Projectile(this.getHitBox().getPosX(), this.getHitBox().getPosY(),
                   psx*ps, psy*ps, strength, tileMap, this.getTeam(), this.bulletKind, bulletSize);
       } else {
           p = new Projectile(this.getHitBox().getPosX(), this.getHitBox().getPosY(),
                   psx*ps, psy*ps, strength, tileMap, this.getTeam());
       }
       setCanFire(false);
       return List.of(p);
    }

    public void setUser(final Entity user) {
        this.user = user;
    }

    public void setBulletKind (final GameObjectType kind) {
        this.bulletKind = kind;
    }

    public void setBulletSize (final int size) {
        this.bulletSize = size;
    }
}
