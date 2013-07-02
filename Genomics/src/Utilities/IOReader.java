package Utilities;

/*
 * Utility for reading the parameters and input sequence files
 */


import GenomicsAlign.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author Joshua
 */
public class IOReader {
    
    public Parameters ReadParameters(String paramConfig) throws FileNotFoundException
    {
        Parameters param = new Parameters();
        File file = new File(paramConfig);
        Scanner scanner = new Scanner(file);
        String line;
        while(scanner.hasNext())
        {
            line = scanner.nextLine();
            String[] lineArray = (line.trim()).split("\\s+");
            int paramVal = Integer.parseInt(lineArray[1]);
            if(lineArray[0].equals("match"))
            {
                param.setMatch(paramVal);
            }else if(lineArray[0].equals("mismatch"))
            {
                param.setMismatch(paramVal);
            }else if(lineArray[0].equals("g"))
            {
                param.setG(paramVal);
            }else if(lineArray[0].equals("h"))
            {
                param.setH(paramVal);
            }
        }
        return param;
    }
    
    /**
     *
     * @param filePath
     * @return a list of sequence objects which provide 
     * the name and the sequence.
     */
    public ArrayList<Sequence> ReadFasta(String filePath) throws FileNotFoundException
    {
        ArrayList<Sequence> sequenceList = new ArrayList<>();
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        String line;
        StringBuilder sequence = new StringBuilder();
        String sequenceName = "";
        Sequence fastaEntry = null;

        while(scanner.hasNext())
        {
            line = scanner.nextLine();
            if(line.length() > 0 && line.charAt(0) == '>')
            {
                if(fastaEntry != null)
                {
                    fastaEntry.header = sequenceName.split("\\s+")[0].substring(1);
                    fastaEntry.sequence = sequence.toString();
                    sequenceList.add(fastaEntry);
                    sequence = new StringBuilder();
                }
                fastaEntry = new Sequence();
                sequenceName = line;
            }
            else
            {
                sequence.append(line);
            }
        }
        if(fastaEntry != null)
        {
          fastaEntry.header = sequenceName.split("\\s+")[0].substring(1);
          fastaEntry.sequence = sequence.toString();
          sequenceList.add(fastaEntry);
          sequence = new StringBuilder();
        }
        return sequenceList; 
    }
    
    //Reads in the alphabet for checking strings
        public char[] ReadAlphabet(String filePath) throws FileNotFoundException
    {
        ArrayList<String> alphabet = new ArrayList<String>();
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        String line;
        while(scanner.hasNext())
        {
            line = scanner.next();
            alphabet.addAll(Arrays.asList(line.split(" ")));
        }
        scanner.close();
        char[] charalph = new char[alphabet.size()];
        for(int i = 0; i < alphabet.size(); i++)
        {
            charalph[i] = alphabet.get(i).charAt(0);
        }
        
        return charalph;
    }
    
}
