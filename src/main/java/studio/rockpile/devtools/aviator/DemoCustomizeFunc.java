package studio.rockpile.devtools.aviator;

import java.util.Map;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

public class DemoCustomizeFunc extends AbstractFunction {

	@Override
	public String getName() {
		return "addition";
	}

	@Override
	public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
		// FunctionUtils.getStringValue(arg, env)
		// FunctionUtils.getJavaObject(arg, env)
		Number x = FunctionUtils.getNumberValue(arg1, env);
		Number y = FunctionUtils.getNumberValue(arg2, env);
		
		AviatorDouble res = new AviatorDouble(x.doubleValue() + y.doubleValue());
		return res;
	}

}
