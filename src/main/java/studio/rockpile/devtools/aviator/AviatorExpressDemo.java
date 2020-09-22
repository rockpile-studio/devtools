package studio.rockpile.devtools.aviator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;

import studio.rockpile.devtools.entity.UserInfo;

public class AviatorExpressDemo {

	@Before
	public void before() {
		// aviator两种运行模式
		// 默认 AviatorEvaluator=EVAL 以执行速度优先
		AviatorEvaluator.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.EVAL);
		// 修改为COMPILE 编译速度优先,这样不会做编译优化
		// AviatorEvaluator.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.COMPILE);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		String expression = "1+2+3.6";
		// Aviator的数值类型支持Long和Double
		// rand()             返回一个介于0-1的随机数,double类型
		// print([out],obj)   打印对象,如果指定out,向out打印,否则输出到控制台
		// println([out],obj) 与print类似,但是在输出后换行
		// long(v)            将值的类型转为long
		// double(v)          将值的类型转为double
		// str(v)             将值的类型转为string
		// math.abs(d)        求d的绝对值
		// math.sqrt(d)       求d的平方根
		// math.pow(d1,d2)    求d1的d2次方
		// math.log(d)        求d的自然对数
		// math.log10(d)      求d以10为底的对数
		// math.sin(d)        正弦函数
		// math.cos(d)        余弦函数
		// math.tan(d)        正切函数
		Double num = (Double) AviatorEvaluator.execute(expression);
		System.out.println("num : " + num);

		// Aviator的String是任何用单引号或者双引号括起来的字符序列
		// date_to_string(date,format)                  将Date对象转化化特定格式的字符串
		// string_to_date(source,format)                将特定格式的字符串转化为Date对象
		// string.contains(s1,s2)                       判断s1是否包含s2,返回Boolean
		// string.length(s)                             求字符串长度,返回Long
		// string.startsWith(s1,s2)                     s1是否以s2开始,返回Boolean
		// string.endsWith(s1,s2)                       s1是否以s2结尾,返回Boolean
		// string.substring(s,begin[,end])              截取字符串s,从begin到end,如果忽略end的话,将从begin到结尾,与java.util.String.substring一样。
		// string.indexOf(s1,s2)                        Java中的s1.indexOf(s2),求s2在s1中的起始索引位置,如果不存在为-1
		// string.split(target,regex,[limit])           Java里的String.split方法一致函数
		// string.join(seq,seperator)                   将集合seq里的元素以seperator为间隔连接起来形成字符串函数
		// string.replace_first(s,regex,replacement)    Java里的String.replaceFirst方法
		// string.replace_all(s,regex,replacement)      Java里的String.replaceAll方法 
		String name = "rockpile";
		Map<String, Object> env = new HashMap<>();
		env.put("name", name);
		String str = (String) AviatorEvaluator.execute("'hello ' + name", env);
		System.out.println("str : " + str);

		// Aviator 2.2开始新增加一个exec方法, 可以更方便地传入变量并执行, 而不需要构造env这个map了
		System.out.println("exec : " + AviatorEvaluator.exec("'hello ' + name", name));

		// Aviator支持函数调用, 函数调用的风格类似 lua
		Long len = (Long) AviatorEvaluator.execute("string.length('hello')");
		System.out.println("len : " + len);
		Boolean flag = (Boolean) AviatorEvaluator.execute("string.contains(\"test\", string.substring('hello', 1, 2))");
		System.out.println("flag : " + flag);

