package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail;

import org.junit.Test;
import org.junit.Before;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.EmailException;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*; // در صورت نبود EvoSuite می‌توان حذفش کرد
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.GmailProvider;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.EmailMessage;

import java.util.List;

public class EmailClientTest{

  // تاخیر نیم‌ثانیه‌ای قبل از هر تست
  @Before
  public void halfSecondDelay() throws Exception {
      Thread.sleep(500);
  }

  @Test(timeout = 4000)
  public void test0_constantEncoding() {
    assertEquals("UTF-8", EmailClient.CHARACTER_ENCODING);
  }

  @Test(timeout = 4000)
  public void test1_constructorDefaultEncoding() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    assertNotNull(client);
  }

  @Test(timeout = 4000)
  public void test2_constructorCustomEncoding() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider, "ISO-8859-1");
    assertNotNull(client);
  }

  @Test(timeout = 4000)
  public void test3_validateEmailMessage_valid_shouldBeEmpty() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);

    EmailMessage m = new EmailMessage("from@x.com", "to@y.com", "cc@z.com", "", "subject", "body");
    List<String> errors = client.validateEmailMessage(m);
    assertNotNull(errors);
    assertTrue("Expected no validation errors for a well-formed message", errors.isEmpty());
  }

  @Test(timeout = 4000)
  public void test4_validateEmailMessage_missingTo_shouldReportErrors() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);

    EmailMessage m = new EmailMessage("from@x.com", null, null, null, "subject", "body");
    List<String> errors = client.validateEmailMessage(m);
    assertNotNull(errors);
    assertFalse("Expected validation errors when 'to' is missing", errors.isEmpty());
  }

  @Test(timeout = 4000)
  public void test5_validateEmailMessage_missingSubjectAndBody_shouldReportErrors() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);

    EmailMessage m = new EmailMessage(
        "from@x.com", "to@y.com",
        "subject", "body",
        (java.util.Date) null, (irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.IrisFolder) null
    );
    List<String> errors = client.validateEmailMessage(m);
    assertNotNull(errors);
    assertFalse("Expected validation errors when subject/body are missing", errors.isEmpty());
  }

  @Test(timeout = 4000)
  public void test6_listFolders_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.listFolders();
      fail("Expecting exception: NoClassDefFoundError or EmailException");
    } catch (NoClassDefFoundError | EmailException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test7_getMessagesByFolder_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.getMessages("INBOX");
      fail("Expecting exception: NoClassDefFoundError or EmailException");
    } catch (NoClassDefFoundError | EmailException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test8_getMessagesBySeqnum_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.getMessages("INBOX", 1);
      fail("Expecting exception: NoClassDefFoundError or EmailException");
    } catch (NoClassDefFoundError | EmailException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test9_getMessagesByRange_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.getMessages("INBOX", 1, 10);
      fail("Expecting exception: NoClassDefFoundError or EmailException");
    } catch (NoClassDefFoundError | EmailException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test10_send_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    EmailMessage m = new EmailMessage("from@x.com", "to@y.com", null, null, "s", "b");
    try {
      client.send(m);
      fail("Expecting exception: NoClassDefFoundError or EmailException");
    } catch (NoClassDefFoundError | EmailException e) {
      // ok
    }
  }

  // ===== تست‌های افزوده‌شده =====

  @Test(timeout = 4000)
  public void test11_validateEmailMessage_invalidAddresses_shouldReportErrors() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);

    EmailMessage m = new EmailMessage("from-at-x.com", "to-at-y.com", "bad-cc", "bad-bcc", "subject", "body");
    List<String> errors = client.validateEmailMessage(m);
    assertNotNull(errors);
    assertFalse("Expected validation errors for invalid email addresses", errors.isEmpty());
  }



  @Test(timeout = 4000)
  public void test13_getMessages_nullFolder_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.getMessages((String) null);
      fail("Expecting exception");
    } catch (NoClassDefFoundError | EmailException | IllegalArgumentException | NullPointerException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test14_getMessages_emptyFolder_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.getMessages("");
      fail("Expecting exception");
    } catch (NoClassDefFoundError | EmailException | IllegalArgumentException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test15_getMessages_negativeSeq_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.getMessages("INBOX", -1);
      fail("Expecting exception");
    } catch (NoClassDefFoundError | EmailException | IllegalArgumentException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test16_getMessages_invalidRange_startGreaterThanEnd_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.getMessages("INBOX", 10, 1);
      fail("Expecting exception");
    } catch (NoClassDefFoundError | EmailException | IllegalArgumentException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test17_send_nullMessage_throwsExpected() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    try {
      client.send(null);
      fail("Expecting exception");
    } catch (NoClassDefFoundError | EmailException | NullPointerException | IllegalArgumentException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test18_listFolders_twice_throwsExpectedAgain() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    for (int i = 0; i < 2; i++) {
      try {
        client.listFolders();
        fail("Expecting exception on call #" + (i + 1));
      } catch (NoClassDefFoundError | EmailException e) {
        // ok
      }
    }
  }

  @Test(timeout = 4000)
  public void test19_send_missingTo_shouldThrow() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);
    EmailMessage m = new EmailMessage("from@x.com", null, null, null, "s", "b");
    try {
      client.send(m);
      fail("Expecting exception");
    } catch (NoClassDefFoundError | EmailException | IllegalArgumentException e) {
      // ok
    }
  }

  @Test(timeout = 4000)
  public void test20_validateEmailMessage_longSubjectAndBody_stillHandled() {
    GmailProvider provider = new GmailProvider();
    EmailClient client = new EmailClient(provider);

    String longSubject = new String(new char[1024]).replace('\0', 'S');
    String longBody = new String(new char[4096]).replace('\0', 'B');

    EmailMessage m = new EmailMessage("from@x.com", "to@y.com", null, null, longSubject, longBody);
    List<String> errors = client.validateEmailMessage(m);
    assertNotNull(errors); // درست بودن یا نبودن را به پیاده‌سازی می‌سپاریم، فقط عدم NPE مهم است
  }
}
