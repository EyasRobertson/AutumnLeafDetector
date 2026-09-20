package wit.ca1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;


public class LeafController {

    public LeafAPI API = new LeafAPI();

    @FXML
    private ImageView imageView;
    @FXML
    private ImageView altImageView;
    @FXML
    private ImageView boxView;
    @FXML
    private ImageView hsbView;
    @FXML
    private Slider noiseRed;
    @FXML
    private ImageView numImageView;
    @FXML
    private Label clusterSizeLabel;
    @FXML
    private Label leafEstLabel;
    @FXML
    private Slider hueSlider;
    @FXML
    private Slider satSlider;
    @FXML
    private Slider briSlider;



    //getting the image
    @FXML
    private void handleChooseImage() {
        API.reset();//everything needs to be reset if you choose another image

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        // Show the file chooser and make selected into a file obj
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {//if there's a file
            Image image = new Image(file.toURI().toString());//make an image from the file that was chosen
            imageView.setImage(image);
            makeInvPic();
        }
    }

    @FXML
    public void getMaxAndMin(){
        String[] rootTracker = new String[API.numRoots()];
        makeInvPic();
        int i =0;
        for (int y = 0; y < yAxis(); y++) {//creates the for loop to loop through all the y values
            for (int x = 0; x < xAxis(); x++) {//creates the for loop for the x values
                if(!checkIfBlack(x,y)){//if the pixel is white
                    DisjointNode<Pixel> n = API.getPixel(x,y);
                    if(!contained(rootTracker,n)) {//if the root tracker doesnt contain
                        rootTracker[i]= API.getRoot(n).getData().getxAxis()+ " " + API.getRoot(n).getData().getYAxis();
                        if(i<=rootTracker.length){
                            i++;
                        }
                        int minX = API.findXMin(API.getPixel(x, y));
                        int minY = API.findYMin(API.getPixel(x, y));
                        int maxX = API.findXMax(API.getPixel(x, y));
                        int maxY = API.findYMax(API.getPixel(x, y));
                        boundingBox(minX, minY, maxX, maxY);
                    }
                }
            }
        }
    }

    @FXML
    public void makeColourful(){
        WritableImage blackAndWhite = (WritableImage) altImageView.getImage();
        PixelWriter pixelWriter = blackAndWhite.getPixelWriter();

        for(int y = 0; y < yAxis(); y++){
            for(int x = 0; x < xAxis(); x++){
                if(checkIfWhite(x,y)){
                    DisjointNode<Pixel> node = API.getPixel(x,y);
                    int argbval = randomColour(x,y);//makes the random colour for each set cohesive
                    for(int i = 0; i<yAxis(); i++){//checks each y
                        for(int j = 0; j<xAxis(); j++){//checks each x
                            if(checkIfWhite(j,i)){//checks if its a white pixel
                                DisjointNode<Pixel> n = API.getPixel(j,i);//gets the obj of that pixel
                                if(API.matchingRoots(node,n)){//if the root of the set matches the root of the node
                                    pixelWriter.setArgb(j,i,argbval);//set it to be the same colour
                                }
                            }
                        }
                    }
                    altImageView.setImage(blackAndWhite);
                }
            }
        }
    }

    @FXML
    public void noiseReduction(){
        int smallestSetSize = (int) noiseRed.getValue();
        System.out.println(smallestSetSize);


        for(int y =0 ; y<yAxis(); y++){
            for(int x =0 ; x<xAxis(); x++){
                if(!checkIfBlack(x,y)){
                    DisjointNode<Pixel> node = API.getPixel(x,y);
                    if(API.tooSmall(smallestSetSize, node)){
                        removePixelCluster(node);
                    }
                }
            }
        }

    }

    @FXML
    public void leafEstimate(){
        //some large ones rep more than 1 leaf?
        int numRoots = API.numRoots();
        System.out.println("Leaf estimate " + numRoots);
        leafEstLabel.setText("Leaf/Cluster estimate " + numRoots);
    }

