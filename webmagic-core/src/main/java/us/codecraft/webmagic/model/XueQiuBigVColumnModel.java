package us.codecraft.webmagic.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.JsonPathSelector;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class XueQiuBigVColumnModel implements AfterExtractor {

	private String fileUrlAllName = "E:\\tmp.urls.txt";

	private PrintWriter fileUrlWriter;

	private Set<String> lines;

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

	private void readFile() {
		try {
			readUrlFile();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	private void initWriter() {
		try {
			fileUrlWriter = new PrintWriter(new FileWriter(getFileName(fileUrlAllName), true));
		} catch (IOException e) {
			throw new RuntimeException("init cache scheduler error", e);
		}
	}

	private void readUrlFile() throws IOException {
		String line;
		BufferedReader fileUrlReader = null;
		try {
			lines = new LinkedHashSet<String>();
			fileUrlReader = new BufferedReader(new FileReader(getFileName(fileUrlAllName)));
			while ((line = fileUrlReader.readLine()) != null) {
				lines.add(line.trim());
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
        String maxPage = new JsonPathSelector("$.maxPage").selectList(page.getRawText()).toString();
		String url = page.getUrl().toString();
		List<NameValuePair> params = null;

		try {
			params = URLEncodedUtils.parse(new URI(url), "UTF-8");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (NameValuePair param : params) {
			int currentPage = Integer.parseInt(param.getValue());
            if(param.getName() == "page" && currentPage < Integer.parseInt(maxPage)){
            	currentPage++;
                page.addTargetRequest("https://xueqiu.com/statuses/original/timeline.json?user_id=1955602780&page=" + currentPage);
            }
		}

		init("/home/zhangleigang/webmagic/XueQiuBigVColumn");

		List<String> lists = new JsonPathSelector("$.list").selectList(page.getRawText());
		for (String objStr : lists) {
			String line = new JsonPathSelector("$.title").select(objStr) + ","
					+ "https://xueqiu.com/1955602780/97945030" + new JsonPathSelector("$.target").select(objStr);
			if (lines != null && lines.contains(line)) {
				continue;
			} else {
				fileUrlWriter.println(line);
				lines.add(line);
			}
		}

		/*
		 * try { close(); } catch (IOException e) { // TODO 自动生成的 catch 块
		 * e.printStackTrace(); }
		 */
	}
}
