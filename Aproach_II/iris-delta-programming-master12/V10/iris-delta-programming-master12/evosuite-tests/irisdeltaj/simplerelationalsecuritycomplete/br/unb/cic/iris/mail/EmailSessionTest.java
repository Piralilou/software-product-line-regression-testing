/*
 * Manual tests for EmailSession
 * Compatible with EvoSuite scaffolding style
 */

package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.provider.OutlookProvider;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.mail.EmailProvider;

import java.util.Properties;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import org.junit.Before;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(
    mockJVMNonDeterminism = true,
    useVFS = true, useVNET = true,
    resetStaticState = true,
    separateClassLoader = true
)
public class EmailSessionTest  {

  /** تاخیر نیم‌ثانیه‌ای قبل از هر تست */
  @Before
  public void halfSecondDelay() {
      try { Thread.sleep(500L); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
  }

  /** سناریو: provider نال -> NPE در سازنده */
  @Test(timeout = 4000)
  public void test0_constructorWithNullProvider() throws Throwable {
    try {
      new EmailSession((EmailProvider) null);
      fail("Expecting exception: NullPointerException");
    } catch (NullPointerException e) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", e);
    }
  }

  /** سناریو: provider واقعی (Outlook) – در نبود JavaMail انتظار NoClassDefFoundError */
  @Test(timeout = 4000)
  public void test1_constructorWithOutlookProvider() throws Throwable {
    OutlookProvider provider = new OutlookProvider("user@outlook.com", "secret");
    try {
      new EmailSession(provider);
      EmailSession s = new EmailSession(provider, EmailClient.CHARACTER_ENCODING);
      assertNotNull(s.getSession());
      assertSame(provider, s.getProvider());
      assertEquals(EmailClient.CHARACTER_ENCODING, s.getEncoding());
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** سناریو: سازنده با encoding سفارشی */
  @Test(timeout = 4000)
  public void test2_constructorWithCustomEncoding() throws Throwable {
    OutlookProvider provider = new OutlookProvider("u", "p");
    try {
      EmailSession s = new EmailSession(provider, "UTF-16");
      assertEquals("UTF-16", s.getEncoding());
      assertSame(provider, s.getProvider());
      assertNotNull(s.getSession());
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** سناریو: provider سفارشی بدون احراز هویت (isAuthenticationEnabled=false) */
  @Test(timeout = 4000)
  public void test3_constructorWithNoAuthProvider() throws Throwable {
    EmailProvider noAuthProvider = new DummyNoAuthProvider();
    try {
      EmailSession s = new EmailSession(noAuthProvider);
      assertSame(noAuthProvider, s.getProvider());
      assertNotNull(s.getSession());
      assertEquals(EmailClient.CHARACTER_ENCODING, s.getEncoding());
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** سناریو: متدهای ConnectionListener باید safe باشند حتی با پارامتر null (در صورت ساخت موفق نمونه) */
  @Test(timeout = 4000)
  public void test4_connectionListenerMethodsAreNoop() throws Throwable {
    EmailProvider noAuthProvider = new DummyNoAuthProvider();
    try {
      EmailSession s = new EmailSession(noAuthProvider);
      s.opened(null);
      s.disconnected(null);
      s.closed(null);
      assertTrue(true);
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** سناریو: provider با Properties خالی؛ بررسی رفتار سازنده */
  @Test(timeout = 4000)
  public void test5_constructorWithEmptyPropsProvider() throws Throwable {
    EmailProvider emptyPropsProvider = new EmailProvider() {
      private final Properties props = new Properties();
      public Properties getProperties() { return props; }
      public boolean isAuthenticationEnabled() { return false; }
      public EmailProvider clone() throws CloneNotSupportedException { return this; }
      public String getName() { return "empty"; }
      public String getDescription() { return "empty"; }
      public String getUsername() { return null; }
      public String getPassword() { return null; }
      public void setUsername(String u) {}
      public void setPassword(String p) {}
      public String getTransportProtocol() { return "smtp"; }
      public String getStoreProtocol() { return "imap"; }
      public String getTransportHost() { return "smtp.example.com"; }
      public int getTransportPort() { return 25; }
      public String getStoreHost() { return "imap.example.com"; }
      public int getStorePort() { return 143; }
    };
    try {
      EmailSession s = new EmailSession(emptyPropsProvider);
      assertNotNull(s.getSession());
      assertSame(emptyPropsProvider, s.getProvider());
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  // ======================= تست‌های اضافه‌شده =======================

  /** provider با احراز هویت روشن و اعتبارنامه‌ها */
  @Test(timeout = 4000)
  public void test6_constructorWithAuthProvider() throws Throwable {
    EmailProvider authProvider = new EmailProvider() {
      private final Properties props = new Properties();
      { props.setProperty("mail.smtps.auth", "true"); }
      public Properties getProperties() { return props; }
      public boolean isAuthenticationEnabled() { return true; }
      public EmailProvider clone() { return this; }
      public String getName() { return "auth"; }
      public String getDescription() { return "auth provider"; }
      public String getUsername() { return "user"; }
      public String getPassword() { return "pass"; }
      public void setUsername(String u) {}
      public void setPassword(String p) {}
      public String getTransportProtocol() { return "smtps"; }
      public String getStoreProtocol() { return "imaps"; }
      public String getTransportHost() { return "smtp.example.com"; }
      public int getTransportPort() { return 465; }
      public String getStoreHost() { return "imap.example.com"; }
      public int getStorePort() { return 993; }
    };
    try {
      EmailSession s = new EmailSession(authProvider);
      assertNotNull(s.getSession());
      assertSame(authProvider, s.getProvider());
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** انتشار پراپرتی سفارشی به Session (اگر JavaMail حاضر باشد) */
  @Test(timeout = 4000)
  public void test7_customPropertyPropagatesToSession() throws Throwable {
    EmailProvider provider = new DummyNoAuthProvider();
    provider.getProperties().setProperty("custom.flag", "on");
    try {
      EmailSession s = new EmailSession(provider);
      assertEquals("on", s.getSession().getProperties().getProperty("custom.flag"));
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** سازنده با encoding = null: یا null می‌ماند یا به پیش‌فرض EmailClient برمی‌گردد */
  @Test(timeout = 4000)
  public void test8_constructorWithNullEncoding() throws Throwable {
    EmailProvider provider = new DummyNoAuthProvider();
    try {
      EmailSession s = new EmailSession(provider, null);
      String enc = s.getEncoding();
      assertTrue(enc == null || EmailClient.CHARACTER_ENCODING.equals(enc));
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** ساخت چند سشن برای یک provider – سشن‌ها نباید یک آبجکت واحد باشند (اگر در دسترس) */
  @Test(timeout = 4000)
  public void test9_multipleSessionsAreDistinct() throws Throwable {
    EmailProvider provider = new DummyNoAuthProvider();
    try {
      EmailSession s1 = new EmailSession(provider);
      EmailSession s2 = new EmailSession(provider);
      assertNotNull(s1.getSession());
      assertNotNull(s2.getSession());
      assertNotSame(s1.getSession(), s2.getSession());
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** listenerها پس از ساخت با provider احراز هویت‌دار هم نباید استثنا دهند */
  @Test(timeout = 4000)
  public void test10_connectionListeners_noThrow_withAuthProvider() throws Throwable {
    EmailProvider authProvider = new EmailProvider() {
      private final Properties props = new Properties();
      public Properties getProperties() { return props; }
      public boolean isAuthenticationEnabled() { return true; }
      public EmailProvider clone() { return this; }
      public String getName() { return "auth2"; }
      public String getDescription() { return "auth2"; }
      public String getUsername() { return "u"; }
      public String getPassword() { return "p"; }
      public void setUsername(String u) {}
      public void setPassword(String p) {}
      public String getTransportProtocol() { return "smtp"; }
      public String getStoreProtocol() { return "imap"; }
      public String getTransportHost() { return "smtp.example.com"; }
      public int getTransportPort() { return 25; }
      public String getStoreHost() { return "imap.example.com"; }
      public int getStorePort() { return 143; }
    };
    try {
      EmailSession s = new EmailSession(authProvider);
      s.opened(null);
      s.disconnected(null);
      s.closed(null);
      assertTrue(true);
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** EmailSession نباید پراپرتی‌های provider را دستکاری کند */
  @Test(timeout = 4000)
  public void test11_providerPropertiesRemainUntouched() throws Throwable {
    EmailProvider provider = new DummyNoAuthProvider();
    provider.getProperties().setProperty("sentinel", "1");
    try {
      EmailSession s = new EmailSession(provider);
      assertEquals("1", provider.getProperties().getProperty("sentinel"));
      assertNotNull(s.getSession());
    } catch (NoClassDefFoundError err) {
      verifyException("irisdeltaj.simplerelational.br.unb.cic.iris.mail.EmailSession", err);
    }
  }

  /** Provider مینیمال بدون احراز هویت */
  static class DummyNoAuthProvider implements EmailProvider {
    private final Properties props = new Properties();
    public DummyNoAuthProvider() {
      props.setProperty("mail.debug", "false");
    }
    @Override public Properties getProperties() { return props; }
    @Override public boolean isAuthenticationEnabled() { return false; }
    @Override public EmailProvider clone() throws CloneNotSupportedException { return this; }
    @Override public String getName() { return "dummy"; }
    @Override public String getDescription() { return "dummy no-auth"; }
    @Override public String getUsername() { return null; }
    @Override public String getPassword() { return null; }
    @Override public void setUsername(String username) {}
    @Override public void setPassword(String password) {}
    @Override public String getTransportProtocol() { return "smtp"; }
    @Override public String getStoreProtocol() { return "imap"; }
    @Override public String getTransportHost() { return "smtp.example.com"; }
    @Override public int getTransportPort() { return 25; }
    @Override public String getStoreHost() { return "imap.example.com"; }
    @Override public int getStorePort() { return 143; }
  }
}
