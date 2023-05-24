import java.io.Serializable;

public class DLList<E> implements Serializable {
    private Node<E> head;
    private Node<E> tail;
    private int size;
    private int randomIndex;
    public DLList(){
        head = null;
        tail = null;
        size = 0;
    }

    public boolean add(E a){
        Node<E> newNode = new Node<E>(a);
        if (size == 0){
            head = newNode;
            tail = newNode;
            size++;
        }
        else if (size == 1){
            tail = newNode;
            head.setNext(tail);
            tail.setPrev(head);
            size++;
        }
        else{
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
            size++;
        }
        return true;
    }

    public E get(int a){
        int index = 0;
        Node<E> tempNode = head;
        while (index < a){
            tempNode = tempNode.next();
            index++;
        }
        return tempNode.get();
    }

    public void set(int a, E b){
        int index = 0;
        Node<E> tempNode = head;
        while (index < a){
            tempNode = tempNode.next();
            index++;
        }
        tempNode.set(b);
    }

    public int getSize(){
        return size;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public void remove(int a){
        Node<E> tempNode = head;
        int index = 0;
        while (index < a){
            tempNode = tempNode.next();
            index++;
        }
        if (a != 0 && a != size-1) {
            tempNode.prev().setNext(tempNode.next());
            tempNode.next().setPrev(tempNode.prev());
        }
        else if (size == 1){
            head = null;
            tail = null;
        }
        else if (a == 0){
            tempNode.next().setPrev(null);
            head = tempNode.next();
        }
        else if (a != size-1){
            tempNode.prev().setNext(null);
            tail = tempNode.prev();
        }
        size--;
    }

    public void remove(E a){
        Node<E> tempNode = head;
        int index = 0;
        while (index < size){
            if (tempNode.get() == a){
                remove(index);
                break;
            }
            tempNode = tempNode.next();
        }
    }
    public String toString(){
        String returned = "";
        int index = 0;
        Node<E> tempNode = head;
        while (index < size){
            returned += tempNode.get().toString() + "\n";
            tempNode = tempNode.next();
            index++;
        }
        return returned;
    }


}
