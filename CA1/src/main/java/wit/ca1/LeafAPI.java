package wit.ca1;

import java.util.HashMap;
import java.util.Map;

public class LeafAPI {

    UnionFind unionFind = new UnionFind();
    private Map<String, DisjointNode<Pixel>> pixelMap;//to know if a pixel exists/is white/is a node
    private Map<String, DisjointNode<Pixel>> rootMap;//this is used to calculate the sizes of the clusters

    private String[] rootTracker; //for use by functions to keep track of which roots they have checked

    //puts each new pixel in the set of the pixel to the left and above
    //TO DO should add a thing to check the root of the up to see if it's already in a set
    public void clusterPixel(DisjointNode<Pixel> node) {
        //get the x and y
        int x = node.getData().getxAxis();
        int y = node.getData().getYAxis();

        //add it to the hashmap
        String coord = x+","+y;
        pixelMap.put(coord, node);

        //checking the left pixel
        String leftCoord = (x-1)+","+(y);//creates the key to look for
        DisjointNode<Pixel> left = pixelMap.get(leftCoord);//returns the pixel if it exists

        //checking the above pixel
        String upKey = (x)+","+(y-1);//creates the key to look for
        DisjointNode<Pixel> up = pixelMap.get(upKey);//looks for the key

        if(up!= null&&left!=null) {//if both of them exist
            addSizes(node,left);
            unionFind.union(node, left);
            addSizes(node,up);
            unionFind.union(node, up);
        } else if (up != null) {//if the one above exists
            addSizes(node,up);
            unionFind.union(node, up);
        }else if(left != null) { //if the one to the left exists
            addSizes(node,left);
            unionFind.union(node, left);
        }

    }

    public boolean addSizes(DisjointNode<Pixel> node1, DisjointNode<Pixel> node2) {
        DisjointNode<Pixel> root1 = getRoot(node1);
        DisjointNode<Pixel> root2 = getRoot(node2);//going to be new root
        if(root1!=null&&root2!=null) {//if both of the roots exist
            if (root1 == root2) {
                return false;
            }

            int size1 = root1.getSize();
            int size2 = root2.getSize();
            int comb = size1 + size2;

            node1.setSize(0);//node 2 should already be size 0 as it will have gone through this before
            root1.setSize(0);

            root2.setSize(comb);
            return true;
        }
        return false;
    }

    //for reseting all the stuff when chnaing the original picture from files
    public void reset(){
        rootMap = new HashMap<>();
        pixelMap = new HashMap<>();
    }

    //gets the pixel from the hashmap
    public DisjointNode<Pixel> getPixel(int x, int y) {
        String k = x+","+y;

        if (!pixelMap.containsKey(k)) {//if the pixel doesnt exist
            System.out.println("Missing key: " + k);
            return null;
        }
        return pixelMap.get(k);
    }
    public DisjointNode<Pixel> getRoot(DisjointNode<Pixel> node){
        return unionFind.find(node);
    }
    //checks if the roots of 2 given pixels match
    public boolean matchingRoots(DisjointNode<Pixel> pixel,DisjointNode<Pixel> pixel2){
        if(getRoot(pixel) == getRoot(pixel2)) {
            return true;
        }
        return false;
    }

    public void addRoots(DisjointNode<Pixel> root) {
        int x = root.getData().getxAxis();
        int y = root.getData().getYAxis();

        String coord = x+","+y;

        rootMap.putIfAbsent(coord,root);
        rootTracker = new String[numRoots()];
    }
    public void removeRoots(DisjointNode<Pixel> root) {
        int x = root.getData().getxAxis();
        int y = root.getData().getYAxis();
        String coord = x+","+y;
        if(rootMap.containsKey(coord)){
            rootMap.remove(coord,root);
            rootTracker = new String[numRoots()];
        }
    }
    public int numRoots(){
        return rootMap.size();
    }
    public boolean rootCheck(DisjointNode<Pixel> root){
        int x = root.getData().getxAxis();
        int y = root.getData().getYAxis();
        return rootMap.containsKey(x+","+y);
    }

    //checks if the pixel exists in the hashmap
    public boolean pixelCheck(DisjointNode<Pixel> pixel) {
        int x = pixel.getData().getxAxis();
        int y = pixel.getData().getYAxis();
        String k = x+","+y;
        return pixelMap.containsKey(k);
    }

    //finds the minimum x in a set
    public int findXMin(DisjointNode<Pixel> pixel){
        if(!pixelCheck(pixel)){
            return -1;
        }
        int minX = pixel.getData().getxAxis();
        int counter = 1;

        while(counter<getSetSize(pixel)) {
            for (DisjointNode<Pixel> node : pixelMap.values()) {//for every pixel in the hashmap
                if (matchingRoots(pixel, node)) {//checks if the pixels root is the same root
                    counter++;
                    if (node.getData().getxAxis() < minX) {//if the x is lower set minx
                        minX = node.getData().getxAxis();
                    }
                }
            }
        }
        return minX;
    }

    //finds the lowest y in a set
    public int findYMin(DisjointNode<Pixel> pixel) {
        if(!pixelCheck(pixel)){
            return -1;
        }
        int minY = pixel.getData().getYAxis();
        int counter = 1;
        while(counter<getSetSize(pixel)) {
            for (DisjointNode<Pixel> node : pixelMap.values()) {//for every pixel in the hashmap
                if (matchingRoots(pixel, node)) {//checks if the pixels root is the same root
                    counter++;
                    if (node.getData().getYAxis() < minY) {//if the x is lower set minx
                        minY = node.getData().getYAxis();
                    }
                }
            }
        }
        return minY;
    }

