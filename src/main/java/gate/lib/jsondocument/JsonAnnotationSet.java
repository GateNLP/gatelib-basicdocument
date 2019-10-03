package gate.lib.jsondocument;

import java.util.List;

/**
 * Representation of an annotaton set.
 * We use the name "set" though this representation is really just a list.
 * The order of annotations in the list is probably usually not relevant.
 *
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class JsonAnnotationSet {
  
  // Fields representing the various aspects of an annotation set
  
  /**
   * The annotation set name: the empty string corresponds to the "default
   * annotation set".
   */
  public String name; 
  
  public List<JsonAnnotation> annotations;
  
  // For partial annotation sets, contains the id to use for new
  // annotations. 
  public Integer max_annid; 
  
}
