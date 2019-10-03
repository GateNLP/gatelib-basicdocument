/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.jsondocument;

import com.fasterxml.jackson.jr.ob.JSON;
import gate.Annotation;
import gate.Document;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for building a JsonDocument.
 * 
 * This allows building the document from a GATE document or from scratch,
 * adding individual parts or removing existing parts and returning the 
 * document or immediately serialising and optionally writing it. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class JsonDocumentBuilder {
  
  String text;
  Map<String,Set<Annotation>> includedSets = new HashMap<>();
  Map<String, Object> includedFeatures = new HashMap<String, Object>();
  File toFile = null;
  String offset_type = "j";
  List<JSON.Feature> addJSONFeatures = new ArrayList<>();

  public JsonDocumentBuilder() {
  }

  /**
   * Tell the builder to create the JsonDocument from a GATE document.
   * By default, this will include all features and all annotation sets,
   * but this can be changed later.
   * Alternately, the JsonDocument can be constructed by adding the parts
   * (text, document features, annotation sets) individualy. 
   * @param doc
   * @return 
   */
  public JsonDocumentBuilder fromGate(Document doc) {
    // TODO: for now check that Document is a SimpleDocument
    this.text = doc.getContent().toString(); 
    includedSets.put("", doc.getAnnotations());
    for (String name : doc.getNamedAnnotationSets().keySet()) {      
      includedSets.put(name, doc.getAnnotations(name));
    }
    Utils.featureMap2Map(doc.getFeatures(), includedFeatures);
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
   * @param set a set of annotations, could be an AnnotationSet or a set
   * of annotations.
   * @return 
   */
  public JsonDocumentBuilder addSet(String name, Set<Annotation> annset) {
    includedSets.put(name, annset);
    return this;
  }
  
  /**
   * Add all features from the given (feature) map.
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
   * @return 
   */
  public JsonDocumentBuilder addFeatures(Map<Object,Object> fm) {
    Utils.featureMap2Map(fm, includedFeatures);
    return this;
  }
  
  /**
   * Add/set a single feature.
   * @param name
   * @param value
   * @return 
   */
  public JsonDocumentBuilder addFeature(String name, Object value) {
    includedFeatures.put(name, value);
    return this;
  }
  
  /**
   * Make the JsonDocument use java offsets (the default). 
   * @return 
   */
  public JsonDocumentBuilder javaOffsets() {
    offset_type = "j";
    return this;
  }
  
  /**
   * Make the JsonDocument use python/unicode codepoint offsets. 
   * This wil fix all the annotation offsets so they refer to unicode
   * code points instead of java utf16 code units. This will only work
   * if the text of the json document is set and compatible with the 
   * offsets of the added annotations, otherwise an exception is thrown. 
   * @return 
   */
  public JsonDocumentBuilder pythonOffsets() {
    offset_type = "p";
    return this;
  }
  
  
  public JsonDocumentBuilder withJSONFeature(JSON.Feature feature) {
    addJSONFeatures.add(feature);
    return this;
  }
  
  /**
   * Given all the info accumulated, build a JsonDocument and return it.
   * @return 
   */
  private JsonDocument buildJsonDoc() {
    JsonDocument ret = new JsonDocument();
    ret.text = text;
    if(includedFeatures.size() > 0) {
      ret.features = includedFeatures;
    }
    if(includedSets.size() > 0) {
      Map<String, JsonAnnotationSet> annotation_sets = new HashMap<>();
      for(String name : includedSets.keySet()) {        
        JsonAnnotationSet annset = new JsonAnnotationSet();
        annset.name = name;
        annset.annotations = new ArrayList<JsonAnnotation>();
        int max_annid = -1;
        for (Annotation ann : includedSets.get(name)) {
          JsonAnnotation jsonann = JsonAnnotation.fromGateAnnotation(ann);
          if(jsonann.id > max_annid) {
            max_annid = jsonann.id;
          }
          annset.annotations.add(jsonann);
        }
        annset.max_annid = max_annid;
        annotation_sets.put(annset.name, annset);
      }      
    }
    // do any offset fixup, if necessary
    ret.fixupOffsets(offset_type);
    return ret;
  }
  
  // Various methods to create the final JSON representation
  private JSON initialJSON() {
    JSON jsonbuilder = JSON.std;
    for (JSON.Feature feature : addJSONFeatures) {
      jsonbuilder.with(feature);
    }
    return jsonbuilder;
  }
  
  public void toFile(File path) {
    try {
      JsonDocument jsondoc = buildJsonDoc();
      initialJSON().composeTo(toFile);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON", ex);
    }
  }
  
  public void toWriter(Writer writer) {
    try {
      JsonDocument jsondoc = buildJsonDoc();
      initialJSON().composeTo(writer);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON", ex);
    }
  }
  
  public void toOutputStream(OutputStream ostream) {
    try {
      JsonDocument jsondoc = buildJsonDoc();
      initialJSON().composeTo(ostream);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build and save JSON", ex);
    }
  }
    public String asString() {
    try {
      JsonDocument jsondoc = buildJsonDoc();
      return initialJSON().asString(jsondoc);
    } catch (IOException ex) {
      throw new RuntimeException("Could not build JSON", ex);
    }
  }
  
}
