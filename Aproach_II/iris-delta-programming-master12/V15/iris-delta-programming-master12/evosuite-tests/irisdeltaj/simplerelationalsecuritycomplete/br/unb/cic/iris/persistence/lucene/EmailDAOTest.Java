package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.IndexWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import irisdeltaj.simplelucene.br.unb.cic.iris.core.exception.EmailException;
import irisdeltaj.simplelucene.br.unb.cic.iris.core.model.EmailMessage;
import irisdeltaj.simplelucene.br.unb.cic.iris.core.model.IrisFolder;

public class EmailDAOTest {

    private EmailDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = EmailDAO.instance();

        // پاک‌سازی ایندکس برای ایزوله‌کردن تست‌ها
        IndexWriter writer = IndexManager.getWriter();
        writer.deleteAll();
        writer.commit();
    }

    @After
    public void tearDown() throws Exception {
        // منابع توسط IndexManager مدیریت می‌شوند.
    }

    private static EmailMessage newMsg(String from, String to, String cc, String bcc,
                                       String subject, String body, Date date, String folderId) {
        EmailMessage m = new EmailMessage();
        m.setFrom(from);
        m.setTo(to);
        m.setCc(cc);
        m.setBcc(bcc);
        m.setSubject(subject);
        m.setMessage(body);
        m.setDate(date);
        m.setFolder(new IrisFolder(folderId, "Folder-" + folderId));
        return m;
    }

    @Test
    public void saveAndFindAll_shouldInsertAndRetrieve() throws Exception {
        EmailMessage m1 = newMsg("a@x.com", "t@x.com", "c@x.com", "b@x.com",
                "S1", "B1", new Date(), "F1");
        EmailMessage m2 = newMsg("a2@x.com", "t2@x.com", "c2@x.com", "b2@x.com",
                "S2", "B2", new Date(), "F2");

        dao.saveMessage(m1);
        dao.saveMessage(m2);

        List<EmailMessage> all = dao.findAll();
        assertEquals(2, all.size());

        // اطمینان از داشتن id تولیدی معتبر
        for (EmailMessage em : all) {
            assertNotNull(em.getId());
            assertNotEquals("0", em.getId());
            assertNotEquals("null", em.getId());
        }
    }

    @Test
    public void listMessages_shouldFilterByFolderId() throws Exception {
        // سه پیام در دو پوشه متفاوت
        dao.saveMessage(newMsg("a@x.com", "t@x.com", "", "",
                "S1", "B1", new Date(), "19"));
        dao.saveMessage(newMsg("b@x.com", "t@x.com", "", "",
                "S2", "B2", new Date(), "19"));
        dao.saveMessage(newMsg("c@x.com", "t@x.com", "", "",
                "S3", "B3", new Date(), "20"));

        List<EmailMessage> in19 = dao.listMessages("19");
        assertEquals(2, in19.size());
        for (EmailMessage em : in19) {
            assertEquals("19", em.getFolder().getId());
        }

        List<EmailMessage> in20 = dao.listMessages("20");
        assertEquals(1, in20.size());
        assertEquals("20", in20.get(0).getFolder().getId());
    }

    @Test
    public void findById_shouldReturnSavedEmail() throws Exception {
        dao.saveMessage(newMsg("from@x.com", "to@x.com", "cc@x.com", "bcc@x.com",
                "Hello", "Body", new Date(), "19"));

        EmailMessage any = dao.findAll().get(0);
        EmailMessage byId = dao.findById(any.getId());

        assertEquals(any.getId(), byId.getId());
        assertEquals("Hello", byId.getSubject());
        assertEquals("from@x.com", byId.getFrom());
        assertEquals("to@x.com", byId.getTo());
        assertEquals("cc@x.com", byId.getCc());
        assertEquals("bcc@x.com", byId.getBcc());
        assertEquals("Body", byId.getMessage());
        assertEquals("19", byId.getFolder().getId());
    }

    @Test(expected = EmailException.class)
    public void findById_shouldThrowWhenNotFound() throws Exception {
        dao.findById("does-not-exist");
    }

    @Test
    public void saveMessage_shouldUpdateWhenIdPresent() throws Exception {
        // درج
        dao.saveMessage(newMsg("a@x.com", "t@x.com", "", "", "S", "B", new Date(), "19"));
        EmailMessage persisted = dao.findAll().get(0);

        // تغییر subject و به‌روزرسانی با همان id
        persisted.setSubject("S-updated");
        dao.saveMessage(persisted);

        EmailMessage after = dao.findById(persisted.getId());
        assertEquals("S-updated", after.getSubject());
    }

    @Test
    public void lastMessageReceived_shouldReturnMaxDateOrNull() throws Exception {
        // وقتی ایندکس خالی است باید null باشد
        assertNull(dao.lastMessageReceived());

        // درج چند پیام با تاریخ‌های متفاوت (برش به ثانیه)
        Date now = new Date();
        Date earlier = new Date(now.getTime() - 20_000L);
        Date middle  = new Date(now.getTime() - 10_000L);

        List<Date> dates = new ArrayList<Date>();
        dates.add(earlier);
        dates.add(middle);
        dates.add(now);

        for (int i = 0; i < dates.size(); i++) {
            dao.saveMessage(newMsg("u"+i+"@x.com", "t@x.com", "", "",
                    "S"+i, "B"+i, dates.get(i), "F"));
        }

        // انتظار: بیشترین تاریخ به‌صورت DateTools.SECOND
        Date expected = DateTools.stringToDate(
                DateTools.dateToString(now, DateTools.Resolution.SECOND));
        Date last = dao.lastMessageReceived();

        assertEquals(expected, last);
    }

    @Test
    public void roundTrip_shouldPreserveAllStoredFields() throws Exception {
        Date d = new Date();
        EmailMessage input = newMsg("from@x.com", "to@x.com", "cc@x.com", "bcc@x.com",
                "Subj ذخیره", "متن پیام", d, "F-42");
        dao.saveMessage(input);

        EmailMessage out = dao.findAll().get(0);

        assertEquals("from@x.com", out.getFrom());
        assertEquals("to@x.com", out.getTo());
        assertEquals("cc@x.com", out.getCc());
        assertEquals("bcc@x.com", out.getBcc());
        assertEquals("Subj ذخیره", out.getSubject());
        assertEquals("متن پیام", out.getMessage());
        assertEquals("F-42", out.getFolder().getId());

        // تاریخ‌ها به ثانیه برش می‌خورند؛ با تبدیل/بازگردانی مقایسه کنیم
        Date expected = DateTools.stringToDate(
                DateTools.dateToString(d, DateTools.Resolution.SECOND));
        assertEquals(expected, out.getDate());
    }
}
