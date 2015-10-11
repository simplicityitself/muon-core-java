package io.muoncore.protocol.event.client;

import io.muoncore.channel.ChannelConnection;
import io.muoncore.protocol.event.Event;
import io.muoncore.protocol.requestresponse.Request;
import io.muoncore.protocol.requestresponse.Response;

/**
 * This middleware will accept an Event. It will then attempt to locate an event store to persist it in.
 */
public class EventClientProtocol<X> {

    public EventClientProtocol(ChannelConnection<Response, Event<X>> leftChannelConnection,
                               ChannelConnection<Request<X>, Response> rightChannelConnection) {

        rightChannelConnection.receive( message -> { leftChannelConnection.send(new Response()); });

        leftChannelConnection.receive(event -> {
            Request<X> msg = new Request<X>();
            msg.setId(event.getId());
            rightChannelConnection.send(msg);
        });

        /**
         * lookup event store
         *
         * if no store, send 404 back.
         */
    }
}