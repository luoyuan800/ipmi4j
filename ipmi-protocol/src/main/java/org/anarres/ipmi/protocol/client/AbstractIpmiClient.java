/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.ipmi.protocol.client;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.anarres.ipmi.protocol.client.dispatch.IpmiPayloadReceiveDispatcher;
import org.anarres.ipmi.protocol.client.dispatch.IpmiPayloadTransmitQueue;
import org.anarres.ipmi.protocol.client.session.IpmiSessionManager;
import org.anarres.ipmi.protocol.client.visitor.IpmiHandlerContext;
import org.anarres.ipmi.protocol.packet.rmcp.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public abstract class AbstractIpmiClient implements IpmiClient {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractIpmiClient.class);
    private final IpmiSessionManager sessionManager = new IpmiSessionManager();
    private final IpmiPayloadReceiveDispatcher dispatcher = new IpmiPayloadReceiveDispatcher(sessionManager);
    private final IpmiPayloadTransmitQueue queue = new IpmiPayloadTransmitQueue(this);

    @Override
    public IpmiSessionManager getSessionManager() {
        return sessionManager;
    }

    @PostConstruct
    public abstract void start() throws IOException, InterruptedException;

    @VisibleForTesting
    public void receive(@Nonnull Packet packet) throws IOException {
        LOG.info("Receive\n" + packet);
        IpmiHandlerContext context = new IpmiHandlerContext(this, (InetSocketAddress) packet.getRemoteAddress());
        packet.apply(dispatcher, context);
    }

    @PreDestroy
    public abstract void stop() throws IOException, InterruptedException;
}
