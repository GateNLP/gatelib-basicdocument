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
      if (k == null) {
        // we ignore null keys here, if we have any
        continue;
      }
      ret.put((k instanceof String) ? (String)k : k.toString(), fm.get(k));
    }
    return ret; 
  }
}
