package gate.lib.jsondocument;

import gate.Annotation;
import java.util.Map;

/**
 * Representation of an annotation within a set. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
class JsonAnnotation {
  
  public static JsonAnnotation fromGateAnnotation(Annotation ann) {
    JsonAnnotation ret = new JsonAnnotation();
    ret.type = ann.getType();
    ret.start = ann.getStartNode().getOffset().intValue();
    ret.end = ann.getEndNode().getOffset().intValue();
    ret.features = Utils.featureMap2Map(ann.getFeatures(), null);
    ret.id = ann.getId();
    return ret;
  }
  
  // Fields
  public Map<String, Object> features;
  
  public String type; 

  /**
   * The annotation id. 
   * If set, this id should get assigned to a newly created annotations. 
   * If null, assign the next free id from the containing annotation set.
   */
  public Integer id;
  
  /**
   * Start offset.
   */
  public int start;
  
  /**
   * End offset.
   */
  public int end;
  
  
}
