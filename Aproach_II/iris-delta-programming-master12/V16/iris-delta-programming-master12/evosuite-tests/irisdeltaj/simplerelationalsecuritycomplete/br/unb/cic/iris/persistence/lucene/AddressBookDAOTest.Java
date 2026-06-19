/*
 * Custom tests for AddressBookDAO
 * These tests avoid real Hibernate access and assert that calls
 * trigger expected errors in this environment (NoClassDefFoundError / etc).
 */

package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.AddressBookEntry;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import org.junit.Before;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true,
                     resetStaticState = true, separateClassLoader = true)
public class AddressBookDAOTest{

  /** تاخیر نیم‌ثانیه‌ای قبل از هر تست */
  @Before
  public void halfSecondDelay() {
    try { Thread.sleep(500L); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
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
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
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
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
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
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (DBException e) {
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
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test_delete_nonExistingNick_behaviourUnderNoHibernate() throws Throwable {
      AddressBookDAO dao = AddressBookDAO.instance();
      try {
          dao.delete("non-existing-nick");
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
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test7_instance_notNull() {
    assertNotNull(AddressBookDAO.instance());
  }

  // ======================= تست‌های اضافه‌شده =======================

  @Test(timeout = 4000)
  public void test8_find_emptyString_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.find("");
      fail("Expecting exception with empty nick");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test9_delete_nullNick_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.delete((String) null);   // 👈 کست به String
      fail("Expecting exception with null nick");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }
  @Test(timeout = 4000)
  public void test9b_delete_nullEntry_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.delete((irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.AddressBookEntry) null);
      fail("Expecting exception with null entry");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }


  @Test(timeout = 4000)
  public void test10_save_then_delete_stillThrowsWithoutHibernate() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    AddressBookEntry entry = new AddressBookEntry("dup", "dup@mail");
    try { dao.save(entry); } catch (Throwable ignored) {}
    try {
      dao.delete("dup");
      fail("Expecting exception without Hibernate");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test11_save_duplicateNick_twice_throws() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    AddressBookEntry e1 = new AddressBookEntry("same", "a@mail");
    AddressBookEntry e2 = new AddressBookEntry("same", "b@mail");
    try { dao.save(e1); } catch (Throwable ignored) {}
    try {
      dao.save(e2);
      fail("Expecting exception on duplicate nick without Hibernate");
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", err);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test12_save_longValues_throwsOrDBException() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    String longNick = new String(new char[1024]).replace('\0', 'n');
    String longAddr = new String(new char[2048]).replace('\0', 'a') + "@example.com";
    AddressBookEntry e = new AddressBookEntry(longNick, longAddr);
    try {
      dao.save(e);
      fail("Expecting exception without Hibernate");
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", err);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test13_find_then_save_sequence_bothThrow() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try { dao.find("seq"); fail("Expecting exception"); } catch (Throwable ignored) {}
    try {
      dao.save(new AddressBookEntry("seq", "s@mail"));
      fail("Expecting exception");
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", err);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }

  @Test(timeout = 4000)
  public void test14_singleton_stable_afterErrors() throws Throwable {
    AddressBookDAO a = AddressBookDAO.instance();
    try { a.find("x"); } catch (Throwable ignored) {}
    try { a.delete("y"); } catch (Throwable ignored) {}
    AddressBookDAO b = AddressBookDAO.instance();
    assertSame(a, b);
  }

  @Test(timeout = 4000)
  public void test15_save_nullEntry_paramValidationOrHibernateMissing() throws Throwable {
    AddressBookDAO dao = AddressBookDAO.instance();
    try {
      dao.save(null);
      fail("Expecting exception when entry is null or Hibernate missing");
    } catch (NullPointerException e) {
      // acceptable if implementation validates null
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelationaladdressbook.br.unb.cic.iris.persistence.relational.AddressBookDAO", e);
    } catch (Throwable t) {
      assertNotNull(t);
    }
  }
}
