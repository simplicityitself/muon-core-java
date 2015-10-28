package io.muoncore.protocol.requestresponse.server;

import io.muoncore.Discovery;
import io.muoncore.channel.ChannelConnection;
import io.muoncore.channel.async.StandardAsyncChannel;
import io.muoncore.codec.Codecs;
import io.muoncore.protocol.ServerProtocolStack;
import io.muoncore.protocol.requestresponse.RRPTransformers;
import io.muoncore.protocol.requestresponse.Request;
import io.muoncore.protocol.requestresponse.RequestMetaData;
import io.muoncore.protocol.requestresponse.Response;
import io.muoncore.transport.TransportInboundMessage;
import io.muoncore.transport.TransportOutboundMessage;

/**
 * Server side of the Requestr Response protocol.
 *
 * Transports open channels on this protocol when a remote request response client opens a channel through them
 * and sends a first message.
 */
public class RequestResponseServerProtocolStack implements
        ServerProtocolStack {

    private final RequestResponseHandlers handlers;
    private Codecs codecs;
    private Discovery discovery;

    public RequestResponseServerProtocolStack(RequestResponseHandlers handlers,
                                              Codecs codecs,
                                              Discovery discover) {
        this.codecs = codecs;
        this.handlers = handlers;
        this.discovery = discover;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChannelConnection<TransportInboundMessage, TransportOutboundMessage> createChannel() {

        StandardAsyncChannel<TransportOutboundMessage, TransportInboundMessage> api2 = new StandardAsyncChannel<>();

        api2.left().receive( message -> {
            RequestMetaData meta = RRPTransformers.toRequestMetaData(message);
            RequestResponseServerHandler handler = handlers.findHandler(meta);

            Request request = RRPTransformers.toRequest(message, codecs, handler.getRequestType());

            handler.handle(new RequestWrapper() {
                @Override
                public Request getRequest() {
                    return request;
                }

                @Override
                public void answer(Response response) {
                    TransportOutboundMessage msg = RRPTransformers.toOutbound("thiservice",response, codecs,
                            discovery.findService( svc ->
                                    svc.getIdentifier().equals(
                                            request.getMetaData().getTargetService())).get().getCodecs());
                    api2.left().send(msg);
                }
            });
        });

        return api2.right();
    }
}
