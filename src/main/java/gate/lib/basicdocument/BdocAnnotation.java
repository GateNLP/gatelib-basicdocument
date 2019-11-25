package gate.lib.basicdocument;

import gate.Annotation;
import java.util.Map;

/**
 * Representation of an annotation within a set. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocAnnotation {
  
  /**
   * Create from a GATE annotation.
   * @param ann GATE annotation
   * @return BdocAnnotation
   */
  public static BdocAnnotation fromGateAnnotation(Annotation ann) {
    BdocAnnotation ret = new BdocAnnotation();
    ret.type = ann.getType();
    ret.start = ann.getStartNode().getOffset().intValue();
    ret.end = ann.getEndNode().getOffset().intValue();
    ret.features = BdocUtils.featureMap2Map(ann.getFeatures(), null);
    ret.id = ann.getId();
    return ret;
  }
  
  // Fields
  /**
   * BdocAnnotation features
   */
  public Map<String, Object> features;
  
  /**
   * BdocAnnotation type
   */
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
