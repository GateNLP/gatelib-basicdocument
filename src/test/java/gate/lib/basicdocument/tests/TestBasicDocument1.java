package gate.lib.basicdocument.tests;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.Utils;
import gate.creole.ResourceInstantiationException;
import gate.lib.basicdocument.BdocAnnotationSet;
import gate.lib.basicdocument.BdocAnnotation;
import gate.lib.basicdocument.BdocDocument;
import gate.lib.basicdocument.BdocDocumentBuilder;
import gate.lib.basicdocument.docformats.SimpleJson;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.io.File;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * First set of simple tests.
 * @author Johann Petrak
 */
public class TestBasicDocument1 extends TestCase {
  String sampleText1 = "This is a simple 💩 document. It has two sentences.";
  
  @Override
  public void setUp() throws GateException {
    Gate.init();
  }
  
  @Test
  public void testBasic1() throws ResourceInstantiationException, InvalidOffsetException {
    // create a simple GATE document, convert to BdocDocument
    Document doc = Factory.newDocument(sampleText1);
    AnnotationSet defSet = doc.getAnnotations();
    defSet.add(0L, 4L, "Token", Utils.featureMap("string", "This"));
    defSet.add(6L, 8L, "Token", Utils.featureMap("string", "is"));
    defSet.add(17L, 19L, "Token", Utils.featureMap("string", "poo"));
    BdocDocument bdoc = new BdocDocumentBuilder()
            .fromGate(doc)
            .pythonOffsets()
            .buildBdoc();
    Assert.assertEquals(sampleText1, bdoc.text);
    BdocAnnotationSet bset = bdoc.annotation_sets.get("");
    Assert.assertEquals(3, bset.annotations.size());
    Assert.assertEquals((Integer)2, bset.max_annid);
    Assert.assertEquals("", bset.name);
    BdocAnnotation bann1 = new BdocAnnotation();  // suppress null pointer warning
    BdocAnnotation bann2 = bann1;
    BdocAnnotation bann3 = bann1;
    for (BdocAnnotation bann : bset.annotations) {
      Assert.assertNotNull(bann.id);
      Assert.assertTrue(bann.id >= 0);
      Assert.assertTrue(bann.id <= 2);
      if(null == bann.id) {
        bann3 = bann;
      } else switch (bann.id) {
        case 0:
          bann1 = bann;
          break;
        case 1:
          bann2 = bann;
          break;
        default:
          bann3 = bann;
          break;
      }
    }
    Assert.assertEquals(0, bann1.start);
    Assert.assertEquals(4, bann1.end);
    Assert.assertEquals("Token", bann1.type);
    Assert.assertEquals(6, bann2.start);
    Assert.assertEquals(8, bann2.end);
    Assert.assertEquals("Token", bann2.type);
    Assert.assertEquals(17, bann3.start);
    Assert.assertEquals(18, bann3.end);     // !!! one less because python!
    Assert.assertEquals("Token", bann3.type);
    
    String json = new SimpleJson().dumps(bdoc);
    new SimpleJson().dump(bdoc, new File("test-doc1.gate_sj"));
    
    
    // try to re-create Bdoc from JSON
    BdocDocument bdoc2 = new SimpleJson().loads_doc(json);
    Assert.assertNotNull(bdoc2.annotation_sets);
    System.err.println("annotation_sets: "+bdoc2.annotation_sets.getClass().getName());
    Assert.assertTrue(bdoc2.annotation_sets.getClass().getName().equals("java.util.HashMap"));
    Assert.assertTrue(bdoc2.annotation_sets.containsKey(""));
    BdocAnnotationSet bset2 = bdoc2.annotation_sets.get("");
    Assert.assertNotNull(bset2);
    System.err.println("the set: "+bset2.getClass().getName());
    Assert.assertTrue(bset2.getClass().getName().equals("gate.lib.basicdocument.BdocAnnotationSet"));
  }
  
  
}
