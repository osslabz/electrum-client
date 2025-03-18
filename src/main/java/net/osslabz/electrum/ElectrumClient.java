package net.osslabz.electrum;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.osslabz.bitcoin.BitcoinUtils;
import net.osslabz.electrum.result.ServerVersion;
import net.osslabz.electrum.result.TxListEntry;
import net.osslabz.jsonrpc.JsonRpcTcpClient;
import org.bitcoinj.base.BitcoinNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ElectrumClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(ElectrumClient.class);

    private final BitcoinNetwork network;

    private final ResultMapper resultMapper = new ResultMapper();

    private final JsonRpcTcpClient client;


    public ElectrumClient(BitcoinNetwork network, String host, int port) {

        this.network = network;
        this.client = new JsonRpcTcpClient(host, port);
    }


    public List<TxListEntry> addressGetHistory(String addr) {

        String reversedScriptHash = BitcoinUtils.convertAddressToReversedScriptHash(this.network, addr);
        return this.scriptHashGetHistory(reversedScriptHash);
    }


    public List<TxListEntry> scriptHashGetHistory(String reversedScriptHash) {

        List<TxListEntry> result = this.client.callAndMapList("blockchain.scripthash.get_history", Collections.singleton(reversedScriptHash), TxListEntry.class);
        return result;
    }


    public ServerVersion getServerVersion() {

        JsonNode result = this.client.call("server.version", List.of("Electrum", "1.4"));
        return this.resultMapper.mapToServerVersion(result);
    }


    @Override
    public void close() throws IOException {

        this.client.close();
    }


    public void closeSilently() {

        try {
            this.close();
        } catch (IOException e) {
            log.warn("Couldn't close client: {}.", e.getMessage());
        }
    }
}