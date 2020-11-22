package com.terraboxstudios.classchartsapi.obj;

import com.google.gson.JsonObject;

public final class Homework {

    private final JsonObject json;

    public Homework(JsonObject json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
