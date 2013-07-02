/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package McCreight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aldr699
 */
public class STTree3 {
    STNode2 root;
    String masterSequence;
    int nodeNums = 0;
    int recursionCounter = 0;

    /*
     * Constructor
     */
    public STTree3()
    {
        root = new STNode2();
        root.suffixLink = root;
        root.subSeq = "";
  
    }
    
    /*
     * Initiater of tree generation
     */
    public void GenerateTree(String inputSequence)
    {
        this.masterSequence = inputSequence.concat("$");
        STNode2 activeNode = InsertLeafNode(0, root);
        for(int i = 1; i < masterSequence.length(); i++)
        {
            activeNode = Insert(i, activeNode); //active node the current node position
        }
    }

    public String getMasterSequence() {
        return masterSequence;
    }
    
    
    
    /*
     * All cases for insertion
     */
    public STNode2 Insert(int i, STNode2 activeNode)
    {
        STNode2 u_node = activeNode.parent;
        if(u_node.suffixLink != null && u_node != root)
        {
            activeNode = caseIA(i, u_node);
        }
        else if(u_node.suffixLink != null && u_node == root)
        {
            activeNode = caseIB(i, u_node);
        }
        else if(u_node.suffixLink == null && u_node.parent == root)
        {
            activeNode = caseIIB(i, u_node);
        }
        else if(u_node.suffixLink == null && u_node.parent != root)
        {
            activeNode = caseIIA(i, u_node);
        }

        return activeNode;
    }
/*
 * has suffix link no the root
 */
    private STNode2 caseIA(int i, STNode2 activeNode) {
        int k = activeNode.stringDepth;
        activeNode = activeNode.suffixLink;
        activeNode = FindPath(activeNode, i+k-1);
        return activeNode;
    }
    /*
     * has suffix link is the root
     */
    private STNode2 caseIB(int i, STNode2 activeNode) {
        activeNode = FindPath(activeNode, i);
        return activeNode;
    }
    /*
     * No suffix link not the root
     */
    private STNode2 caseIIA(int i, STNode2 activeNode) {
        int k = activeNode.stringDepth;
        STNode2 NewSLNode = activeNode;
        String edgeLabel = masterSequence.substring(activeNode.start, 
                activeNode.start+activeNode.length);//get the common edge to new sufflink
        activeNode = activeNode.parent.suffixLink; //go uprime
        activeNode = NodeHop(edgeLabel, activeNode, NewSLNode);//hop to correct node and link it
        activeNode = FindPath(activeNode, i+k-1);//find spot and insert new leaf
        return activeNode;
    }
    /*
     * No suffix link is root
     */
    private STNode2 caseIIB(int i, STNode2 activeNode) {
        int k = activeNode.stringDepth;
        STNode2 NewSLNode = activeNode;
        String edgeLabel = masterSequence.substring(activeNode.start+1, 
                activeNode.start+activeNode.length);//beta of xbeta
        activeNode = activeNode.parent.suffixLink;
        activeNode = NodeHop(edgeLabel, activeNode, NewSLNode);//insert link
        activeNode = FindPath(activeNode, i+k-1);//add new leaf
        return activeNode;
    }
    
    /*
     * Node hopper adds a suffix link at v and returns a refernce to v
     */
    private STNode2 NodeHop(String edge, STNode2 activeNode, STNode2 sufflinkToAdd)
    {
        STNode2 currNode = activeNode;
        int index = 0;
        if(edge.length() > 0)
        {
        while(index <= edge.length())
        {
            Iterator<STNode2> iter = currNode.children.iterator();
            while(iter.hasNext())// loop through children look for match to first char
            {
                STNode2 temp = iter.next();
                if(masterSequence.charAt(temp.start)==edge.charAt(index))
                {
                    if(temp.length + index > edge.length())
                    {
                        //found a match and is within an edge need to break and add link
                        STNode2 newInternal = InsertInternalNode(
                                 edge.length()- index, 
                                temp.parent, temp);
                        
                        sufflinkToAdd.suffixLink = newInternal;
                        return newInternal;
                        
                    }
                    else if(temp.length + index == edge.length())
                    { //node is already there
                        sufflinkToAdd.suffixLink = temp;
                        return temp;
                    }
                    else
                    {//need to try this childs children 
                        index = index + temp.length;
                        iter = temp.children.iterator();
                    }
                    
                }
                
            }
        }
        }
        sufflinkToAdd.suffixLink = activeNode;
        return activeNode;
    }
    
