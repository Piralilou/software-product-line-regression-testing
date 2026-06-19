package irisdeltaj.simplerelationalsecuritycomplete.br.unb.cic.iris.persistence.lucene;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.lucene.index.Term;

public class IndexManagerTest {

    private String previousRamProp;

    @Before
    public void setUp() throws Exception {
        // برای اینکه getIndex روی RAM بیاید و به مسیر هوم دست نزند
        previousRamProp = System.getProperty("iris.lucene.ram");
        System.setProperty("iris.lucene.ram", "true");

        // اطمینان از شروع تمیز
        safeCloseIndex();
    }

    @After
    public void tearDown() throws Exception {
        // بازگرداندن پراپرتی سیستم به حالت قبل
        if (previousRamProp == null) {
            System.clearProperty("iris.lucene.ram");
        } else {
            System.setProperty("iris.lucene.ram", previousRamProp);
        }
        // بستن منابع
        safeCloseIndex();
    }

    private void safeCloseIndex() throws Exception {
        try {
            Directory idx = null;
            try { idx = IndexManager.getIndex(); } catch (Throwable ignore) {}
            if (idx != null) IndexManager.closeIndex();
        } catch (IOException ignore) {
        }
    }

    private void addDoc(String field, String value) throws Exception {
        IndexWriter w = IndexManager.getWriter();
        Document d = new Document();
        d.add(new StringField(field, value, Store.YES));
        w.addDocument(d);
        w.commit();
    }

    @Test
    public void typeStored_shouldHaveExpectedFlags() {
        assertTrue(IndexManager.TYPE_STORED.indexed());
        assertTrue(IndexManager.TYPE_STORED.tokenized());
        assertTrue(IndexManager.TYPE_STORED.stored());
        assertTrue(IndexManager.TYPE_STORED.storeTermVectors());
        assertTrue(IndexManager.TYPE_STORED.storeTermVectorPositions());
    }

    @Test
    public void createIndex_shouldUseRAMDirectory_whenPathIsNull() throws Exception {
        Directory dir = IndexManager.createIndex(null);
        assertNotNull(dir);
        assertTrue("Expected RAMDirectory", dir instanceof RAMDirectory);

        // نویسنده نیز باید ساخته شده و commit شده باشد
        IndexWriter w = IndexManager.getWriter();
        assertNotNull(w);

        // getIndex باید همان نمونه را برگرداند
        assertSame(dir, IndexManager.getIndex());
    }

    @Test
    public void createIndex_shouldCreateFSDirectory_whenPathDoesNotExist() throws Exception {
        // دایرکتوری موقت ریشه
        Path root = Files.createTempDirectory("lucene-fs-root-");
        File idxPath = root.resolve("idx").toFile();
        assertFalse(idxPath.exists());

        try {
            Directory dir = IndexManager.createIndex(idxPath.getAbsolutePath());
            assertNotNull(dir);
            assertTrue("Expected FSDirectory", dir instanceof FSDirectory);
            assertTrue("Index directory should exist", idxPath.exists());

            // commit اولیه
            IndexWriter w = IndexManager.getWriter();
            assertNotNull(w);
        } finally {
            // آزادسازی منابع و پاک‌سازی
            IndexManager.closeIndex();
            deleteRecursively(idxPath);
            deleteRecursively(root.toFile());
        }
    }

    @Test(expected = IOException.class)
    public void setIndex_shouldThrow_whenPathInvalid() throws Exception {
        File notExists = new File("path/that/does/not/exist-" + System.nanoTime());
        IndexManager.setIndex(notExists.getAbsolutePath());
    }

    @Test
    public void getIndex_shouldReturnRAMDirectory_whenRamPropertyTrue() throws Exception {
        System.setProperty("iris.lucene.ram", "true");
        Directory dir = IndexManager.getIndex();
        assertTrue(dir instanceof RAMDirectory);
    }

    @Test
    public void closeIndex_shouldResetStatics_andNewWriterAfterReopen() throws Exception {
        System.setProperty("iris.lucene.ram", "true");
        Directory before = IndexManager.getIndex();
        IndexWriter w1 = IndexManager.getWriter();

        IndexManager.closeIndex();

        Directory after = IndexManager.getIndex();
        IndexWriter w2 = IndexManager.getWriter();

        assertNotSame("Directory must be recreated after close", before, after);
        assertNotSame("Writer must be recreated after close", w1, w2);
    }

    @Test
    public void getReader_shouldRefreshAfterCommit_andSearcherWorks() throws Exception {
        System.setProperty("iris.lucene.ram", "true");
        // اطمینان از ایجاد ایندکس RAM
        Directory dir = IndexManager.getIndex();
        assertTrue(dir instanceof RAMDirectory);

        // افزودن سند اول
        addDoc("type", "email");

        IndexReader r1 = IndexManager.getReader();
        assertTrue("Reader should be a DirectoryReader", r1 instanceof DirectoryReader);

        IndexSearcher s1 = new IndexSearcher(r1);
        TopDocs td1 = s1.search(new TermQuery(new Term("type", "email")), 10);
        assertEquals(1, td1.totalHits);

        // افزودن سند دوم و commit
        addDoc("type", "email");

        // getReader باید با openIfChanged تازه شود
        IndexReader r2 = IndexManager.getReader();
        IndexSearcher s2 = new IndexSearcher(r2);
        TopDocs td2 = s2.search(new TermQuery(new Term("type", "email")), 10);
        assertEquals(2, td2.totalHits);
    }

    // ————— Helpers —————
    private static void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) for (File k : kids) deleteRecursively(k);
        }
        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }
}
