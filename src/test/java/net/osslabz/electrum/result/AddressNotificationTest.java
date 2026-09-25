package net.osslabz.electrum.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class AddressNotificationTest {

    @Test
    void deserializesAScriptHashStatusNotification() throws Exception {

        // Notification format of blockchain.scripthash.subscribe, Electrum protocol 1.4.
        String json = """
                {"jsonrpc": "2.0",
                 "method": "blockchain.scripthash.subscribe",
                 "params": ["8b01df4e368ea28f8dc0423bcf7a4923e3a12d307c875e47a0cfbf90b5c39161",
                            "f0f2b5c2a9d6f1b3c4e5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7"]}
                """;

        AddressNotification notification = new ObjectMapper().readValue(json, AddressNotification.class);

        assertEquals("2.0", notification.getJsonrpc());
        assertNull(notification.getId());
        assertEquals("blockchain.scripthash.subscribe", notification.getMethod());
        assertEquals(
                List.of(
                        "8b01df4e368ea28f8dc0423bcf7a4923e3a12d307c875e47a0cfbf90b5c39161",
                        "f0f2b5c2a9d6f1b3c4e5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7"),
                notification.getParams());
    }
}
