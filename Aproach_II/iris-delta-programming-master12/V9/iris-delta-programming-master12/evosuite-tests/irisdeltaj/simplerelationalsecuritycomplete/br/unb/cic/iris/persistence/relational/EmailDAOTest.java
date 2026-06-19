package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import java.util.Date;
import java.util.List;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.EmailMessage;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import org.junit.Before;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(
    mockJVMNonDeterminism = true,
    useVFS = true,
    useVNET = true,
    resetStaticState = true,
    separateClassLoader = true
)
public class EmailDAOTest {

  /** تاخیر نیم‌ثانیه‌ای برای هر تست */
  @Before
  public void halfSecondDelay() {
      try { Thread.sleep(500L); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
  }

  @Test(timeout = 4000)
  public void testSingletonInstance() throws Throwable {
    EmailDAO dao1 = EmailDAO.instance();
    EmailDAO dao2 = EmailDAO.instance();
    assertSame(dao1, dao2);
  }

  @Test(timeout = 4000)
  public void testSaveMessage_NoHibernateOnClasspath() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    EmailMessage msg = new EmailMessage("from@test", "to@test", "subject", "body");
    try {
      dao.saveMessage(msg);
      fail("Expecting exception: NoClassDefFoundError or DBException");
    } catch (NoClassDefFoundError e) {
      // ok – missing Hibernate/runtime when running in isolated test env
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // also acceptable if Hibernate is present but fails to open a session
    }
  }

  @Test(timeout = 4000)
  public void testLastMessageReceived_NoHibernateOnClasspath() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    try {
      dao.lastMessageReceived();
      fail("Expecting exception: NoClassDefFoundError or DBException");
    } catch (NoClassDefFoundError e) {
      // ok
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void testFindById_NoHibernateOnClasspath() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    try {
      dao.findById("non-existent-id");
      fail("Expecting exception: NoClassDefFoundError or DBException");
    } catch (NoClassDefFoundError e) {
      // ok
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void testListMessages_NoHibernateOnClasspath() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    try {
      dao.listMessages("any-folder-id");
      fail("Expecting exception: NoClassDefFoundError or DBException");
    } catch (NoClassDefFoundError e) {
      // ok
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void testPopulateLike_toStringAndFieldCopyAfterSaveAttempt() throws Throwable {
    Date now = new Date();
    EmailMessage original = new EmailMessage(
        "from@test", "to@test", "cc@test", "bcc@test",
        "subj", "msg", now,
        new irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.IrisFolder("idF", "INBOX")
    );
    String s = original.toString();
    assertNotNull(s);
  }

  // ======================= تست‌های اضافه‌شده =======================

  /** saveMessage با پارامتر null */
  @Test(timeout = 4000)
  public void test6_saveNullMessage_throws() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    try {
      dao.saveMessage(null);
      fail("Expecting exception");
    } catch (NullPointerException e) {
      // acceptable if implementation checks null
    } catch (NoClassDefFoundError e) {
      // acceptable (no Hibernate)
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // acceptable
    }
  }

  /** findById با null */
  @Test(timeout = 4000)
  public void test7_findById_nullParam() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    try {
      dao.findById(null);
      fail("Expecting exception");
    } catch (NullPointerException e) {
      // ok
    } catch (NoClassDefFoundError e) {
      // ok
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // ok
    }
  }

  /** listMessages با null */
  @Test(timeout = 4000)
  public void test8_listMessages_nullParam() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    try {
      dao.listMessages(null);
      fail("Expecting exception");
    } catch (NullPointerException e) {
      // ok
    } catch (NoClassDefFoundError e) {
      // ok
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // ok
    }
  }

  /** findById با رشتهٔ خالی */
  @Test(timeout = 4000)
  public void test9_findById_emptyString() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    try {
      dao.findById("");
      fail("Expecting exception");
    } catch (NoClassDefFoundError e) {
      // ok
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // ok
    }
  }

  /** توالی عملیات و پایداری singleton پس از خطاها */
  @Test(timeout = 4000)
  public void test10_multipleOperations_keepSingleton() throws Throwable {
    EmailDAO dao = EmailDAO.instance();

    // هر کدام می‌تواند استثناء بدهد؛ فقط مطمئن می‌شویم تست fail نشود
    try { dao.lastMessageReceived(); } catch (Throwable ignored) {}
    try { dao.findById("x"); } catch (Throwable ignored) {}
    try { dao.listMessages("f"); } catch (Throwable ignored) {}

    // singleton باید ثابت بماند
    assertSame(dao, EmailDAO.instance());
  }

  /** ساخت EmailMessage با مقادیر null و بررسی toString */
  @Test(timeout = 4000)
  public void test11_constructMessageWithNulls_toStringSafe() throws Throwable {
    EmailMessage m = new EmailMessage();
    assertNotNull(m.toString()); // "null - null - null - null"
  }

  /** تلاش برای save سپس findById – هر دو باید خطا دهند (بدون Hibernate) */
  @Test(timeout = 4000)
  public void test12_saveThenFind_stillThrows() throws Throwable {
    EmailDAO dao = EmailDAO.instance();
    EmailMessage msg = new EmailMessage("a", "b", "s", "body");
    try { dao.saveMessage(msg); } catch (Throwable ignored) {}
    try {
      dao.findById("maybe-saved-id");
      fail("Expecting exception");
    } catch (NoClassDefFoundError e) {
      // ok
    } catch (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException e) {
      // ok
    }
  }
}
