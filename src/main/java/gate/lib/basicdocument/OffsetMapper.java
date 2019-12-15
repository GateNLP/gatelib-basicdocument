/*
 * Copyright (c) 2019 The University of Sheffield.
 *
 * This file is part of gatelib-basicdocument 
 * (see https://github.com/GateNLP/gatelib-basicdocument).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package gate.lib.basicdocument;

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
  
  /**
   * Create the offset mappings for the given string and cache them. 
   * 
   * @param string the string to use
   */
  public OffsetMapper(String string) {
    cache(string);
  }
  
  /**
   * Convert the offset from Java to Python.
   * 
   * @param offset java offset
   * @return  python offset
   */
  public int convertToPython(int offset) {
    if(offset >= 0 && offset < java2python.length) {
      return java2python[offset];
    } else {
      throw new GateRuntimeException("Attempt to find offset outside of range");
    }
  }

  /**
   * Convert the offset from Python to Java.
   * 
   * @param offset the python offset
   * @return java offset
   */
  public int convertToJava(int offset) {
    if(offset >= 0 && offset < python2java.length) {
      return python2java[offset];
    } else {
      throw new GateRuntimeException("Attempt to find offset outside of range");
    }
  }
  
  
}