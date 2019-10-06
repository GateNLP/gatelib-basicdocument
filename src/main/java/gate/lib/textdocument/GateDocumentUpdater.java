/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.textdocument;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.util.InvalidOffsetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class that allows to update a GATE document from a TextDocument
 * @author johann
 */
public class GateDocumentUpdater {
  /**
   * What to do if an annotation to add already exists in the gate doc
   */
  public enum HandleExistingAnns {
    REPLACE_ANNOTATION,  // completely replace with the new one
    REPLACE_FEATURES,  // just completely replace the features 
    UPDATE_FEATURES,  // add new and update existing features, do not delete any
    ADD_NEW_FEATURES,  // only add new features
    IGNORE,            // ignore that annotation, do nothing
    ADD_WITH_NEW_ID,  // add that annotation with a new id
  }
  private Document doc;
  
  private HandleExistingAnns handleAnns = HandleExistingAnns.ADD_WITH_NEW_ID;
  
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
  public GateDocumentUpdater handleExistingAnnotation(HandleExistingAnns option) {
    handleAnns = option;
    return this;
  }
  
  private void addAnnotation(AnnotationSet gateset, 
          int annid, int tdocstart, int tdocend, String tdoctype, 
          Map<String, Object> tdocfeatures) {
      Annotation gateann = gateset.get(annid);
      Map<String, Object> tmpmap = tdocfeatures == null ? 
                new HashMap<>() :
                tdocfeatures;
      if(gateann == null || handleAnns == HandleExistingAnns.ADD_WITH_NEW_ID) {
        try {
          gateset.add(
                (long)tdocstart, (long)tdocend, 
                tdoctype, gate.Utils.toFeatureMap(tmpmap));
        } catch(InvalidOffsetException ex) {
          throw new RuntimeException("Cannot add annotation", ex);
        }      
      } else {
        // an annotation with this id already exists, choose what to do
        // first get the existing featuremap and map string feature names
        // to the original keys. in theory this could yield duplicates but
        // we do not care about this for now, those features really should all
        // have string names! null keys are ignored
        FeatureMap gatefm = gateann.getFeatures();
        Map<String, Object> name2key = new HashMap<>();
        for(Object key : gatefm.keySet()) {
          if(key != null) {
            name2key.put(
                    (key instanceof String) ? 
                            (String)key : key.toString(), key);
          }          
        }
        // Subsequently, when we need to figure out if a feature is in the 
        // featuremap, use the name2key mapping
        switch(handleAnns) {
          case ADD_NEW_FEATURES:
            for(String fname : tdocfeatures.keySet()) {              
              if(!(name2key.containsKey(fname) && gatefm.containsKey(name2key.get(fname)))) {
                gatefm.put(fname, tdocfeatures.get(fname));
              }
            }
            break;
          // already gets handled above!
          // case ADD_WITH_NEW_ID:            
          //  break;
          case REPLACE_ANNOTATION:
            // I think there is no way to actually update need to remove and add with id
            gateset.remove(gateann);
            try {
              gateset.add(annid, (long)tdocstart, (long)tdocend, 
                  tdoctype, gate.Utils.toFeatureMap(tmpmap));
            } catch(InvalidOffsetException ex) {
              throw new RuntimeException("Cannot add annotation", ex);
            }
            break;
          case REPLACE_FEATURES:
            gatefm.clear();
            for(String fname : tdocfeatures.keySet()) {              
              gatefm.put(fname, tdocfeatures.get(fname));
            }
            break;
          case UPDATE_FEATURES:
            for(String fname : tdocfeatures.keySet()) {              
              gatefm.put(fname, tdocfeatures.get(fname));
            }
            break;
          case IGNORE:
            break;
          default:
            throw new RuntimeException("Should never happen!");                    
        }
      }
    
  }
  
  private void addAnnotationSet(TdocAnnotationSet annset) {
    String setname = annset.name;
    AnnotationSet gateset;
    if(setname.equals("")) {
      gateset = doc.getAnnotations();
    } else {
      gateset = doc.getAnnotations(setname);
    }
    for(TdocAnnotation tdocann : annset.annotations) {
      addAnnotation(gateset, 
              tdocann.id, tdocann.start, tdocann.end, tdocann.type, tdocann.features);
    }
  }
  
  /**
   * Actually carry out the update of the GATE document from the TdocDocument.
   * 
   * This carries out the update with whatever options have been set.
   * 
   * @param tdoc 
   */
  public void fromTdocDocument(TdocDocument tdoc) {
    // can only assign features if there are any in the tdoc
    if(tdoc.features != null) {
      if(featurenames == null) {
        doc.getFeatures().putAll(tdoc.features);
      } else {
        for(String fname : featurenames) {
          doc.getFeatures().put(fname, tdoc.features.get(fname));
        }
      }
    }
    if(tdoc.annotation_sets != null) {
      if(annsetnames == null) {
        for(String annsetname : tdoc.annotation_sets.keySet()) {
          addAnnotationSet(tdoc.annotation_sets.get(annsetname));
        }
      } else {
        for(String annsetname : annsetnames) {
          addAnnotationSet(tdoc.annotation_sets.get(annsetname));
        }
      }
    }
    
  }
  
  /**
   * Actually carry out the update of the GATE document from the TdocChangeLog.
   * 
   * This carries out the update with whatever options have been set.
   * 
   * @param tdoc 
   */
  public void fromTdocChangeLog(TdocChangeLog log) {
    for(Map<String, Object> chg : log.changes) {
      // features:clear
      // feature:set, feature, value
      // feature:remove, feature

      // features:clear, set, id
      // feature:set, set, id, feature, value
      // feature:remove, set, id, feature
      
      // annotation:add, set, start, end, type, features, id
      // annotation:remove, set, id
      // annotation:clear, set
      String cmd = (String)chg.get("command");
      String setname = (String)chg.get("set");
      AnnotationSet annset = null;
      if(setname != null) {
        annset = 
                    setname.equals("") ? 
                    doc.getAnnotations() : 
                    doc.getAnnotations(setname);        
      }
      Integer id = (Integer)chg.get("id");
      String feature = (String)chg.get("feature");
      Object value = chg.get("value");
      switch(cmd) {
        case "features:clear":      
          if(setname == null) {
            doc.getFeatures().clear();
          } else {
            if(setname.equals("")) {
              doc.getAnnotations().clear();
            } else {
              doc.getAnnotations(setname).clear();
            }
          }
          break;
        case "feature:set":
          if(setname == null) {
            doc.getFeatures().put(feature, value);
          } else {
            Annotation ann = annset.get(id);
            if(ann == null) {
              throw new RuntimeException("Annotation does not exist with id "+id);
            } else {
              ann.getFeatures().put(feature, value);
            }
          }
          break;
        case "feature:remove":
          if(setname == null) {
            doc.getFeatures().remove(feature);
          } else {
            Annotation ann = annset.get(id);
            if(ann == null) {
              throw new RuntimeException("Annotation does not exist with id "+id);
            } else {
              ann.getFeatures().remove(feature);
            }
          }
          break;
        case "annotation:add":
          int start = (Integer)chg.get("start");
          int end = (Integer)chg.get("end");
          String type = (String)chg.get("type");
          Map<String, Object> features = (Map<String,Object>)chg.get("features");
          addAnnotation(annset, id, start, end, type, features);
          break;
        case "annotation:remove":
          Annotation gateann = annset.get(id);
          annset.remove(gateann);
          break;
        case "annotations:clear":
          annset.clear();
          break;
      }
              
      
    }
  }
}
