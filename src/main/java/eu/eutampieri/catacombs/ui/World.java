package eu.eutampieri.catacombs.ui;

import eu.eutampieri.catacombs.model.*;
import eu.eutampieri.catacombs.model.map.TileMap;
import eu.eutampieri.catacombs.model.mobgen.MobFactory;
import eu.eutampieri.catacombs.model.mobgen.MobFactoryImpl;
import eu.eutampieri.catacombs.ui.gamefx.AssetManagerProxy;
import eu.eutampieri.catacombs.ui.input.KeyManager;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class World {
    // private final BufferedImage background;
    private final TileMap tileMap;
    private final KeyManagerProxy km = new KeyManagerProxy();
    private final DungeonGame game;
    // private final DungeonGame game = new DungeonGame();
    // TODO Camera
    private final Camera camera;

    private List<GameObject> entities;

    private Player player;

    private static final class KeyManagerProxy {
        private final KeyManager km = KeyManager.getKeyManager();

        public boolean up() {
            return this.km.isKeyPressed(KeyEvent.VK_W) || this.km.isKeyPressed(KeyEvent.VK_UP);
        }

        public boolean down() {
            return this.km.isKeyPressed(KeyEvent.VK_S) || this.km.isKeyPressed(KeyEvent.VK_DOWN);
        }

        public boolean left() {
            return this.km.isKeyPressed(KeyEvent.VK_A) || this.km.isKeyPressed(KeyEvent.VK_LEFT);
        }

        public boolean right() {
            return this.km.isKeyPressed(KeyEvent.VK_D) || this.km.isKeyPressed(KeyEvent.VK_RIGHT);
        }

        public boolean fire() {
            return this.km.isKeyPressed(KeyEvent.VK_SPACE);
        }
    }

    public World(final TileMap tileMap, final DungeonGame game) {
        // this.background = am.getImage("background");
        this.tileMap = tileMap;
        final MobFactory mf = new MobFactoryImpl(this.tileMap);
        camera = new Camera(0, 0, tileMap.width() * AssetManagerProxy.getMapTileSize(), tileMap.height() * AssetManagerProxy.getMapTileSize());
        this.entities = mf.spawnRandom().stream().map((x) -> (GameObject)x).collect(Collectors.toList());
        this.player = (Player)mf
                .spawnSome(1, (x, y, tm) -> new Player(x, y, "", tm))
                .get(0);

        this.game = game;
    }

    public TileMap getTileMap() {
        return this.tileMap;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    private List<GameObject> getAllEntitiesExcept(final GameObject e) {
        return Stream.concat(
                this.entities.stream(),
                Stream.of(this.player)
        )
                .filter((x) -> !e.equals(x))
                .collect(Collectors.toUnmodifiableList());
    }

    public void update(final long delta) {
        this.player.stop();
        if(this.km.up()) {
            this.player.move(Direction.UP);
        } else if(this.km.down()) {
            this.player.move(Direction.DOWN);
        } else if(this.km.left()) {
            this.player.move(Direction.LEFT);
        } else if(this.km.right()) {
            this.player.move(Direction.RIGHT);
        }

        if(this.km.fire()) {
            this.player.fire();
        }

        player.update(delta, this.getAllEntitiesExcept(this.player));

        for (final GameObject entity : this.entities) {
            if(this.isOnCamera(entity.getPosX(), entity.getPosY())) {
                entity.update(delta, this.getAllEntitiesExcept(entity));
            }
        }

        final List<GameObject> newEntities = Stream.concat(entities.stream(), Stream.of(player))
                .filter((x) -> x instanceof Entity)
                .flatMap((x) -> ((Entity) x).spawnObject().stream())
                .collect(Collectors.toList());
        this.entities.addAll(newEntities);

        this.entities = this.entities
                .stream()
                .filter((x) -> !x.isMarkedForDeletion())
                .collect(Collectors.toList());
    }

    private boolean isOnCamera(final int x, final int y) {
        final int canvasX = x - camera.getXOffset();
        final int canvasY = y - camera.getYOffset();
        return canvasX > -AssetManagerProxy.getMapTileSize() && canvasX <= game.getWidth() &&
                canvasY > -AssetManagerProxy.getMapTileSize() && canvasY <= game.getHeight();
    }

    public void render(final Graphics2D g2) {
        camera.centerOnEntity(this.player, game.getWidth(), game.getHeight());
        // g2.drawImage(background, 0, 0, game.getGameWidth(), game.getGameHeight(),
        // null);
        /*
         * for(int y=0; y<7; y++){ for(int x=0; x<7; x++){ var i = y*7+x; var im =
         * am.getImage(i+""); g2.drawImage(im, null, x*16, y*16); } }
         */
        for (int y = 0; y < tileMap.height(); y++) {
            for (int x = 0; x < tileMap.width(); x++) {
                final int canvasX = x * AssetManagerProxy.getMapTileSize() - camera.getXOffset();
                final int canvasY = y * AssetManagerProxy.getMapTileSize() - camera.getYOffset();
                if(isOnCamera(x * AssetManagerProxy.getMapTileSize(), y * AssetManagerProxy.getMapTileSize())) {
                    final Optional<BufferedImage> tile = AssetManagerProxy.getTileSprite(tileMap.at(x, y));
                    tile.ifPresent(bufferedImage -> g2.drawImage(bufferedImage, null, canvasX, canvasY));
                }
            }
        }

        Stream.concat(this.entities.stream(), Stream.of(this.player))
                .filter((x) -> this.isOnCamera(x.getPosX(), x.getPosY()))
                .forEach((currentObj) -> {
                    g2.drawRect(currentObj.getHitBox().getPosX()-camera.getXOffset(), currentObj.getHitBox()
                            .getPosY() - camera.getYOffset(), currentObj.getHitBox().getWidth(), currentObj.getHitBox().getHeight());
                    try {
                    final Entity currentEntity = (Entity) currentObj;
                    final Pair<Action, Direction> action = currentEntity.getActionWithDirection();
                    final List<BufferedImage> img = AssetManagerProxy.getFrames(currentEntity, action.getLeft(), action.getRight());
                    if (currentEntity.isMoving()) {
                        Animation animation = new Animation(img.stream().map(Optional::of).collect(Collectors.toList()), 0.5f);
                        BufferedImage toShow = animation.getCurrentFrame().get();
                        g2.drawImage(toShow, null, currentEntity.getPosX() - camera.getXOffset(), currentEntity.getPosY() - camera.getYOffset());
                    } else {
                        g2.drawImage(img.get(0), null, currentEntity.getPosX() - camera.getXOffset(), currentEntity.getPosY() - camera.getYOffset());
                    }
                } catch (ClassCastException e) {
                    // Treat it as a game object
                    final BufferedImage img = AssetManagerProxy.getSprite(currentObj);
                    g2.drawImage(img, null, currentObj.getPosX() - camera.getXOffset(), currentObj.getPosY() - camera.getYOffset());
                }});

        // TODO player.render parameters
        // this.player.render(g2, camera);

    }

}
