package studio.rockpile.devtools.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

// 通过注解的方式实现chunk的监听
public class DemoChunkListener {
    private static final Logger logger = LoggerFactory.getLogger(DemoChunkListener.class);

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        logger.debug("... before chunk {}", context.getStepContext().getJobName());
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        logger.debug("... after chunk {}", context.getStepContext().getJobName());
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        logger.debug("... after error chunk {}", context.getStepContext().getJobName());
    }
}
