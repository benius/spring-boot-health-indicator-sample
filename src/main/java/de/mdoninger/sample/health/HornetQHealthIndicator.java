package de.mdoninger.sample.health;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by mdoninger on 30/04/15.
 */
@Component
@EnableConfigurationProperties(HornetQHealthConfigurationProperties.class)
public class HornetQHealthIndicator implements HealthIndicator {

    private final ClientSessionFactory sf;

    private final String[] queueNames;

    private final HornetQHealthConfigurationProperties properties;

    @Autowired
    public HornetQHealthIndicator(ClientSessionFactory sf, EmbeddedJMS embeddedJMS, HornetQHealthConfigurationProperties properties) {
        this.sf = sf;
        this.properties = properties;
        this.queueNames = embeddedJMS.getHornetQServer().getHornetQServerControl().getQueueNames();
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        builder.up();
        for (Map.Entry<String, Integer> thresholdEntry : properties.getThresholds().entrySet()) {
            long queueSize = queryQueueSize(thresholdEntry.getKey());

            if (queueSize > thresholdEntry.getValue()) {
                builder.down().withDetail(thresholdEntry.getKey(), thresholdEntry.getKey() + " size exceeds threshold. Current size: "
                        + queueSize + "; threshold: " + thresholdEntry.getValue());
            }
        }
        return builder.build();
    }

    private long queryQueueSize(String queueName) {
        ClientSession coreSession = null;
        long count = 0;
        try {
            coreSession = sf.createSession(false, false, false);
            ClientSession.QueueQuery result;
            result = coreSession.queueQuery(new SimpleString("jms.queue." + queueName));
            count = result.getMessageCount();
        } catch (HornetQException e) {
            e.printStackTrace();
        } finally {
            if (coreSession != null) {
                try {
                    coreSession.close();
                } catch (HornetQException e) {
                    e.printStackTrace();
                }
            }
        }
        return count;
    }
}
