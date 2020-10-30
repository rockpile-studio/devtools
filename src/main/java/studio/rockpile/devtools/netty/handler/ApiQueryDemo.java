package studio.rockpile.devtools.netty.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import studio.rockpile.devtools.netty.annotation.HandlerService;
import studio.rockpile.devtools.netty.procedure.ExterIntfSvcException;


//http://ip:25030/rockpile/studio/intf/ext/data-api-call/ApiQueryDemo
@HandlerService(serviceName = "ApiQueryDemo")
public class ApiQueryDemo extends IntfSvcCallBaseHandler {

	@Override
	public List<Map<String, Object>> perform(Map<String, Object> arguments) throws ExterIntfSvcException, Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> content = new ArrayList<>();
		Map<String, Object> record = new HashMap<>();
		record.put("xm", "苏大强"); // 姓名
		record.put("xb", "1"); // 1 男
		record.put("mz", "汉"); // 民族
		record.put("csrq", formatter.format(new Date())); // 出生日期
		record.put("sfzhm", "350102199512041819"); // 身份证
		record.put("xzdz", "福建省福州市鼓楼区工业路100号1座1单元"); // 地址
		record.put("yxqx", "10年"); // 有效期限
		record.put("qfjg", "福州市公安局鼓楼分局"); // 签发机关
		content.add(record);
		return content;
	}

}
