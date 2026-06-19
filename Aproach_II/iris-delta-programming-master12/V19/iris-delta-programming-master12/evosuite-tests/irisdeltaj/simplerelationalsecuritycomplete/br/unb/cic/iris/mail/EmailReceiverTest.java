/*
 * دستی نوشته شده - هم‌سبک تست‌های EvoSuite
 */

package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.EmailReceiver;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.GmailProvider;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.OutlookProvider;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.YahooProvider;

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
public class EmailReceiverTest {

  // تاخیر نیم‌ثانیه‌ای قبل از اجرای هر تست تا با @Test(timeout = 4000) هم‌خوان باشد
  @Before
  public void halfSecondDelay() throws Exception {
    Thread.sleep(500L);
  }

  @Test(timeout = 4000)
  public void test00_constructor_gmail()  throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new GmailProvider(), "UTF-8");
    assertNotNull(receiver);
  }

  @Test(timeout = 4000)
  public void test01_constructor_outlook()  throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new OutlookProvider(), "UTF-8");
    assertNotNull(receiver);
  }

  @Test(timeout = 4000)
  public void test02_listFolders_throws_NoClassDefFoundError()  throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new GmailProvider(), "UTF-8");
    try {
      receiver.listFolders();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      // javax.mail در زمان اجرا موجود نیست
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test03_getMessages_bySeq_throws_NoClassDefFoundError()  throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new YahooProvider(), "UTF-8");
    try {
      receiver.getMessages("INBOX", 1);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test04_getMessages_range_throws_NoClassDefFoundError()  throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new OutlookProvider(), "UTF-8");
    try {
      receiver.getMessages("INBOX", 1, 10);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test05_getStore_throws_NoClassDefFoundError()  throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new GmailProvider(), "UTF-8");
    try {
      receiver.getStore(); // درونش createStoreAndConnect فراخوانی می‌شود
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test06_renew_throws_NoClassDefFoundError()  throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new YahooProvider(), "UTF-8");
    try {
      receiver.renew(); // چون store=null است، نهایتاً به getStore و javax.mail می‌رسد
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  // ---------- تست‌های افزوده‌شده ----------

  @Test(timeout = 4000)
  public void test07_constructor_yahoo() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new YahooProvider(), "UTF-8");
    assertNotNull(receiver);
  }

  @Test(timeout = 4000)
  public void test08_listFolders_twice_consistentNoClassDef() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new OutlookProvider(), "UTF-8");
    for (int i = 0; i < 2; i++) {
      try {
        receiver.listFolders();
        fail("Expecting exception: NoClassDefFoundError");
      } catch (NoClassDefFoundError e) {
        verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
      }
    }
  }

  @Test(timeout = 4000)
  public void test09_getMessages_emptyFolderName_throwsNoClassDef() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new GmailProvider(), "UTF-8");
    try {
      receiver.getMessages("", 5);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test10_getMessages_negativeRange_stillNoClassDef() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new YahooProvider(), "UTF-8");
    try {
      receiver.getMessages("INBOX", -10, -1);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test11_getMessages_startGreaterThanEnd_noClassDef() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new OutlookProvider(), "UTF-8");
    try {
      receiver.getMessages("INBOX", 100, 1);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test12_getStore_twice_consistentNoClassDef() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new GmailProvider(), "UTF-8");
    for (int i = 0; i < 2; i++) {
      try {
        receiver.getStore();
        fail("Expecting exception: NoClassDefFoundError");
      } catch (NoClassDefFoundError e) {
        verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
      }
    }
  }

  @Test(timeout = 4000)
  public void test13_renew_twice_consistentNoClassDef() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new YahooProvider(), "UTF-8");
    try {
      receiver.renew();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
    try {
      receiver.renew();
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }

  @Test(timeout = 4000)
  public void test14_constructor_withDifferentCharset_iso() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new OutlookProvider(), "ISO-8859-1");
    assertNotNull(receiver);
  }

  @Test(timeout = 4000)
  public void test15_getMessages_largeRange_noClassDef() throws Throwable {
    EmailReceiver receiver = new EmailReceiver(new GmailProvider(), "UTF-8");
    try {
      receiver.getMessages("INBOX", 1, 1000000);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailReceiver", e);
    }
  }
}
