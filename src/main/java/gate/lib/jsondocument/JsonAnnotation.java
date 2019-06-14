package gate.lib.jsondocument;

import java.util.Map;

/**
 * Representation of an annotation within a set. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
class JsonAnnotation {
  
  // Fields
  protected Map<String, Object> features;
  
  protected String type; 

  /**
   * The annotation id. 
   * If set, this id should get assigned to a newly created annotations. 
   */
  protected Integer id;
  
}
