package com.groo.chatapp.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString

public class DataResponse<T> extends BaseResDto {

    private final T data;

    @JsonCreator
    private DataResponse(@JsonProperty("data") T data) {
        super();
        this.data = data;
    }

    private DataResponse(@JsonProperty("data") T data, String message) {
        super(message);
        this.data = data;
    }

    public static <T> DataResponse<T> create(T data) {
        return new DataResponse<>(data);
    }

    public static <T> DataResponse<T> create(T data, String message) {
        return new DataResponse<>(data, message);
    }

    public static <T> DataResponse<T> empty() {
        return new DataResponse<>(null);
    }

    public static <T> DataResponse<T> of(T data, String message) {
        return new DataResponse<>(data, message);
    }
}
