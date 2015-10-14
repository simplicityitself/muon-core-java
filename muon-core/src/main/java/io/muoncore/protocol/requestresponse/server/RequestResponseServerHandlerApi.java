package io.muoncore.protocol.requestresponse.server;

import io.muoncore.protocol.requestresponse.Request;
import io.muoncore.transport.TransportClientSource;
import org.reactivestreams.Publisher;

import java.util.function.Predicate;

public interface RequestResponseServerHandlerApi extends
        RequestResponseHandlersSource {

    default <X, Y> Publisher<RequestWrapper<X,Y>> handleRequest(Predicate<Request<X>> request) {

        //generate a handler from the predicate

        //some default predicates?

        //create a publisher will somehow be linked to the channels?
        // should publisher be the real api?

        //have another, more traditional api as well?

        //the wrappers created should be linked to the channel.

        return null;
    }
}