  /*
   * Finds the path following the node hopping for remaining characters
   */
    public STNode2 FindPath(STNode2 activeNode, int index)
    {
        Iterator<STNode2> iter = activeNode.children.iterator();
        int numMatches = 0;
        while(iter != null && iter.hasNext())
        {//iterate through the children of active node looking for char match
            STNode2 temp = iter.next();
            int tempIndex = temp.start;
            while(masterSequence.charAt(index) == masterSequence.charAt(tempIndex))
            {
                numMatches++;
                tempIndex++;
                index++;
                if(numMatches == temp.length)//Have exhausted this node, now its children
                { 
                    activeNode = temp;
                    iter = activeNode.children.iterator();
                    numMatches = 0;
                    break;
                }

            }
            if(numMatches > 0)//this means we did not match entirely but have before split edge
            {

                STNode2 newInternal = InsertInternalNode(numMatches, activeNode, temp);
                activeNode = InsertLeafNode(index,newInternal);
                return activeNode;
            }
        }
        if(activeNode.children == null || numMatches == 0)
        {   //No children or no matching children
            activeNode = InsertLeafNode(index, activeNode);
            return activeNode;
        }
        
        return null;
            
    }       
    
    /*
     * Finds the region of highest sequence similarity for later alignment
     * I found that I was running into long leaf edges and short stringdepths with
     * some internal nodes which prevented it from being larger than lambda.
     * I switched to keeping a reference to the internal if it is long enough or the 
     * the leaf edge increases the matches above lambda
     */
    public STNode2 FindLoc(String read, int lambda)
    {
        STNode2 DeepestNode = null;
        STNode2 activeNode = root; //start searching at the root
        ReadPointer rdptr = new ReadPointer(0);//using an object to store the int so it can be passed by reference, stupid java
        int longestRead = 0;
        for(int i = 0; i+activeNode.stringDepth < read.length(); i++)
        {
            rdptr.readptr = i + activeNode.stringDepth;//used for storing the length of the match
            activeNode = FindPathNoInsert(activeNode, read, rdptr);//Find the path and return the last visited node.
            if(rdptr.readptr - i > lambda && (DeepestNode == null || //Check if > 25 matches based on readptr value - i
                    rdptr.readptr > longestRead))//
            {
                DeepestNode = activeNode;
                longestRead = rdptr.readptr;
            }

            activeNode = activeNode.suffixLink;
        }
        return DeepestNode;
    }
    
    //Class to store an int to pass it by reference.
    class ReadPointer
    {
        public ReadPointer(int n)
        {
            readptr = n;
        }
       
        int readptr;
    }
    
    
    /*
     * Finds the path without inserting the node.
     */
    public STNode2 FindPathNoInsert(STNode2 activeNode, String read, ReadPointer read_ptr)
    {
        Iterator<STNode2> iter = activeNode.children.iterator();
        int numMatches = 0;
        while(iter != null && iter.hasNext())
        {//iterate through the children of active node looking for char match
            STNode2 temp = iter.next();
            int tempIndex = temp.start;
            while(read_ptr.readptr != read.length() && read.charAt(read_ptr.readptr) == masterSequence.charAt(tempIndex))
            {
                numMatches++;
                tempIndex++;
                read_ptr.readptr++;
                if(numMatches == temp.length)//Have exhausted this node, now its children
                { 
                    activeNode = temp;
                    iter = activeNode.children.iterator();
                    numMatches = 0;
                    break;
                }

            }
            if(numMatches > 0 || read_ptr.readptr == read.length())//this means we did not match entirely but have before split edge
            {
                
                return activeNode;
            }
        }
        if(activeNode.children == null || numMatches == 0)
        {   //No children or no matching children
            return activeNode;
        }
        
        return null;
    }
    
    /*
     * Provides unique ID
     */
    private int getNodeNum()
    {
        nodeNums++;
        return nodeNums;
    }
    
    /*
     * method for inserting a new leaf
     */
    public STNode2 InsertLeafNode(int index, STNode2 mParent)
    {
        STNode2 nodeToInsert = new STNode2();
        nodeToInsert.subSeq = masterSequence; //keep reference to master for collection sort
        nodeToInsert.start = index;
        nodeToInsert.length = masterSequence.length() - index;
        nodeToInsert.parent = mParent;
        nodeToInsert.stringDepth = nodeToInsert.length + mParent.stringDepth;
        mParent.children.add(nodeToInsert);
        Collections.sort(mParent.children);         
        return nodeToInsert;
    }
    
