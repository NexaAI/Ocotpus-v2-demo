package com.nexa4ai.octopustest;

import java.util.ArrayList;

public class MessageHandler {
    private final ArrayList<MessageModal> messageModalArrayList;
    private final MessageRVAdapter messageRVAdapter;

    public MessageHandler(ArrayList<MessageModal> messageModalArrayList, MessageRVAdapter messageRVAdapter) {
        this.messageModalArrayList = messageModalArrayList;
        this.messageRVAdapter = messageRVAdapter;
    }

    public void addMessage(MessageModal message) {
        messageModalArrayList.add(message);
        messageRVAdapter.notifyDataSetChanged();
    }
}