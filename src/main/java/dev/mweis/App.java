package dev.mweis;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages.LOGGER;

public class App {

    @Outgoing("cloudevents-out")
    public Multi<String> toCloudEvents() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .map(x -> "Hello World");
    }

    @Incoming("cloudevents-in")
    public CompletionStage<Void> process(Message<String> msg) {
        IncomingCloudEventMetadata<Integer> cloudEventMetadata = msg.getMetadata(IncomingCloudEventMetadata.class)
            .orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));

        System.out.println(String.format("Received Cloud Events (spec-version: %s): source:  '%s', type: '%s', subject: '%s' , data: '%s'",
            cloudEventMetadata.getSpecVersion(),
            cloudEventMetadata.getSource(),
            cloudEventMetadata.getType(),
            cloudEventMetadata.getSubject().orElse("no subject"),
            cloudEventMetadata.getData()));

        return msg.ack();
    }

    @Incoming("cloudevents-in")
    public CompletionStage<Void> process2(Message<String> msg) {
        System.out.println(msg.getPayload());

        return msg.ack();
    }
}
