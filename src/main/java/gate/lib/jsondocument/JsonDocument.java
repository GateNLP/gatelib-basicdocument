package gate.lib.jsondocument;

import java.util.List;
import org.apache.log4j.Logger;
import java.util.Map;

/**
 * Representation of a document as we convert it to JSON and back. 
 * This also has the API for accessing parts, finding which parts are present,
 * creating a proper GATE document from it or updating an existing GATE 
 * document with it. It also contains factory methods for creating a 
 * JsonDocument object from all or parts of a GATE document.
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
  protected Map<String, Object> features;
  
  protected String text;
  
  protected List<JsonAnnotationSet> annotationSets;
  
  protected Integer offset;
  protected Integer len;
  
  /**
   * Create an empty document with no text. 
   * Note that the text is immutable and cannot get changed later!
   */
  public JsonDocument() {
  
  }
  
  /**
   * Create a document with the given text and the default offset.
   * @param text the text of the document
   */
  public JsonDocument(String text) {
    this.text = text;
    this.offset = 0;
    this.len = text.length();
  }
  
  /**
   * Create a document with the given text and a offset and length.
   * The offset specifies where the given text comes from in some 
   * other unknown text, not the offset within the given text. 
   * The Text may be null, representing unknown text
   * 
   * @param text, may be null to represent an unknown text of the given length
   * @param offset the offset the text/annotations start at
   * @param length the length of the text/range of annotations
   */
  public JsonDocument(String text, int offset, int length) {
    if(text != null && length != text.length()) {
      throw new RuntimeException("If text is not null the length parameter must match the length of the text");
    }
  }
  
  
}
