package McCreight;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
/**
 *
 * @author aldr699
 */


public class STNode2 implements Comparable<STNode2>{
    STNode2 parent = null;
    LinkedList<STNode2> children;
    STNode2 suffixLink = null;
    String subSeq = null; //reference to master for sorting
    int stringDepth = 0;
    int start;
    int length;
    private static int nodeIDGenerator = -1;
    int nodeID;
    int startLeafIndex;


    int endLeafIndex;

    
    /*
     * Node constructor 
     * Generates a node ID and prepares arraylist
     */
    public STNode2()
    {
        nodeID = nodeIDGenerator++;
        children = new LinkedList<STNode2>();
        startLeafIndex = -1;
        endLeafIndex = -1;
    }
    
    /*
     * Compare string method, for children sorting
     */
    @Override
    public int compareTo(STNode2 other)
    {
        return this.subSeq.charAt(this.start) - other.subSeq.charAt(other.start);
    }
 //   public STNode 
    @Override
    public String toString()
    {
        return subSeq;
    }
    
    public int getStartLeafIndex() {
        return startLeafIndex;
    }

    public int getEndLeafIndex() {
        return endLeafIndex;
    }

    public int getStart() {
        return start;
    }
    
}