    /*
     * Breaks up an edge and insert an internal node at the break
     */
    public STNode2 InsertInternalNode(int breakIndex, STNode2 mParent, STNode2 mChild)
    {
        STNode2 nodeToInsert = new STNode2();
        nodeToInsert.start = mChild.start;//break child up
        nodeToInsert.subSeq = masterSequence;
        nodeToInsert.length = breakIndex;
        mChild.start = mChild.start + breakIndex;
        mChild.length = mChild.length - breakIndex;
        nodeToInsert.parent = mParent;  
        mChild.parent = nodeToInsert;
        nodeToInsert.stringDepth = breakIndex + mParent.stringDepth;
        mParent.children.add(nodeToInsert);
        mParent.children.remove(mChild);
        Collections.sort(mParent.children);//add new node to parent collection remove old child
        nodeToInsert.children.add(mChild);
        return nodeToInsert;
    }

    
    /*
     * Trying to be clever with interfaces.  Not to useful except for viewing a few things in the tree
     */
    public void DFS(NodeActions action)
    {
        DFS(root, action);
    }
    
    private void DFS(STNode2 node, NodeActions action)
    {
        action.execute(node);
        for(int i = 0; i < node.children.size(); i++)
        {
            DFS(node.children.get(i),action);
        }
    }
    
    public void InorderTraversal(NodeActions action)
    {
        InorderTraversal(root, action);
    }
    
    private void InorderTraversal(STNode2 node, NodeActions action)
    {
        action.execute(node);
        for(int i = 0; i < node.children.size(); i++)
        {
            InorderTraversal(node.children.get(i),action);
        }
    }
  
