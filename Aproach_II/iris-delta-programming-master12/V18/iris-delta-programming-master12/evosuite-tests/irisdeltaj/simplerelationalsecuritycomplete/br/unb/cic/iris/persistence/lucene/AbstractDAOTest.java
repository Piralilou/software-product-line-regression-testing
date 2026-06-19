package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.EmailException;

/**
 * تست‌های واحد برای AbstractDAO<T>.
 * این تست‌ها یک DAO ساده برای موجودیت Foo می‌سازند و کل چرخهٔ CRUD + جستجو را بررسی می‌کنند.
 */
public class AbstractDAOTest {

    /** موجودیت ساده برای تست */
    static class Foo {
        String id;
        String name;

        Foo(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /** پیاده‌سازی DAO برای Foo */
    static class FooDAO extends AbstractDAO<Foo> {
        FooDAO() {
            this.type = "foo";
        }

        @Override
        protected Foo fromDocument(Document doc) throws Exception {
            return new Foo(doc.get("id"), doc.get("name"));
        }

        @Override
        protected Document toDocument(Foo t) throws Exception {
            Document d = new Document();
            // ممکن است id تهی/"0"/"null" باشد؛ saveDocument خودش مدیریت می‌کند
            if (t.id != null) {
                d.add(new StringField("id", t.id, Store.YES));
            } else {
                d.add(new StringField("id", "null", Store.YES));
            }
            d.add(new StringField("name", t.name, Store.YES));
            return d;
        }
    }

    /** یک DAO دیگر با type متفاوت برای اطمینان از فیلتر شدن بر اساس type */
    static class BarDAO extends AbstractDAO<Foo> {
        BarDAO() {
            this.type = "bar";
        }

        @Override
        protected Foo fromDocument(Document doc) throws Exception {
            return new Foo(doc.get("id"), doc.get("name"));
        }

        @Override
        protected Document toDocument(Foo t) throws Exception {
            Document d = new Document();
            if (t.id != null) {
                d.add(new StringField("id", t.id, Store.YES));
            } else {
                d.add(new StringField("id", "null", Store.YES));
            }
            d.add(new StringField("name", t.name, Store.YES));
            return d;
        }
    }

    private FooDAO fooDAO;
    private BarDAO barDAO;

    @Before
    public void setUp() throws Exception {
        fooDAO = new FooDAO();
        barDAO = new BarDAO();

        // پاک‌سازی ایندکس برای هر تست
        IndexWriter w = IndexManager.getWriter();
        w.deleteAll();
        w.commit();
    }

    @After
    public void tearDown() throws Exception {
        // کار خاصی لازم نیست؛ IndexManager مدیریت منابع را انجام می‌دهد
    }

    @Test
    public void saveOrUpdate_shouldInsertWhenIdIsNullOrZeroOrLiteralNull() throws Exception {
        // id = null
        Foo f1 = new Foo(null, "alice");
        fooDAO.saveOrUpdate(f1);

        // id = "0"
        Foo f2 = new Foo("0", "bob");
        fooDAO.saveOrUpdate(f2);

        // id = "null"
        Foo f3 = new Foo("null", "carol");
        fooDAO.saveOrUpdate(f3);

        List<Foo> all = fooDAO.findAll();
        assertEquals(3, all.size());
        for (Foo f : all) {
            assertNotNull("Inserted docs must have non-null id", f.id);
            assertNotEquals("0", f.id);
            assertNotEquals("null", f.id);
            assertTrue("UUID-like id expected", f.id.length() >= 8);
        }
    }

    @Test
    public void saveOrUpdate_shouldUpdateWhenIdExists() throws Exception {
        Foo f = new Foo(null, "alice");
        fooDAO.saveOrUpdate(f);

        // id ایجاد شده را از ایندکس می‌گیریم
        Foo persisted = fooDAO.findAll().get(0);
        String id = persisted.id;

        // به‌روزرسانی اسم با همان id
        Foo updated = new Foo(id, "alice-updated");
        fooDAO.saveOrUpdate(updated);

        Foo fetched = fooDAO.findById(id);
        assertEquals(id, fetched.id);
        assertEquals("alice-updated", fetched.name);
    }

    @Test
    public void findAll_shouldReturnOnlyDocsWithMatchingType() throws Exception {
        fooDAO.saveOrUpdate(new Foo(null, "foo-1"));
        barDAO.saveOrUpdate(new Foo(null, "bar-1"));
        fooDAO.saveOrUpdate(new Foo(null, "foo-2"));

        List<Foo> foos = fooDAO.findAll();
        assertEquals(2, foos.size());

        // تأیید می‌کنیم که سند type=bar توسط fooDAO دیده نشود
        for (Foo f : foos) {
            assertTrue(f.name.startsWith("foo-"));
        }
    }

    @Test
    public void findById_shouldFindInsertedDoc() throws Exception {
        fooDAO.saveOrUpdate(new Foo(null, "dave"));
        Foo persisted = fooDAO.findAll().get(0);
        Foo byId = fooDAO.findById(persisted.id);
        assertEquals(persisted.id, byId.id);
        assertEquals("dave", byId.name);
    }

    @Test(expected = EmailException.class)
    public void findById_shouldThrowWhenNotFound() throws Exception {
        fooDAO.findById("does-not-exist");
    }

    @Test
    public void findByTerms_shouldApplyAdditionalConstraints() throws Exception {
        fooDAO.saveOrUpdate(new Foo(null, "alice"));
        fooDAO.saveOrUpdate(new Foo(null, "bob"));
        fooDAO.saveOrUpdate(new Foo(null, "alice"));

        // جستجوی name = alice (به‌علاوهٔ فیلتر type=foo که DAO اضافه می‌کند)
        Query q = new TermQuery(new Term("name", "alice"));
        List<Foo> result = fooDAO.findByTerms(new Query[]{ q });

        assertEquals(2, result.size());
        for (Foo f : result) {
            assertEquals("alice", f.name);
        }
    }

    @Test
    public void delete_shouldRemoveDocument() throws Exception {
        fooDAO.saveOrUpdate(new Foo(null, "eve"));
        Foo persisted = fooDAO.findAll().get(0);

        fooDAO.delete(persisted);

        List<Foo> remaining = fooDAO.findAll();
        assertTrue(remaining.isEmpty());
    }

    @Test
    public void tCollector_shouldCollectDocsViaSearcher() throws Exception {
        // این تست غیرمستقیم TCollector را می‌آزماید چون findAll از آن استفاده می‌کند
        fooDAO.saveOrUpdate(new Foo(null, "x"));
        fooDAO.saveOrUpdate(new Foo(null, "y"));

        List<Foo> all = fooDAO.findAll();
        assertEquals(2, all.size());
    }
}
