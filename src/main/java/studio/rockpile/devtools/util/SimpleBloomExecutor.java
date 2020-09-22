package studio.rockpile.devtools.util;

import java.nio.charset.Charset;

import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;

public class SimpleBloomExecutor {
	private final static Double BLOOM_FILTER_ERR_RATE = Double.valueOf(0.01);
	private final static Long BLOOM_FILTER_INIT_SIZE = Long.valueOf(1000);

	private Funnel<CharSequence> funnel = null;
	private Long initSize = BLOOM_FILTER_INIT_SIZE;
	private Double errorRate = BLOOM_FILTER_ERR_RATE;
	private Integer bitSize = null;
	private Integer funcNum = null;

	public SimpleBloomExecutor() {
		this.bitSize = optimalBitSize();
		this.funcNum = optimalHashFuncNum();
		this.funnel = Funnels.stringFunnel(Charset.forName("UTF-8"));
	}

	public SimpleBloomExecutor(Long initSize, Double errorRate) {
		this.initSize = initSize;
		this.errorRate = errorRate;
		this.bitSize = optimalBitSize();
		this.funcNum = optimalHashFuncNum();
		this.funnel = Funnels.stringFunnel(Charset.forName("UTF-8"));
	}

	public long[] hashOffset(String value) {
		long[] offset = new long[this.funcNum];

		long hash64 = Hashing.murmur3_128().hashString(value, Charset.forName("UTF-8")).asLong();
		int hash1 = (int) hash64;
		int hash2 = (int) (hash64 >>> 32);

		for (int i = 1; i <= this.funcNum; i++) {
			int combinedHash = hash1 + i * hash2;
			if (combinedHash < 0) {
				combinedHash = ~combinedHash;
			}
			offset[i - 1] = combinedHash % this.bitSize;
		}
		return offset;
	}

	private Integer optimalBitSize() {
		if (this.errorRate == 0) {
			errorRate = Double.MIN_VALUE;
		}
		return Integer.valueOf((int) (-initSize * Math.log(errorRate) / (Math.log(2) * Math.log(2))));
	}

	private Integer optimalHashFuncNum() {
		return Math.max(1, (int) Math.round((double) bitSize / initSize * Math.log(2)));
	}

	public Funnel<CharSequence> getFunnel() {
		return funnel;
	}

	public Long getInitSize() {
		return initSize;
	}

	public Double getErrorRate() {
		return errorRate;
	}

	public Integer getBitSize() {
		return bitSize;
	}

	public Integer getFuncNum() {
		return funcNum;
	}

}
