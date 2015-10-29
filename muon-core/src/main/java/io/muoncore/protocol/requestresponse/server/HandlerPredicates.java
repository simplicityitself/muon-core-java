package io.muoncore.protocol.requestresponse.server;

import io.muoncore.protocol.requestresponse.RequestMetaData;

import java.util.function.Predicate;

public class HandlerPredicates {

    /**
     * Matches all requests coming into a service.
     *
     * If you use this handler, all requests will be matched.
     *
     * If you want a default, only picked if no others match, then investigate overriding the
     * default Handler.
     *
     */
    public static HandlerPredicate all() {
        return new HandlerPredicate() {
            @Override
            public String resourceString() {
                return "/*";
            }

            @Override
            public Predicate<RequestMetaData> matcher() {
                return meta -> true;
            }
        };
    }

    public static HandlerPredicate none() {
        return new HandlerPredicate() {
            @Override
            public String resourceString() {
                return "-";
            }

            @Override
            public Predicate<RequestMetaData> matcher() {
                return meta -> false;
            }
        };
    }

    /**
     * Match requests for a given fixed endpoint.
     *
     * Exact matching
     *
     * @param path The path to match exactly on the request.
     */
    public static HandlerPredicate path(String path) {
        return new HandlerPredicate() {
            @Override
            public String resourceString() {
                return path;
            }

            @Override
            public Predicate<RequestMetaData> matcher() {
                return msg -> msg.getUrl().equals(path);
            }
        };
    }
}
