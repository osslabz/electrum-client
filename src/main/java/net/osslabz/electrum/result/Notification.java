package net.osslabz.electrum.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification<T> implements Serializable {

    @JsonProperty("jsonrpc")
    private String jsonrpc;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("method")
    private String method;

    @JsonProperty("params")
    private T params;

    public String getJsonrpc() {

        return jsonrpc;
    }

    public Integer getId() {

        return id;
    }

    public String getMethod() {

        return method;
    }

    public T getParams() {

        return params;
    }
}
