/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package McCreight;

import Utilities.*;
import GenomicsAlign.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aldr699
 */
public class STTree3Test {
    
    public STTree3Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of GenerateTree method, of class STTree3.
     */
    @Test
 public void testSTTree3(){
      //  System.out.println("AverageNodeDepth");
        STTree3 instance = new STTree3();
        instance.GenerateTree("MISSISSIPPI");
        int[] A = instance.DFS_PrepareST(2);
                instance.PrintTreeNodes();
        STNode2 node = instance.FindLoc("NISISSIPMM",2);
  //      instance.PrintTree();
        int[] bwt = instance.GetBWTIndex();

     //   instance.PrintBWTIndex(bwt);
        System.out.println("Internal Node Count: " + instance.GetNodeCount(0));
      //  System.out.println("Leaf Node Count: " + instance.CountLeafNode());
//        instance.PrintTreeNodes();
   //     instance.PrintTreeDepth();
        double nodeCount = 11;
        double sumNodeDepth = 34;
 //       int sumndepth = instance.SumDepth();
 //       int ncount = instance.CountNodes();
        STNode2 deepNode = instance.DeepestInternalNode();
        System.out.println("Max Internal Depth: " + deepNode.stringDepth);      
     //   assertEquals(sumNodeDepth/nodeCount, instance.AverageDepth(),  0.01);
        
        
    }
   
        @Test
    public void PrepareSTTest()
    {
        System.out.println("Prepare Suffix Tree");
        
        STTree3 instance = new STTree3();
        instance.GenerateTree("accgaccgtact");
        instance.PrintTreeNodes();
  //      instance.PrintTreeNodes();
        int A[] = instance.DFS_PrepareST(2);    
        String read = "tacaccg";
        STNode2 node = instance.FindLoc(read,2);
        Parameters P = new Parameters();
        int l = 2;
        LocalAlignment al = new LocalAlignment(P);
        for(int i = node.startLeafIndex; i <= node.endLeafIndex; i++)
        {
            int start = A[i] - read.length();
            int end = A[i] + read.length();
            if(start < 0)
            {
                start = 0;
            }
            if(end > instance.masterSequence.length())
            {
                end = instance.masterSequence.length();
            }
            DP_TableSDI table = new DP_TableSDI(read, instance.masterSequence.substring(start, end),al);
            table.Getalignment();
            table.BacktrackAlignment();
            int matches = table.GetMatches();
            int mismatches = table.GetMismatches();
            int gaps = table.GetGaps();
        }
            
            
            
        
    }
    
        
    @Test
    public void PrepareSTTest2() throws FileNotFoundException
    {  
        System.out.println("Results");
        STTree3 instance = new STTree3();
        IOReader reader = new IOReader();
        ArrayList<Sequence> s1 = reader.ReadFasta(
                "C:\\Users\\aldr699\\Documents\\Classes\\cpts571\\PA3\\Peach_reference.fasta");
        
        instance.GenerateTree(s1.get(0).sequence);
        System.out.println(instance.GetNodeCount(-1));
        int A[] = instance.DFS_PrepareST(25); 
        ArrayList<Sequence> s2 = reader.ReadFasta(
                "C:\\Users\\aldr699\\Documents\\Classes\\cpts571\\PA3\\Peach_simulated_reads2.fasta");
        ArrayList<Coverages> bestCovers = new ArrayList<Coverages>();
        for(int i = 0; i < s2.size(); i++)
        {
            String read = s2.get(i).sequence;
            STNode2 node = instance.FindLoc(read,25);
            if(node != null)
            {
                Coverages bestCoverage = new Coverages();
                for(int j = node.startLeafIndex; j <= node.endLeafIndex; j++)
                {
                    int start = A[j] - read.length();
                    int end = A[j] + read.length();
                    if(start < 0)
                    {
                        start = 0;
                    }
                    if(end > instance.masterSequence.length())
                    {
                        end = instance.masterSequence.length();
                    }
                    DP_TableSDI table = new DP_TableSDI(read, 
                            instance.masterSequence.substring(start, end));
                    table.Getalignment();
                    int endofAlignment = table.BacktrackAlignment();
                    BestCoverage(s2.get(i).header, table, read, start + endofAlignment, bestCoverage);
                     
                }
                bestCovers.add(bestCoverage);
            }
        }
    }

    private void BestCoverage(String header, DP_TableSDI table, String read, int start, 
            Coverages bestCoverage) {
        int matches = table.GetMatches();
        int mismatches = table.GetMismatches();
        int gaps = table.GetGaps();
        
        int alignLength = matches + mismatches +gaps;
        float percIdentity = (float)matches/alignLength;
        float lengthCoverage = (float)alignLength/read.length();
        if(percIdentity > 0.9 && lengthCoverage > 0.8)
        {
            if(lengthCoverage > bestCoverage.lengthCoverage)
            {
                bestCoverage.header = header;
                bestCoverage.PercentIdentity = percIdentity;
                bestCoverage.lengthCoverage = lengthCoverage;
                bestCoverage.start = start;
                bestCoverage.end = start + alignLength;
            }
        }
    }
    
    
    
    class Coverages{
        float lengthCoverage;
        float PercentIdentity;
        int start;
        int end;
        String header = "";
        
        public Coverages()
        {
            this.lengthCoverage = 0.0f;
            this.PercentIdentity = 0.0f;
            this.start = -1;
            this.end = -1;
        }

    }
    
    
    
     @Test
    public void testSTInput1() throws FileNotFoundException, IOException{
         
        System.out.println("Results");
        STTree3 instance = new STTree3();
        IOReader reader = new IOReader();
        ArrayList<Sequence> s1 = reader.ReadFasta(
                "C:\\Users\\aldr699\\Documents\\Classes\\cpts571\\PA3\\Peach_reference.fasta");
        long startTime = System.nanoTime();
        instance.GenerateTree(s1.get(0).sequence);
        long estimatedTime = System.nanoTime() - startTime;
        double seconds = (double)estimatedTime / 1000000000.0;
        System.out.println("Time to generate tree: " + seconds + " secs");
     //   instance.PrintTree();
   //     int[] bwt = instance.GetBWTIndex();

   //     instance.PrintTree();
  //      instance.PrintTreeDepth();
     //   System.out.println("Internal Node Count: " + instance.CountInternalNode());
     //   System.out.println("Leaf Node Count: " + instance.CountLeafNode());
        System.out.println("Deepest Node");
        instance.PrintDeepestMatch();
        
        
        PrintWriter out = new PrintWriter(
                new FileWriter(
                "C:\\Users\\aldr699\\Documents\\Classes\\cpts571\\s1.fasBTW.txt"));
 //       instance.PrintBWTIndex(bwt, out);
        out.close();

    }
  
}