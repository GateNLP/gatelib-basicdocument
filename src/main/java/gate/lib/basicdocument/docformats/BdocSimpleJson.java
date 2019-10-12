/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.basicdocument.docformats;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.impl.JSONReader;
import com.fasterxml.jackson.jr.ob.api.ValueReader;
import gate.lib.basicdocument.BdocAnnotation;
import gate.lib.basicdocument.BdocDocument;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Convert Bdoc document from and to the simple JSON format also used by Python.
 * 
 * These are convenience methods to make it easy to serialise and deserialise
 * BdocDocument instances as JSON. This is done in a separate class since 
 * we may want to use other serialisation formats in the future and to 
 * hide any details of the de/serialisation.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocSimpleJson {
  
  // 1) Writing to JSON: this is really simple, we essentially just write 
  // exactly what we have
  
  /**
   * Serialise a BdocDocument to a file. 
   * 
   * @param bdoc Bdoc document
   * @param path the file path where to write to, will get overwritten
   */
  public void dump(BdocDocument bdoc, File path) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(bdoc, path);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to "+path, ex);
    }
  }
  
  /**
   * Serialise a BdocDocument to a Writer.
   * 
   * @param bdoc Bdoc document
   * @param writer writer to serialise to
   */
  public void dump(BdocDocument bdoc, Writer writer) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(bdoc, writer);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to writer", ex);
    }
    
  }

  /**
   * Serialise a BdocDocument to an OutputStream.
   * 
   * @param bdoc Bdoc document
   * @param ostream output stream to serialise to
   */
  public void dump(BdocDocument bdoc, OutputStream ostream) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(bdoc, ostream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to output stream", ex);
    }    
  }

  /**
   * Serialise a BdocDocument as a String
   * 
   * @param bdoc Bdoc document
   * @return the generated JSON string
   */
  public String dumps(BdocDocument bdoc) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.asString(bdoc);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build JSON document as String", ex);
    }    
  }
  
  
  // 2) Load: for this we define custom value readers 
  
  static class BdocAnnotationReader extends ValueReader {
    
    public BdocAnnotationReader(BdocAnnotation bann) {
      super(BdocAnnotation.class);
    }

    @Override
    public Object read(JSONReader reader, JsonParser jp) throws IOException {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  }
  
  public BdocDocument load(InputStream instream) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocDocument.class, instream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocDocument from input stream", ex);
    }
  }
  
}
