package net.osslabz.electrum;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import net.osslabz.bitcoin.Network;
import net.osslabz.electrum.result.ServerVersion;
import net.osslabz.electrum.result.TxListEntry;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("live")
class ElectrumClientLiveTest {

    @Test
    void publicServerAnswersVersionAndAddressHistory() throws Exception {

        try (ElectrumClient electrumClient = new ElectrumClient(Network.MAIN_NET, "blockstream.info", 110)) {
            ServerVersion serverVersion = electrumClient.getServerVersion();
            assertNotNull(serverVersion.name());

            List<TxListEntry> txListEntries =
                    electrumClient.addressGetHistory("bc1qe5adquf84x5hss2kwz05f4xy4jglc5ldyjuldt");
            assertFalse(txListEntries.isEmpty());
        }
    }
}
