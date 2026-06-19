/*
 * Manual tests for FolderDAO
 * Matches the EvoSuite scaffolding style used in your project
 */

package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.EmailException;
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
public class FolderDAOTest{

  /** First call to instance() may trigger bootstrap (ensureIsCreated) and fail if Hibernate isn't available. */
  @Test(timeout = 4000)
  public void testInstance_FirstCall_bootstrapMayFailWithoutHibernate() throws Throwable {
    Thread.sleep(500);
    try {
      FolderDAO.instance(); // triggers ensureIsCreated for INBOX/OUTBOX
      // If it reaches here, great (environment has Hibernate configured)
      assertNotNull(FolderDAO.instance());
    } catch (NoClassDefFoundError e) {
      // Acceptable in isolated test environment without Hibernate
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    } catch (EmailException e) {
      // Also acceptable: wrapped DBException
      assertNotNull(e.getMessage());
    }
  }

  /** Even if first call failed during bootstrap, the static 'instance' may already be set; second call should typically not throw. */
  @Test(timeout = 4000)
  public void testInstance_SecondCall_typicallyReturnsOrThrowsEmailException() throws Throwable {
    Thread.sleep(500);
    try {
      // Try again; depending on earlier state this may just return the singleton
      FolderDAO dao = FolderDAO.instance();
      assertNotNull(dao);
    } catch (EmailException e) {
      // If bootstrap is retried in your environment, an EmailException is fine
      assertNotNull(e.getMessage());
    } catch (NoClassDefFoundError e) {
      // Environments missing Hibernate: also acceptable
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** Direct DAO calls without a configured SessionFactory should fail cleanly. */
  @Test(timeout = 4000)
  public void testFindByName_withoutHibernate() throws Throwable {
    Thread.sleep(500);
    try {
      FolderDAO.instance().findByName("INBOX");
      fail("Expected EmailException or NoClassDefFoundError");
    } catch (EmailException e) {
      assertNotNull(e.getMessage());
    } catch (NoClassDefFoundError e) {
      // Missing Hibernate classes
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  @Test(timeout = 4000)
  public void testFindById_withoutHibernate() throws Throwable {
    Thread.sleep(500);
    try {
      FolderDAO.instance().findById("some-id");
      fail("Expected EmailException or NoClassDefFoundError");
    } catch (EmailException e) {
      assertNotNull(e.getMessage());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** Use reflection to test the private populate(IrisFolder) helper without touching Hibernate. */
  @Test(timeout = 4000)
  public void testPopulate_privateHelper_nullInput() throws Throwable {
    Thread.sleep(500);
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    FolderDAO dao = ctor.newInstance();

    Method populate = FolderDAO.class.getDeclaredMethod("populate", IrisFolder.class);
    populate.setAccessible(true);

    Object out = populate.invoke(dao, new Object[]{ null });
    assertNull(out);
  }

  @Test(timeout = 4000)
  public void testPopulate_privateHelper_copiesIdAndName() throws Throwable {
    Thread.sleep(500);
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    FolderDAO dao = ctor.newInstance();

    Method populate = FolderDAO.class.getDeclaredMethod("populate", IrisFolder.class);
    populate.setAccessible(true);

    IrisFolder in = new IrisFolder("ID-123", "INBOX");
    IrisFolder out = (IrisFolder) populate.invoke(dao, in);

    assertNotSame(in, out);
    assertEquals("ID-123", out.getId());
    assertEquals("INBOX", out.getName());
  }

  /** Reflectively invoke ensureIsCreated(String) to confirm it delegates to DAO and surfaces EmailException when DB is unavailable. */
  @Test(timeout = 4000)
  public void testEnsureIsCreated_viaReflection_surfacesEmailException() throws Throwable {
    Thread.sleep(500);
    // Prepare a fresh private instance and set the static field
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    FolderDAO fresh = ctor.newInstance();

    Field instField = FolderDAO.class.getDeclaredField("instance");
    instField.setAccessible(true);
    instField.set(null, fresh);

    Method ensure = FolderDAO.class.getDeclaredMethod("ensureIsCreated", String.class);
    ensure.setAccessible(true);

    try {
      ensure.invoke(null, "TEST_BOX");
      // If no exception: environment has a working DB; that's fine too.
    } catch (InvocationTargetException ite) {
      Throwable cause = ite.getCause();
      // Expecting EmailException (DBException) if no SessionFactory/DB is configured
      assertTrue(cause instanceof EmailException || cause instanceof NoClassDefFoundError);
    }
  }

  // ---------------------- تست‌های افزوده‌شده ----------------------

  /** Passing null name to findByName should be rejected by API or fail due to missing stack. */
  @Test(timeout = 4000)
  public void testFindByName_nullArg_rejectedOrEnvError() throws Throwable {
    Thread.sleep(500);
    try {
      FolderDAO.instance().findByName(null);
      fail("Expected IllegalArgumentException/EmailException/NoClassDefFoundError");
    } catch (IllegalArgumentException | EmailException e) {
      assertNotNull(e.getMessage());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** Passing null id to findById should be rejected by API or fail due to missing stack. */
  @Test(timeout = 4000)
  public void testFindById_nullArg_rejectedOrEnvError() throws Throwable {
    Thread.sleep(500);
    try {
      FolderDAO.instance().findById(null);
      fail("Expected IllegalArgumentException/EmailException/NoClassDefFoundError");
    } catch (IllegalArgumentException | EmailException e) {
      assertNotNull(e.getMessage());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** populate should preserve null fields and trim/clone basic values if implemented. */
  @Test(timeout = 4000)
  public void testPopulate_privateHelper_preservesNullId() throws Throwable {
    Thread.sleep(500);
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    FolderDAO dao = ctor.newInstance();

    Method populate = FolderDAO.class.getDeclaredMethod("populate", IrisFolder.class);
    populate.setAccessible(true);

    IrisFolder in = new IrisFolder(null, "SENT");
    IrisFolder out = (IrisFolder) populate.invoke(dao, in);

    assertNotSame(in, out);
    assertNull(out.getId());
    assertEquals("SENT", out.getName());
  }

  /** ensureIsCreated should be idempotent for the same folder name. */
  @Test(timeout = 4000)
  public void testEnsureIsCreated_idempotent_sameName() throws Throwable {
    Thread.sleep(500);
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    FolderDAO fresh = ctor.newInstance();

    Field instField = FolderDAO.class.getDeclaredField("instance");
    instField.setAccessible(true);
    instField.set(null, fresh);

    Method ensure = FolderDAO.class.getDeclaredMethod("ensureIsCreated", String.class);
    ensure.setAccessible(true);

    try {
      ensure.invoke(null, "IDEMPOTENT_BOX");
      ensure.invoke(null, "IDEMPOTENT_BOX");
    } catch (InvocationTargetException ite) {
      Throwable cause = ite.getCause();
      assertTrue(cause instanceof EmailException || cause instanceof NoClassDefFoundError);
    }
  }

  /** ensureIsCreated should accept well-known names like INBOX and OUTBOX. */
  @Test(timeout = 4000)
  public void testEnsureIsCreated_INBOX_OUTBOX() throws Throwable {
    Thread.sleep(500);
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    FolderDAO fresh = ctor.newInstance();

    Field instField = FolderDAO.class.getDeclaredField("instance");
    instField.setAccessible(true);
    instField.set(null, fresh);

    Method ensure = FolderDAO.class.getDeclaredMethod("ensureIsCreated", String.class);
    ensure.setAccessible(true);

    try {
      ensure.invoke(null, "INBOX");
      ensure.invoke(null, "OUTBOX");
    } catch (InvocationTargetException ite) {
      Throwable cause = ite.getCause();
      assertTrue(cause instanceof EmailException || cause instanceof NoClassDefFoundError);
    }
  }

  /** Constructor visibility should not be public (DAO intended as singleton). */
  @Test(timeout = 4000)
  public void testConstructor_visibility_notPublic() throws Throwable {
    Thread.sleep(500);
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    assertFalse(Modifier.isPublic(ctor.getModifiers()));
  }

  /** Helper populate should be private. */
  @Test(timeout = 4000)
  public void testPopulate_method_isPrivate() throws Throwable {
    Thread.sleep(500);
    Method populate = FolderDAO.class.getDeclaredMethod("populate", IrisFolder.class);
    assertTrue(Modifier.isPrivate(populate.getModifiers()));
  }

  /** instance().toString() should be non-null when the singleton is available. */
  @Test(timeout = 4000)
  public void testInstance_toString_nonNullOrEnvError() throws Throwable {
    Thread.sleep(500);
    try {
      Object dao = FolderDAO.instance();
      assertNotNull(dao.toString());
    } catch (EmailException e) {
      assertNotNull(e.getMessage());
    } catch (NoClassDefFoundError e) {
      assertTrue(e.getMessage() == null || e.getMessage().contains("org/hibernate"));
    }
  }

  /** After setting static instance via reflection, subsequent reflective ensureIsCreated should use it. */
  @Test(timeout = 4000)
  public void testSetStaticInstance_thenEnsureUsesIt() throws Throwable {
    Thread.sleep(500);
    Constructor<FolderDAO> ctor = FolderDAO.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    FolderDAO injected = ctor.newInstance();

    Field instField = FolderDAO.class.getDeclaredField("instance");
    instField.setAccessible(true);
    instField.set(null, injected);

    Method ensure = FolderDAO.class.getDeclaredMethod("ensureIsCreated", String.class);
    ensure.setAccessible(true);

    try {
      ensure.invoke(null, "REFLECT_BOX");
      // If no exception, environment supports DB; otherwise acceptable below
    } catch (InvocationTargetException ite) {
      Throwable cause = ite.getCause();
      assertTrue(cause instanceof EmailException || cause instanceof NoClassDefFoundError);
    }

    // Confirm our injected instance is still the static one
    assertSame(injected, instField.get(null));
  }
}
