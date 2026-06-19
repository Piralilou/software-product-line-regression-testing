package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.EmailException;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.Tag;

public class TagDAOTest {

    private String prevRamProp;
    private TagDAO dao;

    @Before
    public void setUp() throws Exception {
        // استفاده از RAM برای ایندکس تا تست‌ها سبک و ایزوله باشند
        prevRamProp = System.getProperty("iris.lucene.ram");
        System.setProperty("iris.lucene.ram", "true");

        // پاک‌سازی ایندکس
        IndexWriter w = IndexManager.getWriter();
        w.deleteAll();
        w.commit();

        dao = TagDAO.instance();
    }

    @After
    public void tearDown() throws Exception {
        // برگرداندن پراپرتی سیستم به حالت قبل
        if (prevRamProp == null) System.clearProperty("iris.lucene.ram");
        else System.setProperty("iris.lucene.ram", prevRamProp);
    }

    @Test
    public void instance_shouldBeSingleton() {
        assertSame(dao, TagDAO.instance());
    }

    @Test
    public void saveOrUpdate_shouldInsertAndGenerateId_whenMissing() throws Exception {
        Tag t = new Tag(null, "alpha");
        dao.saveOrUpdate(t);

        Tag found = dao.findByName("alpha");
        assertNotNull(found.getId());
        assertNotEquals("0", found.getId());
        assertNotEquals("null", found.getId());
        assertEquals("alpha", found.getName());
    }

    @Test
    public void saveOrUpdate_shouldUpdate_whenIdPresent_andChangeName() throws Exception {
        // درج اولیه
        dao.saveOrUpdate(new Tag(null, "old"));
        Tag existing = dao.findByName("old");
        String id = existing.getId();

        // به‌روزرسانی با همان id ولی نام جدید
        Tag updated = new Tag(id, "new");
        dao.saveOrUpdate(updated);

        // باید با نام جدید پیدا شود و id ثابت بماند
        Tag after = dao.findByName("new");
        assertEquals(id, after.getId());

        // نام قبلی نباید پیدا شود
        try {
            dao.findByName("old");
            fail("Expected EmailException for old name");
        } catch (EmailException expected) {
            // ok
        }
    }

    @Test
    public void findByName_shouldBeCaseSensitive() throws Exception {
        dao.saveOrUpdate(new Tag(null, "News"));

        // تطابق دقیق
        Tag exact = dao.findByName("News");
        assertEquals("News", exact.getName());

        // حروف متفاوت → نباید پیدا شود
        try {
            dao.findByName("news");
            fail("Expected EmailException for different case");
        } catch (EmailException expected) {
            // ok
        }
    }

    @Test
    public void findAll_shouldReturnAllSaved() throws Exception {
        dao.saveOrUpdate(new Tag(null, "t1"));
        dao.saveOrUpdate(new Tag(null, "t2"));
        dao.saveOrUpdate(new Tag(null, "t3"));

        List<Tag> all = dao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    public void findByName_shouldThrowWithMessage_whenNotFound() {
        try {
            dao.findByName("missing-tag");
            fail("Expected EmailException");
        } catch (EmailException ex) {
            assertEquals("Tag name not found", ex.getMessage());
        }
    }

    @Test
    public void toDocument_fromDocument_shouldRoundTrip() throws Exception {
        Tag in = new Tag("777", "round");
        // متدهای protected چون تست در همان پکیج است قابل دسترسی‌اند
        org.apache.lucene.document.Document doc = dao.toDocument(in);

        assertEquals("777", doc.get("id"));
        assertEquals("round", doc.get("name"));

        Tag out = dao.fromDocument(doc);
        assertEquals("777", out.getId());
        assertEquals("round", out.getName());
    }

    @Test
    public void toDocument_shouldWriteLiteralNullWhenIdIsNull() throws Exception {
        Tag in = new Tag(null, "beta");
        org.apache.lucene.document.Document doc = dao.toDocument(in);
        // String.valueOf(null) -> "null"؛ ذخیره‌سازی واقعی حین saveOrUpdate UUID تولید می‌کند،
        // ولی اینجا فقط رفتار toDocument را می‌سنجیم.
        assertEquals("null", doc.get("id"));
        assertEquals("beta", doc.get("name"));
    }
}
