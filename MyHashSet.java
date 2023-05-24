public class MyHashSet<E> {
    Object[] arr = new Object[100000];

    public boolean add(E a){
        if (arr[a.hashCode()] == null){
            arr[a.hashCode()] = a;
            return true;
        }
        return false;
    }

    public void clear(){
        for (int i = 0; i < arr.length; i++){
            arr[i] = null;
        }
    }

    public boolean contains(Object a){
        if (arr[a.hashCode()] != null){
            return true;
        }
        return false;
    }

    public boolean remove(Object a){
        if (arr[a.hashCode()] != null){
            arr[a.hashCode()] = null;
            return true;
        }
        return false;
    }

    public String toString(){
        String a = "";
        for (int i = 0; i < arr.length; i++){
            if (arr[i] != null) {
                a += arr[i].toString() + ", ";
            }
        }
        return a;
    }

    public int size(){
        int size = 0;
        for (int i = 0; i < arr.length; i++){
            if (arr[i] != null){
                size++;
            }
        }
        return size;
    }

    public Object[] toArray(){
        Object[] newArr = new Object[size()];
        int currentIndex = 0;
        for (int i = 0; i < arr.length; i++){
            if (arr[i] != null){
                newArr[currentIndex] = arr[i];
                currentIndex++;
            }
        }
        return newArr;
    }
}
