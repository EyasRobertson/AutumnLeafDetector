package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import wit.ca1.DisjointNode;
import wit.ca1.LeafAPI;
import wit.ca1.Pixel;
import org.openjdk.jmh.Main;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@State(Scope.Thread)
public class BenchmarkAPI {

    private LeafAPI api;

    private DisjointNode<Pixel> parent1;
    private DisjointNode<Pixel> parent2;
    private DisjointNode<Pixel> parent3;
    private DisjointNode<Pixel> parent4;
    private DisjointNode<Pixel> parent5;
    private DisjointNode<Pixel> parent6;
    private DisjointNode<Pixel> parent7;
    private DisjointNode<Pixel> parent8;
    private DisjointNode<Pixel> parent9;
    private DisjointNode<Pixel> parent10;
    private DisjointNode<Pixel> parent11;
    private DisjointNode<Pixel> parent12;
    private DisjointNode<Pixel> parent13;
    private DisjointNode<Pixel> parent14;
    private DisjointNode<Pixel> parent15;

    private DisjointNode<Pixel> node1;
    private DisjointNode<Pixel> node2;
    private DisjointNode<Pixel> node3;
    private DisjointNode<Pixel> node4;
    private DisjointNode<Pixel> node5;
    private DisjointNode<Pixel> node6;

    @Setup(Level.Iteration)
    public void prepare() {
        api = new LeafAPI();

        Pixel root1 = new Pixel(0, 0, 0);
        Pixel root2 = new Pixel(1, 0, 255);
        Pixel root3 = new Pixel(0, 1, 255);
        Pixel root4 = new Pixel(1, 1, 255);
        Pixel root5 = new Pixel(0, 2, 255);
        Pixel root6 = new Pixel(5, 10, 255);
        Pixel root7 = new Pixel(6, 10, 255);
        Pixel root8 = new Pixel(6, 11, 255);
        Pixel root9 = new Pixel(7, 10, 255);
        Pixel root10 = new Pixel(7, 11, 255);
        Pixel root11 = new Pixel(8, 10, 255);
        Pixel root12 = new Pixel(8, 11, 255);
        Pixel root13 = new Pixel(9, 10, 255);
        Pixel root14 = new Pixel(9, 11, 255);
        Pixel root15 = new Pixel(10, 10, 255);

        Pixel pixel1 = new Pixel(1, 2, 255);
        Pixel pixel2 = new Pixel(2, 3, 255);
        Pixel pixel3 = new Pixel(4, 10, 255);
        Pixel pixel4 = new Pixel(5, 11, 255);
        Pixel pixel5 = new Pixel(4, 10, 255);
        Pixel pixel6 = new Pixel(6, 9, 255);

        parent1 = new DisjointNode<Pixel>(root1);
        parent2 = new DisjointNode<Pixel>(root2);
        parent3 = new DisjointNode<Pixel>(root3);
        parent4 = new DisjointNode<Pixel>(root4);
        parent5 = new DisjointNode<Pixel>(root5);
        parent6 = new DisjointNode<Pixel>(root6);
        parent7 = new DisjointNode<Pixel>(root7);
        parent8 = new DisjointNode<Pixel>(root8);
        parent9 = new DisjointNode<Pixel>(root9);
        parent10 = new DisjointNode<Pixel>(root10);
        parent11 = new DisjointNode<Pixel>(root11);
        parent12 = new DisjointNode<Pixel>(root12);
        parent13 = new DisjointNode<Pixel>(root13);
        parent14 = new DisjointNode<Pixel>(root14);
        parent15 = new DisjointNode<Pixel>(root15);

        node1 = new DisjointNode<Pixel>(pixel1);
        node2 = new DisjointNode<Pixel>(pixel2);
        node3 = new DisjointNode<Pixel>(pixel3);
        node4 = new DisjointNode<Pixel>(pixel4);
        node5 = new DisjointNode<Pixel>(pixel5);
        node6 = new DisjointNode<Pixel>(pixel6);

        api.reset();//to initialize the hashmaps

        api.clusterPixel(node1);
        api.clusterPixel(node2);
        api.clusterPixel(node3);
        api.clusterPixel(node4);
        api.clusterPixel(node5);
        api.clusterPixel(node6);

        api.addRoots(parent1);
        api.addRoots(parent2);
        api.addRoots(parent3);
        api.addRoots(parent4);
        api.addRoots(parent5);
        api.addRoots(parent6);
        api.addRoots(parent7);
        api.addRoots(parent8);
        api.addRoots(parent9);
        api.addRoots(parent10);
        api.addRoots(parent11);
        api.addRoots(parent12);
        api.addRoots(parent13);
        api.addRoots(parent14);
        api.addRoots(parent15);

    }

    @Benchmark
    public void benchmarkSortRootsBySize(){
        api.sortRootsBySize();
    }

    @Benchmark
    public void benchmarkMinX(){
        api.findXMin(node1);
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Main.main(args);
    }


}
