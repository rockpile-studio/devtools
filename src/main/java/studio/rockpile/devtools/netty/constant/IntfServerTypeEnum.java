package studio.rockpile.devtools.netty.constant;

import studio.rockpile.devtools.netty.procedure.BaseServiceProcedure;

public enum IntfServerTypeEnum {
	DATA_API_CALL("data-api-call", "com.linewell.form.intf.server.procedure.IntfSvcCallProcedure");

	private final String key;
	private final String className;

	IntfServerTypeEnum(final String key, final String className) {
		this.key = key;
		this.className = className;
	}

	public static BaseServiceProcedure buildProcedure(IntfServerTypeEnum type) throws Exception {
		Class<?> clazz = Class.forName(type.getClassName());
		if (BaseServiceProcedure.class.isAssignableFrom(clazz)) {
			return (BaseServiceProcedure) clazz.newInstance();
		} else {
			throw new Exception("对应的过程处理类(" + type.getClassName() + ")未继承BaseServiceProcedure");
		}
	}

	public static IntfServerTypeEnum getType(String key) {
		IntfServerTypeEnum[] types = IntfServerTypeEnum.values();
		for (IntfServerTypeEnum type : types) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public String getKey() {
		return key;
	}

	public String getClassName() {
		return className;
	}

}
