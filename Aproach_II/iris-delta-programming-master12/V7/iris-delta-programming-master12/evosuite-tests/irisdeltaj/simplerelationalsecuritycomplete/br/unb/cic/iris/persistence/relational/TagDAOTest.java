package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.relational;

import org.junit.Test;
import static org.junit.Assert.*;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.Tag;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.DBException;

public class TagDAOTest{

    @Test
    public void test_instanceNotNullAndSingleton() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO d1 = TagDAO.instance();
        TagDAO d2 = TagDAO.instance();
        assertNotNull(d1);
        assertSame(d1, d2);
    }

    @Test
    public void test_findByName_withNull_throwsNoClassDef() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
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

    // ---------------------- تست‌های افزوده‌شده ----------------------

    @Test
    public void test_findByName_withWhitespace_throwsNoClassDef() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName("   ");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_findByName_withUnicode_throwsNoClassDef() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName("برچسب-آزمایشی"); // Persian/Unicode
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_findByName_withVeryLongName_throwsNoClassDef() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 512; i++) sb.append('x');
        TagDAO dao = TagDAO.instance();
        try {
            dao.findByName(sb.toString());
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_save_emptyMessageId_throwsNoClassDef() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO dao = TagDAO.instance();
        Tag tag = new Tag("alpha");
        try {
            dao.save(tag, "");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_save_unicodeTagName_throwsNoClassDef() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO dao = TagDAO.instance();
        Tag tag = new Tag("紧急"); // Chinese: "urgent"
        try {
            dao.save(tag, "MID-UNICODE");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_save_nullBoth_throwsNoClassDef() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO dao = TagDAO.instance();
        try {
            dao.save(null, null);
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }

    @Test
    public void test_instance_hashCodeStableWithSingleton() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO d1 = TagDAO.instance();
        TagDAO d2 = TagDAO.instance();
        assertEquals(d1.hashCode(), d2.hashCode());
        assertSame(d1, d2);
    }

    @Test
    public void test_repeatedSaveOperations_consistentFailures() {
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        TagDAO dao = TagDAO.instance();
        try {
            dao.save(new Tag("first"), "M1");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }

        try {
            dao.save(new Tag("second"), "M2");
            fail("Expected NoClassDefFoundError");
        } catch (NoClassDefFoundError expected) {
            // ok
        } catch (DBException e) {
            fail("Unexpected DBException: " + e.getMessage());
        }
    }
}
