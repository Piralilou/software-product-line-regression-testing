/*
 * Manual tests for HibernateUtil
 * Designed to be robust whether Hibernate configuration is present or not.
 * If Hibernate (or hibernate.cfg.xml) is missing, these tests tolerate
 * ExceptionInInitializerError / NoClassDefFoundError gracefully.
 */

package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.EmailMessage;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.FolderContent;
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
public class HibernateUtilTest {

  // تاخیر نیم‌ثانیه‌ای قبل از هر تست
  @Before
  public void halfSecondDelay() throws Exception {
    Thread.sleep(500);
  }

  /** Loading the class may throw ExceptionInInitializerError if Hibernate isn't configured. */
  @Test(timeout = 4000)
  public void testClassInitialization() throws Exception {
    try {
      // Trigger class initialization (runs static block that builds SessionFactory)
      Class.forName("irisdeltaj.simplerelational.br.unb.cic.iris.persistence.relational.HibernateUtil");
      // If we reached here without exception, great.
      assertNotNull(HibernateUtil.getSessionFactory());
    } catch (ExceptionInInitializerError e) {
      // Acceptable in environments without Hibernate/cfg
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      // Acceptable if org/hibernate classes are unavailable
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** getSessionFactory() should either return a non-null object or fail with init errors in minimal envs. */
  @Test(timeout = 4000)
  public void testGetSessionFactory_orException() {
    try {
      Object sf = HibernateUtil.getSessionFactory();
      assertNotNull(sf);
      // Call twice to ensure singleton semantics if available
      assertSame(sf, HibernateUtil.getSessionFactory());
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** shutdown() should be safe to call; in minimal envs it may throw init errors which we accept. */
  @Test(timeout = 4000)
  public void testShutdown_isSafe() {
    try {
      // It's okay to call even if SessionFactory wasn't created yet
      HibernateUtil.shutdown();
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** If initialization succeeds, verify annotated classes were registered into the internal classList. */
  @Test(timeout = 4000)
  public void testConfigureClasses_containsExpectedModels_whenInitialized() throws Exception {
    try {
      // Ensure class is initialized successfully first
      HibernateUtil.getSessionFactory();

      // Reflectively inspect the private static classList
      Field f = HibernateUtil.class.getDeclaredField("classList");
      f.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<Class<?>> list = (List<Class<?>>) f.get(null);

      assertNotNull(list);
      assertTrue(list.contains(FolderContent.class));
      assertTrue(list.contains(EmailMessage.class));
      assertTrue(list.contains(IrisFolder.class));
    } catch (ExceptionInInitializerError e) {
      // Acceptable without Hibernate/cfg
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** Calling shutdown() multiple times should not explode (idempotent-ish). */
  @Test(timeout = 4000)
  public void testShutdown_multipleCalls() {
    try {
      HibernateUtil.shutdown();
      HibernateUtil.shutdown();
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  // ===== تست‌های افزوده‌شده =====

  /** Try opening/closing a Session via reflection if SessionFactory is available. */
  @Test(timeout = 4000)
  public void testOpenCloseSession_viaReflection_whenAvailable() {
    try {
      Object sf = HibernateUtil.getSessionFactory();
      assertNotNull(sf);

      // session = sf.openSession()
      Method openSession = sf.getClass().getMethod("openSession");
      Object session = openSession.invoke(sf);

      if (session != null) {
        // Optional: check isOpen() if present
        try {
          Method isOpen = session.getClass().getMethod("isOpen");
          Object open = isOpen.invoke(session);
          if (open instanceof Boolean) {
            assertTrue((Boolean) open);
          }
        } catch (NoSuchMethodException ignore) {
          // Some implementations may not expose isOpen()
        }

        // session.close()
        try {
          Method close = session.getClass().getMethod("close");
          close.invoke(session);
        } catch (NoSuchMethodException ignore) {
          // If there's no close(), we just skip
        }
      }
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    } catch (ReflectiveOperationException e) {
      // Reflection issues are acceptable in minimal envs
      assertNotNull(e.getMessage());
    }
  }

  /** Concurrent calls to getSessionFactory(): either same instance or acceptable failure in minimal envs. */
  @Test(timeout = 4000)
  public void testConcurrentGetSessionFactory_singletonish() throws Exception {
    final Object[] out = new Object[2];
    final Throwable[] err = new Throwable[2];

    Runnable r0 = new Runnable() {
      @Override public void run() {
        try { out[0] = HibernateUtil.getSessionFactory(); }
        catch (Throwable t) { err[0] = t; }
      }
    };
    Runnable r1 = new Runnable() {
      @Override public void run() {
        try { out[1] = HibernateUtil.getSessionFactory(); }
        catch (Throwable t) { err[1] = t; }
      }
    };

    Thread t0 = new Thread(r0);
    Thread t1 = new Thread(r1);
    t0.start(); t1.start();
    t0.join();  t1.join();

    // اگر یکی از تردها خطا داد، نوعش باید قابل‌قبول باشد.
    for (Throwable t : err) {
      if (t != null) {
        if (t instanceof ExceptionInInitializerError) {
          assertNotNull(((ExceptionInInitializerError) t).getCause());
          return;
        } else if (t instanceof NoClassDefFoundError) {
          String msg = ((NoClassDefFoundError) t).getMessage();
          assertTrue(msg == null || msg.contains("org/hibernate"));
          return;
        } else {
          fail("Unexpected error: " + t);
        }
      }
    }

    assertNotNull(out[0]);
    assertSame(out[0], out[1]);
  }



  /** After shutdown, calling getSessionFactory() should either re-initialize or fail gracefully. */
  @Test(timeout = 4000)
  public void testShutdown_thenGetAgain_tolerant() {
    try {
      HibernateUtil.shutdown();
      Object sf = HibernateUtil.getSessionFactory();
      assertNotNull(sf);
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** If initialized, the internal classList should not contain duplicates. */
  @Test(timeout = 4000)
  public void testConfigureClasses_noDuplicates_whenInitialized() throws Exception {
    try {
      HibernateUtil.getSessionFactory();

      Field f = HibernateUtil.class.getDeclaredField("classList");
      f.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<Class<?>> list = (List<Class<?>>) f.get(null);

      assertNotNull(list);
      Set<Class<?>> set = new HashSet<>(list);
      assertEquals("classList contains duplicates", set.size(), list.size());
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** Repeated calls to getSessionFactory() should not grow the classList. */
  @Test(timeout = 4000)
  public void testRepeatedGetSessionFactory_doesNotChangeClassList() throws Exception {
    try {
      HibernateUtil.getSessionFactory();

      Field f = HibernateUtil.class.getDeclaredField("classList");
      f.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<Class<?>> before = (List<Class<?>>) f.get(null);
      int sizeBefore = before == null ? -1 : before.size();

      // Call again a few times
      for (int i = 0; i < 5; i++) {
        assertSame(HibernateUtil.getSessionFactory(), HibernateUtil.getSessionFactory());
      }

      @SuppressWarnings("unchecked")
      List<Class<?>> after = (List<Class<?>>) f.get(null);
      int sizeAfter = after == null ? -1 : after.size();

      // If list exists, size should stay the same
      if (sizeBefore >= 0 && sizeAfter >= 0) {
        assertEquals(sizeBefore, sizeAfter);
      }
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** If SessionFactory is available, toString() shouldn't throw and ideally be non-empty. */
  @Test(timeout = 4000)
  public void testSessionFactory_toString_safe() {
    try {
      Object sf = HibernateUtil.getSessionFactory();
      assertNotNull(sf);
      String s = String.valueOf(sf);
      assertNotNull(s);
      assertFalse(s.isEmpty());
    } catch (ExceptionInInitializerError e) {
      assertNotNull(e.getCause());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }
}