    @FXML
    public void handleColouringClick(MouseEvent event) {//click on the altered image to colour a set
        double x = event.getX();
        double y = event.getY();

        //this is to scale it

        double offset = offsetY();

        double adjustedX = x*scaleX();
        double adjustedY = (y-offset) * scaleY();

        DisjointNode<Pixel> node = API.getPixel( (int) adjustedX, (int) adjustedY);//get the actual pixel that the x and y represent

        WritableImage blackAndWhite = (WritableImage) altImageView.getImage();
        PixelWriter pixelWriter = blackAndWhite.getPixelWriter();

        int argbval = randomColour(adjustedX,adjustedY);

        for(int i=0;i<yAxis();i++) {//for each y
            for (int j = 0; j < xAxis(); j++) {//for each x in bounds
                if(checkIfWhite(j,i)){//if the pixel is white
                    DisjointNode<Pixel> n = API.getPixel(j,i);
                    if(API.matchingRoots(node,n)){//if the roots match
                        pixelWriter.setArgb(j,i,argbval);
                    }
                }
            }
        }
        altImageView.setImage(blackAndWhite);
    }

    @FXML
    public void handleMouseHover(MouseEvent event){
        double x = event.getX();
        double y = event.getY();

        double offset = offsetY();

        double adjustedX = x*scaleX();
        double adjustedY = (y-offset) * scaleY();

        int clustersize = getSetSize((int)adjustedX,(int)adjustedY);
        if(!(clustersize ==-1)){
            clusterSizeLabel.setText("Cluster Size : "+clustersize);
        }
    }

    @FXML
    public void handleMouseClickOGImage(MouseEvent event) {//passes the x and y of the mouse click so that the colour can be found
        double x = event.getX();
        double y = event.getY();

        double offset = offsetY();

        double adjustedX = x*scaleX();
        double adjustedY = (y-offset) * scaleY();

        int argb = getTheColour(adjustedX, adjustedY);//gets the colour of the pixel the mouse clicked on


        //this uh
        //doesnt actually affect anything
        //intended to pass the argb of the image to the black and white
        //so that it would take those colours into consideration
        //but it does not do that right now 
    }

    //makes the og overlay invisible
    private void makeInvPic(){
        Image originalImage = imageView.getImage();//grabs the og image

        WritableImage inv = new WritableImage((int) originalImage.getWidth(), (int) originalImage.getHeight());
        PixelWriter pixelWriter = inv.getPixelWriter();

        int invargb = 0;

        for (int y = 0; y < originalImage.getHeight(); y++) {//creates the for loop to loop through all the y values
            for (int x = 0; x < originalImage.getWidth(); x++) {//the for loop to loop through all the x values
                pixelWriter.setArgb(x, y, invargb);//actually sets the argb value for that pixel
            }
        }
        boxView.setImage(inv);
    }
    public void makeInvNumPic(){
        Image blackAndWhite = altImageView.getImage();//grabs the og image

        WritableImage inv = new WritableImage((int) blackAndWhite.getWidth(), (int) blackAndWhite.getHeight());
        PixelWriter pixelWriter = inv.getPixelWriter();

        int invargb = 0;

        for (int y = 0; y < blackAndWhite.getHeight(); y++) {//creates the for loop to loop through all the y values
            for (int x = 0; x < blackAndWhite.getWidth(); x++) {//the for loop to loop through all the x values
                pixelWriter.setArgb(x, y, invargb);//actually sets the argb value for that pixel
            }
        }
        numImageView.setImage(inv);
    }

