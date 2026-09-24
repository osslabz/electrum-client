package net.osslabz.electrum.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class TxListEntryTest {

    private static TxListEntry entry(String txHash, Integer height, Integer fee) {

        TxListEntry entry = new TxListEntry();
        entry.setTxHash(txHash);
        entry.setHeight(height);
        entry.setFee(fee);
        return entry;
    }

    @Test
    void entriesWithTheSameValuesAreEqualAndHashAlike() {

        TxListEntry a = entry("aa", 1, 141);
        a.setAdditionalProperty("mempool_rank", 3);
        TxListEntry b = entry("aa", 1, 141);
        b.setAdditionalProperty("mempool_rank", 3);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void entriesDifferingInAnyValueAreNotEqual() {

        TxListEntry entry = entry("aa", 1, 141);
        TxListEntry withOtherProperty = entry("aa", 1, 141);
        withOtherProperty.setAdditionalProperty("mempool_rank", 3);

        assertNotEquals(entry, entry("bb", 1, 141));
        assertNotEquals(entry, entry("aa", 2, 141));
        assertNotEquals(entry, entry("aa", 1, null));
        assertNotEquals(entry, withOtherProperty);
        assertNotEquals(entry, null);
    }

    @Test
    void anEntryIsNotEqualToASubclassInstanceWithTheSameValues() {

        TxListEntry subclassInstance = new TxListEntry() {};
        subclassInstance.setTxHash("aa");

        assertNotEquals(entry("aa", null, null), subclassInstance);
    }
}
