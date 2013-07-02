/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GenomicsAlign;

import java.util.ArrayList;

/**
 *
 * @author Joshua
 */
public class LocalAlignment {
    
    Parameters param;
    int countMatch;
    int countMismatch;
    int countGaps;
    
    public LocalAlignment(Parameters param)
    {
        this.param = param;

    }
 
    //Check the character match and return appropriate result from parameters
    private int substitution(char c1, char c2)
    {
        if(c1 == c2)
        {
            return param.getMatch();
        }
        else
        {
            return param.getMismatch();
        }
    }

    
    //finds the max value in the array and returns the index
    private int[] getMaxIndex(DP_TableSDI table)
    {
        int currMax = 0;
        int[] maxIndex = new int[2];
        for(int i = 0; i < table.m; i++)
        {
            for(int j = 0; j < table.n;j++)
            {
                int tempMax = Tmax(table.m_table[i][j]);
                if(tempMax > currMax)
                {
                    currMax = tempMax;
                    maxIndex[0] = i;
                    maxIndex[1] = j;
                }
            }
        }
        return maxIndex;
    }
    
    //Performs the backtracking, returns the index of the leftmost column in the backtrack
    public int backtrack(DP_TableSDI mytable)
    {
        countMatch = 0;
        countMismatch = 0;
        this.countGaps = 0;

        int[] maxIndex = getMaxIndex(mytable);
        int i = maxIndex[0];
        int j = maxIndex[1];

        while(Tmax(mytable.m_table[i][j])!= 0)//got to zero, quit
        {

            
            int max = Tmax(mytable.m_table[i][j]);  //Find largest value between the 3 table types
            char c1 = mytable.seq1.charAt(i-1);
            char c2 = mytable.seq2.charAt(j-1);

            //depending on where the max comes from we will perform appropriate action
            if(mytable.m_table[i][j].subScore == max)
            {

                if(c1 == c2)
                {
                    countMatch++;
                }
                else
                {
                    countMismatch++;
                }
                i--;
                j--;
            }
            else if(mytable.m_table[i][j].delScore == max)
            {   
                countGaps++;
                i--;
            }
            else if(mytable.m_table[i][j].insScore == max)
            {
                countGaps++;                  
                j--;
            }
        }
        return j; //return column indice when done backtracking this will be offset into refernce genome

    }
    
    
    private String reverseString(String seqToRev)//REverses the string
    {
        String s1rev = "";
        int len = seqToRev.length()-1;
        for(int i = len; i >=0; i--)
        {
            s1rev += seqToRev.charAt(i);
        }
        return s1rev;
    }

    /**
 * Performs the smith-waterman local alignment
 * @param myTable
 * @return
 */
    public boolean align(DP_TableSDI myTable)
    {
        int mp1 = myTable.m;
        int np1 = myTable.n;


        for(int i = 0; i < mp1; i++)//initialize the edges
        {
            myTable.m_table[i][0].subScore = 0;
            myTable.m_table[i][0].delScore = 0;
            myTable.m_table[i][0].insScore = 0;
        }
        for(int j = 0; j < np1; j++)
        {
            myTable.m_table[0][j].subScore = 0;
            myTable.m_table[0][j].insScore = 0;
            myTable.m_table[0][j].delScore = 0;
        }

        for(int i = 1; i < mp1; i++)
        {
            for(int j= 1; j < np1; j++)
            {

                myTable.m_table[i][j].subScore = Math.max(0,Tmax(myTable.m_table[i-1][j-1])
                        + substitution(myTable.seq1.charAt(i-1),myTable.seq2.charAt(j-1)));

                myTable.m_table[i][j].delScore = Math.max(0,Math.max(
                        myTable.m_table[i-1][j].delScore + param.g,
                        Tmax(myTable.m_table[i-1][j]) + param.h + param.g));

                myTable.m_table[i][j].insScore = Math.max(0,Math.max(
                        myTable.m_table[i][j-1].insScore + param.g,
                        Tmax(myTable.m_table[i][j-1]) + param.h + param.g));
            }
        }
        return true;
    }

    //gets the max max value in a particular cell.
    private int Tmax(DP_CellSDI sdi)
    {
            return Math.max(sdi.subScore, 
                        Math.max(sdi.delScore,sdi.insScore));
    }
}
