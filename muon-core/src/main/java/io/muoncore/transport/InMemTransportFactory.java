package io.muoncore.transport;

import com.google.common.eventbus.EventBus;
import io.muoncore.config.AutoConfiguration;
import io.muoncore.memory.transport.InMemTransport;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemTransportFactory implements MuonTransportFactory {

    private static final String IN_MEM_TRANSPORT_ENABLED_PROPERTY_NAME = "transport.inmem.enabled";
    public static EventBus EVENT_BUS;

    private static Logger LOG = Logger.getLogger(InMemTransportFactory.class.getName());
    private AutoConfiguration autoConfiguration;

    @Override
    public MuonTransport build(Properties properties) {
        MuonTransport transport = null;
        try {
            if (Boolean.valueOf(properties.getProperty(IN_MEM_TRANSPORT_ENABLED_PROPERTY_NAME))) {
                transport = new InMemTransport(autoConfiguration, getSharedEventBus());
            }
        } catch (Exception e) {
            LOG.log(Level.INFO, "Error creating InMemTransport", e);
        }
        return transport;
    }

    private EventBus getSharedEventBus() {
        if (EVENT_BUS == null) {
            synchronized (InMemTransportFactory.class) {
                if (EVENT_BUS == null) {
                    EVENT_BUS = new EventBus();
                }
            }
        }
        return EVENT_BUS;
    }

    @Override
    public void setAutoConfiguration(AutoConfiguration autoConfiguration) {
        this.autoConfiguration = autoConfiguration;
    }
}