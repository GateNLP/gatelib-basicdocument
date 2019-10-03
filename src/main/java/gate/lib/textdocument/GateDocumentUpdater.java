/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.textdocument;

import gate.Document;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that allows to update a GATE document from a TextDocument
 * @author johann
 */
public class GateDocumentUpdater {
  public enum HandleAnns {
    REPLACE_ANNOTATION,
    REPLACE_FEATURES,
    UPDATE_FEATURES,
    ADD_NEW_FEATURES,
    IGNORE,
    ADD_WITH_NEW_ID
  }
  private Document doc;
  
  private HandleAnns handleAnns = HandleAnns.ADD_WITH_NEW_ID;
  
  /**
   * If null, use all, otherwise the set of annotation set names to use.
   */
  private Set<String> annsetnames;
  
  /**
   * If null, use all, otherwise the set of document feature names to use.
   */
  private Set<String> featurenames;
  
  /**
   * Create a document updater with the default options.
   * Initially, all information from the update source except text will
   * be used to update the GATE document. Use the noXxx() methods followed 
   * by useXxx() methods to select a specific set of information. 
   * 
   * @param doc  the GATE document to update
   */
  public GateDocumentUpdater(Document doc) {
    this.doc = doc;
    
  }
  
  // Methods to set options about how to update the document
  // These can be chained as necessary
  
  /**
   * Set the current list of known annotation set names to add to empty.
   * Initially, all annotation sets are added, this can be used to 
   * start giving an explicit list of annotation set names to use by 
   * subsequently calling useAnnotationSet(name)
   * @return 
   */
  public GateDocumentUpdater noAnnotationSet() {
    annsetnames = new HashSet<String>();
    return this;
  }
  
  /**
   * Include this annotation set in the updates.
   * @param name
   * @return 
   */
  public GateDocumentUpdater useAnnotationSet(String name) {
    annsetnames.add(name);
    return this;
  }
  
  /**
   * Clear the list of document feature names to use for updating.
   * @return 
   */
  public GateDocumentUpdater noFeature() {
    featurenames = new HashSet<String>();
    return this;
  }
  
  public GateDocumentUpdater useFeature(String name) {
    featurenames.add(name);
    return this;
  }
  
  /**
   * Specify how annotations with an id that already exists should be handled.
   * Default is ADD_WITH_NEW_ID
   * @return 
   */
  public GateDocumentUpdater handleExistingAnnotation(HandleAnns option) {
    handleAnns = option;
    return this;
  }
  
  /**
   * Actually carry out the update of the GATE document from the TdocDocument.
   * 
   * This carries out the update with whatever options have been set.
   * 
   * @param tdoc 
   */
  public void fromTdocDocument(TdocDocument tdoc) {
    // TODO
  }
  
  /**
   * Actually carry out the update of the GATE document from the TdocChangeLog.
   * 
   * This carries out the update with whatever options have been set.
   * 
   * @param tdoc 
   */
  public void fromTdocChangeLog(TdocChangeLog log) {
    // TODO
  }
}
