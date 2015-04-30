package de.mdoninger.sample.health;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Created by mdoninger on 30/04/15.
 */
public class HornetQHealthIndicator implements HealthIndicator {

    private final ClientSessionFactory sf;

    private final String[] queueNames;

    public HornetQHealthIndicator(ClientSessionFactory sf, String[] queueNames) {
        this.sf = sf;
        this.queueNames = queueNames;
    }

    @Override
    public Health health() {
        long queueSize = queryQueueSizes();
        Health.Builder builder = new Health.Builder();
        if (queueSize > 10) {
            return builder.down().withDetail("reason", "testQueue size exceeds threshold").build();
        }
        return new Health.Builder().up().build();
    }

    private long queryQueueSizes() {
        ClientSession coreSession = null;
        long count = 0;
        try {
            for (String queueName : queueNames) {
                coreSession = sf.createSession(false, false, false);
                ClientSession.QueueQuery result;
                result = coreSession.queueQuery(new SimpleString(queueName));
                count = result.getMessageCount();
            }
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
