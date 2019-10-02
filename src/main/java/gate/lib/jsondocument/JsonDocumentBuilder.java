/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.jsondocument;

import com.fasterxml.jackson.jr.ob.JSON;
import java.util.HashSet;
import gate.Document;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class JsonDocumentBuilder {
  Document doc;
  Set<String> includedSets;
  File toFile = null;
  private JsonDocumentBuilder() {}
  public JsonDocumentBuilder(Document doc) {
    // TODO: for now check that Document is a SimpleDocument
    this.doc = doc;
    includedSets = new HashSet<>();
    for (String annname : doc.getNamedAnnotationSets().keySet()) {
      includedSets.add(annname);
    }
  }
  
  public JsonDocumentBuilder includeAllSets() {
    for (String annname : doc.getNamedAnnotationSets().keySet()) {
      includedSets.add(annname);
    }    
    return this;
  }

  public JsonDocumentBuilder excludeAllSets() {
    includedSets.clear();
    return this;
  }
  
  public JsonDocumentBuilder includeSet(String setname) {
    includedSets.add(setname);
    return this;
  }
  
  public JsonDocumentBuilder excludeSet(String setname) {
    includedSets.remove(setname);
    return this;
  }
  
  private JsonDocument buildJsonDoc() {
    // actually build the jsdondoc and return it, using the current features
    // TODO: set fields etc
    return new JsonDocument();
  }
  
  public void toFile(File path) {
  }
  
  public String asString() {
    try {
      JsonDocument jsondoc = buildJsonDoc();
      return JSON.std.with(JSON.Feature.PRETTY_PRINT_OUTPUT).asString(jsondoc);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build JSON", ex);
    }
  }
  
}
