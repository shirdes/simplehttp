package com.wizbang.web.http;

public final class Main {

    public static void main(String[] args) {
        final Server server = new Server();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stopAsync().awaitTerminated();
            }
        });

        server.startAsync().awaitRunning();
    }
}
