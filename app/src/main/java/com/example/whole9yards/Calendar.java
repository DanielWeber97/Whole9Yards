package com.example.whole9yards;

public class Calendar {
    String clientId;
    String clientName;
    String clientDateOfService;
    String clientRepeat;
    String clientAddress;

    public Calendar(String clientId, String clientName, String clientDateOfService, String clientRepeat, String clientAddress) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientDateOfService = clientDateOfService;
        this.clientRepeat = clientRepeat;
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

    public String getClientDateOfService() {
        return clientDateOfService;
    }

    public String getClientRepeat() {
        return clientRepeat;
    }

    public String getClientAddress() {
        return clientAddress;
    }
}
