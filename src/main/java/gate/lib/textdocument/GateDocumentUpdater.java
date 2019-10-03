/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.textdocument;

import gate.Document;

/**
 * A class that allows to update a GATE document from a TextDocument
 * @author johann
 */
public class GateDocumentUpdater {
  private Document doc;
  public GateDocumentUpdater(Document doc) {
    this.doc = doc;
  }
  
  // Methods to set options about how to update the document
  // These can be chained as necessary
  
  // Methods to carry out the update, using the given options, from 
  // some specific source of data.
  public void fromTdocDocument(TdocDocument tdoc) {
    // TODO
  }
  
  public void fromTdocChangeLog(TdocChangeLog log) {
    // TODO
  }
}