    //makes the black and white version of the og image
    @FXML
    public void blackAndWhite() {
        API.reset();//resets the hashmaps
        Image originalImage = imageView.getImage();
        if(hsbView.getImage()!=null){
            originalImage = hsbView.getImage();
        }

        //sets up the writable image, the pixel reader And the pixel writer
        WritableImage alteredImage = new WritableImage((int) originalImage.getWidth(), (int) originalImage.getHeight());
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = alteredImage.getPixelWriter();

        ColorAdjust colorAdjust = new ColorAdjust();

        for (int y = 0; y < originalImage.getHeight(); y++) {//creates the for loop to loop through all the y values
            for (int x = 0; x < originalImage.getWidth(); x++) {//the for loop to loop through all the x values
                double hue = colorAdjust.getHue();
                double brightness = colorAdjust.getBrightness();
                double saturation = colorAdjust.getSaturation();

                int argbVal = pixelReader.getArgb(x, y);//gets the argb value for the pixel

                int a = (argbVal >> 24) & 0xff;
                int r = (argbVal >> 16) & 0xFF;
                int g = (argbVal >> 8) & 0xFF;
                int b = argbVal & 0xFF;

                int gray = (r + g + b) / 3;//finds out the grey scale value

                if (r > g && gray >= 120) {//checks if it should be white
                    r = g = b = 255;

                    Pixel pixel = new Pixel(x, y, 255);
                    DisjointNode<Pixel> node = new DisjointNode<>(pixel);

                    API.clusterPixel(node);//passes the pixel to be put into a set
                } else {//makes the pixel black
                    r = g = b = 0;
                }
                int changeArgb = (a << 24) | (r << 16) | (g << 8) | b;//reconstructs the argb value to be useable

                pixelWriter.setArgb(x, y, changeArgb);//actually sets the argb value for that pixel
            }
        }
        altImageView.setImage(alteredImage);//shows the black and white image in the other image view
        makeInvNumPic();
        everyPixel();
    }

    //goes through every pixel, passes it to the API for it to be added to the roothashmap
    public void everyPixel(){
        for(int y = 0; y<yAxis(); y++){
            for(int x = 0; x<xAxis(); x++){
                if(!checkIfBlack(x,y)){
                    DisjointNode<Pixel> n = API.getPixel(x,y);
                    DisjointNode<Pixel> r = API.getRoot(n);

                    API.addRoots(r);
                }
            }
        }
    }

    //draws the bounding boxes around the clusters
    private void boundingBox(int minX, int minY, int maxX, int maxY){
        WritableImage box = (WritableImage) boxView.getImage();
        PixelWriter pixelWriter = box.getPixelWriter();

        int blue = (255<<24) | 200;

        //goes through every pixel for a given box and makes the box
        for(int y=minY;y<=maxY;y++){//for each y
            for(int x=minX;x<=maxX;x++){//for each x in bounds
                if(y==minY || y==minY+1 || y==maxY || y==maxY-1){
                    if (x >= 0 && x < box.getWidth() && y >= 0 && y < box.getHeight()) {
                        pixelWriter.setArgb(x, y, blue);
                    }
                }
                if(x==minX || x==minX+1 || x==maxX || x==maxX-1){
                    if (x >= 0 && x < box.getWidth() && y >= 0 && y < box.getHeight()) {
                        pixelWriter.setArgb(x, y, blue);
                    }
                }
            }
        }
        boxView.setImage(box);
    }

    //takes in a pixel, then changes every other pixel that shares it's root to black
    public void removePixelCluster(DisjointNode<Pixel> node){
        WritableImage blackAndWhite = (WritableImage) altImageView.getImage();
        PixelWriter pixelWriter = blackAndWhite.getPixelWriter();

        int argb = (255<<24);

        for(int y =0 ; y<yAxis(); y++){
            for(int x =0 ; x<xAxis(); x++){
                if(!checkIfBlack(x,y)){
                    DisjointNode<Pixel> n = API.getPixel(x,y);
                    if(API.matchingRoots(node,n)){
                        pixelWriter.setArgb(x,y,argb);
                    }
                }
            }
        }

        altImageView.setImage(blackAndWhite);

        API.removeSpecificSet(node);

        makeInvPic();
        makeInvNumPic();
    }

    //checks if a pixel is white or not
    public boolean checkIfWhite(int x, int y){
        Image blackAndWhite = altImageView.getImage();//gets the black and white image
        if(blackAndWhite==null){
            return false;
        }
        PixelReader pixelReader = blackAndWhite.getPixelReader();//can I initialize this without having to have the b&w too?
        int argbVal = pixelReader.getArgb(x, y);//gets the argb of the pixel x,y
        int r = (argbVal >> 16) & 0xFF;
        int g = (argbVal >> 8) & 0xFF;
        int b = argbVal & 0xFF;

        //if the pixel is white
        return r == 255 && g == 255 && b == 255;
    }
    private boolean checkIfBlack(int x, int y){
        Image blackAndWhite = altImageView.getImage();
        if(blackAndWhite==null){
            return true;
        }
        PixelReader pixelReader = blackAndWhite.getPixelReader();
        int argbVal = pixelReader.getArgb(x, y);
        int r = (argbVal >> 16) & 0xFF;
        int g = (argbVal >> 8) & 0xFF;
        int b = argbVal & 0xFF;

        return r==0 && g==0 && b==0;
    }
    private boolean checkIfInv(int x, int y){
        Image number = numImageView.getImage();//gets the black and white image
        if(number==null){
            return false;
        }
        PixelReader pixelReader = number.getPixelReader();//can i initialize this without having to have the b&w too?
        int argbVal = pixelReader.getArgb(x, y);//gets the argb of the pixel x,y

        return (argbVal >> 24)==0;
    }


