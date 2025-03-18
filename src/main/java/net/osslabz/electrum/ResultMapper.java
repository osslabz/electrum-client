package net.osslabz.electrum;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import net.osslabz.electrum.result.ServerVersion;
import net.osslabz.jsonrpc.JsonRcpException;


public class ResultMapper {

    ServerVersion mapToServerVersion(JsonNode jsonNode) {

        if (!jsonNode.isArray()) {
            throw new JsonRcpException("Invalid JSON-RPC Response (string array expected)");
        }
        List<String> values = new ArrayList<>();
        jsonNode.iterator().forEachRemaining(e -> values.add(e.asText()));

        if (values.size() != 2) {
            throw new JsonRcpException("Invalid JSON-RPC Response (string array with 2 elements expected)");
        }
        return new ServerVersion(values.get(0), Double.parseDouble(values.get(1)));

    }
}