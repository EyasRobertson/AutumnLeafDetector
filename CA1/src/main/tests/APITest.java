
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.ca1.DisjointNode;
import wit.ca1.LeafAPI;
import wit.ca1.Pixel;

public class APITest {
    LeafAPI api = new LeafAPI();

    private DisjointNode<Pixel> parent1;
    private DisjointNode<Pixel> parent2;
    private DisjointNode<Pixel> node1;
    private DisjointNode<Pixel> node2;
    private DisjointNode<Pixel> node3;
    private DisjointNode<Pixel> node4;
    private DisjointNode<Pixel> node5;
    private DisjointNode<Pixel> node6;
    private DisjointNode<Pixel> not;

    @BeforeEach
    public void setUp() {
        Pixel root1 = new Pixel(0, 0, 0);
        Pixel pix1 = new Pixel(1, 0, 255);
        Pixel pix2 = new Pixel(0, 1, 255);
        Pixel pix5 = new Pixel(1, 1, 255);
        Pixel pix6 = new Pixel(0, 2, 255);

        Pixel root2 = new Pixel(5, 10, 255);
        Pixel pix3 = new Pixel(6, 10, 255);
        Pixel pix4 = new Pixel(6, 11, 255);

        Pixel notPix = new Pixel(10, 10, 0);

        parent1 = new DisjointNode<Pixel>(root1);
        parent2 = new DisjointNode<Pixel>(root2);
        node1 = new DisjointNode<Pixel>(pix1);
        node2 = new DisjointNode<Pixel>(pix2);
        node3 = new DisjointNode<Pixel>(pix3);
        node4 = new DisjointNode<Pixel>(pix4);
        node5 = new DisjointNode<Pixel>(pix5);
        node6 = new DisjointNode<Pixel>(pix6);

        not = new DisjointNode<Pixel>(notPix);

        api.reset();//to initialize the hashmaps
        api.clusterPixel(parent1);
        api.clusterPixel(node1);
        api.clusterPixel(node2);
        api.clusterPixel(parent2);
        api.clusterPixel(node3);
        api.clusterPixel(node4);
        api.clusterPixel(node5);
        api.clusterPixel(node6);
    }

    @Test
    public void testClustering() {
        assert(node1.getParent().equals(parent1));
        assert(node2.getParent().equals(parent1));
        assert(node3.getParent().equals(parent2));
    }

    @Test
    public void testGetPixel(){
        assert(api.getPixel(0,0) == parent1);
        assert(api.getPixel(5,10) == parent2);
        assert(api.getPixel(1,0) == node1);
        assert(api.getPixel(0,1) == node2);
        assert(api.getPixel(6,10) == node3);

        assert(api.getPixel(10,10) == null);
    }

    @Test
    public void testGetRoot(){
        assert(api.getRoot(node1)==parent1);
        assert(api.getRoot(node2)==parent1);
        assert(api.getRoot(node3)==parent2);
    }

    @Test
    public void testAddSizes(){
        assert(node1.getSize() == 0);
        assert(node2.getSize() == 0);
        assert(node3.getSize() == 0);

        assert(parent1.getSize() == 5);
        assert(parent2.getSize() == 3);
    }

    @Test
    public void testMatchingRoots(){
        assert(api.matchingRoots(node1, node2));
        assert(!api.matchingRoots(node1, node3));
    }

    @Test
    public void testAddRoot(){
        api.addRoots(parent1);
        assert(api.rootCheck(parent1));
        assert(!api.rootCheck(node1));
        assert(!api.rootCheck(parent2));
        api.addRoots(parent2);
        assert(api.rootCheck(parent2));
    }

    @Test
    public void testRemoveRoot(){
        api.addRoots(parent1);
        assert(api.rootCheck(parent1));
        api.removeRoots(parent1);
        assert(!api.rootCheck(parent1));
    }

    @Test
    public void testNumRoots(){
        assert(api.numRoots()==0);
        api.addRoots(parent1);
        assert(api.numRoots()==1);
        api.addRoots(parent2);
        assert(api.numRoots()==2);
    }

    @Test
    public void testPixelCheck(){
        assert(api.pixelCheck(node1));
        Pixel newpixel = new Pixel(20, 0, 170);
        DisjointNode<Pixel> newnode = new DisjointNode<>(newpixel);
        assert!(api.pixelCheck(newnode));
    }

    @Test
    public void testXMinMax(){
        assert(api.findXMin(node1) == 0);
        assert(api.findXMin(node3) == 5);
        assert(api.findXMin(node4) == 5);

        assert(api.findXMax(node1)== 1);
        assert(api.findXMax(node3) == 6);
        assert(api.findXMax(node4) == 6);

        assert(api.findXMin(not)==-1);
        assert(api.findXMax(not)==-1);
    }

    @Test
    public void testYMinMax(){
        assert(api.findYMin(node1) == 0);
        assert(api.findYMin(node3) == 10);
        assert(api.findYMin(node4) == 10);

        assert(api.findYMax(node1)== 2);
        assert(api.findYMax(node3) == 11);
        assert(api.findYMax(node4) == 11);

        assert(api.findYMin(not)==-1);
        assert(api.findYMax(not)==-1);
    }

    @Test
    public void testTooSmall(){
        assert!(api.tooSmall(5,node1));
        assert(api.tooSmall(5,node3));
    }

    @Test
    public void testSetSize(){
        assert(api.getSetSize(node1)==5);
        assert(api.getSetSize(node2)==5);
        assert(api.getSetSize(node3)==3);
        assert(api.getSetSize(node4)==3);
        assert(api.getSetSize(node5)==5);
        assert(api.getSetSize(node6)==5);

        assert(api.getSetSize(parent1)==5);
        assert(api.getSetSize(parent2)==3);

        assert(api.getSetSize(not)==-1);
    }

    @Test
    public void testRemoveSpecificSet(){
        assert(api.getPixel(0,0) == parent1);

        api.removeSpecificSet(parent1);

    }
}
