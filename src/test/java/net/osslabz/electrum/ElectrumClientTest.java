package net.osslabz.electrum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import net.osslabz.bitcoin.Network;
import net.osslabz.electrum.result.ServerVersion;
import net.osslabz.electrum.result.TxListEntry;
import net.osslabz.jsonrpc.JsonRpcException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ElectrumClientTest {

    // Example from the Electrum protocol docs: the genesis block address and its reversed script hash.
    private static final String GENESIS_ADDRESS = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa";

    private static final String GENESIS_SCRIPT_HASH =
            "8b01df4e368ea28f8dc0423bcf7a4923e3a12d307c875e47a0cfbf90b5c39161";

    private MockJsonRpcServer server;

    @BeforeEach
    void startServer() throws IOException {

        server = new MockJsonRpcServer();
    }

    @AfterEach
    void stopServer() throws IOException {

        server.close();
    }

    private ElectrumClient connect() {

        return new ElectrumClient(Network.MAIN_NET, server.getHost(), server.getPort());
    }

    private JsonNode onlyRequest() {

        List<JsonNode> requests = server.requests();
        assertEquals(1, requests.size());
        return requests.get(0);
    }

    @Test
    void getServerVersionNegotiatesProtocolAndMapsTheReply() throws IOException {

        server.handle("server.version", params -> List.of("electrs-esplora 0.4.1", "1.4"));

        try (ElectrumClient client = connect()) {
            assertEquals(new ServerVersion("electrs-esplora 0.4.1", 1.4), client.getServerVersion());
        }
        assertEquals("[\"Electrum\",\"1.4\"]", onlyRequest().get("params").toString());
    }

    @Test
    void getServerVersionRejectsAResultThatIsNoArray() throws IOException {

        server.handle("server.version", params -> "1.4");

        try (ElectrumClient client = connect()) {
            JsonRpcException e = assertThrows(JsonRpcException.class, client::getServerVersion);
            assertEquals("Invalid JSON-RPC Response (string array expected)", e.getMessage());
        }
    }

    @Test
    void getServerVersionRejectsAnArrayWithoutTwoElements() throws IOException {

        server.handle("server.version", params -> List.of("electrs-esplora 0.4.1"));

        try (ElectrumClient client = connect()) {
            JsonRpcException e = assertThrows(JsonRpcException.class, client::getServerVersion);
            assertEquals("Invalid JSON-RPC Response (string array with 2 elements expected)", e.getMessage());
        }
    }

    @Test
    void getServerVersionPassesOnTheServerError() throws IOException {

        try (ElectrumClient client = connect()) {
            JsonRpcException e = assertThrows(JsonRpcException.class, client::getServerVersion);
            assertEquals("-32601: Method not found", e.getMessage());
        }
    }

    @Test
    void scriptHashGetHistoryMapsEveryEntryIncludingUnknownFields() throws IOException {

        server.handle(
                "blockchain.scripthash.get_history",
                params -> List.of(
                        Map.of("tx_hash", "aa", "height", 887746),
                        Map.of("tx_hash", "bb", "height", 0, "fee", 141, "mempool_rank", 3)));

        List<TxListEntry> history;
        try (ElectrumClient client = connect()) {
            history = client.scriptHashGetHistory(GENESIS_SCRIPT_HASH);
        }

        assertEquals(2, history.size());
        assertEquals("aa", history.get(0).getTxHash());
        assertEquals(887746, history.get(0).getHeight());
        assertNull(history.get(0).getFee());
        assertTrue(history.get(0).getAdditionalProperties().isEmpty());
        assertEquals("bb", history.get(1).getTxHash());
        assertEquals(0, history.get(1).getHeight());
        assertEquals(141, history.get(1).getFee());
        assertEquals(Map.of("mempool_rank", 3), history.get(1).getAdditionalProperties());
        assertEquals(
                "[\"" + GENESIS_SCRIPT_HASH + "\"]", onlyRequest().get("params").toString());
    }

    @Test
    void scriptHashGetHistoryPassesOnTheServerError() throws IOException {

        try (ElectrumClient client = connect()) {
            JsonRpcException e =
                    assertThrows(JsonRpcException.class, () -> client.scriptHashGetHistory(GENESIS_SCRIPT_HASH));
            assertEquals("-32601: Method not found", e.getMessage());
        }
    }

    @Test
    void addressGetHistoryQueriesTheReversedScriptHashOfTheAddress() throws IOException {

        server.handle("blockchain.scripthash.get_history", params -> List.of());

        try (ElectrumClient client = connect()) {
            assertEquals(List.of(), client.addressGetHistory(GENESIS_ADDRESS));
        }
        JsonNode request = onlyRequest();
        assertEquals("blockchain.scripthash.get_history", request.get("method").asText());
        assertEquals("[\"" + GENESIS_SCRIPT_HASH + "\"]", request.get("params").toString());
    }

    @Test
    void addressGetHistoryRejectsAnInvalidAddressWithoutCallingTheServer() throws IOException {

        try (ElectrumClient client = connect()) {
            assertThrows(IllegalArgumentException.class, () -> client.addressGetHistory("not-an-address"));
        }
        assertTrue(server.requests().isEmpty());
    }

    @Test
    void constructorFailsWhenNoServerListens() throws IOException {

        int unusedPort;
        try (ServerSocket socket = new ServerSocket(0, 0, InetAddress.getLoopbackAddress())) {
            unusedPort = socket.getLocalPort();
        }

        assertThrows(
                JsonRpcException.class,
                () -> new ElectrumClient(Network.MAIN_NET, server.getHost(), unusedPort).close());
    }

    @Test
    void closeSilentlyClosesTheConnection() {

        server.handle("server.version", params -> List.of("electrs-esplora 0.4.1", "1.4"));
        ElectrumClient client = connect();

        client.closeSilently();

        JsonRpcException e = assertThrows(JsonRpcException.class, client::getServerVersion);
        assertTrue(e.getMessage().endsWith("Client is closed"), e.getMessage());
    }
}
