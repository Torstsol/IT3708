import java.awt.*;

public class Pixel {

    public Color color;
    int rowIdx;
    int colIdx;
    int pixelIdx;

    public Pixel(int rgb, int rowIdx, int colIdx, int pixelIdx) {
        this.color = new Color(rgb);
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
        this.pixelIdx = pixelIdx;
    }

    public double getRed(){
        return color.getRed();
    }

    public double getGreen(){
        return color.getGreen();
    }

    public double getBlue(){
        return color.getBlue();
    }

    public int getRowIdx() {
        return rowIdx;
    }

    public int getColIdx() {
        return colIdx;
    }

    public int getPixelIdx() { return pixelIdx; }
}
