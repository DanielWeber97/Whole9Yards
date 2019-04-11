package com.example.whole9yards;

public class Client {
    String clientId;
    String clientName;
    String clientNumber;
    String clientAddress;

    public Client(String clientId, String clientName, String clientNumber, String clientAddress) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientNumber = clientNumber;
        this.clientAddress = clientAddress;
    }

   // public Client(){

    //}

    public String getClientId(){
        return clientId;
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
