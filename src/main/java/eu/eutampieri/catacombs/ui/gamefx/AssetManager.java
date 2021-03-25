package eu.eutampieri.catacombs.ui.gamefx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import eu.eutampieri.catacombs.ui.utils.ImageRotator;

public class AssetManager {
	
	private  static final HashMap<String,BufferedImage[]> allAnimations = new HashMap<String,BufferedImage[]>();
	private final HashMap<String,BufferedImage> allImages = new HashMap<String,BufferedImage>();
	
	private ImageRotator imageRot;
	
	public static BufferedImage[] getFrames(String key) {
		return allAnimations.get(key);
	}
	
	public BufferedImage getImage(String key) {
		return this.allImages.get(key);
	}
	
	public String getImageKey(BufferedImage image) {
		for (Entry<String,BufferedImage> entry : this.allImages.entrySet()) {
			if (entry.getValue() == image) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public void load() {
		loadAnimations("Walk_up", "/playersheet.png", 8, 96, 32, false);
		loadAnimations("Walk_down", "/playersheet.png", 8, 64, 32, false );
		loadAnimations("Walk_left", "/playersheet.png", 8, 32, 32, false );
		loadAnimations("Walk_right", "/playersheet.png",8, 0, 32, false );

		loadAnimations("Die", "/playersheet.png", 8, 128, 32, false );

		loadAnimations("Attack_up", "/playersheet.png", 8, 160, 32, false );
		loadAnimations("Attack_down", "/playersheet.png", 8, 192, 32, false );
		loadAnimations("Attack_left", "/playersheet.png", 8, 256, 32, false );
		loadAnimations("Attack_right", "/playersheet.png", 8, 224, 32, false );

		loadAnimations("Slime_up", "/slimesheet.png", 4, 63, 32, false );
		loadAnimations("Slime_down", "/slimesheet.png", 4, 0, 32, false );
		loadAnimations("Slime_left", "/slimesheet.png", 4, 96, 32, false );
		loadAnimations("Slime_right", "/slimesheet.png", 4, 32, 32, false );

		loadAnimations("Bat_right", "/batsheet.png", 3, 224, 32, false );
		loadAnimations("Bat_left", "/batsheet.png", 3, 224, 32, true );
		loadImages();
	}
	
	public BufferedImage horizontalFlip(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
		Graphics2D g = flippedImage.createGraphics();
		g.drawImage(image ,0 ,0 ,width ,height ,width ,0 ,0 ,height , null);
		g.dispose();
		return flippedImage;
	}
	
	public void loadImages() {
		// Tiles
		GameSheets tileSheet = new GameSheets("/tileSheet.png");
		int count = 1;
		BufferedImage image = tileSheet.cutImage(112, 0, 16, 16);
		
		this.allImages.put("" + count++, image);
		allImages.put("" + count++, imageRot.rotate(image, 90));
		allImages.put("" + count++, imageRot.rotate(image, 180));
		allImages.put("" + count++, tileSheet.cutImage(112 + 16, 0, 16, 16));
		allImages.put("" + count++, tileSheet.cutImage(112 + 16, 0, 16, 16));
		allImages.put("" + count++, tileSheet.cutImage(112, 16, 16, 16));
		allImages.put("" + count++, tileSheet.cutImage(112 + 16, 16, 16, 16));
		allImages.put("" + count++, tileSheet.cutImage(112 + 16, 16, 16, 16));
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; i < 7; j++) {
				if (i == 0 && j < 3) {
					BufferedImage img;
					img = tileSheet.cutImage(j * 16, i * 16, 16, 16);
					this.allImages.put("" + count++, img);
					this.allImages.put("" + count++, imageRot.rotate(image, 90));
					this.allImages.put("" + count++, imageRot.rotate(image, 180));
					this.allImages.put("" + count++, imageRot.rotate(image, 270));
				} else {
					this.allImages.put("" + count++, tileSheet.cutImage(j * 16, i * 16, 16, 16));
				}
			}
		}
		
		// background
		this.allImages.put("background", tileSheet.cutImage(64, 96, 16, 16));
		
		// world objects
		allImages.put("coin", tileSheet.cutImage(0, 144, 16,16));
		allImages.put("skull", tileSheet.cutImage(16, 144, 16,16));
		allImages.put("fire", tileSheet.cutImage(0, 160, 8, 21));
		allImages.put("potion", tileSheet.cutImage(0,181,9,11));
		allImages.put("gun", tileSheet.cutImage(0, 193, 21, 17));
	}

	public void loadAnimations(String name, String image, int numFrames, int offset, int dimension, boolean flip) {
		GameSheets sheet = new GameSheets(image);
		BufferedImage[] res = new BufferedImage[numFrames];
		for (int i = 0; i < numFrames; i++) {
			if (!flip){
				res[i] = sheet.cutImage(dimension * i, offset * dimension, dimension, dimension);
			} else {
				res[i] = horizontalFlip(sheet.cutImage(dimension * i, offset * dimension, dimension, dimension));
			}

		}
		allAnimations.put(name, res);
	}

}
