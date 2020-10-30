package studio.rockpile.devtools.netty.procedure;

public abstract class BaseServiceProcedure {

	public abstract String call(String content, String[] uriPaths) throws Exception;
}
