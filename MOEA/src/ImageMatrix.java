import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageMatrix {

    int[][] pixels;

    public ImageMatrix(String imageString) {

        //Creates a two-dimentional array where each input is a pixel's RGB-value
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

    public int getRowLength(){
        return pixels.length;
    }

    public int getColLength(){
        return pixels[0].length;
    }

    public int[][] getIntMatrix(){
        return pixels;
    }
}