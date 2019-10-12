/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.basicdocument.docformats;

import com.fasterxml.jackson.jr.ob.JSON;
import gate.lib.basicdocument.BdocChangeLog;
import gate.lib.basicdocument.BdocDocument;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Serialize and deserialize BdocDocument and BdocChangeLog instances as JSON.
 * 
 * These are convenience methods to make it easy to serialise and deserialise
 * BdocDocument and BdocChangeLog instances as JSON. 
 * This is done in a separate class since 
 * we may want to use other serialisation formats in the future and to 
 * hide any details of the de/serialisation.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class SimpleJson {
  
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
   * Serialise a BdocChangeLog to a file. 
   * 
   * @param clog BdocChangeLog instance
   * @param path the file path where to write to, will get overwritten
   */
  public void dump(BdocChangeLog clog, File path) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(clog, path);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to "+path, ex);
    }
  }

  /**
   * Serialise a BdocDocument to a Writer.
   * 
   * @param bdoc BdocDocument instance
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
   * Serialise a BdocChangeLog to a Writer.
   * 
   * @param clog BdocChangeLog instance
   * @param writer writer to serialise to
   */
  public void dump(BdocChangeLog clog, Writer writer) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(clog, writer);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON to writer", ex);
    }
    
  }
  
  /**
   * Serialise a BdocDocument to an OutputStream.
   * 
   * @param bdoc BdocDocument instance
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
   * Serialise a BdocChangeLog to an OutputStream.
   * 
   * @param clog BdocChangeLog instance
   * @param ostream output stream to serialise to
   */
  public void dump(BdocChangeLog clog, OutputStream ostream) {
    JSON jsonbuilder = JSON.std;
    try {
      jsonbuilder.write(clog, ostream);
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
      throw new RuntimeException("Could not build JSON String from BdocDocument", ex);
    }    
  }
  
  /**
   * Serialise a BdocDocument as a String
   * 
   * @param clog BdocChangeLog instance
   * @return the generated JSON string
   */
  public String dumps(BdocChangeLog clog) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.asString(clog);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build JSON String from ChangeLog", ex);
    }    
  }
  
  
  
  // 2) Load: it seems this works properly out of the box, no need for custom readers
  
  // 2.1) BdocDocument
  
  public BdocDocument load_doc(InputStream instream) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocDocument.class, instream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocDocument from input stream", ex);
    }
  }
  
  public BdocDocument load_doc(Reader reader) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocDocument.class, reader);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocDocument from reader", ex);
    }
  }
  
  public BdocDocument loads_doc(String json) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocDocument.class, json);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocDocument from String", ex);
    }
  }

  // 2.1) BdocDocument
  
  public BdocChangeLog load_log(InputStream instream) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocChangeLog.class, instream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocChangeLog from input stream", ex);
    }
  }
  
  public BdocChangeLog load_log(Reader reader) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocChangeLog.class, reader);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocChangeLog from reader", ex);
    }
  }
  
  public BdocChangeLog loads_log(String json) {
    JSON jsonbuilder = JSON.std;
    try {
      return jsonbuilder.beanFrom(BdocChangeLog.class, json);
    } catch (IOException ex) {
      throw new RuntimeException("Could not read BdocChangeLog from String", ex);
    }
  }
  
}
