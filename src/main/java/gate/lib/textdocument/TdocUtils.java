package gate.lib.textdocument;

import gate.FeatureMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Mainly static helper functions.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class TdocUtils {
  /**
   * Add features from the original feature map into the given map or a new map.
   * 
   * This either copies over features from the feature map into the given map
   * or if the map is null, creates a new map that gets the featres.
   * If a feature is of type String, it is copied over as is, otherwise
   * the feature is converted to String using its toString method. 
   * 
   * @param fm  the feature map from which to add features
   * @param map target map, if null will create a new map.
   * @return The given map filled with the features or a new map.
   */
  public static Map<String, Object> featureMap2Map(
          Map<Object, Object> fm, Map<String, Object> map) {
    Map<String, Object> ret;
    if(map == null) {
      ret = new HashMap<>();
    } else {
      ret = map;
    }
    for(Object k : fm.keySet()) {
      if (k == null) {
        // we ignore null keys here, if we have any
        continue;
      }
      ret.put((k instanceof String) ? (String)k : k.toString(), fm.get(k));
    }
    return ret; 
  }
}
