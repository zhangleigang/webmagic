package us.codecraft.webmagic.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.selector.JsonPathSelector;

public class XueQiuBigVColumnModel implements AfterExtractor {
	
    private String fileUrlAllName = "/home/zhangleigang/webmagic/XueQiuBigVColumn/tmp.urls.txt";

    private Task task;

    private PrintWriter fileUrlWriter;

    private AtomicInteger cursor = new AtomicInteger();

    private AtomicBoolean inited = new AtomicBoolean(false);

    private BlockingQueue<Request> queue;

    private Set<String> urls;
    
    private ScheduledExecutorService flushThreadPool;

    private void flush() {
        fileUrlWriter.flush();
    }

    private void init(String filePath) {
        if (!filePath.endsWith("/") && !filePath.endsWith("\\")) {
            filePath += "/";
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        readFile();
        initWriter();
        initFlushThread();
        inited.set(true);
    }

    private void initFlushThread() {
    	flushThreadPool = Executors.newScheduledThreadPool(1);
    	flushThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                flush();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void initWriter() {
        try {
            fileUrlWriter = new PrintWriter(new FileWriter(getFileName(fileUrlAllName), true));
        } catch (IOException e) {
            throw new RuntimeException("init cache scheduler error", e);
        }
    }

    private void readFile() {
        try {
            queue = new LinkedBlockingQueue<Request>();
            urls = new LinkedHashSet<String>();
            readUrlFile();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private void readUrlFile() throws IOException {
        String line;
        BufferedReader fileUrlReader = null;
        try {
            fileUrlReader = new BufferedReader(new FileReader(getFileName(fileUrlAllName)));
            int lineReaded = 0;
            while ((line = fileUrlReader.readLine()) != null) {
                urls.add(line.trim());
                lineReaded++;
                if (lineReaded > cursor.get()) {
                    queue.add(new Request(line));
                }
            }
        } finally {
            if (fileUrlReader != null) {
                IOUtils.closeQuietly(fileUrlReader);
            }
        }
    }
    
    public void close() throws IOException {
		flushThreadPool.shutdown();	
		fileUrlWriter.close();
	}

    private String getFileName(String filename) {
        return filename;
    }

	@Override
	public void afterProcess(Page page) {
		init("/home/zhangleigang/webmagic/XueQiuBigVColumn");
		List<String> lists = new JsonPathSelector("$.list").selectList(page.getRawText());
		for (String objStr : lists) {
	        fileUrlWriter.println(new JsonPathSelector("$.title").select(objStr) + ","
	        + "https://xueqiu.com/1955602780/97945030" + new JsonPathSelector("$.target").select(objStr));
			//System.out.println(new JsonPathSelector("$.title").select(objStr));
			//System.out.println("https://xueqiu.com/1955602780/97945030" + new JsonPathSelector("$.target").select(objStr));
			//page.putField("title",new JsonPathSelector("$.title").select(objStr));
			//page.putField("url", "https://xueqiu.com/1955602780/97945030" + new JsonPathSelector("$.target").select(objStr));
		}
		try {
			close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
