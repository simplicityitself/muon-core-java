package io.muoncore.transport.client

import io.muoncore.channel.ChannelConnection
import io.muoncore.protocol.ChannelFunctionExecShimBecauseGroovyCantCallLambda
import io.muoncore.transport.MuonTransport
import io.muoncore.transport.TransportInboundMessage
import io.muoncore.transport.TransportOutboundMessage
import spock.lang.Specification

class SingleTransportChannelConnectionSpec extends Specification {

    def "transport channel requires full connection before sending"() {

        def transport = Mock(MuonTransport)

        def connection = new SingleTransportChannelConnection(transport)

        when:
        connection.send(new TransportOutboundMessage("mymessage", "myService1", "requestresponse"))

        then:
        thrown(IllegalStateException)
    }

    def "transport channel to a service is opened for every new service/proto combo seen"() {

        def transport = Mock(MuonTransport)

        def connection = new SingleTransportChannelConnection(transport)
        connection.receive({})

        when:
        connection.send(new TransportOutboundMessage("mymessage", "myService1", "requestresponse"))
        connection.send(new TransportOutboundMessage("mymessage", "myService2", "requestresponse"))
        connection.send(new TransportOutboundMessage("mymessage", "myService3", "requestresponse"))
        connection.send(new TransportOutboundMessage("mymessage", "myService1", "wibble"))
        connection.send(new TransportOutboundMessage("mymessage", "myService2", "simple"))

        and: "a message that is the same combo as previously seen on this channel"
        connection.send(new TransportOutboundMessage("mymessage", "myService2", "simple"))

        then:

        1 * transport.openClientChannel("myService1", "requestresponse") >> Stub(ChannelConnection)
        1 * transport.openClientChannel("myService2", "requestresponse") >> Stub(ChannelConnection)
        1 * transport.openClientChannel("myService3", "requestresponse") >> Stub(ChannelConnection)
        1 * transport.openClientChannel("myService1", "wibble") >> Stub(ChannelConnection)
        1 * transport.openClientChannel("myService2", "simple") >> Stub(ChannelConnection)

        0 * transport._
    }

    def "all inbound messages on the channels are pushed into the function for back propogation along the channel"() {

        def channelFunctions = []

        //capture the receive functions being generated and passed to our mock functions.
        //slightly elaborate, we are stepping two levels into the interaction mocks.
        def transport = Mock(MuonTransport) {
            openClientChannel(_, _) >> {
                def c = Mock(ChannelConnection) {
                    receive(_) >> { func ->
                        channelFunctions << new ChannelFunctionExecShimBecauseGroovyCantCallLambda(func[0])
                    }
                }
                return c
            }
        }

        def receive = Mock(ChannelConnection.ChannelFunction)

        def connection = new SingleTransportChannelConnection(transport)
        connection.receive(receive)

        when:
        connection.send(new TransportOutboundMessage("mymessage", "myService1", "requestresponse"))
        connection.send(new TransportOutboundMessage("mymessage", "myService2", "requestresponse"))
        connection.send(new TransportOutboundMessage("mymessage", "myService3", "requestresponse"))

        and: "Messages sent down all functions"
        channelFunctions[0].call(new TransportInboundMessage("id", "source", "requestresponse"))
        channelFunctions[1].call(new TransportInboundMessage("id", "source", "requestresponse"))
        channelFunctions[2].call(new TransportInboundMessage("id", "source", "requestresponse"))
        channelFunctions[2].call(new TransportInboundMessage("id", "source", "requestresponse"))
        channelFunctions[1].call(new TransportInboundMessage("id", "source", "requestresponse"))
        channelFunctions[0].call(new TransportInboundMessage("id", "source", "requestresponse"))
        channelFunctions[1].call(new TransportInboundMessage("id", "source", "requestresponse"))

        then:
        channelFunctions.size() == 3
        7 * receive.apply(_)
    }
}