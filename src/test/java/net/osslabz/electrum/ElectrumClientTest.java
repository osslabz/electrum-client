package net.osslabz.electrum;

import java.util.List;
import net.osslabz.bitcoin.Network;
import net.osslabz.electrum.result.ServerVersion;
import net.osslabz.electrum.result.TxListEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class ElectrumClientTest {

    private static final Logger log = LoggerFactory.getLogger(ElectrumClientTest.class);


    public static void main(String[] args) throws Exception {

        ElectrumClient electrumClient = new ElectrumClient(Network.MAIN_NET, "deathgate", 50001);
        ServerVersion bl = electrumClient.getServerVersion();

        log.debug("result {}", bl);

        List<TxListEntry> txListEntries = electrumClient.addressGetHistory("bc1qe5adquf84x5hss2kwz05f4xy4jglc5ldyjuldt");

        log.debug("result {}", txListEntries);

        electrumClient.close();
    }
}