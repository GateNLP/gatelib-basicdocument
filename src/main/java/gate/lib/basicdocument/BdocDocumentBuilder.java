/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.basicdocument;

import com.fasterxml.jackson.jr.ob.JSON;
import gate.Annotation;
import gate.Document;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for building a JsonDocument.
 * 
 * This allows building the document from a GATE document or from scratch,
 * optionally limiting the parts to add to the document.
 * Once all the parts are ready, this can be used to return the BdocDocument
 * instance or directly serialise it as JSON to various destinations.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocDocumentBuilder {
  
  String text;
  HashMap<String,Set<Annotation>> includedSets = new HashMap<>();
  HashMap<String, Object> includedFeatures = new HashMap<>();
  File toFile = null;
  String offset_type = "j";
  List<JSON.Feature> addJSONFeatures = new ArrayList<>();

  public BdocDocumentBuilder() {
  }

  /**
   * Tell the builder to create the JsonDocument from a GATE document.
   * By default, this will include all features and all annotation sets,
   * but this can be changed later.
   * Alternately, the JsonDocument can be constructed by adding the parts
   * (text, document features, annotation sets) individually. 
   * 
   * @param doc the Gate document to build the BdocDocument/JSON from
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder fromGate(Document doc) {
    // TODO: for now check that Document is a SimpleDocument
    this.text = doc.getContent().toString(); 
    includedSets.put("", doc.getAnnotations());
    for (String name : doc.getNamedAnnotationSets().keySet()) {      
      includedSets.put(name, doc.getAnnotations(name));
    }
    BdocUtils.featureMap2Map(doc.getFeatures(), includedFeatures);
    return this;
  }
  
  /**
   * Add an annotation set. 
   * This does not check that the set comes from the same document as other
   * sets or the text, if added, it is up to the user to make sure that 
   * whatever is added here makes sense. 
   * 
   * Note: if a set with the same name is added more than once, the last 
   * addition is used.
   * 
   * @param name the name of the annotation set (this can differ from the 
   * original name if an annotation set is passed. Must not be null, the 
   * "default" set uses the empty string as name. 
   * @param annset a set of annotations, could be an AnnotationSet or a set
   * of annotations.
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder addSet(String name, Set<Annotation> annset) {
    includedSets.put(name, annset);
    return this;
  }
  
  /**
   * Add all features from the given (feature) map as document features.
   * 
   * The map can have keys and values of any type, but when adding features,
   * the following conversions are carried out: null keys are removed, 
   * any key that is not a String is converted to String. The value of all
   * features should be something that is directly JSON-serialisable, but this
   * is not checked. Note that some types can get serialised to JSON but 
   * will get converted to a different type when read back from JSON!
   * 
   * The user is responsible for making sure that value types work with the 
   * JSON default serialisation. 
   * 
   * If a feature has already been added previously, its old value is 
   * replaced. 
   * 
   * @param fm a map to interpret as a feature map
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder addFeatures(Map<Object,Object> fm) {
    BdocUtils.featureMap2Map(fm, includedFeatures);
    return this;
  }
  
  /**
   * Set/update the text of the document.
   * 
   * @param text the new document text
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setText(String text) {
    this.text = text;
    return this;
  }
  
  
  /**
   * Set the list of included AnnotationSet names.
   * 
   * All the names must already be registered to get added, otherwise an 
   * exception is thrown. 
   * This allows you to select the annotations to  actually use from 
   * a GATE document. 
   * @param names a collection of names to choose and use for the BdocDocument
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setAnnotationSetNames(Collection<String> names) {
    HashMap<String,Set<Annotation>> newSets = new HashMap<>();
    for (String tmpname : names) {
      if(includedSets.containsKey(tmpname)) {
        newSets.put(tmpname, includedSets.get(tmpname));
      } else {
        throw new GateRuntimeException("Cannot select annotation set "+
                tmpname+" because it does not exist");
      }
    }
    includedSets = newSets;
    return this;
  }
  
  
  /**
   * Set the list of included features.
   * 
   * All the names must already be registered to get added, otherwise an 
   * exception is thrown. 
   * This allows you to select the features to actually use from a GATE document. 
   * @param featurenames a collection of  feature names to choose and use for 
   * the BdocDocument
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder setFeatureNames(Collection<String> featurenames) {
    HashMap<String, Object> newFeatures = new HashMap<>();
    for (String tmpname : featurenames) {
      if(includedFeatures.containsKey(tmpname)) {
        newFeatures.put(tmpname, includedFeatures.get(tmpname));
      } else {
        throw new GateRuntimeException("Cannot select feature "+
                tmpname+" because it does not exist");
      }
    }
    includedFeatures = newFeatures;
    return this;    
  }
  
  /**
   * Add/set a single feature as a document feature.
   * 
   * @param name Feature name 
   * @param value Feature value
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder addFeature(String name, Object value) {
    includedFeatures.put(name, value);
    return this;
  }
  
  /**
   * Make the JsonDocument use java offsets (the default). 
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder javaOffsets() {
    offset_type = "j";
    return this;
  }
  
  /**
   * Make the JsonDocument use python/unicode codepoint offsets. 
   * This wil fix all the annotation offsets so they refer to unicode
   * code points instead of java utf16 code units. This will only work
   * if the text of the json document is set and compatible with the 
   * offsets of the added annotations, otherwise an exception is thrown. 
   * @return modified BdocDocumentBuilder
   */
  public BdocDocumentBuilder pythonOffsets() {
    offset_type = "p";
    return this;
  }
  
  
  public BdocDocumentBuilder withJSONFeature(JSON.Feature feature) {
    addJSONFeatures.add(feature);
    return this;
  }
  
  /**
   * Given all the info accumulated, build a JsonDocument and return it.
   * 
   * This adds a BdocDocument instance with all the information added
   * so far. 
   * 
   * @return the BdocDocument containing all the information added so far
   */
  public BdocDocument buildBdoc() {
    BdocDocument ret = new BdocDocument();
    System.err.println("DEBUG: text="+text);
    ret.text = text;
    if(includedFeatures.size() > 0) {
      System.err.println("DEBUG: feature="+includedFeatures);
      ret.features = includedFeatures;
    }
    if(includedSets.size() > 0) {
      HashMap<String, BdocAnnotationSet> annotation_sets = new HashMap<>();
      for(String name : includedSets.keySet()) {     
        System.err.println("DEBUG: included set="+name+" set="+includedSets.get(name));
        BdocAnnotationSet annset = new BdocAnnotationSet();
        annset.name = name;
        annset.annotations = new ArrayList<BdocAnnotation>();
        int max_annid = -1;
        for (Annotation ann : includedSets.get(name)) {
          BdocAnnotation bdocann = BdocAnnotation.fromGateAnnotation(ann);
          if(bdocann.id > max_annid) {
            max_annid = bdocann.id;
          }
          annset.annotations.add(bdocann);
        }
        annset.max_annid = max_annid;
        annotation_sets.put(annset.name, annset);
      }     
      ret.annotation_sets = annotation_sets;
    }
    // do any offset fixup, if necessary
    System.err.println("DEBUG: fixup="+offset_type);

    ret.fixupOffsets(offset_type);
    return ret;
  }
  
  /**
   * Create a JSON builder instance with the JSON Features passed so far.
   * 
   * @return JSON builder
   */
  public JSON initialJSON() {
    JSON jsonbuilder = JSON.std;
    for (JSON.Feature feature : addJSONFeatures) {
      jsonbuilder.with(feature);
    }
    jsonbuilder.with(JSON.Feature.HANDLE_JAVA_BEANS);
    jsonbuilder.with(JSON.Feature.FORCE_REFLECTION_ACCESS);
    jsonbuilder.with(JSON.Feature.WRITE_NULL_PROPERTIES);
    jsonbuilder.without(JSON.Feature.USE_FIELDS);
    
    return jsonbuilder;
  }
  
  /**
   * Serialise the BdocDocument built so far as JSON to the given File.
   * 
   * @param path where to write the JSON to
   */
  public void dump(File path) {
    try {
      BdocDocument jsondoc = buildBdoc();
      initialJSON().write(jsondoc, toFile);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON", ex);
    }
  }
  
  /**
   * Serialise the BdocDocument built so far as JSON to the given writer.
   * 
   * @param writer where to send the JSON to.
   */
  public void dump(Writer writer) {
    try {
      BdocDocument jsondoc = buildBdoc();
      initialJSON().write(jsondoc, writer);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON", ex);
    }
  }
  
  /**
   * Serialise the BdocDocument built so far as JSON to the given output stream.
   * 
   * @param ostream where to send the JSON to.
   */
  public void dump(OutputStream ostream) {
    try {
      BdocDocument jsondoc = buildBdoc();
      initialJSON().write(jsondoc, ostream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON", ex);
    }
  }
  
  
  /**
   * Serialise the BdocDocument built so far as JSON String.
   * 
   * @return the JSON representation of the BdocDocument.
   */
  public String dumps() {
    try {
      BdocDocument jsondoc = buildBdoc();
      return initialJSON().asString(jsondoc);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build JSON", ex);
    }
  }
  
}
