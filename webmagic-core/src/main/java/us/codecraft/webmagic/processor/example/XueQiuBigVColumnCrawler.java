package us.codecraft.webmagic.processor.example;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.XueQiuBigVColumnModel;
import us.codecraft.webmagic.pipeline.XueQiuBigVColumnDaoPipeline;

/**
 * @author zhangleigang
 *
 */
public class XueQiuBigVColumnCrawler {

	public static void main(String[] args) {
		OOSpider.create(Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000)
				.addCookie("u", "9908540820")
				.addCookie("xq_a_token", "3de6f1ebe7a2e5e316e7a47eaf8df0610e5909c5")
				.addCookie("xq_is_login", "1")
				.addCookie("xq_r_token", "f8b423252994c7922fdbb68e9989e10b3b50b2dd")
				.addCookie("xq_token_expire", "Tue%20Feb%2013%202018%2014%3A58%3A37%20GMT%2B0800%20(CST)"), 
				 new XueQiuBigVColumnDaoPipeline(),
				 XueQiuBigVColumnModel.class)
		        .addUrl("https://xueqiu.com/statuses/original/timeline.json?user_id=1955602780&page=2")
		        .thread(1).run();
	}
}
