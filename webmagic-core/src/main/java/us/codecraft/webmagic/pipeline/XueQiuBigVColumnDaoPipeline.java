package us.codecraft.webmagic.pipeline;

import javax.annotation.Resource;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.dao.XueQiuBigVColumnDao;
import us.codecraft.webmagic.model.XueQiuBigVColumnModel;

public class XueQiuBigVColumnDaoPipeline implements PageModelPipeline<XueQiuBigVColumnModel>{

    @Resource
    private XueQiuBigVColumnDao xueQiuBigVColumnDao;

	@Override
	public void process(XueQiuBigVColumnModel columnModel, Task task) {
    	//xueQiuBigVColumnDao.add(columnModel);
	}
}
