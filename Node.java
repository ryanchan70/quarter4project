import java.io.Serializable;

public class Node<E> implements Serializable {
    private E data;
    private Node<E> prev;
    private Node<E> next;

    public Node(E a){
        data = a;
        prev = null;
        next = null;
    }

    public void setPrev(Node<E> a){
        prev = a;
    }

    public void setNext(Node<E> a){
        next = a;
    }

    public E get(){
        return data;
    }

    public Node<E> next(){
        return next;
    }

    public Node<E> prev(){
        return prev;
    }

    public void set(E a){
        data = a;
    }
}
