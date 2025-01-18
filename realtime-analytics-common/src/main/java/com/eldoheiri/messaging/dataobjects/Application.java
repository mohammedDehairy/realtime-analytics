package com.eldoheiri.messaging.dataobjects;

public enum Application {
    IMOTION("iMotion: Stop Motion","Create stop motion animations with iMotion", "imotion");

    private final String name;
    private final String description;
    private final String id;

    Application(String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public static Application fromId(String id) {
        for (Application application : Application.values()) {
            if (application.getId().equals(id)) {
                return application;
            }
        }
        return null;
    }
}
