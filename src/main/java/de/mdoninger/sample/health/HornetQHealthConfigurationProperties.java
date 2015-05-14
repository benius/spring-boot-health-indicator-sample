package de.mdoninger.sample.health;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by mdoninger on 30/04/15.
 */
@ConfigurationProperties(prefix = "hornetq_health")
public class HornetQHealthConfigurationProperties {

    /**
     * Threshold per queue name.
     */
    private Map<String, Integer> thresholds;

    private Integer count;

    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Map<String, Integer> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, Integer> thresholds) {
        this.thresholds = thresholds;
    }
}
