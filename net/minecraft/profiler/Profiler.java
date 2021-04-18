package net.minecraft.profiler;

import org.apache.logging.log4j.*;
import com.google.common.collect.*;
import java.util.*;

public class Profiler
{
    private static final Logger logger;
    private final List<String> sectionList;
    private final List<Long> timestampList;
    public boolean profilingEnabled;
    private String profilingSection;
    private final Map<String, Long> profilingMap;
    
    static {
        logger = LogManager.getLogger();
    }
    
    public Profiler() {
        this.sectionList = (List<String>)Lists.newArrayList();
        this.timestampList = (List<Long>)Lists.newArrayList();
        this.profilingSection = "";
        this.profilingMap = (Map<String, Long>)Maps.newHashMap();
    }
    
    public void clearProfiling() {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }
    
    public void startSection(final String name) {
        if (this.profilingEnabled) {
            if (this.profilingSection.length() > 0) {
                this.profilingSection = String.valueOf(this.profilingSection) + ".";
            }
            this.profilingSection = String.valueOf(this.profilingSection) + name;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(System.nanoTime());
        }
    }
    
    public void endSection() {
        if (this.profilingEnabled) {
            final long i = System.nanoTime();
            final long j = this.timestampList.remove(this.timestampList.size() - 1);
            this.sectionList.remove(this.sectionList.size() - 1);
            final long k = i - j;
            if (this.profilingMap.containsKey(this.profilingSection)) {
                this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + k);
            }
            else {
                this.profilingMap.put(this.profilingSection, k);
            }
            if (k > 100000000L) {
                Profiler.logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + k / 1000000.0 + " ms");
            }
            this.profilingSection = (this.sectionList.isEmpty() ? "" : this.sectionList.get(this.sectionList.size() - 1));
        }
    }
    
    public List<Result> getProfilingData(String p_76321_1_) {
        if (!this.profilingEnabled) {
            return null;
        }
        long i = this.profilingMap.containsKey("root") ? this.profilingMap.get("root") : 0L;
        final long j = this.profilingMap.containsKey(p_76321_1_) ? this.profilingMap.get(p_76321_1_) : -1L;
        final List<Result> list = (List<Result>)Lists.newArrayList();
        if (p_76321_1_.length() > 0) {
            p_76321_1_ = String.valueOf(p_76321_1_) + ".";
        }
        long k = 0L;
        for (final String s : this.profilingMap.keySet()) {
            if (s.length() > p_76321_1_.length() && s.startsWith(p_76321_1_) && s.indexOf(".", p_76321_1_.length() + 1) < 0) {
                k += this.profilingMap.get(s);
            }
        }
        final float f = (float)k;
        if (k < j) {
            k = j;
        }
        if (i < k) {
            i = k;
        }
        for (final String s2 : this.profilingMap.keySet()) {
            if (s2.length() > p_76321_1_.length() && s2.startsWith(p_76321_1_) && s2.indexOf(".", p_76321_1_.length() + 1) < 0) {
                final long l = this.profilingMap.get(s2);
                final double d0 = l * 100.0 / k;
                final double d2 = l * 100.0 / i;
                final String s3 = s2.substring(p_76321_1_.length());
                list.add(new Result(s3, d0, d2));
            }
        }
        for (final String s4 : this.profilingMap.keySet()) {
            this.profilingMap.put(s4, this.profilingMap.get(s4) * 999L / 1000L);
        }
        if (k > f) {
            list.add(new Result("unspecified", (k - f) * 100.0 / k, (k - f) * 100.0 / i));
        }
        Collections.sort(list);
        list.add(0, new Result(p_76321_1_, 100.0, k * 100.0 / i));
        return list;
    }
    
    public void endStartSection(final String name) {
        this.endSection();
        this.startSection(name);
    }
    
    public String getNameOfLastSection() {
        return (this.sectionList.size() == 0) ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
    }
    
    public static final class Result implements Comparable<Result>
    {
        public double field_76332_a;
        public double field_76330_b;
        public String field_76331_c;
        
        public Result(final String p_i1554_1_, final double p_i1554_2_, final double p_i1554_4_) {
            this.field_76331_c = p_i1554_1_;
            this.field_76332_a = p_i1554_2_;
            this.field_76330_b = p_i1554_4_;
        }
        
        @Override
        public int compareTo(final Result p_compareTo_1_) {
            return (p_compareTo_1_.field_76332_a < this.field_76332_a) ? -1 : ((p_compareTo_1_.field_76332_a > this.field_76332_a) ? 1 : p_compareTo_1_.field_76331_c.compareTo(this.field_76331_c));
        }
        
        public int func_76329_a() {
            return (this.field_76331_c.hashCode() & 0xAAAAAA) + 4473924;
        }
    }
}
