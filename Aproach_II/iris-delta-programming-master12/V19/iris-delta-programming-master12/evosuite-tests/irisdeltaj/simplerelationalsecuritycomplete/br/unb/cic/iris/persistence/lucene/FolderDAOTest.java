package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.exception.EmailException;
import irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.core.model.IrisFolder;

public class FolderDAOTest {

    private FolderDAO dao;

    @Before
    public void setUp() throws Exception {
        // پاک‌سازی ایندکس برای هر تست
        IndexWriter writer = IndexManager.getWriter();
        writer.deleteAll();
        writer.commit();

        // ریست Singleton تا ensureIsCreated دوباره اجرا شود
        Field inst = FolderDAO.class.getDeclaredField("instance");
        inst.setAccessible(true);
        inst.set(null, null);

        dao = FolderDAO.instance(); // این باید INBOX/OUTBOX را بسازد
    }

    @After
    public void tearDown() throws Exception {
        // منابع توسط IndexManager مدیریت می‌شوند
    }

    @Test
    public void instance_shouldCreateDefaultFolders_ifMissing() throws Exception {
        // باید INBOX و OUTBOX ساخته شده باشند
        IrisFolder inbox = dao.findByName(IrisFolder.INBOX);
        IrisFolder outbox = dao.findByName(IrisFolder.OUTBOX);

        assertNotNull(inbox.getId());
        assertNotNull(outbox.getId());

        // فراخوانی مجدد instance نباید پوشه‌های تکراری بسازد
        FolderDAO again = FolderDAO.instance();
        assertSame(dao, again);

        List<IrisFolder> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void saveOrUpdate_shouldInsertAndThenUpdateById() throws Exception {
        // درج پوشه جدید
        IrisFolder f = new IrisFolder();
        f.setName("Projects");
        dao.saveOrUpdate(f);

        IrisFolder saved = dao.findByName("Projects");
        assertNotNull(saved.getId());

        // به‌روزرسانی نام با همان id
        String id = saved.getId();
        saved.setName("Projects-Updated");
        dao.saveOrUpdate(saved);

        IrisFolder updated = dao.findByName("Projects-Updated");
        assertEquals(id, updated.getId());

        // نام قدیمی دیگر نباید پیدا شود
        try {
            dao.findByName("Projects");
            fail("Expected EmailException for old name");
        } catch (EmailException expected) {
            // ok
        }
    }

    @Test
    public void doFindByName_shouldReturnOnlyMatchingFolder() throws Exception {
        // اضافه بر پیش‌فرض‌ها، دو پوشه دیگر
        IrisFolder a = new IrisFolder(); a.setName("A");
        IrisFolder b = new IrisFolder(); b.setName("B");
        dao.saveOrUpdate(a);
        dao.saveOrUpdate(b);

        List<IrisFolder> onlyA = dao.doFindByName("A");
        assertEquals(1, onlyA.size());
        assertEquals("A", onlyA.get(0).getName());

        List<IrisFolder> onlyB = dao.doFindByName("B");
        assertEquals(1, onlyB.size());
        assertEquals("B", onlyB.get(0).getName());
    }

    @Test(expected = EmailException.class)
    public void findByName_shouldThrowWhenNotFound() throws Exception {
        dao.findByName("Does-Not-Exist");
    }

    @Test
    public void findAll_shouldReturnDefaultsPlusInsertedOnes() throws Exception {
        // پیش‌فرض‌ها: 2 پوشه
        assertEquals(2, dao.findAll().size());

        IrisFolder x = new IrisFolder(); x.setName("X");
        IrisFolder y = new IrisFolder(); y.setName("Y");
        dao.saveOrUpdate(x);
        dao.saveOrUpdate(y);

        List<IrisFolder> all = dao.findAll();
        assertEquals(4, all.size());
    }
}
