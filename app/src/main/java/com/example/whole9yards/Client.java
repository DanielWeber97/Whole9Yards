package com.example.whole9yards;

public class Client {
    String clientName;
    String clientNumber;
    String clientAddress;

    public Client(String clientName, String clientNumber, String clientAddress) {
        this.clientName = clientName;
        this.clientNumber = clientNumber;
        this.clientAddress = clientAddress;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public String getClientAddress() {
        return clientAddress;
    }
}
