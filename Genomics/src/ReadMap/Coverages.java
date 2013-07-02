/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ReadMap;

/**
 *Class used for storing read mapping info
 * @author Joshua Aldrich
 */

   public class Coverages{
        public float lengthCoverage;
        public float PercentIdentity;
        public int start;
        public int end;
        public String header = "";
        
        public Coverages(String header)
        {
            this.header = header;
            this.lengthCoverage = 0.0f;
            this.PercentIdentity = 0.0f;
            this.start = -1;
            this.end = -1;
        }

    }