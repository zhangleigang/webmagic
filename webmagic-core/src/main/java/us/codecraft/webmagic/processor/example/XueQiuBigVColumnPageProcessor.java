package us.codecraft.webmagic.processor.example;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

/**
 * @author zhangleigang
 *
 */
public class XueQiuBigVColumnPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000).addCookie("u", "9908540820")
			.addCookie("xq_a_token", "3de6f1ebe7a2e5e316e7a47eaf8df0610e5909c5").addCookie("xq_is_login", "1")
			.addCookie("xq_r_token", "f8b423252994c7922fdbb68e9989e10b3b50b2dd")
			.addCookie("xq_token_expire", "Tue%20Feb%2013%202018%2014%3A58%3A37%20GMT%2B0800%20(CST)");

	@Override
	public void process(Page page) {
		List<String> lists = new JsonPathSelector("$.list").selectList(page.getRawText());
		for (String objStr : lists) {
			page.putField("title",new JsonPathSelector("$.title").select(objStr));
			page.putField("url", "https://xueqiu.com/1955602780/97945030" + new JsonPathSelector("$.target").select(objStr));
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new XueQiuBigVColumnPageProcessor())
				.addUrl("https://xueqiu.com/statuses/original/timeline.json?user_id=1955602780&page=1")
                .addPipeline(new FilePipeline("D:\\xueqiu_coulumn_data\\"))
				.thread(1).run();
	}
}
