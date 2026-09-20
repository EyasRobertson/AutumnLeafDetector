package wit.ca1;

public class DisjointNode<T> {
    private DisjointNode<T> parent;
    private T data;
    private int size; //tracks how big the sets


    public DisjointNode(T data){
        this.data=data;
        this.parent=this; //sets the node to be it's own root
        this.size = 1;
    }

    //getters
    public DisjointNode<T> getParent(){
        return this.parent;
    }

    public T getData(){
        return this.data;
    }

    public int getSize(){
        return this.size;
    }


    //setters

    public void setData(T data){
        this.data=data;
    }

    public void setParent(DisjointNode<T> parent){
        this.parent=parent;
    }

    public void setSize(int size){
        this.size=size;
    }

    public void remove(){
        this.parent = null;
        this.data = null;
        this.size = 0;
    }

}
