package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractCollectorTest {

    private RAMDirectory directory;
    private DirectoryReader reader;

    @Before
    public void setUp() throws Exception {
        directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_4);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);

        // سگمنت اول: 3 داک
        addDoc(writer, "a");
        addDoc(writer, "b");
        addDoc(writer, "c");
        writer.commit(); // باعث می‌شود یک سگمنت ایجاد شود

        // سگمنت دوم: 2 داک
        addDoc(writer, "d");
        addDoc(writer, "e");
        writer.commit();

        writer.close();

        reader = DirectoryReader.open(directory);
    }

    @After
    public void tearDown() throws Exception {
        if (reader != null) reader.close();
        if (directory != null) directory.close();
    }

    private static void addDoc(IndexWriter writer, String val) throws Exception {
        Document doc = new Document();
        doc.add(new StringField("id", val, Field.Store.NO));
        writer.addDocument(doc);
    }

    /** یک کالکتور ساده که docهای جمع‌آوری شده را ذخیره می‌کند. */
    private static class RecordingCollector extends AbstractCollector {
        final List<Integer> seen = new ArrayList<>();
        @Override
        void tryCollect(int doc) throws Exception {
            seen.add(doc);
        }
    }

    /** کالکتوری که عمداً خطا می‌دهد تا رفتار رپ شدن در RuntimeException تست شود. */
    private static class FailingCollector extends AbstractCollector {
        @Override
        void tryCollect(int doc) throws Exception {
            throw new ParseException("boom", 0);
        }
    }

    @Test
    public void acceptsDocsOutOfOrder_shouldBeTrue() {
        RecordingCollector c = new RecordingCollector();
        assertTrue(c.acceptsDocsOutOfOrder());
    }

    @Test
    public void collect_shouldAddBaseToLocalDocId() throws Exception {
        RecordingCollector c = new RecordingCollector();

        // باید حداقل دو سگمنت داشته باشیم
        List<AtomicReaderContext> leaves = reader.leaves();
        assertTrue("Expected at least 2 segments", leaves.size() >= 2);

        AtomicReaderContext first = leaves.get(0);
        AtomicReaderContext second = leaves.get(1);

        // در سگمنت اول، base معمولاً 0 است
        c.setNextReader(first);
        c.collect(0); // انتظار داریم 0 + base (که 0 است)

        // در سگمنت دوم، base برابر docBase سگمنت دوم است (مثلاً 3)
        c.setNextReader(second);
        c.collect(1); // انتظار داریم 1 + base

        // انتظار: [0, first.maxDoc + 1] => با داده‌های ما: [0, 4]
        assertEquals(2, c.seen.size());
        assertEquals(Integer.valueOf(first.docBase + 0), c.seen.get(0));
        assertEquals(Integer.valueOf(second.docBase + 1), c.seen.get(1));
    }

    @Test(expected = RuntimeException.class)
    public void collect_shouldWrapExceptionsIntoRuntimeException() throws Exception {
        FailingCollector c = new FailingCollector();

        // هر کانتکستی کافی است؛ فقط برای مقداردهی base
        AtomicReaderContext leaf = reader.leaves().get(0);
        c.setNextReader(leaf);

        // این باید RuntimeException پرتاب کند
        c.collect(0);
    }
}
