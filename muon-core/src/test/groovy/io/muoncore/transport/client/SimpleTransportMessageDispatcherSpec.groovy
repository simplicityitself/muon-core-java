package io.muoncore.transport.client
import io.muoncore.transport.TransportInboundMessage
import io.muoncore.transport.TransportMessage
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class SimpleTransportMessageDispatcherSpec extends Specification {

    def "distributes data to a listener"() {

        given:

        def data = []

        def dispatcher = new SimpleTransportMessageDispatcher()
        dispatcher.observe({ true }).subscribe(new Subscriber<TransportMessage>() {
            @Override
            void onSubscribe(Subscription s) {
                s.request(100)
            }

            @Override
            void onNext(TransportMessage transportMessage) {
                println "Got data"
                data << transportMessage
            }

            @Override
            void onError(Throwable t) {}

            @Override
            void onComplete() {}
        })

        when:
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())

        then:
        new PollingConditions().eventually {
            data.size() == 5
        }
    }

    def "distributes data to multiple listener"() {

        given:

        def data = []

        def dispatcher = new SimpleTransportMessageDispatcher()
        dispatcher.observe({ true }).subscribe(new Subscriber<TransportMessage>() {
            @Override
            void onSubscribe(Subscription s) {
                s.request(100)
            }

            @Override
            void onNext(TransportMessage transportMessage) {
                println "Got data"
                data << transportMessage
            }

            @Override
            void onError(Throwable t) {}

            @Override
            void onComplete() {}
        })
        dispatcher.observe({ true }).subscribe(new Subscriber<TransportMessage>() {
            @Override
            void onSubscribe(Subscription s) {
                s.request(100)
            }

            @Override
            void onNext(TransportMessage transportMessage) {
                println "Got data"
                data << transportMessage
            }

            @Override
            void onError(Throwable t) {}

            @Override
            void onComplete() {}
        })
        dispatcher.observe({ true }).subscribe(new Subscriber<TransportMessage>() {
            @Override
            void onSubscribe(Subscription s) {
                s.request(100)
            }

            @Override
            void onNext(TransportMessage transportMessage) {
                println "Got data"
                data << transportMessage
            }

            @Override
            void onError(Throwable t) {}

            @Override
            void onComplete() {}
        })

        when:
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())
        dispatcher.dispatch(inbound())

        then:
        new PollingConditions().eventually {
            data.size() == 15
        }
    }

    def inbound() {
        new TransportInboundMessage(
                "mydata",
                "faked",
                "myTarget",
                "mySource",
                "streamish",
                [:],
                "application/json+AES",
                [] as byte[],
                []
        )
    }
}