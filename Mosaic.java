import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class Mosaic {
	// sizes for image scaling
	private static final int tileWidth = 24;
	private static final int tileHeight = 24;
	private static final int tileScale = 12;
	private static int scaledHeight = tileHeight / tileScale;
	private static int scaledWidth = tileWidth / tileScale;
	public static Pixel[][] pixels = new Pixel[scaledWidth][scaledHeight];
	private static final String outputImg = "source/output/output.jpg";

	// method for easy printing
	private static void log(String msg) {
		System.out.println(msg);
	}

	public static void main(String[] args) throws IOException {
		log("Reading tiles...");
		ArrayList<Tile> tileImages = getImagesFromTiles(new File("source/jpg"));
		log("Reading base image...");
		File base = new File("source/base/base.jpg");
		BufferedImage img = ImageIO.read(base);
		log("Matching tiles...");
		final Collection<BufferedImageSec> outputImageSecs = Collections
				.synchronizedSet(new HashSet<BufferedImageSec>());
		Collection<BufferedImageSec> inputImageSecs = getImagesFromInput(base);
		for (final BufferedImageSec inputImageSec : inputImageSecs) {
			Tile bestMatch = getMatch(inputImageSec.image, tileImages);
			outputImageSecs.add(new BufferedImageSec(bestMatch.image, inputImageSec.x, inputImageSec.y));
		}
		int width = img.getWidth();
		int height = img.getHeight();
		log("Creating output image...");
		BufferedImage output = outputImage(width, height, outputImageSecs);
		ImageIO.write(output, "jpg", new File(outputImg));
		log("Finshed");
	}

	// writes outputImage
	private static BufferedImage outputImage(int width, int height, Collection<BufferedImageSec> sections) {
		BufferedImage image = new BufferedImage(width * tileScale, height * tileScale, BufferedImage.TYPE_3BYTE_BGR);

		for (BufferedImageSec sec : sections) {
			BufferedImage section = image.getSubimage(sec.x * tileScale, sec.y * tileScale, tileHeight, tileWidth);
			section.setData(sec.image.getData());
		}

		return image;
	}

	// segregates image into sections
	public static class BufferedImageSec {
		public BufferedImage image;
		public int x;
		public int y;

		public BufferedImageSec(BufferedImage image, int x, int y) {
			this.image = image;
			this.x = x;
			this.y = y;
		}
	}

	public class Pixel {
		public int r;
		public int g;
		public int b;

		public Pixel(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		@Override
		public String toString() {
			return r + "," + g + "," + b;
		}
	}

	// adds sections to a collection
	private static Collection<BufferedImageSec> getImagesFromInput(File inputImgFile) throws IOException {
		Collection<BufferedImageSec> parts = new HashSet<BufferedImageSec>();

		BufferedImage inputImage = ImageIO.read(inputImgFile);
		int totalHeight = inputImage.getHeight();
		int totalWidth = inputImage.getWidth();

		int x = 0;
		int y = 0;
		int w = scaledWidth;
		int h = scaledHeight;
		while (x + w <= totalWidth) {
			while (y + h <= totalHeight) {
				BufferedImage inputImagePart = inputImage.getSubimage(x, y, w, h);
				parts.add(new BufferedImageSec(inputImagePart, x, y));
				y += h;
			}
			y = 0;
			x += w;
		}

		return parts;
	}

	// This method takes in a directory, which it will read in all the tile images
	// from.
	// It returns an ArrayList of type Tile, so that it has the file and the average
	// colors for that image.
	private static ArrayList<Tile> getImagesFromTiles(File tilesDir) throws IOException {
		ArrayList<Tile> tileImages = new ArrayList<Tile>();
		File[] files = tilesDir.listFiles();
		for (File file : files) {
			BufferedImage img = ImageIO.read(file);
			if (img != null) {
				// log("Reading " + file);
				tileImages.add(new Tile(img));
			} else {
				System.err.println("null image for file " + file.getName());
			}
		}
		return tileImages;
	}

	// matches appropriate image to tile
	public static Tile getMatch(BufferedImage image, Collection<Tile> tiles) {
		int avgRed;
		int avgGreen;
		int avgBlue;
		Tile bestMatch = null;
		int closestMatch = -1;
		int width = image.getWidth();
		int height = image.getHeight();
		long red = 0, green = 0, blue = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color pixel = new Color(image.getRGB(x, y));
				red += pixel.getRed();
				green += pixel.getGreen();
				blue += pixel.getBlue();
			}
		}
		int numPixels = width * height;
		avgRed = (int) (red / numPixels);
		avgGreen = (int) (green / numPixels);
		avgBlue = (int) (blue / numPixels);
		for (Tile tile : tiles) {
			int average = 255 * 3 - (Math.abs(avgRed - tile.averageRed) + Math.abs(avgGreen - tile.averageGreen)
					+ Math.abs(avgBlue - tile.averageBlue));
			if (average > closestMatch) {
				closestMatch = average;
				bestMatch = tile;
			}
		}
		return bestMatch;
	}

//A tile class to handle a single tile, with constructor calculating the average colors of the tile image
	public static class Tile {
		public int averageRed;
		public int averageGreen;
		public int averageBlue;
		public BufferedImage image;
		public static int tileWidth = 24;
		public static int tileHeight = 24;
		public static int tileScale = 8;
		private static int scaledHeight = tileHeight / tileScale;
		private static int scaledWidth = tileWidth / tileScale;
		public Pixel[][] pixels = new Pixel[scaledWidth][scaledHeight];

		// Constructor receives an image, and calculates it's average
		public Tile(BufferedImage i) {
			image = i;
			calculateAverage();
		}

		// Calculate the average RGB for the local image
		// local variables are updated, nothing is returned.
		private void calculateAverage() {
			int width = image.getWidth();
			int height = image.getHeight();
			long red = 0, green = 0, blue = 0;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					Color pixel = new Color(image.getRGB(x, y));
					red += pixel.getRed();
					green += pixel.getGreen();
					blue += pixel.getBlue();
				}
			}
			int numPixels = width * height;
			averageRed = (int) (red / numPixels);
			averageGreen = (int) (green / numPixels);
			averageBlue = (int) (blue / numPixels);
		}
	}
}