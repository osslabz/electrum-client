package net.osslabz.electrum.result;

public record ServerVersion(String name, Double protocolVersion) {


    public ServerVersion(String name, Double protocolVersion) {

        this.name = name;
        this.protocolVersion = protocolVersion;
    }
}