    //Applies the above traversals using a given action as necessary
    public int GetNodeCount(int j)
    {
        int count = 0;
        try {
            NodeActions getcount = new NodeCount(j);
            InorderTraversal(getcount);
            count = ((NodeCount)getcount).GetCount();
        } catch (Exception ex) {
            Logger.getLogger(STTree3.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return count;
    }
    
    public void PrintTreeNodes()
    {
        NodeActions printTree = new PrintTree();
        InorderTraversal(printTree);
    }
    
    
    public int[] DFS_PrepareST(int lambda)
    {
        PrepareLeafList leaflist = new PrepareLeafList(masterSequence.length(), lambda);
        leaflist.ST_DFS(root);
        return leaflist.A;
    }
    
    
    
    //<editor-fold defaultstate="collapsed" desc="NodeActions">
    interface NodeActions
    {
        void execute(STNode2 node);

    }
    
    /*
     * Prepares the leaflist and sets the start and end leaf indexes.
    */
    class PrepareLeafList
    {
        public int[] A;
        int nextIndex;
        int lambda;
        //Initializes the array and sets lambda
        public PrepareLeafList(int n, int lambda)
        {
            A = new int[n];
            for(int i = 0; i < n; i++)
            {
                A[i] = -1;
            }
            nextIndex = 0;
            this.lambda = lambda;
        }
        
        public void ST_DFS(STNode2 node)
        {

            int children = node.children.size();
            if(children == 0)//Set the current array index to the start index of this leaf node in the master sequence
            {
                A[nextIndex] = masterSequence.length() - node.stringDepth;
                if(node.stringDepth > lambda)
                {
                    node.startLeafIndex = nextIndex;//this doesnt always seem to be working...
                    node.endLeafIndex = nextIndex;
                }
                nextIndex++;
                return;
            }
            else
            {
                for(int i = 0; i < children; i++)
                {
                    ST_DFS(node.children.get(i));
                }
                if(node.stringDepth > lambda)
                {
                    node.startLeafIndex = node.children.get(0).startLeafIndex;
                    node.endLeafIndex = node.children.get(children-1).endLeafIndex;
                }
            }

        }
    }
    
    //Prints the tree depth for each node
    class PrintTreeDepth implements NodeActions
    {
        public void execute(STNode2 toPrint)
        {
            if(toPrint.equals(root))
            {
                System.out.print(toPrint.stringDepth);
            }
            else
            {
                System.out.print(toPrint.stringDepth + ", ");
            }
        }
        
    }
    
    /*
     * Prints out the edges in the tree
     */
    class PrintTree implements NodeActions
    {
        public void execute(STNode2 toPrint)
        {
            for(int i = 0; i < toPrint.stringDepth-toPrint.length; i++)
                System.out.print(" ");
                        
            System.out.println(masterSequence.substring(toPrint.start,
                    toPrint.start+ toPrint.length));
        }
        
        public void execute2(STNode2 node){}
    }
    
    /*
     * Provides a count of visited nodes given a search method
     */
    
    class NodeCount implements NodeActions
    {
        int type;
        int count;
        public NodeCount(int type) throws Exception
        {
            count = 0;
            if(type == 1 || type == 0 || type == -1)
            {
                this.type = type;
            }
            else
            {
                throw new Exception();
            }
        }
        public void execute(STNode2 node)
        {
            if(type == 1 && node.children.isEmpty())
            {
                count++;
            }
            else if(type == -1 && !node.children.isEmpty())
            {
                count++;
            }
            else if(type == 0)
            {
                count++;
            }
        }
        
        
        public int GetCount()
        {
            return count;
        }
    }
    
    //</editor-fold>
    
    /*
     * Prints a particular nodes childrens sequences
     */
    public void PrintNodesChildren(STNode2 node)
    {
        for(int i = 0; i < node.children.size(); i++)
        {
            STNode2 temp = node.children.get(i);
            System.out.print(masterSequence.substring(temp.start, temp.start+
                    temp.length));
            if(i< node.children.size()-1)
            {
                System.out.print(", ");
            }
        }
        System.out.print("\n");
    }
    

    /*
     * Helper interanl class for getting the bwt index
     */
    class BWTIndexer
    {
        
        public ArrayList<Integer> bwtIndex;
        public BWTIndexer()
        {
            bwtIndex = new ArrayList<Integer>();
        }
        
        public void BWT()
        {
            BWT(root);
        }
        //Recursively generates the bwt index and then stores them in an array
        private void BWT(STNode2 internalNode)
        {
            if(internalNode.children.size() == 0)
            {
                bwtIndex.add(masterSequence.length() - internalNode.stringDepth);
            }
            else
            {
                for(int i = 0; i < internalNode.children.size(); i++)
                { 
                    BWT(internalNode.children.get(i));
                }
            
            }
        }
        
    }
    
    /*
     * Exposes the bwt function to the application and provides the index as an int array
     */
    public int[] GetBWTIndex()
    {
        BWTIndexer bwt = new BWTIndexer();
        bwt.BWT();
        int[] bwtindex = new int[bwt.bwtIndex.size()];
        Iterator<Integer> iterator = bwt.bwtIndex.iterator();
        //junk to convert to array
        for(int i = 0; i < bwtindex.length; i++)
        {
            bwtindex[i] = iterator.next().intValue();
        }
        return bwtindex;
    }
    
    /*
     * Leaf node counter
     */
    public int CountLeafNode()
    {
        return CountLeafNode(root);
    }
    
    private int CountLeafNode(STNode2 internalNode)
    {
        if(internalNode.children.size() == 0)
        {
            return 1;
        }
        else
        {
            int localCount = 0;
            for(int i = 0; i < internalNode.children.size(); i++)
            { 
                localCount += CountLeafNode(internalNode.children.get(i));
            }
            return localCount;
        }
    }
    
    /*
     * BWT sequence printer
     */
    public void PrintBWTIndex(int[] bwt, PrintWriter out)
    {
            for(int i = 0; i < bwt.length; i++)
        {
            if(bwt[i] == 0)
            {
                out.println("$");
            }
            else
            {
                out.println("" + masterSequence.charAt(bwt[i]-1));
            }
        }
    }
    
    /*
     * Finds the deepest node which has more than one child
     */
    public STNode2 DeepestInternalNode()
    {
        return DeepestInternalNode(root, root);
    }
    
    private STNode2 DeepestInternalNode(STNode2 internalNode, STNode2 currMax)
    {
        if(internalNode.children.size() == 0)
        {
            return null;
        }
        if(internalNode.stringDepth > currMax.stringDepth)
        {
            currMax = internalNode;
        }
        for(int i = 0; i < internalNode.children.size(); i++)
        {
            STNode2 TempNode = DeepestInternalNode(internalNode.children.get(i), currMax);
            if(TempNode != null && TempNode.stringDepth > currMax.stringDepth)
            {
                currMax = TempNode;
            }
        }
        return currMax;
    }
    
    /*
     * Helper method for printing the results of the deepest internal node
     */
    public void PrintDeepestMatch()
    {
        STNode2 deepest = DeepestInternalNode();
        System.out.print("Starts: ");
        for(int i = 0; i < deepest.children.size(); i++)
        {
            System.out.print(""+ (deepest.children.get(i).start - deepest.stringDepth) + ",");
        }
        System.out.print("\nLength: " + deepest.stringDepth+ "\n");
    }
    
    
}

