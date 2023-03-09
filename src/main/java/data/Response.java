package data;

import lombok.Data;

@Data
public class Response<T> {
    private T body;
    private int statusCode;
}