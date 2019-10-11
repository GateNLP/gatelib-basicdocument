package gate.lib.basicdocument;

import org.apache.log4j.Logger;
import java.util.Map;

/**
 * A very basic representation of all the components of a GATE document.
 * 
 * This is a very basic POJO representation of a GATE document and 
 * this representation also corresponds to the external JSON representation
 * of GATE documents in Python. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocDocument
{

  private static final Logger LOGGER = 
          Logger.getLogger(BdocDocument.class.getName());

  /**
   * Document features. 
   * Other than the feature map of a GATE document, only String keys are 
   * supported. Non strings in the original document get convert using toString()
   * If there are no features, this may be null.
   */
  public Map<String, Object> features;
  
  /**
   * Document text.
   * 
   */
  public String text;
  
  /**
   * Map from annotation set name to annotation set with that name. 
   */
  public Map<String, BdocAnnotationSet> annotation_sets;
  
  /**
   * Indicates the style of offsets used in this document.
   * j=Java (number of UTF16 code units), p=Python (number of Unicode code 
   * points)
   */
  public String offset_type = "j";
  
  /**
   * Change all the annotation offsets to the required type (java/python).
   * This only works if the text of the document is set and all annotations
   * do have compatible offsets, otherwise a RuntimeException is thrown. 
   * @param newtype the target offset type
   */
  public void fixupOffsets(String newtype) {
    if(offset_type.equals(newtype)) {
      return;
    }
    if(annotation_sets == null || annotation_sets.isEmpty()) { 
      return;
    }
    if(text == null) {
      throw new RuntimeException("Fixing offsets only possible if the text is known");
    }
    // create the offset mapper
    OffsetMapper om = new OffsetMapper(this.text);
    // go through all annotation sets and all annotations and fix them
    for(BdocAnnotationSet annset : annotation_sets.values()) {
      for(BdocAnnotation ann : annset.annotations) {
        if(newtype == "p") {
          ann.start = om.convertToPython(ann.start);
          ann.end = om.convertToPython(ann.end);
        } else {
          ann.start = om.convertToJava(ann.start);
          ann.end = om.convertToJava(ann.end);          
        }
      }
    }
  }
  
  
  
}
