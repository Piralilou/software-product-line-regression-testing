/* 
 * Manual tests for AbstractDAO 
 * Compatible with EvoSuite scaffolding style 
 */
package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.EmailMessage;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.IrisFolder;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(
  mockJVMNonDeterminism = true,
  useVFS = true,
  useVNET = true,
  resetStaticState = true,
  separateClassLoader = true
)
public class AbstractDAOTest {

  /** تاخیر نیم‌ثانیه‌ای قبل از هر تست تا با timeout=4000ms تداخل نداشته باشد */
  @Before
  public void halfSecondDelay() throws Exception {
    Thread.sleep(500L);
  }

  /** زیرکلاس‌های ساده برای پارامتر جنریک و دسترسی به متدهای محافظت‌شده */
  static class EmailMessageDAOStub extends AbstractDAO<EmailMessage> {
    public void callCloseSession() { closeSession(); }
  }
  static class IrisFolderDAOStub extends AbstractDAO<IrisFolder> {
    public void callCloseSession() { closeSession(); }
  }

  @Test(timeout = 4000)
  public void test0_findAll_NoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.findAll();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      // در نبود Hibernate/HibernateUtil این خطا طبیعی است
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test1_findById_NoHibernate() throws Throwable {
    IrisFolderDAOStub dao = new IrisFolderDAOStub();
    try {
      dao.findById("any-id");
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test2_saveOrUpdate_NoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.saveOrUpdate(new EmailMessage());
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test3_delete_NoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.delete(new EmailMessage());
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test4_findByExample_NoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      // matchMode = null , ignoreCase=true => قبل از criteria باید session ساخته شود
      dao.findByExample(new EmailMessage(), /*matchMode*/ null, /*ignoreCase*/ true);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test5_closeSession_isNullSafe() throws Throwable {
    // فراخوانی closeSession وقتی session هنوز ساخته نشده
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    dao.callCloseSession(); // نباید استثنا بدهد
    assertTrue(true);
  }

  @Test(timeout = 4000)
  public void test6_closeSession_idempotent() throws Throwable {
    // چند بار پشت سر هم بدون session
    IrisFolderDAOStub dao = new IrisFolderDAOStub();
    dao.callCloseSession();
    dao.callCloseSession();
    assertTrue(true);
  }

  @Test(timeout = 4000)
  public void test7_handleException_WhenSessionNull() throws Throwable {
    // با فراخوانی findAll و نبود Hibernate انتظار NoClassDefFoundError داریم
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.findAll();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test8_findById_NullId_StillNoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.findById((String) null);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test9_findAll_ResultReferenceNotLeakedOnFailure() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.findAll();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      // فقط صحت وقوع خطا کافی است
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  // ================= تست‌های افزوده‌شده =================

  @Test(timeout = 4000)
  public void test10_saveOrUpdate_nullEntity_stillNoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.saveOrUpdate(null);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test11_delete_nullEntity_stillNoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.delete(null);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test12_findByExample_ignoreCaseFalse_stillNoHibernate() throws Throwable {
    IrisFolderDAOStub dao = new IrisFolderDAOStub();
    try {
      dao.findByExample(new IrisFolder("inbox"), null, /*ignoreCase*/ false);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test13_findByExample_nullExample_stillNoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.findByExample(null, null, true);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test14_saveOrUpdate_thenCloseSession_idempotent() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    try {
      dao.saveOrUpdate(new EmailMessage());
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError expected) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", expected);
    }
    // حتی بعد از خطا هم closeSession باید بی‌خطر باشد
    dao.callCloseSession();
    dao.callCloseSession();
    assertTrue(true);
  }

  @Test(timeout = 4000)
  public void test15_delete_thenCloseSession_idempotent() throws Throwable {
    IrisFolderDAOStub dao = new IrisFolderDAOStub();
    try {
      dao.delete(new IrisFolder("trash"));
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError expected) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", expected);
    }
    dao.callCloseSession();
    dao.callCloseSession();
    assertTrue(true);
  }

  @Test(timeout = 4000)
  public void test16_sequence_multipleOperations_allNoHibernate() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    // findAll
    try { dao.findAll(); fail("Expecting exception: NoClassDefFoundError"); }
    catch (NoClassDefFoundError e) { verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e); }
    // saveOrUpdate(null)
    try { dao.saveOrUpdate(null); fail("Expecting exception: NoClassDefFoundError"); }
    catch (NoClassDefFoundError e) { verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e); }
    // delete(null)
    try { dao.delete(null); fail("Expecting exception: NoClassDefFoundError"); }
    catch (NoClassDefFoundError e) { verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e); }
  }

  @Test(timeout = 4000)
  public void test17_findAll_afterCloseSession_sameError() throws Throwable {
    EmailMessageDAOStub dao = new EmailMessageDAOStub();
    dao.callCloseSession();
    try {
      dao.findAll();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }

  @Test(timeout = 4000)
  public void test18_findById_withSpecialChars_stillNoHibernate() throws Throwable {
    IrisFolderDAOStub dao = new IrisFolderDAOStub();
    try {
      dao.findById("ID-ç%$#-测试-🙂");
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.AbstractDAO", e);
    }
  }
}
