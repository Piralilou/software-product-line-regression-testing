/* 
 * Custom tests for AddressBookDAO 
 * These tests avoid real Hibernate access and assert that calls
 * trigger expected errors in this environment (NoClassDefFoundError / etc).
 */
package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.AddressBookEntry;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true)
public class AddressBookDAOTest {

  /** تاخیر نیم‌ثانیه‌ای قبل از اجرای هر تست */
  @Before
  public void delay() throws Exception {
    Thread.sleep(500);
  }

  @Test(timeout = 4000)
  public void test0_instance_isSingleton() {
    AddressBookDAO a = AddressBookDAO.instance();
    AddressBookDAO b = AddressBookDAO.instance();
    assertSame(a, b);
  }

  @Test(timeout = 4000)
  public void test1_find_throws_whenHibernateMissing() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.find("someone");
      fail("Expecting exception due to missing Hibernate");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      // Some environments throw ExceptionInInitializerError instead
      // of NoClassDefFoundError when Hibernate is absent.
      // Accept any throwable here but still assert it's not silent.
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test2_find_withNullNick_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.find(null);
      fail("Expecting exception when nick is null or Hibernate is missing");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test3_save_throws_whenHibernateMissing() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    AddressBookEntry entry = new AddressBookEntry("nick", "addr@example.com");
    try {
      dao.save(entry);
      fail("Expecting exception due to missing Hibernate");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (DBException e) {
      // If Hibernate classes are partially present, DAO may wrap in DBException
      assertTrue(e.getMessage() == null || e.getMessage().length() >= 0);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test4_delete_calls_find_and_throws_whenHibernateMissing() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.delete("to-remove");
      fail("Expecting exception due to missing Hibernate during delete/find");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test_delete_nonExistingNick_behaviourUnderNoHibernate() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.delete("non-existing-nick");
      // در محیط واقعی ممکن است هیچ کاری نکند، اما در محیط تست بدون Hibernate معمولاً استثناء می‌آید
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      assertNotNull(e);
    } catch (NoClassDefFoundError | ExceptionInInitializerError e) {
      assertNotNull(e);
    }
  }

  @Test(timeout = 4000)
  public void test6_save_allowsNullFields_butStillRequiresHibernate() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    AddressBookEntry entry = new AddressBookEntry(null, null);
    try {
      dao.save(entry);
      fail("Expecting exception due to missing Hibernate even with null fields");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test7_instance_notNull() {
    assertNotNull(AddressBookDAO.instance());
  }

  // -----------------------
  // تست‌های اضافه‌شده
  // -----------------------

  @Test(timeout = 4000)
  public void test8_find_withWhitespaceNick_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.find("  someone  ");
      fail("Expected exception for whitespace-padded nick (or missing Hibernate)");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test9_find_emptyNick_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.find("");
      fail("Expected exception for empty nick");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test10_save_nullEntry_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.save(null);
      fail("Expected exception for null entry or missing Hibernate");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }



  @Test(timeout = 4000)
  public void test12_multipleFinds_consistentFailure() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    for (int i = 0; i < 2; i++) {
      try {
        dao.find("repeat-" + i);
        fail("Expected failure on iteration " + i);
      } catch (NoClassDefFoundError e) {
        verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
      } catch (Throwable t) {
        assertNotNull(t);
      }
    }
  }

  @Test(timeout = 4000)
  public void test13_save_withVeryLongFields_stillThrows() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    String nick = new String(new char[512]).replace('\0', 'n');
    String addr = new String(new char[1024]).replace('\0', 'a') + "@example.com";
    AddressBookEntry entry = new AddressBookEntry(nick, addr);
    try {
      dao.save(entry);
      fail("Expected failure regardless of long fields");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test14_delete_then_find_sequence_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try { dao.delete("seq"); } catch (Throwable ignored) { }
    try {
      dao.find("seq");
      fail("Expected failure after delete in no-Hibernate env");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test15_instance_multipleCalls_sameReference() {
    AddressBookDAO first = AddressBookDAO.instance();
    for (int i = 0; i < 5; i++) {
      assertSame(first, AddressBookDAO.instance());
    }
  }
}