		System.out.println("三元表达式 : " + AviatorEvaluator.exec("a>0? 'yes':'no'", 1));
	}

	// 访问数组和集合
	@Test
	public void ArrayAndSetTest() {
		try {
			List<String> list = new ArrayList<>();
			list.add("hello");
			list.add(" world");

			int[] array = new int[3];
			array[0] = 0;
			array[1] = 1;
			array[2] = 3;

			Map<String, Date> map = new HashMap<>();
			map.put("date", Calendar.getInstance().getTime());

			Map<String, Object> env = new HashMap<String, Object>();
			env.put("list", list);
			env.put("array", array);
			env.put("mmap", map);

			String message = (String) AviatorEvaluator.execute("list[0] + list[1]", env);
			System.out.println("list[0] + list[1] = " + message);

			String express = (String) AviatorEvaluator
					.execute("'array[0]+array[1]+array[2]=' + (array[0] + array[1] + array[2])", env);
			System.out.println(express);

			System.out.println(AviatorEvaluator.execute("'today is ' + mmap.date ", env));

			// aviator拥有强大的操作集合和数组的seq库
			// 在aviator中, 数组以及java.util.Collection下的子类都称为seq
			// map(seq,fun)             将函数fun作用到集合seq每个元素上, 返回新元素组成的集合
			// filter(seq,predicate)    将谓词predicate作用在集合的每个元素上,返回谓词为true的元素组成的集合
			// count(seq)               返回集合大小
			// include(seq,element)     判断element是否在集合seq中,返回boolean值
			// sort(seq)                排序集合,仅对数组和List有效,返回排序后的新集合
			// reduce(seq,fun,init)     fun接收两个参数,第一个是集合元素, 第二个是累积的函数,本函数用于将fun作用在集合每个元素和初始值上面,返回最终的init值
			// seq.eq(value)    返回一个谓词,用来判断传入的参数是否跟value相等,用于filter函数,如filter(seq,seq.eq(3))过滤返回等于3的元素组成的集合
			// seq.neq(value)   与seq.eq类似,返回判断不等于的谓词
			// seq.gt(value)    返回判断大于value的谓词
			// seq.ge(value)    返回判断大于等于value的谓词
			// seq.lt(value)    返回判断小于value的谓词
			// seq.le(value)    返回判断小于等于value的谓词
			// seq.nil()        返回判断是否为nil的谓词
			// seq.exists()     返回判断不为nil的谓词
			List<Integer> nums = new ArrayList<>();
			nums.add(10);
			nums.add(30);
			nums.add(66);
			env.put("nums", nums);

			Object result = AviatorEvaluator.execute("count(nums)", env);
			System.out.println(result.getClass() + " : " + result); // 3
			// 求和: reduce(nums,+,0), reduce函数接收三个参数,第一个是seq,第二个是聚合的函数,如+等,第三个是聚合的初始值
			result = AviatorEvaluator.execute("reduce(nums,+,0)", env);
			System.out.println(result);
			// 过滤: filter(nums,seq.gt(9)), 过滤出nums中所有大于9的元素并返回集合; seq.gt函数用于生成一个谓词,表示大于某个值
			result = AviatorEvaluator.execute("filter(nums,seq.gt(20))", env);
			System.out.println(result);
			// 判断元素在不在集合里: include(nums,10)
			result = AviatorEvaluator.execute("include(nums,10)", env);
			System.out.println(result);
			// 排序: sort(nums)
			result = AviatorEvaluator.execute("sort(nums)", env);
			System.out.println(result);
			// 遍历整个集合: map(nums,println), map接受的第二个函数将作用于集合中的每个元素,这里简单地调用println打印每个元素
			AviatorEvaluator.execute("map(nums,println)", env);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 正则表达式
	@Test
	public void regulExpTest() {
		String email = "killme2008@gmail.com";
		Map<String, Object> env = new HashMap<String, Object>();
		env.put("email", email);
		// email与正则表达式([\w0-8]+@\w+[\.\w+]+)通过=~操作符来匹配
		// 通过//括起来的字符序列构成一个正则表达式
		// 结果为一个 Boolean 类型，因此可以用三元表达式判断
		// 匹配成功的时候返回$1（指代正则表达式的分组1），也就是用户名，否则返回unknown
		// 匹配成功后, Aviator 会自动将匹配成功的分组放入$num的变量中，其中$0指代整个匹配的字符串。而$1表示第一个分组，以此类推
		String username = (String) AviatorEvaluator.execute("email=~/([\\w0-8]+)@\\w+[\\.\\w+]+/ ? $1 : 'unknow' ",
				env);
		System.out.println(username);
	}

	@Test
	public void javaEntityTest() {
		try {
			UserInfo user = new UserInfo();
			user.setName("rockpile");
			user.setAge(36);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String date = "2020-01-01";
			user.setCreateDate(format.parse(date));
			user.setInterests(Lists.newArrayList("tea", "coffee"));

			Map<String, String> tags = new HashMap<>();
			tags.put("job", "programmer");
			user.setTags(tags);

			Map<String, Object> env = new HashMap<>();
			env.put("user", user);

			System.out.println(AviatorEvaluator.execute("'user.age = '+user.age", env));
			System.out.println(AviatorEvaluator.execute("'user.name = '+user.name", env));
			System.out.println(AviatorEvaluator.execute("'user.createDate.year = '+(user.createDate.year+1900)", env));
			System.out.println(AviatorEvaluator.execute("'user.interests[0] = '+user.interests[0]", env));
			System.out.println(AviatorEvaluator.execute("'user.tags.job = '+user.tags.job", env));

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void dateTest() {
		// Aviator 并不支持日期类型，如果要比较日期，需要将日期写字符串的形式
		// 并且要求是形如"yyyy-MM-dd HH:mm:ss:SS"的字符串,否则都将报错
		// sysdate()          返回当前日期对象java.util.Date
		// now()              返回System.currentTimeMillis
		Map<String, Object> env = new HashMap<String, Object>();
		Date date = Calendar.getInstance().getTime();
		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(date);
		env.put("date", date);
		env.put("dateStr", dateStr);

		Boolean result = (Boolean) AviatorEvaluator.execute("date==dateStr", env);
		System.out.println(result); // true

		result = (Boolean) AviatorEvaluator.execute("date > '2010-12-20 00:00:00:00' ", env);
		System.out.println(result); // true

		result = (Boolean) AviatorEvaluator.execute("date < '2200-12-20 00:00:00:00' ", env);
		System.out.println(result); // true

		result = (Boolean) AviatorEvaluator.execute("date==date ", env);
		System.out.println(result); // true
	}

	@Test
	public void customizeFuncTest() {
		// 注册自定义函数
		AviatorEvaluator.addFunction(new DemoCustomizeFunc());
		Double sum = (Double) AviatorEvaluator.execute("addition(1.2, 2.6)");
		System.out.println("sum : " + sum);

		Map<String, Object> env = new HashMap<>();
		env.put("num1", Double.valueOf(3.3));
		env.put("num2", Double.valueOf(3.3));
		Double res = (Double) AviatorEvaluator.execute("addition(addition(num1, num2), 2.2)", env);
		System.out.println("res : " + res);
	}

	// 上面例子都是直接执行表达式, 事实上Aviator做了编译并执行的工作
	// 可以自己先编译表达式, 返回一个编译的结果, 然后传入不同的env来复用编译结果, 提高性能, 这是更推荐的使用方式
	@Test
	public void expressCompileTest() {
		// 比较运算符!=、==、>、>=、<、<=不仅可以用于数值
		// 也可用于String、Pattern、Boolean，甚至是任何传入的两个实现java.lang.Comparable接口的对象
		String expression = "x-(y-z)>100";

		boolean cached = true;
		// 编译表达式
		// Expression compiledExp = AviatorEvaluator.compile(expression);
		// 编译后的结果可以自己缓存, 也可交Aviator帮你缓存
		// AviatorEvaluator 内部有一个全局的缓存池，如果你决定缓存编译结果, 可以通过
		Expression compiledExp = AviatorEvaluator.compile(expression, cached);
		Map<String, Object> env = new HashMap<>();
		env.put("x", 100.3);
		env.put("y", 45);
		env.put("z", -199.100);

		// 执行表达式
		Boolean result = (Boolean) compiledExp.execute(env);
		System.out.println(result);

		// 使缓存失效
		AviatorEvaluator.invalidateCache(expression);
	}

}
