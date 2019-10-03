package gate.lib.textdocument;

import org.apache.log4j.Logger;
import java.util.Map;

/**
 * Representation of a document as we convert it to JSON and back. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class TdocDocument
{

  private static final Logger LOGGER = 
          Logger.getLogger(TdocDocument.class.getName());

  // Fields used to represent the various parts of documents
  // NOTE: the value null for each of these fields gets interpreted depending
  // on the USE of the JsonDocument: if it is used as a full document, the 
  // corresponding "empty" values are used (e.g. an empty string for the 
  // document text). If used for updating etc. null values are ignored
  // 
  
  /**
   * Document features. 
   * Other than the feature map of a GATE document, only String keys are 
   * supported. Non strings in the original document get convert using toString()
   */
  public Map<String, Object> features;
  
  /**
   * Document text.
   */
  public String text;
  
  /**
   * Map from annotation set name to annotation set with that name. 
   */
  public Map<String, TdocAnnotationSet> annotation_sets;
  
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
    for(TdocAnnotationSet annset : annotation_sets.values()) {
      for(TdocAnnotation ann : annset.annotations) {
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
