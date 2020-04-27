package com.devanuze.events;

import org.cometd.bayeux.Message;

import java.util.Map;

public class EventHandler {
    public void handleEvent(Message message) {
        debugPrinter(message);

    }

    private void debugPrinter(Message message) {
        System.out.println("Event Handling...");
        System.out.println("==================");
        System.out.println("Message Success  : " + message.isSuccessful());
        System.out.println("Message String   : " + message.toString());
        System.out.println("Message Data     : " + message.getData());
        System.out.println("Message Advice   : " + message.getAdvice());
        System.out.println("Message ChannelId: " + message.getChannelId());
        System.out.println("Message Channel  : " + message.getChannel());
        System.out.println("^^^^^^^^^^^^^^^^^^");
    }
}
