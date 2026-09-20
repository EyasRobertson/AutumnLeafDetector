package wit.ca1;

public class UnionFind<T> {

    //find the parent
    public DisjointNode<T> find(DisjointNode<T> node) {
        //if the node doesnt exist there can't be a parent
        if (node == null){
            return null;
        }
        //if the node is its own parent then return the node
        return node.getParent() ==node ? node : find(node.getParent());
    }

    public void union(DisjointNode<T> node1, DisjointNode<T> node2) {
        //get both of the nodes parents
        DisjointNode<T> parent1 = find(node1);
        DisjointNode<T> parent2 = find(node2);

        if(parent1==parent2){//checks if they're already in the same set
            return;
        }
        parent1.setParent(parent2);
    }
}