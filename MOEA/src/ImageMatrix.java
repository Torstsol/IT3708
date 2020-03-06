import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;

public class ImageMatrix {

    int[][] pixels;

    public ImageMatrix(String imageString) {

        // Creates a two-dimentional array where each input is a pixel's RGB-value
        try {
            BufferedImage image = ImageIO.read(new File(imageString));
            this.pixels = new int[image.getHeight()][image.getWidth()];

            for (int i = 0; i < pixels.length; i++) {
                for (int j = 0; j < pixels[0].length; j++) {
                    this.pixels[i][j] = image.getRGB(j, i);
                }
            }

        } catch (Exception e) {
            System.out.print(e);
        }

    }

    public int getRowLength() {
        return pixels.length;
    }

    public int getColLength() {
        return pixels[0].length;
    }

    public int[][] getIntMatrix() {
        return pixels;
    }

    public void saveAsBlackAndWhite(String path, ChromosomeNew optimalSolution) {
        try {
            File output = new File(path + "Black.jpg");
            BufferedImage image = new BufferedImage(this.getColLength(), this.getRowLength(),
                    BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < this.getRowLength(); i++) {
                for (int j = 0; j < this.getColLength(); j++) {
                    if (optimalSolution.isEdge(i * this.getColLength() + j)) {
                        image.setRGB(j, i, Color.black.getRGB());
                    } else {
                        image.setRGB(j, i, Color.WHITE.getRGB());
                    }

                }
            }
            // make a border around the image
            for (int j = 0; j < this.getColLength(); j++) {
                image.setRGB(j, 0, Color.black.getRGB());
                image.setRGB(j, this.getRowLength() - 1, Color.black.getRGB());
            }
            for (int i = 0; i < this.getRowLength(); i++) {
                image.setRGB(0, i, Color.black.getRGB());
                image.setRGB(this.getColLength() - 1, i, Color.black.getRGB());
            }
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void saveAsGreen(String path, ChromosomeNew optimalSolution) {
        try {
            File output = new File(path + "green.jpg");
            BufferedImage image = new BufferedImage(this.getColLength(), this.getRowLength(),
                    BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < this.getRowLength(); i++) {
                for (int j = 0; j < this.getColLength(); j++) {
                    if (optimalSolution.isEdge(i * this.getColLength() + j)) {
                        image.setRGB(j, i, Color.green.getRGB());
                    } else {
                        image.setRGB(j, i, pixels[i][j]);
                    }

                }
            }
            // make a border around the image
            for (int j = 0; j < this.getColLength(); j++) {
                image.setRGB(j, 0, Color.green.getRGB());
                image.setRGB(j, this.getRowLength() - 1, Color.green.getRGB());
            }
            for (int i = 0; i < this.getRowLength(); i++) {
                image.setRGB(0, i, Color.green.getRGB());
                image.setRGB(this.getColLength() - 1, i, Color.green.getRGB());
            }
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}