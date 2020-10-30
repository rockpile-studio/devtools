package studio.rockpile.devtools.netty.handler;

import java.util.List;
import java.util.Map;

import studio.rockpile.devtools.netty.procedure.ExterIntfSvcException;


public abstract class IntfSvcCallBaseHandler {

	public abstract List<Map<String, Object>> perform(Map<String, Object> arguments)
			throws ExterIntfSvcException, Exception;
}
