package gate.lib.jsondocument;

import org.apache.log4j.Logger;
import java.util.Map;

/**
 * Representation of a document as we convert it to JSON and back. 
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class JsonDocument
{

  private static final Logger LOGGER = Logger.getLogger(JsonDocument.class.getName());

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
  public Map<String, JsonAnnotationSet> annotation_sets;
  
  /**
   * Indicates the style of offsets used in this document.
   * j=Java (number of UTF16 code units), p=Python (number of Unicode code 
   * points)
   */
  public String offset_type = "j";
}
