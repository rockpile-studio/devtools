package studio.rockpile.devtools.netty.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

public class LoadPackageClasseUtil {
	private static final Logger logger = LoggerFactory.getLogger(LoadPackageClasseUtil.class);
	private static final String RESOURCE_PATTERN = "/**/*.class";

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private List<String> packages = new ArrayList<String>();
	private List<TypeFilter> filters = new ArrayList<TypeFilter>();

	// packagesToScan 指定哪些包需要被扫描,支持多个包"package.a,package.b"并对每个包都会递归搜索
	// annotationFilter 指定扫描包中含有特定注解标记的bean,支持多个注解
	@SuppressWarnings("unchecked")
	public LoadPackageClasseUtil(List<String> packages, Class<? extends Annotation>... annotationFilter) {
		if (packages != null) {
			for (String pkg : packages) {
				this.packages.add(pkg.trim());
			}
		}
		if (annotationFilter != null) {
			for (Class<? extends Annotation> annotation : annotationFilter) {
				filters.add(new AnnotationTypeFilter(annotation, false));
			}
		}
	}

	public Set<Class<?>> getClassSet() throws IOException, ClassNotFoundException {
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		classSet.clear();

		for (int i = 0; i < packages.size(); i++) {
			String pkg = packages.get(i);
			String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
			Resource[] resources = this.resourcePatternResolver.getResources(pattern);
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader reader = readerFactory.getMetadataReader(resource);
					String className = reader.getClassMetadata().getClassName();
					if (matchesEntityTypeFilter(reader, readerFactory)) {
						classSet.add(Class.forName(className));
						if (logger.isInfoEnabled()) {
							logger.info(String.format("Found class:%s", className));
						}
					}
				}
			}
		}
		return classSet;
	}

	private boolean matchesEntityTypeFilter(MetadataReader reader, MetadataReaderFactory readerFactory)
			throws IOException {
		if (!this.filters.isEmpty()) {
			for (TypeFilter filter : this.filters) {
				if (filter.match(reader, readerFactory)) {
					return true;
				}
			}
		}
		return false;
	}

}
