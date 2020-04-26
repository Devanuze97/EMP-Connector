package com.devanuze.events;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

import java.text.SimpleDateFormat;
import java.util.Date;

//

public class EventListener implements ClientSessionChannel.MessageListener {

    @Override
    public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
        if (message.isSuccessful()) {
            printPrefix();
            System.out.println("{");
            System.out.println(clientSessionChannel.getId()); //eg id: /meta/connect
            System.out.println(message);
            System.out.println("}");
        }
    }

    private void printPrefix() {
        System.out.println("[" + timeNow() + "] ");
    }

    private String timeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        return dateFormat.format(now);
    }
}
