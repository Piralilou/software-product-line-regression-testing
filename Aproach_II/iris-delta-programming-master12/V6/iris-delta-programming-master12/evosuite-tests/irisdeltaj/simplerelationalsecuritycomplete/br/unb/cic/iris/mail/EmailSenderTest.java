/*
 * دستی نوشته شده - هم‌سبک تست‌های EvoSuite
 */
package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.EmailMessage;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.EmailMessageValidationException;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.GmailProvider;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.OutlookProvider;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.YahooProvider;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.EmailException;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

import java.util.MissingResourceException;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(
    mockJVMNonDeterminism = true,
    useVFS = true,
    useVNET = true,
    resetStaticState = true,
    separateClassLoader = true
)
public class EmailSenderTest{

  /** تاخیر نیم‌ثانیه‌ای قبل از اجرای هر تست */
  @Before
  public void delay() throws Exception {
    Thread.sleep(500);
  }

  // ---------- Helpers ----------
  private static EmailMessage minimalValidMessage() {
    EmailMessage m = new EmailMessage();
    m.setFrom("from@example.com");
    m.setTo("to@example.com");
    m.setSubject("s");
    m.setMessage("b");
    return m;
  }

  private static void expectSendToFail(EmailSender sender, EmailMessage m) {
    try {
      sender.send(m);
      fail("Expected failure at send()");
    } catch (NoClassDefFoundError e) {
      // javax.mail کلاس‌ها موجود نیستند
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSender", e);
    } catch (EmailException e) {
      // JavaMail هست اما اتصال/ارسال شکست خورده
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.core.exception.EmailException", e);
    } catch (MissingResourceException e) {
      // ResourceBundle پیام‌ها موجود نیست
      verifyException("java.util.ResourceBundle", e);
    }
  }

