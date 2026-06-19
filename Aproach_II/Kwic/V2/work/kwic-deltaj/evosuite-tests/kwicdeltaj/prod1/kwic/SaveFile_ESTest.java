/*
 * Extended tests for SaveFile (no duplicates)
 */

package kwicdeltaj.prod1.kwic;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.evosuite.runtime.testdata.EvoSuiteFile;
import org.evosuite.runtime.testdata.FileSystemHandling;
import org.junit.runner.RunWith;
import org.junit.BeforeClass;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(
    mockJVMNonDeterminism = true,
    useVFS = true,
    useVNET = true,
    resetStaticState = true,
    separateClassLoader = true
)
public class SaveFile_ESTest extends SaveFile_ESTest_scaffolding {

  // --- Added: one-time small delay (~4.5s) before running any test in this class
  @BeforeClass
  public static void slowDownSuite() throws Exception {
      Thread.sleep(4500L);
  }

  /** Helper: read shifts.html without depending on FileSystemHandling.readFile */
  private String readHtml() throws Exception {
      EvoSuiteFile f = new EvoSuiteFile("shifts.html");
      // EvoSuiteFile فقط getPath داره
      java.io.File real = new java.io.File(f.getPath());
      byte[] bytes = java.nio.file.Files.readAllBytes(real.toPath());
      return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
  }

  // ======= Original EvoSuite tests (kept, no duplicates) =======
  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      SaveFile saveFile0 = new SaveFile();
      EvoSuiteFile evoSuiteFile0 = new EvoSuiteFile("shifts.html");
      FileSystemHandling.createFolder(evoSuiteFile0);
      LinkedList<StringStorage> linkedList0 = new LinkedList<StringStorage>();
      saveFile0.save(linkedList0);
      assertEquals(0, linkedList0.size());
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      LinkedList<StringStorage> linkedList0 = new LinkedList<StringStorage>();
      SaveFile saveFile0 = new SaveFile();
      StringStorage stringStorage0 = new StringStorage();
      linkedList0.add(stringStorage0);
      saveFile0.save(linkedList0);
      assertEquals(1, linkedList0.size());
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      SaveFile saveFile0 = new SaveFile();
      try {
        saveFile0.save((List<StringStorage>) null);
        fail("Expecting exception: NullPointerException");
      } catch(NullPointerException e) {
         verifyException("kwicdeltaj.prod1.kwic.SaveFile", e);
      }
  }

  // ======= Extra tests (unique names) =======

  /** Test StringStorage for custom content */
  static class TestStringStorage extends StringStorage {
      private final String left, keyword, right;
      TestStringStorage(String left, String keyword, String right) {
          this.left = left;
          this.keyword = keyword;
          this.right = right;
      }
      @Override public String getLeft() { return left; }
      @Override public String getKeyword() { return keyword; }
      @Override public String getRight() { return right; }
  }

  @Test(timeout = 4000)
  public void test3_save_createsHtmlSkeleton_whenEmptyList() throws Throwable {
      SaveFile saveFile = new SaveFile();
      LinkedList<StringStorage> items = new LinkedList<StringStorage>();

      saveFile.save(items);

      String html = readHtml();
      assertNotNull(html);
      assertTrue(html.contains("<!DOCTYPE html>"));
      assertTrue(html.contains("<title>KEYWORD IN CONTEXT</title>"));
      assertTrue(html.contains("<body>"));
      assertTrue(html.contains("</html>"));
  }

  @Test(timeout = 4000)
  public void test4_save_writesOneEntry_centered() throws Throwable {
      SaveFile saveFile = new SaveFile();
      LinkedList<StringStorage> items = new LinkedList<StringStorage>();
      items.add(new TestStringStorage("L", "K", "R"));

      saveFile.save(items);

      String html = readHtml();
      assertTrue(html.contains("<center>R <b>K</b> L</center>"));
      assertTrue(html.contains("<br/>"));
  }

  @Test(timeout = 4000)
  public void test5_save_overwrites_notAppends() throws Throwable {
      SaveFile saveFile = new SaveFile();

      LinkedList<StringStorage> first = new LinkedList<StringStorage>();
      first.add(new TestStringStorage("L1", "K1", "R1"));
      saveFile.save(first);
      String html1 = readHtml();
      assertTrue(html1.contains("K1"));

      LinkedList<StringStorage> second = new LinkedList<StringStorage>();
      saveFile.save(second);
      String html2 = readHtml();
      assertFalse(html2.contains("K1"));
      assertTrue(html2.contains("<body>"));
  }

  @Test(timeout = 4000)
  public void test6_save_handlesUnicodePersian() throws Throwable {
      SaveFile saveFile = new SaveFile();
      LinkedList<StringStorage> items = new LinkedList<StringStorage>();
      items.add(new TestStringStorage("سمت چپ", "کلیدواژه", "سمت راست"));

      saveFile.save(items);

      String html = readHtml();
      assertTrue(html.contains("سمت چپ"));
      assertTrue(html.contains("کلیدواژه"));
      assertTrue(html.contains("سمت راست"));
      assertTrue(html.contains("<meta charset=\"UTF-8\">"));
  }

  @Test(timeout = 4000)
  public void test7_save_manyEntries_noCrash() throws Throwable {
      SaveFile saveFile = new SaveFile();
      LinkedList<StringStorage> items = new LinkedList<StringStorage>();
      for (int i = 0; i < 1000; i++) {
          items.add(new TestStringStorage("L"+i, "K"+i, "R"+i));
      }

      saveFile.save(items);

      String html = readHtml();
      assertTrue(html.contains("<center>R0 <b>K0</b> L0</center>"));
      assertTrue(html.contains("<center>R999 <b>K999</b> L999</center>"));
  }
}
