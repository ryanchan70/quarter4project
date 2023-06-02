public class MyHashMap<K,V>{
    private Object[] hashArray;
    private int size;
    private MyHashSet<K> keySet;

    public MyHashMap(){
        hashArray = new Object[100000];
        size = 0;
        keySet = new MyHashSet<>();
    }

    public V put(K a, V b){
        V c = (V)(hashArray[a.hashCode()]);
        hashArray[a.hashCode()] = b;
        keySet.remove(a);
        keySet.add(a);
        if (c == null){
            size++;
        }
        return c;
    }

    public V get(Object a){
        V b = (V)(hashArray[a.hashCode()]);
        return b;
    }

    public V remove(Object a){
        V b = (V)(hashArray[a.hashCode()]);
        hashArray[a.hashCode()] = null;
        keySet.remove((K)(a));
        return b;
    }

    public int size(){
        return size;
    }

    public MyHashSet<K> keySet(){
        return keySet;
    }

    public String toString(){
        String str = "";
        for (int i = 0 ; i < keySet.size(); i++){
            str += keySet.toArray()[i] + " " + get(keySet.toArray()[i]) + "\n";
        }
        return str;
    }
}