    //finds the highest x in a set
    public int findXMax(DisjointNode<Pixel> pixel) {
        if(!pixelCheck(pixel)){
            return -1;
        }
        int maxX = pixel.getData().getxAxis();
        int counter = 1;

        while(counter<getSetSize(pixel)) {
            for (DisjointNode<Pixel> node : pixelMap.values()) {//for every pixel in the hashmap
                if (matchingRoots(pixel, node)) {//checks if the pixels root is the same root
                    counter++;
                    if (node.getData().getxAxis() > maxX) {//if the x is lower set minx
                        maxX = node.getData().getxAxis();
                    }
                }
            }
        }
        return maxX;
    }

    //finds the highest y in a set
    public int findYMax(DisjointNode<Pixel> pixel) {
        if(!pixelCheck(pixel)){
            return -1;
        }
        int maxY = pixel.getData().getYAxis();
        int counter=1;

        while(counter<getSetSize(pixel)) {
            for (DisjointNode<Pixel> node : pixelMap.values()) {//for every pixel in the hashmap
                if (matchingRoots(pixel, node)) {//checks if the pixels root is the same root
                    counter++;
                    if (node.getData().getYAxis() > maxY) {//if the x is lower set minx
                        maxY = node.getData().getYAxis();
                    }
                }
            }
        }
        return maxY;
    }

    //get the size of the set that a given pixel is in
    public int getSetSize(DisjointNode<Pixel> pixel){
        if(!pixelCheck(pixel)){
            return -1;
        }
        DisjointNode<Pixel> root = getRoot(pixel);

        return root.getSize();
    }//could be used for noise reduction // for labelling // for hovering and seeing

    public boolean tooSmall(int minSize, DisjointNode<Pixel> pixel){
        return getSetSize(pixel) < minSize;
    }

    public void removeSpecificSet(DisjointNode<Pixel> node){
        //remove every node in the same set as the given node
        DisjointNode<Pixel> root = getRoot(node);
        removeRoots(root);
        for(DisjointNode<Pixel> pixel :pixelMap.values()) {
            if(matchingRoots(root,pixel)){//if it's in the set
                pixelMap.remove(pixel.getData());
                pixel.remove();
            }
        }
    }

    public String[] sortRootsBySize(){
        int rootNum = rootMap.size();

        String[] sortedRoots = new String[rootNum];//holds the x and y of the sorted roots in order
        String[] rootCoords = new String[rootNum];

        int rcount = 0;
        for (DisjointNode<Pixel> root : rootMap.values()) {//has the coordinates and the sizes of all the roots
            rootCoords[rcount] = "x"+root.getData().getxAxis() + "y" + root.getData().getYAxis() +"|"+ root.getSize()+"|";
            //System.out.println("In rootCoords : "+rootCoords[rcount]);
            rcount++;
        }
        int lastelement = sortedRoots.length-1;
        int i = 0;
        while(sortedRoots[lastelement]==null&&i<lastelement+1 ){
            String first = rootCoords[0];
            for(int c = 0; c<lastelement; c++) {
                if(!alreadySorted(sortedRoots,rootCoords[c])){
                    first = rootCoords[c];
                }
            }
            int currentSizeInt = Integer.parseInt(first.substring(first.indexOf('|') + 1, first.lastIndexOf('|')));

            String biggest ="¦" + (i+1) + "¦" + first;
            int biggestInt = currentSizeInt;

            for(int j = 0; j<rootNum;j++){
                String check = rootCoords[j];
                int checkSizeInt = Integer.parseInt(check.substring(check.indexOf('|') + 1, check.lastIndexOf('|')));
                if(checkSizeInt>biggestInt ){//if the one you are checking is a bigger set than the one already marked as biggest
                    if(sortedRoots[0]!=null){//if sorted roots has something in it already
                        if(!alreadySorted(sortedRoots,check)){//if the one bigger than biggest has not already been sorted then add it to the  array
                            //System.out.println("sorted roots does not already contain "+ check);
                            biggest ="¦"+(i+1)+"¦" + check;
                            biggestInt = checkSizeInt;
                        }
                    }
                    else {//if there is nothing in the sorted array
                        //System.out.println("sorted roots has nothing in it!");
                        biggest = "¦" + (i + 1) + "¦" + check;
                        biggestInt = checkSizeInt;
                    }
                }
            }
            //System.out.println(biggest+ " added");
            sortedRoots[i] = biggest;

            i++;
        }
        return sortedRoots;
    }

    public boolean alreadySorted(String[] sorted, String check){
        for(int i =0 ; i<sorted.length;i++){
            if(sorted[i]==null){//if it gets through all the sorted roots and hasnt found it then it is not in there
                //System.out.println(check+" has not been sorted");
                return false;
            }

            //remove the placing in sorted because other wise it wont match
            String withoutNum = sorted[i].substring(sorted[i].lastIndexOf('¦')+1);
            if (withoutNum.equals(check)) {//if it is already in there then return null
                return true;
            }
        }

        return false;
    }
}