  /** پذیرش هر نوع خطای قابل‌انتظار در مسیر ارسال (بدون JavaMail) */
  private static void assertGracefulTransportFailure(Throwable t) {
    if (t instanceof NoClassDefFoundError) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSender", (NoClassDefFoundError) t);
    } else if (t instanceof EmailException) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.core.exception.EmailException", (EmailException) t);
    } else if (t instanceof MissingResourceException) {
      verifyException("java.util.ResourceBundle", (MissingResourceException) t);
    } else {
      fail("Unexpected exception type: " + t);
    }
  }

  // ---------- Existing Tests ----------

  @Test(timeout = 4000)
  public void test_send_gmail_fails_gracefully() {
    EmailSender sender = new EmailSender(new GmailProvider(), "UTF-8");
    expectSendToFail(sender, minimalValidMessage());
  }

  @Test(timeout = 4000)
  public void test_send_outlook_fails_gracefully() {
    EmailSender sender = new EmailSender(new OutlookProvider(), "UTF-8");
    expectSendToFail(sender, minimalValidMessage());
  }

  @Test(timeout = 4000)
  public void test_send_yahoo_fails_gracefully() {
    EmailSender sender = new EmailSender(new YahooProvider(), "UTF-8");
    expectSendToFail(sender, minimalValidMessage());
  }

  // تست‌های اعتبارسنجی (مسیر بدون ورود به JavaMail)
  @Test(timeout = 4000)
  public void test_validation_nullMessage_hasErrors() {
    assertFalse(EmailSender.validateEmailMessage(null).isEmpty());
  }

  @Test(timeout = 4000)
  public void test_validation_missingFrom_hasErrors() {
    EmailMessage m = new EmailMessage();
    m.setTo("to@example.com");
    m.setSubject("subj");
    m.setMessage("body");
    assertFalse(EmailSender.validateEmailMessage(m).isEmpty());
  }

  @Test(timeout = 4000)
  public void test_validation_okWhenFromSet() {
    EmailMessage m = new EmailMessage();
    m.setFrom("from@example.com");
    assertTrue(EmailSender.validateEmailMessage(m).isEmpty());
  }

  @Test(timeout = 4000)
  public void test00_validate_nullMessage_returnsError() {
    assertFalse(EmailSender.validateEmailMessage(null).isEmpty());
  }

  @Test(timeout = 4000)
  public void test01_validate_missingFrom_returnsError() {
    EmailMessage m = new EmailMessage();
    m.setFrom(null); // ست نکردن from
    assertFalse(EmailSender.validateEmailMessage(m).isEmpty());
  }

  @Test(timeout = 4000)
  public void test02_validate_withFromOnly_returnsEmptyList() {
    EmailMessage m = new EmailMessage();
    m.setFrom("sender@example.com");
    assertTrue(EmailSender.validateEmailMessage(m).isEmpty());
  }

  @Test(timeout = 4000)
  public void test04_send_gmail_path_NoClassDefFoundError_whenJavaMailMissing() throws Throwable {
    EmailSender sender = new EmailSender(new GmailProvider(), "UTF-8");
    EmailMessage m = new EmailMessage();
    m.setFrom("from@example.com");
    m.setTo("to@example.com");
    m.setSubject("hello");
    m.setMessage("body");
    try {
      sender.send(m);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSender", e);
    }
  }

  @Test(timeout = 4000)
  public void test05_send_outlook_path_NoClassDefFoundError_whenJavaMailMissing() throws Throwable {
    EmailSender sender = new EmailSender(new OutlookProvider(), "UTF-8");
    EmailMessage m = new EmailMessage();
    m.setFrom("from@outlook.com");
    m.setTo("to@outlook.com");
    m.setSubject("s");
    m.setMessage("b");
    try {
      sender.send(m);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSender", e);
    }
  }

  @Test(timeout = 4000)
  public void test06_send_yahoo_path_NoClassDefFoundError_whenJavaMailMissing() throws Throwable {
    EmailSender sender = new EmailSender(new YahooProvider(), "UTF-8");
    EmailMessage m = new EmailMessage();
    m.setFrom("from@yahoo.com");
    m.setTo("to@yahoo.com");
    m.setSubject("s");
    m.setMessage("b");
    try {
      sender.send(m);
      fail("Expecting exception: NoClassDefFoundError");
    } catch (NoClassDefFoundError e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSender", e);
    }
  }

  @Test(timeout = 4000)
  public void test07_constructor_only() {
    EmailSender s1 = new EmailSender(new GmailProvider(), "UTF-8");
    EmailSender s2 = new EmailSender(new OutlookProvider(), "UTF-8");
    EmailSender s3 = new EmailSender(new YahooProvider(), "UTF-8");
    assertNotNull(s1);
    assertNotNull(s2);
    assertNotNull(s3);
  }

  // -----------------------
  // تست‌های اضافه‌شده
  // -----------------------

  @Test(timeout = 4000)
  public void test08_send_missingFrom_throwsValidationException() {
    EmailSender sender = new EmailSender(new GmailProvider(), "UTF-8");
    EmailMessage m = new EmailMessage();
    m.setTo("to@example.com");
    m.setSubject("subject");
    m.setMessage("body");
    try {
      sender.send(m);
      fail("Expected EmailMessageValidationException due to missing from");
    } catch (EmailMessageValidationException expected) {
      // OK
    } catch (Throwable other) {
      fail("Expected EmailMessageValidationException, got: " + other);
    }
  }

  @Test(timeout = 4000)
  public void test09_validate_fullMessage_hasNoErrors() {
    EmailMessage m = minimalValidMessage();
    assertTrue(EmailSender.validateEmailMessage(m).isEmpty());
  }

  @Test(timeout = 4000)
  public void test11_validate_doesNotMutateMessage() {
    EmailMessage m = new EmailMessage();
    m.setFrom("from@example.com");
    m.setTo("to@example.com");
    m.setSubject("s");
    m.setMessage("b");

    // snapshot
    String f = m.getFrom(), t = m.getTo(), s = m.getSubject(), b = m.getMessage();

    EmailSender.validateEmailMessage(m);

    assertEquals(f, m.getFrom());
    assertEquals(t, m.getTo());
    assertEquals(s, m.getSubject());
    assertEquals(b, m.getMessage());
  }

  @Test(timeout = 4000)
  public void test12_multipleSends_failConsistently_gmail() {
    EmailSender sender = new EmailSender(new GmailProvider(), "UTF-8");
    EmailMessage m = minimalValidMessage();

    for (int i = 0; i < 2; i++) {
      try {
        sender.send(m);
        fail("Expected failure at send() attempt " + i);
      } catch (Throwable t) {
        assertGracefulTransportFailure(t);
      }
    }
  }

  @Test(timeout = 4000)
  public void test13_send_longSubject_stillFailsGracefully() {
    EmailSender sender = new EmailSender(new YahooProvider(), "UTF-8");
    EmailMessage m = minimalValidMessage();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5000; i++) sb.append('X'); // عنوان بسیار طولانی
    m.setSubject(sb.toString());

    try {
      sender.send(m);
      fail("Expected transport failure");
    } catch (Throwable t) {
      assertGracefulTransportFailure(t);
    }
  }

  @Test(timeout = 4000)
  public void test14_validate_emptyFrom_hasErrors() {
    EmailMessage m = new EmailMessage();
    m.setFrom("");
    assertFalse(EmailSender.validateEmailMessage(m).isEmpty());
  }

  @Test(timeout = 4000)
  public void test15_send_emptyFrom_throwsValidationException() {
    EmailSender sender = new EmailSender(new GmailProvider(), "UTF-8");
    EmailMessage m = new EmailMessage();
    m.setFrom("");
    m.setTo("to@example.com");
    m.setSubject("s");
    m.setMessage("b");
    try {
      sender.send(m);
      fail("Expected EmailMessageValidationException for empty from");
    } catch (EmailMessageValidationException expected) {
      // OK
    } catch (Throwable other) {
      fail("Expected EmailMessageValidationException, got: " + other);
    }
  }
}
