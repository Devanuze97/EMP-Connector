package com.devanuze.login;

import com.devanuze.events.EventListener;
import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;
import com.salesforce.emp.connector.LoginHelper;
import com.salesforce.emp.connector.TopicSubscription;
import com.salesforce.emp.connector.example.BearerTokenProvider;
import org.eclipse.jetty.util.ajax.JSON;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static org.cometd.bayeux.Channel.*;

public class DevLogin {
    public static void main(String[] argv) throws Throwable {
        if (argv.length < 4 || argv.length > 5) {
            System.err.println("Usage: DevLoginExample url username password topic [replayFrom]");
            System.exit(1);
        }
        Consumer<Map<String, Object>> consumer = event -> System.out.println(String.format("Received:\n%s", JSON.toString(event)));

        BearerTokenProvider tokenProvider = new BearerTokenProvider(() -> {
            try {
                return LoginHelper.login(new URL(argv[0]), argv[1], argv[2]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BayeuxParameters params = tokenProvider.login();

        //Use EmpConnector as connecter to Salesforce Cometd
        EmpConnector connector = new EmpConnector(params);

        //Use Devanuze eventListener for parsing events
        EventListener eventListener = new EventListener();

        connector.addListener(META_HANDSHAKE, eventListener)
                .addListener(META_CONNECT, eventListener)
                .addListener(META_DISCONNECT, eventListener)
                .addListener(META_SUBSCRIBE, eventListener)
                .addListener(META_UNSUBSCRIBE, eventListener);

        connector.setBearerTokenProvider(tokenProvider);

        connector.start().get(5, TimeUnit.SECONDS);

        long replayFrom = EmpConnector.REPLAY_FROM_EARLIEST;
        if (argv.length == 5) {
            replayFrom = Long.parseLong(argv[4]);
        }
        TopicSubscription subscription;
        try {
            subscription = connector.subscribe(argv[3], replayFrom, consumer).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            System.err.println(e.getCause().toString());
            System.exit(1);
            throw e.getCause();
        } catch (TimeoutException e) {
            System.err.println("Timed out subscribing");
            System.exit(1);
            throw e.getCause();
        }

        System.out.println(String.format("Subscribed: %s", subscription));
    }
}
