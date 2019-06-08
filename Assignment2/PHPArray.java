/* Chris Mananghaya (cam314)
 * CS 1501 Assignment 2: Approximating a PHP array using Java
 */ 
import java.util.*;

public class PHPArray<V> implements Iterable<V> {
    // Global Variables 
    private static final int INIT_CAPACITY = 4;
    private int N;                          // number of key-value pairs in the symbol table 
    private int M;                          // size of linear probing table 
    private boolean reHash;                 // True if put() args are for reHash
    private Node[] hashTable;               // represents array structure for hashed Pair objects 
    private Node next;                      // used during reHashing for any Node in the          
    private Node prev;                      //      middle of the list  
    public Node root;                      // used for Linked List implementation 
    public Node tail;                      // used for Linked List implementation 
    private Iterator<Pair<V>> iterator;     // used for each() method 

    // Represents the objects holding the key/value pairs in each Node. 
    // Written as a public static inner class, allowing it to be nested within PHPArray
    //      but still be publicly accessed. 
    public static class Pair<V> {
        public String key;      // start by declaring two public instance variables
        public V value;   
        
        public Pair() {
            this.key = null;
            this.value = null; 
        } // end default Pair constructor 

        public Pair(String keyString, V valPortion) {
            this.key = keyString; 
            this.value = valPortion; 
        } // end Pair constructor w/ proper data members provided
    }

    // Each Node object holds references to the next and previous Nodes, allowing
    //      for doubly Linked List implementation. 
    // Each Node also holds a Pair<V> object, which contains the matching key/value pair. 
    // Implements Comparable<V> and modified compareTo() method to work with V objects.
    public class Node<V extends Comparable<V>> implements Comparable<V> {
        public Node next;
        private Node prev; 
        public Pair<V> Data; 

        private Node() {
            this.next = null;
            this.prev = null;
            this.Data = null; 
        }

        private Node(Pair<V> newPair) {
            this.next = null;
            this.prev = null; 
            this.Data = new Pair<V>(newPair.key, newPair.value);
        }

        @Override
        public int compareTo(V object) {
            V thisValue = this.Data.value;
            V val = object; 
            if((!(thisValue instanceof Comparable) || !(val instanceof Comparable)))
                throw new IllegalArgumentException("Error: list can't be sorted.");
            return thisValue.compareTo(object);
        }
    }

    // default cosntructor
    // creates an empty hash table - use 16 as default size
    public PHPArray() {
        this(INIT_CAPACITY);
    }

    // constructor based on capacity
    // creates linear probing hash table of given capacity 
    public PHPArray(int capacity) {
        this.M = capacity;
        this.reHash = false;  
        hashTable = (Node[]) new Node[M];
    }

    // cosntructor for PHPArray, where all data members are set by user 
    public PHPArray(int sm, int sn, Node[] table, Node r, Node t) {
        this.M = sm; 
        this.N = sn; 
        this.hashTable = table; 
        this.root = r; 
        this.tail = t; 
        this.reHash = false; 
    }

    // iterator for Linked List values
    @Override
    public Iterator<V> iterator() {
        Iterator<V> itObj = new Iterator<V>() {
            Node currentNode = root; 

            @Override
            public boolean hasNext() {
                return currentNode != null; 
            }

            @Override
            public V next() {
                Node tempNode = currentNode;
                currentNode = currentNode.next;
                return (V)tempNode.Data.value; // must cast to type V here from type Node 
            }
        }; 
        return itObj; 
    }

    // iterator for Pair<V> objects 
    public Iterator<Pair<V>> pairIterator() {
        Iterator<Pair<V>> itObj = new Iterator<Pair<V>>() {
            Node currentNode = root; 

            @Override
            public boolean hasNext() {
                return currentNode != null; 
            }

            @Override
            public Pair<V> next() {
                Node tempNode = currentNode; 
                currentNode = currentNode.next; 
                return tempNode.Data;
            }
        };
        return itObj; 
    }

    // uses global iterator to iterate over Pair<V> objects and access data members
    public Pair<V> each() {
        if(iterator == null)
            reset();
        if(iterator.hasNext()) 
            return (Pair<V>) iterator.next();
        return null; 
    }

    public void reset() {
        iterator = this.pairIterator(); 
    }

    // returns the number of key-value pairs in the symbol table
    public int size() {
         return this.N; 
    }
    
    // returns the current capacity of the hashTable 
    public int length() {
        return this.N;
    }

     // checks if the symbol table is empty
     public boolean isEmpty() {
         return size() == 0; 
     }

     // checks if a key-value pair with the given key exists in the table
     public boolean contains(String key) {
         return get(key) != null;
     }

     // hash function for keys; returns value between 0 and M-1
     private int hash(String key) {
         return (key.hashCode() & 0x7fffffff) % M; 
     }

