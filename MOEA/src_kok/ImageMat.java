import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageMat {
    int width;
    int height;
    Pixel[][] pixels;

    public ImageMat(String imageFile) {
        try {
            File input = new File(imageFile);
            BufferedImage image = ImageIO.read(input);
            this.width = image.getWidth();
            this.height = image.getHeight();

            this.pixels = new Pixel[height][width];

            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {

                    Pixel c = new Pixel(image.getRGB(j, i), i, j, i * this.width + j);
                    pixels[i][j] = c;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void saveAs(String path) {
        try {
            File output = new File(path);
            BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                    Color newColor = this.pixels[i][j].color;
                    image.setRGB(j, i, newColor.getRGB());
                }
            }
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void saveAsGreen(String path, Chromosome optimalSolution) {
        try {
            File output = new File(path + "Green.jpg");
            BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                    if (optimalSolution.isEdge(i * this.width + j)) {
                        Color green = Color.green;
                        image.setRGB(j, i, green.getRGB());
                    } else {
                        Color originalColor = this.pixels[i][j].color;
                        image.setRGB(j, i, originalColor.getRGB());
                    }

                }
            }
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void saveAsBlackAndWhite(String path, Chromosome optimalSolution) {
        try {
            File output = new File(path + "Black.jpg");
            BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                    if (optimalSolution.isEdge(i * this.width + j)) {
                        Color green = Color.black;
                        image.setRGB(j, i, green.getRGB());
                    } else {
                        Color notEdge = Color.WHITE;
                        image.setRGB(j, i, notEdge.getRGB());
                    }

                }
            }
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Pixel[][] getPixels() {
        return pixels;
    }
}