    //makes a 'random' colour
    public int randomColour(double x, double y){

        int argbval = getTheColour(x,y);
        int inty = (int) y;
        int intx = (int) x;
        int a = 255;
        int r = (argbval >> 16) & 0xFF;
        int g = (argbval >> 8) & 0xFF;
        int b = argbval & 0xFF;

        r = r *intx;
        g = g *inty;
        b = b *intx ;

        while(r>230){
            r = r-inty;
        }
        while(g>230){
            g= g-intx;
        }
        while(b>=230){
            b = b-inty;
        }

        argbval = (a<<24) | (r << 16) | (g << 8) | b;

        return argbval;
    }

    public int getSetSize(int x, int y){
        //if it's not black
        if(!checkIfBlack(x,y)){
            DisjointNode<Pixel> node = API.getPixel(x,y);
            return API.getSetSize(node);
        }
        return -1;
    }

    //for labeling in descending order
    @FXML
    public void labelClusters(){
        String[] sortedRoots = API.sortRootsBySize();

        for(int i =0;i<sortedRoots.length;i++){
            int xStart = sortedRoots[i].indexOf('x')+1;
            int xEnd = sortedRoots[i].indexOf('y');

            int x = Integer.parseInt(sortedRoots[i].substring(xStart, xEnd));
            int y = Integer.parseInt(sortedRoots[i].substring(sortedRoots[i].indexOf('y') + 1, sortedRoots[i].indexOf('|')));

            String orderNum =sortedRoots[i].substring(sortedRoots[i].indexOf('¦') + 1, sortedRoots[i].lastIndexOf('¦'));
            for(int j=0; j<orderNum.length();j++){

                if(j==orderNum.length()-1){
                    //System.out.println("Substring " +j+" "+orderNum.substring(j));
                    if(orderNum.substring(j).equals("1")){
                        if(j==1)
                            drawOne(x+3,y);
                        else if(j==2)
                            drawOne(x+6,y);
                        else
                            drawOne(x,y);
                    }
                    else if(orderNum.substring(j).equals("2")){
                        if(j==1)
                            drawTwo(x+3,y);
                        else if(j==2)
                            drawTwo(x+6,y);
                        else
                            drawTwo(x,y);
                    }
                    else if(orderNum.substring(j).equals("3")){
                        if(j==1)
                            drawThree(x+3,y);
                        else if(j==2)
                            drawThree(x+6,y);
                        else
                            drawThree(x,y);
                    }
                    else if(orderNum.substring(j).equals("4")){
                        if(j==1)
                            drawFour(x+3,y);
                        else if(j==2)
                            drawFour(x+6,y);
                        else
                            drawFour(x,y);
                    }
                    else if(orderNum.substring(j).equals("5")){
                        if(j==1)
                            drawFive(x+3,y);
                        else if(j==2)
                            drawFive(x+6,y);
                        else
                            drawFive(x,y);
                    }
                    else if(orderNum.substring(j).equals("6")){
                        if(j==1)
                            drawSix(x+3,y);
                        else if(j==2)
                            drawSix(x+6,y);
                        else
                            drawSix(x,y);
                    }
                    else if(orderNum.substring(j).equals("7")){
                        if(j==1)
                            drawSeven(x+3,y);
                        else if(j==2)
                            drawSeven(x+6,y);
                        else
                            drawSeven(x,y);
                    }
                    else if(orderNum.substring(j).equals("8")){
                        if(j==1)
                            drawEight(x+3,y);
                        else if(j==2)
                            drawEight(x+6,y);
                        else
                            drawEight(x,y);
                    }
                    else if(orderNum.substring(j).equals("9")){
                        if(j==1)
                            drawNine(x+3,y);
                        else if(j==2)
                            drawNine(x+6,y);
                        else
                            drawNine(x,y);
                    }
                    else if(orderNum.substring(j).equals("0")){
                        if(j==1)
                            drawZero(x+3,y);
                        else if(j==2)
                            drawZero(x+6,y);
                        else
                            drawZero(x,y);
                    }
                }
                else {
                    int substringEnd = j+1;
                    //System.out.println("Substring " +j+" "+orderNum.substring(j, substringEnd));
                    if (orderNum.substring(j, substringEnd).equals("1")) {
                        if (j == 1)
                            drawOne(x + 3, y);
                        else if (j == 2)
                            drawOne(x + 6, y);
                        else
                            drawOne(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("2")) {
                        if (j == 1)
                            drawTwo(x + 3, y);
                        else if (j == 2)
                            drawTwo(x + 6, y);
                        else
                            drawTwo(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("3")) {
                        if (j == 1)
                            drawThree(x + 3, y);
                        else if (j == 2)
                            drawThree(x + 6, y);
                        else
                            drawThree(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("4")) {
                        if (j == 1)
                            drawFour(x + 3, y);
                        else if (j == 2)
                            drawFour(x + 6, y);
                        else
                            drawFour(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("5")) {
                        if (j == 1)
                            drawFive(x + 3, y);
                        else if (j == 2)
                            drawFive(x + 6, y);
                        else
                            drawFive(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("6")) {
                        if (j == 1)
                            drawSix(x + 3, y);
                        else if (j == 2)
                            drawSix(x + 6, y);
                        else
                            drawSix(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("7")) {
                        if (j == 1)
                            drawSeven(x + 3, y);
                        else if (j == 2)
                            drawSeven(x + 6, y);
                        else
                            drawSeven(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("8")) {
                        if (j == 1)
                            drawEight(x + 3, y);
                        else if (j == 2)
                            drawEight(x + 6, y);
                        else
                            drawEight(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("9")) {
                        if (j == 1)
                            drawNine(x + 3, y);
                        else if (j == 2)
                            drawNine(x + 6, y);
                        else
                            drawNine(x, y);
                    }
                    else if (orderNum.substring(j, substringEnd).equals("0")){
                        if (j == 1)
                            drawZero(x + 3, y);
                        else if (j == 2)
                            drawZero(x + 6, y);
                        else
                            drawZero(x, y);
                    }
                }
            }
        }
        System.out.println("done labeling");

    }

    private void drawOne(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);

        if((x+3)<xAxis()&&(y+4)<yAxis()) {
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x, y + 1, argb);
            pixelWriter.setArgb(x + 1, y + 1, argb);
            pixelWriter.setArgb(x + 1, y + 2, argb);
            pixelWriter.setArgb(x + 1, y + 3, argb);
            pixelWriter.setArgb(x, y + 4, argb);
            pixelWriter.setArgb(x + 1, y + 4, argb);
            pixelWriter.setArgb(x + 2, y + 4, argb);
            numImageView.setImage(number);
        }
    }
    private void drawTwo(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red
        if((x+3)<xAxis()&&(y+4)<yAxis()) {
            pixelWriter.setArgb(x + 1, y, argb);

            pixelWriter.setArgb(x, y + 1, argb);
            pixelWriter.setArgb(x + 2, y + 1, argb);

            pixelWriter.setArgb(x + 2, y + 2, argb);

            pixelWriter.setArgb(x + 1, y + 3, argb);

            pixelWriter.setArgb(x, y + 4, argb);
            pixelWriter.setArgb(x + 1, y + 4, argb);
            pixelWriter.setArgb(x + 2, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 2 @" + x + y);

        }
    }
    private void drawThree(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();
        int argb = (255<<24) | (200<<16);//should be red

        if((x+3)<xAxis()&&(y+4)<yAxis()) {
            pixelWriter.setArgb(x, y, argb);
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x + 2, y, argb);

            pixelWriter.setArgb(x + 2, y + 1, argb);

            pixelWriter.setArgb(x, y + 2, argb);
            pixelWriter.setArgb(x + 1, y + 2, argb);
            pixelWriter.setArgb(x + 2, y + 2, argb);

            pixelWriter.setArgb(x + 2, y + 3, argb);

            pixelWriter.setArgb(x, y + 4, argb);
            pixelWriter.setArgb(x + 1, y + 4, argb);
            pixelWriter.setArgb(x + 2, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 3 @" + x + y);
        }
    }
    private void drawFour(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red

        if((x+3)<xAxis()&&(y+4)<yAxis()){
            pixelWriter.setArgb(x, y, argb);
            pixelWriter.setArgb(x + 2, y, argb);

            pixelWriter.setArgb(x, y + 1, argb);
            pixelWriter.setArgb(x + 2, y + 1, argb);

            pixelWriter.setArgb(x, y + 2, argb);
            pixelWriter.setArgb(x + 1, y + 2, argb);
            pixelWriter.setArgb(x + 2, y + 2, argb);

            pixelWriter.setArgb(x + 2, y + 3, argb);
            pixelWriter.setArgb(x + 2, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 4 @" + x + y);
        }
    }
    private void drawFive(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red

        if((x+3)<xAxis()&&(y+4)<yAxis()){
            pixelWriter.setArgb(x, y, argb);
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x + 2, y, argb);

            pixelWriter.setArgb(x, y + 1, argb);

            pixelWriter.setArgb(x, y + 2, argb);
            pixelWriter.setArgb(x + 1, y + 2, argb);

            pixelWriter.setArgb(x + 2, y + 3, argb);

            pixelWriter.setArgb(x, y + 4, argb);
            pixelWriter.setArgb(x + 1, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 5 @" + x + y);
        }
    }
    private void drawSix(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red

        if((x+3)<xAxis()&&(y+4)<yAxis()){
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x + 2, y, argb);

            pixelWriter.setArgb(x, y + 1, argb);

            pixelWriter.setArgb(x, y + 2, argb);
            pixelWriter.setArgb(x + 1, y + 2, argb);

            pixelWriter.setArgb(x, y + 3, argb);
            pixelWriter.setArgb(x + 2, y + 3, argb);

            pixelWriter.setArgb(x + 1, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 6 @" + x + y);
        }
    }
    private void drawSeven(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red

        if((x+3)<xAxis()&&(y+4)<yAxis()){
            pixelWriter.setArgb(x, y, argb);
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x + 2, y, argb);

            pixelWriter.setArgb(x + 2, y + 1, argb);
            pixelWriter.setArgb(x + 2, y + 2, argb);
            pixelWriter.setArgb(x + 2, y + 3, argb);
            pixelWriter.setArgb(x + 2, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 7 @" + x + y);
        }

    }
    private void drawEight(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red
        if((x+3)<xAxis()&&(y+4)<yAxis()) {
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x, y + 1, argb);
            pixelWriter.setArgb(x + 2, y + 1, argb);

            pixelWriter.setArgb(x + 1, y + 2, argb);

            pixelWriter.setArgb(x, y + 3, argb);
            pixelWriter.setArgb(x + 2, y + 3, argb);

            pixelWriter.setArgb(x + 1, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 8 @" +(x)+(y));
        }
    }
    private void drawNine(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red

        if((x+3)<xAxis()&&(y+4)<yAxis()){
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x + 2, y, argb);

            pixelWriter.setArgb(x, y + 1, argb);
            pixelWriter.setArgb(x + 2, y + 1, argb);

            pixelWriter.setArgb(x + 1, y + 2, argb);
            pixelWriter.setArgb(x + 2, y + 2, argb);

            pixelWriter.setArgb(x + 2, y + 3, argb);

            pixelWriter.setArgb(x, y + 4, argb);
            pixelWriter.setArgb(x + 1, y + 4, argb);

            numImageView.setImage(number);
            //System.out.println("drew 9 @" + x + y);
        }
    }
    private void drawZero(int x, int y){
        WritableImage number = (WritableImage) numImageView.getImage();
        PixelWriter pixelWriter = number.getPixelWriter();

        int argb = (255<<24) | (200<<16);//should be red

        if((x+3)<xAxis()&&(y+4)<yAxis()){
            pixelWriter.setArgb(x, y, argb);
            pixelWriter.setArgb(x + 1, y, argb);
            pixelWriter.setArgb(x + 2, y, argb);

            pixelWriter.setArgb(x, y + 1, argb);
            pixelWriter.setArgb(x + 2, y + 1, argb);

            pixelWriter.setArgb(x, y + 2, argb);
            pixelWriter.setArgb(x + 2, y + 2, argb);

            pixelWriter.setArgb(x, y + 3, argb);
            pixelWriter.setArgb(x + 2, y + 3, argb);

            pixelWriter.setArgb(x, y + 4, argb);
            pixelWriter.setArgb(x + 1, y + 4, argb);
            pixelWriter.setArgb(x + 2, y + 4, argb);

            numImageView.setImage(number);
        }
    }


    //for scaling
    private double scaleX(){
        double imageWidth = xAxis();
        double viewWidth = imageView.getBoundsInLocal().getWidth();

        return imageWidth / viewWidth;
    }
    private double scaleY(){
        double imageHeight = yAxis();
        double viewHeight = imageView.getBoundsInLocal().getHeight();

        return imageHeight / viewHeight;
    }
    private double offsetY(){
        double imageWidth = xAxis();
        double imageHeight =yAxis();

        double viewWidth = imageView.getBoundsInLocal().getWidth();
        double viewHeight = imageView.getBoundsInLocal().getHeight();

        double displayedHeight = viewWidth * (imageHeight/imageWidth);

        return (viewHeight - displayedHeight)/2;
    }

    //is a root in rootTracker already
    private boolean contained(String[] rootTracker, DisjointNode<Pixel> node){
        if(rootTracker[0]==null)//if the root tracker is empty the node isnt in there
            return false;
        DisjointNode<Pixel> root = API.getRoot(node);
        String coord = root.getData().getxAxis()+ " " + root.getData().getYAxis();
        for (String string : rootTracker) {//for each key in rootTracker
            if (string == null)//if it reaches the end of the added roots then the root hasnt been added
                return false;
            if (string.equals(coord)) {//if the key matches then it is in the hashmap
                return true;
            }
        }
        return false;
    }

    //gets the width of the image
    private int xAxis() {
        Image originalImage = imageView.getImage();
        return (int) originalImage.getWidth();
    }
    //returns height of the image
    private int yAxis() {
        Image originalImage = imageView.getImage();
        return (int) originalImage.getHeight();
    }
    //gets the colour of a given pixel
    private int getTheColour(double x, double y){
        //get the colour of the given x and y
        Image ogPicture = imageView.getImage();
        PixelReader pixelReader = ogPicture.getPixelReader();

        return pixelReader.getArgb((int) x, (int) y);
    }

    @FXML
    public void adjustHSB(){
        Image originalImage = imageView.getImage();
        WritableImage HSBaltImage = new WritableImage(xAxis(), yAxis());
        PixelWriter pixelWriter = HSBaltImage.getPixelWriter();
        PixelReader pixelReader = originalImage.getPixelReader();

        for(int y=0;y<yAxis();y++){
            for(int x=0;x<xAxis();x++){
                int argb = pixelReader.getArgb(x,y);

                int a = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = (argb) & 0xff;

                float[] hsb = Color.RGBtoHSB(r,g,b,null);

                float hue = hsb[0];
                float saturation = hsb[1];
                float brightness = hsb[2];

                hue += (float) (hueSlider.getValue() /10.0);
                saturation += (float) (satSlider.getValue() /10.0);
                brightness += (float) (briSlider.getValue() /10.0);

                int rgb = Color.HSBtoRGB(hue, saturation, brightness);

                int altR = (rgb >> 16) & 0xff;
                int altG = (rgb >> 8) & 0xff;
                int altB = (rgb) & 0xff;

                int altARGB = (a<<24) | (altR<<16) | (altG<<8) | altB;
                pixelWriter.setArgb(x,y,altARGB);
            }
        }
        hsbView.setImage(HSBaltImage);
    }
}