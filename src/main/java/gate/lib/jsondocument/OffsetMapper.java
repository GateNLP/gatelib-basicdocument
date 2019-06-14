/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.jsondocument;

import gate.util.GateRuntimeException;

/**
 * Simple class to map offsets between UTF16 and character sequences.
 * 
 * This class when constructed or re-initialised builds and caches
 * an offset mapping which can be used to map any offset between the Jav
 * representation of the string and a Unicode character sequence 
 * representation of the String. 
 * 
 * This will assume one specific way for how to normalise the unicode
 * character sequence corresponding to the java string and will assume 
 * that the string passed was normalised in the same way as the normalise()
 * method does.
 * 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class OffsetMapper {
  private String string;
  private int[] java2ext_cache;
  private int[] ext2java_cache;
  private OffsetMapper() {}
  private void cache(String str) {
    this.string = str;
    // first create temporary lists that contains the offsets where 
    // the other representation changes and by how much, then from those
    // create the actual index cache (because then we know the size!)
  }
  public OffsetMapper(String string) {
    cache(string);
  }
  
  public void init(String string) {
    cache(string);
  }
  
  public static String normalise(String string) {
    return string;
  }
  
  public int java2ext(int offset) {
    if(offset >= 0 && offset < java2ext_cache.length) {
      return this.java2ext(offset);
    } else {
      throw new GateRuntimeException("Attempt to find offset outside of range");
    }
  }

  public int ext2java(int offset) {
    if(offset >= 0 && offset < ext2java_cache.length) {
      return this.java2ext(offset);
    } else {
      throw new GateRuntimeException("Attempt to find offset outside of range");
    }
  }
  
  
}