     // prints contents of hashTable from 0 to M-1 
     public void showTable() {
        String k;
        V v; 
        System.out.println("\tRaw Hash Table Contents:");
        for (int i = 0; i < hashTable.length; i++) {
            if (hashTable[i] == null) {
                System.out.println(i + ": null");
            } else {
                k = hashTable[i].Data.key;
                v = (V)hashTable[i].Data.value;
                System.out.println(i + ": Key: " + k + " Value:" + v);
            }
        }
     }

    // Resize the hash table to the given capacity by re-hashing all keys. 
    // Creates temporary copy of PHPArray object, doubles table, and copies contents back to 
    //     original object. 
    private void resize(int capacity) {
        System.out.println("\t\tSize: " + N + " -- resizing array from " + M + " to " + capacity);
        PHPArray<V> temp = new PHPArray<V>(capacity);
        Node rePair = root; 
        while(rePair != null) {
            temp.put(rePair.Data.key, (V)rePair.Data.value);
            rePair = rePair.next;  
        }

        this.M = capacity; 
        this.N = temp.N;
        this.root = temp.root;
        this.tail = temp.tail;
        this.hashTable = Arrays.copyOf(temp.hashTable, capacity); 
    }

    // Insert the key-value pair into the symbol table
    // Creates a new Node<V> object that creates a corresponding Pair<V> object using the 
    //      parameters passed. 
    // Hashes object for hashTable, as well as adds it to the LinkedList. 
    public void put(String key, V val) {
        if(val == null)
            unset(key);
        if(N >= M/2) // double table size if 50% full 
            resize(2 * M); 
        int i; 
        for(i = hash(key); hashTable[i] != null; i = (i+1) % M) {
            if(hashTable[i].Data.key.equals(key)) {
                hashTable[i].Data.value = val; 
                return; 
            }
        }
        Pair<V> newPair = new Pair<V>(key, val); 
        hashTable[i] = new Node(newPair);
        
        if (reHash) {
            hashTable[i].next = this.next;
            if (hashTable[i].next != null) {
                hashTable[i].next.prev = hashTable[i];
            }
            hashTable[i].prev = this.prev;
            if (hashTable[i].prev != null) {
                hashTable[i].prev.next = hashTable[i];
            }
            reHash = false;
            return;
        }
        if (root == null) {
            root = hashTable[i];
            tail = root;
            N++; 
            return;
        }
        hashTable[i].prev = tail;
        hashTable[i].next = null;
        tail.next = hashTable[i];
        tail = hashTable[i];
        N++;
    }

    // converts int parmeter to String before passing on to original put() method 
    public void put(int key, V val) {
        put(String.valueOf(key), val);
    }

    // returns value based on key parameter by retrieving hash value and comparing with 
    //      table indices 
    public V get(String key) {
        for (int i = hash(key); hashTable[i] != null; i = (i + 1) % M) {
            if (hashTable[i].Data.key.equals(key)) {
                return (V)hashTable[i].Data.value;
            }
        }
        return null;
    }

    // return the value associated w/ the given key (covers key of type int case) 
    public V get(int key) {
        Integer strKey = new Integer(key);
        return (get(strKey.toString()));
    }

    // Unsets the key (and associated value) from the symbol table.
    // Removes Pair and Node from hashTable and ArrayLlist if the specified key parameter is found 
    //      within one of the Nodes.
    // If removal creates a gap, it reHashes the remaining Nodes to get rid of gap. 
    public void unset(String key) {
         if(!contains(key))
            return; 

        // find position i of key 
        int i = hash(key);
        while(!key.equals(hashTable[i].Data.key)) {
            i = (i + 1) % M;
        }

        // unset key and associated value
        if(root == tail) {
            root = null; 
        } else if(root == hashTable[i]) {
            root = root.next; 
            root.prev = null; 
        } else if(tail == hashTable[i]) {
            System.out.println(tail.Data.key);
            tail = hashTable[i].prev; 
        } else {
            hashTable[i].next.prev = hashTable[i].prev;
            hashTable[i].prev.next = hashTable[i].next;
        }
        hashTable[i] = null;

        // rehash all keys in same cluster
        i = (i + 1) % M; 
        while(hashTable[i] != null) {
            // unset keys and values from hashTable[i] and reinsert 
            System.out.println("\tKey " + hashTable[i].Data.key + " rehashed...\n");
            String keyToRehash = hashTable[i].Data.key;
            V valToRehash = (V)hashTable[i].Data.value;
            this.next = hashTable[i].next;
            this.prev = hashTable[i].prev;
            hashTable[i] = null;
            N--;
            reHash = true; 
            put(keyToRehash, valToRehash);
            i = (i + 1) % M; 
        }
        N--; 

        // half size of array if it's 12.5% full or less
        if(N > 0 && N <= M/8) {
          resize(M/2);
        }
        
        assert check();
    }

