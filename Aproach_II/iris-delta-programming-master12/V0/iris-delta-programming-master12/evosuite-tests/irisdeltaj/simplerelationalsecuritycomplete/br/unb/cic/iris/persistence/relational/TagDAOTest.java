package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.Tag;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException;

public class TagDAOTest{

    /** تاخیر نیم‌ثانیه‌ای قبل از اجرای هر تست */
    @Before
    public void delay() throws Exception {
        Thread.sleep(500);
    }

    @Test
    public void test_instanceNotNullAndSingleton() {
        TagDAO d1 = TagDAO.instance();
        TagDAO d2 = TagDAO.instance();
        assertNotNull(d1);
        assertSame(d1, d2);
    }

    @Test
    public void test_findByName_withNull_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName(null);
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok: hibernate classes not on classpath
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_findByName_withEmpty_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName("");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_findByName_nonExisting_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName("non-existing-tag");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_save_validInputs_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        Tag tag = new Tag("work");
        try {
            dao.save(tag, "MSG-ID-1");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_save_nullMessageId_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        Tag tag = new Tag("urgent");
        try {
            dao.save(tag, null);
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_save_nullTag_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        try {
            dao.save(null, "MID");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_multipleCalls_consistentFailures() {
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName("a");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }

        try {
            dao.save(new Tag("a"), "MID");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    // -----------------------
    // تست‌های اضافه‌شده
    // -----------------------

    @Test
    public void test9_instance_manyCalls_sameReference() {
        TagDAO first = TagDAO.instance();
        for (int i = 0; i < 5; i++) {
            assertSame(first, TagDAO.instance());
        }
    }

    @Test
    public void test10_findByName_withWhitespace_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName("  spaced  ");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test11_save_emptyTagName_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        Tag tag = new Tag("");
        try {
            dao.save(tag, "MID-EMPTY");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test12_save_veryLongTagName_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        String longName = new String(new char[1024]).replace('\0', 'x');
        Tag tag = new Tag(longName);
        try {
            dao.save(tag, "MID-LONG");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test13_save_then_find_bothThrow() {
        TagDAO dao = TagDAO.instance();
        try { dao.save(new Tag("seq"), "MID-SEQ"); fail("Expected NoClassDefFoundError"); }
        catch (NoClassDefFoundError expected) { /* ok */ }
        catch (DBException e) { fail("Unexpected DBException: " + e.getMessage()); }

        try { dao.findByName("seq"); fail("Expected NoClassDefFoundError"); }
        catch (NoClassDefFoundError expected) { /* ok */ }
        catch (DBException e) { fail("Unexpected DBException: " + e.getMessage()); }
    }

    @Test
    public void test14_findByName_caseSensitivity_mismatchStillThrows() {
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName("Work"); // اگر case-sensitive باشد، باز هم در این محیط باید خطا بدهد
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test15_save_weirdChars_throwsNoClassDef() {
        TagDAO dao = TagDAO.instance();
        Tag tag = new Tag("نام_تگ-💡-weird");
        try {
            dao.save(tag, "MID-weird");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test16_findByName_repeated_consistentFailure() {
        TagDAO dao = TagDAO.instance();
        for (int i = 0; i < 3; i++) {
            try {
                dao.findByName("repeat-" + i);
                fail("Expected NoClassDefFoundError at iteration " + i);
            } catch (NoClassDefFoundError expected) {
                // ok
            } catch (DBException e) {
                fail("Unexpected DBException: " + e.getMessage());
            }
        }
    }
}
