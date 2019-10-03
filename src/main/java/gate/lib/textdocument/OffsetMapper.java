/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.textdocument;

import gate.util.GateRuntimeException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to map offsets between UTF16 and character sequences.
 * 
 * This class when constructed or re-initialised builds and caches
 * an offset mapping which can be used to map any offset between the Jav
 * representation of the string and a Unicode character sequence 
 * representation of the String. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class OffsetMapper {
  private int[] java2python;
  private int[] python2java;
  /**
   * Disallow no-argument constructor.
   */
  private OffsetMapper() {}
  private void cache(String str) {
    List<Integer> java2python_list = new ArrayList<Integer>();
    List<Integer> python2java_list = new ArrayList<Integer>();
    
    int off_p = 0;  // in this we keep track of the corresponding python off
    for (int i=0; i<str.length(); i++) {
      char ch = str.charAt(i);      
      java2python_list.add(off_p);       
      if(Character.isHighSurrogate(ch)) {
        // first of the two, we do not increment off_p after this one
        // however if we get the first one, we add the current java offset
        // to the python2java table
        python2java_list.add(i);
      } else if(Character.isLowSurrogate(ch)) {
        off_p += 1;
        // do not add to python2java
      } else {
        off_p += 1;
        python2java_list.add(i);
      }
    }
    java2python = 
            java2python_list.parallelStream().
                    mapToInt(Integer::intValue).toArray();
    python2java = 
            java2python_list.parallelStream().
                    mapToInt(Integer::intValue).toArray();
  }
  public OffsetMapper(String string) {
    cache(string);
  }
  
  public int convertToPython(int offset) {
    if(offset >= 0 && offset < java2python.length) {
      return java2python[offset];
    } else {
      throw new GateRuntimeException("Attempt to find offset outside of range");
    }
  }

  public int convertToJava(int offset) {
    if(offset >= 0 && offset < python2java.length) {
      return python2java[offset];
    } else {
      throw new GateRuntimeException("Attempt to find offset outside of range");
    }
  }
  
  
}
