/*
Dynamic progamming table including substitution,insertion,deletion.
 */
package GenomicsAlign;
import java.util.ArrayList;
import Utilities.Sequence;
/**
 *
 * @author Joshua
 */
public class DP_TableSDI {
        String seq1;
        String seq2;
	int m;
	int n;
	DP_CellSDI[][] m_table;
	LocalAlignment m_align;
	
        
        
	/**
     *Constructor for table with default local alignment
     * @param seq1
     * @param seq2
     */
    public DP_TableSDI(String seq1, String seq2)
	{
		this.seq1 = seq1;
		this.seq2 = seq2;
		m = seq1.length()+1;
		n = seq2.length()+1;
		InitializeTable();
                m_align = new LocalAlignment(new Parameters());

	}
        
    
        /**
     *Constructor which takes a local alignment where parameters may be different
     * @param seq1
     * @param seq2
     * @param al
     */
    public DP_TableSDI(String seq1, String seq2, LocalAlignment al)
	{
		this.seq1 = seq1;
		this.seq2 = seq2;
		m = seq1.length()+1;
		n = seq2.length()+1;
                this.m_align = al;
		InitializeTable();
                
	}
	
        //Initialize cells to 0
        public void InitializeTable()
        {
            m_table = new DP_CellSDI[m][n];
            for(int i = 0; i < m; i++)
            {   
                for(int j = 0; j < n;j++)
                {
                    m_table[i][j] = new DP_CellSDI();
                }
            }
        }
        
        //Use the asigned alignment fucntion
	public boolean Getalignment()
	{
		return m_align.align(this);
	}
        
        //Perform the backtrack and returns the first column indice of the backtrack
        public int BacktrackAlignment()
        {
            return m_align.backtrack(this);
        }
	
        //sets the alignmetn funciton to be used by the table
	public void setAlignment(LocalAlignment a)
	{
		m_align = a;
	}
	
        //Each of these gets there values from the results of the alignment backtrack
        public int GetMatches()
        {
            return m_align.countMatch;
        }
        
        public int GetMismatches()
        {
            return m_align.countMismatch;
        }
        
        
        public int GetGaps()
        {
            return m_align.countGaps;
        }
        
        //Get the reference to this cell
	public DP_CellSDI getcell(int i, int j)
        {
            return m_table[i][j];
        }
        
        public int getm()
	{
		return m;
	}
	
	public int getn()
	{
		return n;
	}
	

}
