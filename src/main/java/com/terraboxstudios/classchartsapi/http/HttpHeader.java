package com.terraboxstudios.classchartsapi.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class HttpHeader {

    private final String name, value;

}