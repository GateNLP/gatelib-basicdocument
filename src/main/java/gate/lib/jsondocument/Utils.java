package gate.lib.jsondocument;

import gate.FeatureMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Mainly static helper functions.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class Utils {
  public static Map<String, Object> featureMap2Map(FeatureMap fm) {
    Map<String, Object> ret = new HashMap<>();
    for(Object k : fm.keySet()) {
      if (k instanceof String) {
        ret.put((String)k, fm.get(k));
      } else {
        ret.put(k.toString(), fm.get(k));
      }
    }
    return ret; 
  }
}