    // same action as unset(String), but handles ints as inputs to convert them
    // into strings before deleting 
    public void unset(int key) {
         Integer strKey = new Integer(key);
         unset(strKey.toString());
    }

    // confirms load factor of hashTable 
    private boolean check() {
        // check that hash table is at most 50% full
        if (M < 2 * N) {
            System.err.println("Hash table size M = " + M + "; array size N = " + N);
            return false;
        }

        // check that each key in table can be found by get()
        for (int i = 0; i < M; i++) {
            if (hashTable[i].Data.key == null) {
                continue;
            } else if (get(hashTable[i].Data.key) != hashTable[i].Data.value) {
                System.err.println("get(" + hashTable[i].Data.key + ") = " + get(hashTable[i].Data.key) + "; hashTable[" + i + "].value = " + hashTable[i].Data.value);
                return false;
            }
        }
        return true;
    }

    // Goes through all Nodes to switch attribution of all keys & values. 
    public PHPArray<String> array_flip() {
        PHPArray<String> flipArray = new PHPArray<>(M);
        Node flipNode = root;

        while (flipNode != null) {

            flipArray.put((String) flipNode.Data.value,
                    flipNode.Data.key);
            flipNode = flipNode.next;
        }

        return flipArray;
    }

    // Returns keys from all Nodes in insertion order via ArrayList 
    public ArrayList<String> keys() {
        Node list = root;
        ArrayList<String> keyList = new ArrayList<>();

        while (list != null) {
            keyList.add(list.Data.key);
            list = list.next;
        }

        return keyList;
    }

    // Returns values from all Nodes in insertion order via ArrayList 
    public ArrayList<V> values() {
        Node list = root;
        ArrayList<V> valList = new ArrayList<>();

        while (list != null) {
            valList.add((V)list.Data.value);
            list = list.next;
        }

        return valList;
    }

    // sorts insertion order linked nodes from smallest to largest 
    // utilizes merge_sort() which is worse case O(n lg n)
    // reHashes in insertion order with new keys from 0 to list.length - 1. 
    public void sort() {
        if (root != null && !(root.Data.value instanceof Comparable)) {
            throw new ClassCastException("Can't be sorted");
        }
        
        root = merge_sort(root);
        
        Node reKey = root;
        PHPArray<V> sortAr = new PHPArray<V>(M);

        for (int i = 0; reKey != null; i++) {
            sortAr.put(String.valueOf(i), (V)reKey.Data.value);
            reKey = reKey.next;
        }

        this.M = sortAr.M;
        this.N = sortAr.N;
        this.root = sortAr.root;
        this.tail = sortAr.tail;
        this.hashTable = Arrays.copyOf(sortAr.hashTable, sortAr.M);
    }

    // Sorts table in insertion order with merge_sort() by iterating over nodes. 
    // reHashes with original keys. 
    public void asort() {
        try {
            root = merge_sort(root);
        } catch (IllegalArgumentException AR) {
            System.out.println("PHPArray values are not Comparable -- cannot be sorted");
        }
        root = merge_sort(root);
        Node reKey = root;
        PHPArray<V> sortAr = new PHPArray<>(M);

        for (int i = 0; reKey != null; i++) {
            sortAr.put(reKey.Data.key, (V)reKey.Data.value);
            reKey = reKey.next;
        }

        this.M = sortAr.M;
        this.N = sortAr.N;
        this.root = sortAr.root;
        this.tail = sortAr.tail;
        this.hashTable = Arrays.copyOf(sortAr.hashTable, sortAr.M);

    }

    // Sorts table vis Linked Nodes in O(n lg n) time 
    private Node merge_sort(Node n) {
        Node nd = n;
        if (nd == null || nd.next == null) {
            return nd;
        }
        Node mid = getMid(nd);
        Node right = mid.next;
        mid.next = null;

        return merge(merge_sort(nd), merge_sort(right));
    }

    // merges and sorts on the way up from bottom of the recursion tree 
    private Node merge(Node l, Node r) {
        Node temp, curr;
        temp = new Node<>();
        curr = temp;

        while (l != null && r != null) {
            if (l.compareTo((Comparable)r.Data.value) < 1) {
                curr.next = l;
                l = l.next;
            } else {
                curr.next = r;
                r = r.next;
            }
            curr = curr.next;
        }
        if (l == null) {
            curr.next = r;
        } else {
            curr.next = l;
        }

        return temp.next;
    }

    // Finds middle node of LinkedList for splitting list 
    private Node getMid(Node n) {
        if (n == null) {
            return n;
        }
        Node big, small;
        small = n;
        big = small;
        while (small.next != null && small.next.next != null) {
            big = big.next;
            small = small.next.next;
        }
        return big;
    }
 }