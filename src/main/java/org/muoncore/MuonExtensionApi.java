package org.muoncore;

import org.muoncore.internal.Dispatcher;
import org.muoncore.transports.MuonEventRegister;
import org.muoncore.transports.MuonEventTransport;
import org.muoncore.transports.MuonResourceRegister;
import org.muoncore.transports.MuonStreamRegister;

import java.util.Collections;
import java.util.List;

/**
 * Passed into library extensions to allow interfering with and inspection of the runtime
 */
public class MuonExtensionApi {

    private List<MuonEventTransport> transports;
    private Muon muon;
    private Dispatcher dispatcher;
    private List<MuonExtension> extensions;
    private List<MuonResourceRegister> resources;
    private List<MuonEventRegister> events;
    private List<MuonStreamRegister> streams;
    private List<String> tags;

    public MuonExtensionApi(
            Muon muon,
            List<String> tags,
            List<MuonEventTransport> transports,
            Dispatcher dispatcher,
            List<MuonExtension> extensions,
            List<MuonEventRegister> events,
            List<MuonStreamRegister> streams,
            List<MuonResourceRegister> resource) {
        this.muon = muon;
        this.tags = tags;
        this.transports = transports;
        this.dispatcher = dispatcher;
        this.extensions = extensions;
        this.resources = resource;
        this.events = events;
        this.streams = streams;
    }

    public List<MuonEventRegister> getEvents() {
        return events;
    }

    public List<MuonResourceRegister> getResources() {
        return resources;
    }

    public List<MuonStreamRegister> getStreams() {
        return streams;
    }

    public List<MuonExtension> getExtensions() {
        return extensions;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public MuonService getMuon() {
        return muon;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<MuonEventTransport> getTransports() {
        return Collections.unmodifiableList(transports);
    }

    public void addTransport(MuonEventTransport transport) {
        muon.registerTransport(transport);
    }

    void setTransports(List<MuonEventTransport> transports) {
        this.transports = transports;
    }
}



