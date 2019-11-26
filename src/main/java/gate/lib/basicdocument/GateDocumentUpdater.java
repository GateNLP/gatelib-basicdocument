
package gate.lib.basicdocument;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: use offset mapper when copying over the annotations from Tdoc/changelog
//   in case those offsets are type python



/**
 * A class that allows to update a GATE document from a TextDocument
 *
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class GateDocumentUpdater {

  /**
   * What to do if an annotation to add already exists in the gate gateDocument
   */
  public enum HandleExistingAnns {
    /**
     * Completely replace the annotation with the new one.
     */
    REPLACE_ANNOTATION, // completely replace with the new one
    /**
     * Completely replace the features of the existing annotation.
     */
    REPLACE_FEATURES, // just completely replace the features 
    /**
     * Add new and update existing features, do not delete any.
     */
    UPDATE_FEATURES, // add new and update existing features, do not delete any
    /**
     * Only add new features.
     */
    ADD_NEW_FEATURES, // only add new features
    /**
     * Ignore that annotation.
     */
    IGNORE, // ignore that annotation, do nothing
    /**
     * Add as a new annotation with a new id.
     */
    ADD_WITH_NEW_ID,  // add that annotation with a new id
  }
  private Document gateDocument;

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
   * OffsetMapper for converting offsets to Java.
   * If we update from a BdocDocument of ChangeLog which does not have Java
   * offsets, we first create the offset mapper and store it here before any
   * annotations get copied. The offset mapper is only built whenever the 
   * first annotation actually needs to get converted.
   */
  private OffsetMapper offsetMapper = null;

  /**
   * Create a document updater with the default options. Initially, all
   * information from the update source except text will be used to update the
   * GATE document. Use the noXxx() methods followed by useXxx() methods to
   * select a specific set of information.
   *
   * @param doc the GATE document to update
   */
  public GateDocumentUpdater(Document doc) {
    this.gateDocument = doc;

  }
  
  /**
   * Create a document updated for updating a brand new document with this text.
   * 
   * This can be used to convert a BdocDocument to a GATE document and still
   * control, if necessary, which annotations/features of the BdocDocument
   * should get converted. 
   * 
   * @param text 
   */
  public GateDocumentUpdater(String text) {
    try {
      this.gateDocument = Factory.newDocument(text);
    } catch (ResourceInstantiationException ex) {
      throw new GateRuntimeException("Could not create GATE document from the given text", ex);
    }
  }

  // Methods to set options about how to update the document
  // These can be chained as necessary
  /**
   * Set the current list of known annotation set names to add to empty.
   * Initially, all annotation sets are added, this can be used to start giving
   * an explicit list of annotation set names to use by subsequently calling
   * useAnnotationSet(name)
   *
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater noAnnotationSet() {
    annsetnames = new HashSet<>();
    return this;
  }

  /**
   * Include this annotation set in the updates.
   *
   * @param name name of annotation set to include
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater useAnnotationSet(String name) {
    annsetnames.add(name);
    return this;
  }

  /**
   * Clear the list of document feature names to use for updating.
   *
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater noFeature() {
    featurenames = new HashSet<String>();
    return this;
  }

  /**
   * Add feature name to include for updating.
   *
   * @param name the name of the feature
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater useFeature(String name) {
    featurenames.add(name);
    return this;
  }

  /**
   * Specify how annotations with an id that already exists should be
   * handled.Default is ADD_WITH_NEW_ID
   *
   *
   * @param option The annotation handling option to use
   * @return modified GateDocumentUpdater
   */
  public GateDocumentUpdater handleExistingAnnotation(HandleExistingAnns option) {
    handleAnns = option;
    return this;
  }

  private void addAnnotation(AnnotationSet gateset,
          int annid, int tdocstart, int tdocend, String tdoctype,
          Map<String, Object> tdocfeatures, String offsetType) {
    Annotation gateann = gateset.get(annid);
    Map<String, Object> tmpmap = 
            (tdocfeatures == null)
            ? new HashMap<>()
            : tdocfeatures;
    if (gateann == null || handleAnns == HandleExistingAnns.ADD_WITH_NEW_ID) {
      try {
        gateset.add(
                (long) tdocstart, (long) tdocend,
                tdoctype, gate.Utils.toFeatureMap(tmpmap));
      } catch (InvalidOffsetException ex) {
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
      for (Object key : gatefm.keySet()) {
        if (key != null) {
          name2key.put(
                  (key instanceof String)
                          ? (String) key : key.toString(), key);
        }
      }
      // Subsequently, when we need to figure out if a feature is in the 
      // featuremap, use the name2key mapping
      switch (handleAnns) {
        case ADD_NEW_FEATURES:
          for (String fname : tmpmap.keySet()) {
            if (!(name2key.containsKey(fname) && gatefm.containsKey(name2key.get(fname)))) {
              gatefm.put(fname, tmpmap.get(fname));
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
            gateset.add(annid, (long) tdocstart, (long) tdocend,
                    tdoctype, gate.Utils.toFeatureMap(tmpmap));
          } catch (InvalidOffsetException ex) {
            throw new RuntimeException("Cannot add annotation", ex);
          }
          break;
        case REPLACE_FEATURES:
          gatefm.clear();
          for (String fname : tmpmap.keySet()) {
            gatefm.put(fname, tmpmap.get(fname));
          }
          break;
        case UPDATE_FEATURES:
          tmpmap.keySet().forEach((fname) -> {
            gatefm.put(fname, tmpmap.get(fname));
        });
          break;

        case IGNORE:
          break;
        default:
          throw new RuntimeException("Should never happen!");
      }
    }

  }

  private void addAnnotationSet(BdocAnnotationSet annset, String offsetType) {
    String setname = annset.name;
    AnnotationSet gateset;
    if (setname.equals("")) {
      gateset = gateDocument.getAnnotations();
    } else {
      gateset = gateDocument.getAnnotations(setname);
    }
    annset.annotations.forEach((tdocann) -> {
      addAnnotation(gateset,
              tdocann.id, tdocann.start, tdocann.end, tdocann.type,
              tdocann.features, offsetType);
    });
  }

  /**
   * Actually carry out the update of the GATE document from the BdocDocument.
   * This carries out the update with whatever options have been set.
   *
   *
   * @param bdoc the bdoc to use for the updates
   * @return the updated GATE document
   */
  public Document fromBdoc(BdocDocument bdoc) {
    // can only assign features if there are any in the bdoc
    if (bdoc.features != null) {
      if (featurenames == null) {
        gateDocument.getFeatures().putAll(bdoc.features);
      } else {
        featurenames.forEach((fname) -> {
          gateDocument.getFeatures().put(fname, bdoc.features.get(fname));
        });
      }
    }
    if (bdoc.annotation_sets != null) {
      if (annsetnames == null) {
        bdoc.annotation_sets.keySet().forEach((annsetname) -> {
          addAnnotationSet(bdoc.annotation_sets.get(annsetname), bdoc.offset_type);
        });
      } else {
        annsetnames.forEach((annsetname) -> {
          addAnnotationSet(bdoc.annotation_sets.get(annsetname), bdoc.offset_type);
        });
      }
    }
    return gateDocument;
  }

  /**
   * Actually carry out the update of the GATE document from the TdocChangeLog.This carries out the update with whatever options have been set.
   *
   *
   * @param chlog the changelog to use for the updates
   * @return returns the updated GATE document 
   */
  public Document fromChangeLog(ChangeLog chlog) {
    for (Map<String, Object> chg : chlog.changes) {
      // features:clear
      // feature:set, feature, value
      // feature:remove, feature

      // features:clear, set, id
      // feature:set, set, id, feature, value
      // feature:remove, set, id, feature
      // annotation:add, set, start, end, type, features, id
      // annotation:remove, set, id
      // annotation:clear, set
      String cmd = (String) chg.get("command");
      String setname = (String) chg.get("set");
      AnnotationSet annset = null;
      if (setname != null) {
        annset
                = setname.equals("")
                ? gateDocument.getAnnotations()
                : gateDocument.getAnnotations(setname);
      }
      Integer id = (Integer) chg.get("id");
      String feature = (String) chg.get("feature");
      Object value = chg.get("value");
      switch (cmd) {
        case "features:clear":
          if (setname == null) {
            gateDocument.getFeatures().clear();
          } else {
            if (setname.equals("")) {
              gateDocument.getAnnotations().clear();
            } else {
              gateDocument.getAnnotations(setname).clear();
            }
          }
          break;
        case "feature:set":
          if (setname == null) {
            gateDocument.getFeatures().put(feature, value);
          } else {
            if (annset != null) {
              Annotation ann = annset.get(id);
              if (ann == null) {
                throw new RuntimeException("Annotation does not exist with id " + id);
              } else {
                ann.getFeatures().put(feature, value);
              }
            }
          }
          break;
        case "feature:remove":
          if (setname == null) {
            gateDocument.getFeatures().remove(feature);
          } else {
            if (annset != null) {
              Annotation ann = annset.get(id);
              if (ann == null) {
                throw new RuntimeException("Annotation does not exist with id " + id);
              } else {
                ann.getFeatures().remove(feature);
              }
            }
          }
          break;
        case "annotation:add":
          int start = (Integer) chg.get("start");
          int end = (Integer) chg.get("end");
          String type = (String) chg.get("type");
          Map<String, Object> features = (Map<String, Object>) chg.get("features");
          addAnnotation(annset, id, start, end, type, features, chlog.offset_type);
          break;
        case "annotation:remove":
          if (annset != null) {
            Annotation gateann = annset.get(id);
            annset.remove(gateann);
          }
          break;
        case "annotations:clear":
          if (annset != null) {
            annset.clear();
          }
          break;
      }

    }
    return gateDocument;
  }
}
