/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.jsondocument;

import gateimport java.util.HashSet;
import gate.Document;

/**
 *
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class JsonDocumentBuilder {
  Document doc;
  Set<String> includedSets;
  private JsonDocumentBuilder() {}
  public JsonDocumentBuilder(Document doc) {
    // TODO: for now check that Document is a SimpleDocument
    this.doc = doc;
    includedSets = new HashSet<String>();
    for (String annname : doc.getNamedAnnotations()) {
      includedSets.add(annname);
    }
  }
  
  public includeAllSets() {
    for (String annname : doc.getNamedAnnotations()) {
      includedSets.add(annname);
    }    
  }

  public excludeAllSets() {
    includedSets.clear();
  }
  
  public includeSet(String setname) {
    includedSets.add(setname);
  }
  
  public excludeSet(String setname) {
    includedSets.remove(setname);
  }
}
