package gate.lib.textdocument;

import gate.Annotation;
import java.util.Map;

/**
 * Representation of an annotation within a set. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
class TdocAnnotation {
  
  public static TdocAnnotation fromGateAnnotation(Annotation ann) {
    TdocAnnotation ret = new TdocAnnotation();
    ret.type = ann.getType();
    ret.start = ann.getStartNode().getOffset().intValue();
    ret.end = ann.getEndNode().getOffset().intValue();
    ret.features = TdocUtils.featureMap2Map(ann.getFeatures(), null);
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
  
  /**
   * Type identifier.
   */
  public String gatenlp_type = "Annotation";
  
  
}
