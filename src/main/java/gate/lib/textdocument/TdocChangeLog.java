/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lib.textdocument;

import java.util.List;
import java.util.Map;

/**
 * Something that represents changes to a document.
 * @author johann
 */
public class TdocChangeLog {
  public List<Map<String, Object>> changes;
  // TODO!
  /**
   * Type identifier.
   */
  public String gatenlp_type = "ChangeLog";
}

