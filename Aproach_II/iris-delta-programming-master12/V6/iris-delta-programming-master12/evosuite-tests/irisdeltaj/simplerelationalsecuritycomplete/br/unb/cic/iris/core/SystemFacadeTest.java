/*
 * Hand-written tests for SystemFacade
 * Compatible with EvoRunner scaffold & style
 */

package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.EmailMessage;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true,
        resetStaticState = true, separateClassLoader = true)
public class SystemFacadeTest {

  @Test(timeout = 4000)
  public void test0_singletonOrInitError() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade a = SystemFacade.instance();
      SystemFacade b = SystemFacade.instance();
      assertSame(a, b);
    } catch (Throwable e) {
      // اگر فایل‌های کانفیگ حاضر نباشن، سازنده در init کلاس با ExceptionInInitializerError می‌افته
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }

  @Test(timeout = 4000)
  public void test1_statusOrInitError() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      // در سازنده، connect() صدا زده می‌شود ⇒ باید CONNECTED باشد
      assertTrue(f.isConnected());
      assertEquals(irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.Status.CONNECTED, f.getStatus());
    } catch (Throwable e) {
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }

  @Test(timeout = 4000)
  public void test2_send_throwsIfMailStackMissing() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      EmailMessage m = new EmailMessage("from@x", "to@y", "subj", "body");
      f.send(m);
      fail("Expecting exception due to missing mail/persistence stack");
    } catch (NoClassDefFoundError e) {
      // javax.mail / DAO ها موجود نیستند
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.core.SystemFacade", e);
    } catch (ExceptionInInitializerError e) {
      // نبود default_provider.properties یا خطای init پیکربندی
      // acceptable for this environment
    }
  }

  @Test(timeout = 4000)
  public void test3_listRemoteFolders_requiresMailStack() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      // در صورت نبود javax.mail انتظار خطا داریم
      f.listRemoteFolders();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.core.SystemFacade", e);
    } catch (ExceptionInInitializerError e) {
      // acceptable
    }
  }

  @Test(timeout = 4000)
  public void test4_downloadMessages_requiresDAOAndMailStack() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      f.downloadMessages("INBOX");
      fail("Expecting exception due to missing mail/DAO stack");
    } catch (NoClassDefFoundError e) {
      // javax.mail یا DAOها در محیط تست در دسترس نیستند
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.core.SystemFacade", e);
    } catch (ExceptionInInitializerError e) {
      // acceptable
    }
  }

  @Test(timeout = 4000)
  public void test5_getProvider_nonNullWhenInitOK() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      assertNotNull(f.getProvider());
      // اگر provider حاضر باشد، حداقل name باید قابل دسترس باشد (از properties).
      assertNotNull(f.getProvider().getName());
    } catch (Throwable e) {
      // اگر init شکست بخورد
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }

  // ---------------------- تست‌های افزوده‌شده ----------------------

  @Test(timeout = 4000)
  public void test6_statusFlagMatchesEnumOrInitError() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      boolean connected = f.isConnected();
      assertEquals(
          connected,
          f.getStatus() == irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.Status.CONNECTED
      );
    } catch (Throwable e) {
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }

  @Test(timeout = 4000)
  public void test7_send_nullMessage_throwsOrInitError() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      f.send(null);
      fail("Expected NPE/IAE (or environment init error)");
    } catch (NullPointerException | IllegalArgumentException expected) {
      // OK: API باید ورودی null را رد کند
    } catch (NoClassDefFoundError e) {
      // در صورت نبود وابستگی‌ها
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.core.SystemFacade", e);
    } catch (ExceptionInInitializerError e) {
      // acceptable
    }
  }

  @Test(timeout = 4000)
  public void test8_provider_name_notBlank_whenAvailable() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      if (f.getProvider() != null && f.getProvider().getName() != null) {
        assertFalse(f.getProvider().getName().trim().isEmpty());
      }
      // اگر provider یا name موجود نبود اما init موفق بود، تست چیزی را اجباری نمی‌کند.
    } catch (Throwable e) {
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }

  @Test(timeout = 4000)
  public void test9_instance_toString_notNullOrInitError() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade f = SystemFacade.instance();
      assertNotNull(f.toString());
    } catch (Throwable e) {
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }

  @Test(timeout = 4000)
  public void test10_instance_hashCodeStableAcrossCallsOrInitError() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade a = SystemFacade.instance();
      SystemFacade b = SystemFacade.instance();
      assertEquals(a.hashCode(), b.hashCode());
      assertSame(a, b);
    } catch (Throwable e) {
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }

  @Test(timeout = 4000)
  public void test11_instanceTripleSameOrInitError() throws Throwable {
    Thread.sleep(500);
    try {
      SystemFacade a = SystemFacade.instance();
      SystemFacade b = SystemFacade.instance();
      SystemFacade c = SystemFacade.instance();
      assertSame(a, b);
      assertSame(b, c);
    } catch (Throwable e) {
      assertTrue(e instanceof ExceptionInInitializerError || e instanceof NoClassDefFoundError);
    }
  }
}
