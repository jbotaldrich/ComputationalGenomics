/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package readmapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import GenomicsAlign.*;
import McCreight.*;
import ReadMap.*;
import Utilities.*;


/**
 *
 * @author aldr699
 */
public class ReadMapper {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here

        RunProgram(args);
    }
    
    


  
    
    public class InvalidTreeFasta extends Exception {
        public InvalidTreeFasta(String message){
            super(message);
        }
    }
    
    
    /**
     *Runs everything necessary mapping the reads.
     * @param args
     * @throws FileNotFoundException
     */
    public static void RunProgram(String[] args) throws FileNotFoundException
    {
        if(args.length != 3)
        {
            return;
        }
        IOReader reader = new IOReader();//Read the alphabet in for comparison
        char[] alph = reader.ReadAlphabet(args[2]);
        System.out.println("Results");
        STTree3 instance = new STTree3();

        ArrayList<Sequence> RefGenome = reader.ReadFasta(args[0]); //read in ref genome
        if(!LegalString(RefGenome.get(0).sequence, alph))// test against alphabet
        {
            System.out.println("Reference genome contains illegal characters!");
            return;//quit if it contains illegal characters
        }
        long startTime = System.nanoTime();
        instance.GenerateTree(RefGenome.get(0).sequence);  //Make tree and time it
        long estimatedTime = System.nanoTime() - startTime;
        double seconds = (double)estimatedTime / 1000000000.0;
        System.out.println("Suffix Tree Construction: "+ seconds + " (sec)");
      
        startTime = System.nanoTime();
        int A[] = instance.DFS_PrepareST(25); // Prepare the tree for read mapping
        estimatedTime = System.nanoTime() - startTime;
        seconds = (double)estimatedTime / 1000000000.0;
        System.out.println("Prepare Suffix Tree: "+ seconds + " (sec)");
        
        ArrayList<Sequence> ReadList = reader.ReadFasta(args[1]); //import the reads
        ArrayList<Coverages> bestCovers = new ArrayList<Coverages>(); //good coverag storage
        
        startTime = System.nanoTime();
        
        int countAlignableReads = 0;
        int CountAlignments = 0;
        for(int i = 0; i < ReadList.size(); i++)
        {
            Coverages bestCoverage = new Coverages(ReadList.get(i).header);
            String read = ReadList.get(i).sequence;
            if(!LegalString(read, alph))//if the read has a bad character skip
            {
                continue;
            }
            STNode2 node = instance.FindLoc(read,25);  //Find best matching region
            if(node != null)
            {
                countAlignableReads++;
                CountAlignments += node.getEndLeafIndex() - node.getStartLeafIndex() + 1;
                for(int j = node.getStartLeafIndex(); j <= node.getEndLeafIndex(); j++)
                {
                    int start;
                    int end;
                    if(j==-1)  //If the best match includes a lot fo leaf characters use the start of the leaf
                    {
                        start = node.getStart()-read.length();
                        end = node.getStart() + read.length();
                    }
                    else  //Otherwise do use the internal node to select the correct start points use array from prepareST
                    {   
                        start = A[j] - read.length();
                        end = A[j] + read.length();
                    }
                    if(start < 0)//handle edge cases
                    {
                        start = 0;
                    }
                    if(end >instance.getMasterSequence().length())
                    {
                        end = instance.getMasterSequence().length();
                    }
                    //Do the alignment
                    DP_TableSDI table = new DP_TableSDI(read, 
                            instance.getMasterSequence().substring(start, end));
                    table.Getalignment();
                    int endofAlignment = table.BacktrackAlignment();
                    //Check if this is the best alignment and store if it is.
                    BestCoverage(ReadList.get(i).header, table, read, start + endofAlignment, bestCoverage);
                     
                }
                
            }
            bestCovers.add(bestCoverage);
        }
        estimatedTime = System.nanoTime() - startTime;
        seconds = (double)estimatedTime / 1000000000.0;
        System.out.println("Read Mapping: "+ seconds + " (sec)");
        System.out.println("Average Alignments per read: " + (float)CountAlignments/countAlignableReads);
        //Output results time on this is negligible
        File fileinfo = new File(args[1]);
        File outfilename = new File(fileinfo.getParent(), ("MappingResults_"+ fileinfo.getName() + ".txt"));
        PrintBestCovers(outfilename.getPath(), bestCovers);
    }

    /*
     * Calculates stats and compares them to old results to find best for this read and keep that
     */
    private static void BestCoverage(String header, DP_TableSDI table, String read, int start, 
        Coverages bestCoverage) {
        int matches = table.GetMatches();
        int mismatches = table.GetMismatches();
        int gaps = table.GetGaps();
        
        int alignLength = matches + mismatches +gaps;
        float percIdentity = (float)matches/alignLength;
        float lengthCoverage = (float)alignLength/read.length();
        if(percIdentity > 0.75 && lengthCoverage > 0.75)//check that its reasonable
        {
            if(lengthCoverage > bestCoverage.lengthCoverage)//Check if its the best and store if it is
            {
                bestCoverage.header = header;
                bestCoverage.PercentIdentity = percIdentity;
                bestCoverage.lengthCoverage = lengthCoverage;
                bestCoverage.start = start;
                bestCoverage.end = start + alignLength;
            }
        }
    }

        
    /**
     *Prints out all the best coverage.
     * @param filepath
     * @param covers
     */
    public static void PrintBestCovers(String filepath, ArrayList<Coverages> covers)
    {
        try{
            FileWriter outfile = new FileWriter(filepath);
            PrintWriter out = new PrintWriter(outfile);
            out.println("ShortRead\tStartIndex\tEndIndex");
            for(Coverages i : covers)
            {
                out.println(i.header + "\t" +i.start + "\t" + i.end);
            }
            out.close();
        }catch(IOException e){
            e.printStackTrace();
        }
      
    }
       
    /*
     * Checks a string to make sure it contains legal characters from alphabet.
     */
    private static boolean LegalString(String s, char[] alph){
        for(int i = 0; i < s.length(); i++)
        {
            boolean hasit = false;
            for(int j = 0; j < alph.length; j++)
            {
                if(alph[j] == s.charAt(i))
                {
                    hasit = true;
                }                
            }
            if(!hasit)
            {
                return false;
            }
        }
        return true;
    }

}