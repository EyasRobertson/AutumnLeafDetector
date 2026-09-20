package wit.ca1;

public class Pixel {

    private int xAxis;
    private int yAxis;
    private int colour;

    public Pixel(int xAxis, int yAxis, int colour) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.colour = colour;
    }

    //getters
    public int getxAxis() {
        return xAxis;
    }
    public int getYAxis() {
        return yAxis;
    }
    public int getColour() {
        return colour;
    }
    //setters
    public void setxAxis(int xAxis) {
        this.xAxis = xAxis;
    }
    public void setYAxis(int yAxis) {
        this.yAxis = yAxis;
    }
    public void setColour(int colour) {
        this.colour = colour;
    }
}