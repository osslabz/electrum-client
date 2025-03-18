package net.osslabz.electrum.result;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


public class TxListEntry {

    @JsonProperty("tx_hash")
    private String txHash;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("fee")
    private Integer fee;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();


    @JsonProperty("tx_hash")
    public String getTxHash() {

        return txHash;
    }


    @JsonProperty("tx_hash")
    public void setTxHash(String txHash) {

        this.txHash = txHash;
    }


    @JsonProperty("height")
    public Integer getHeight() {

        return height;
    }


    @JsonProperty("height")
    public void setHeight(Integer height) {

        this.height = height;
    }


    @JsonProperty("fee")
    public Integer getFee() {

        return fee;
    }


    @JsonProperty("fee")
    public void setFee(Integer fee) {

        this.fee = fee;
    }


    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {

        return this.additionalProperties;
    }


    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {

        this.additionalProperties.put(name, value);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TxListEntry that = (TxListEntry) o;
        return Objects.equals(txHash, that.txHash) && Objects.equals(height, that.height) && Objects.equals(fee, that.fee) && Objects.equals(additionalProperties,
            that.additionalProperties);
    }


    @Override
    public int hashCode() {

        return Objects.hash(txHash, height, fee, additionalProperties);
    }


    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder("TxListEntry{");
        sb.append("txHash='").append(txHash).append('\'');
        sb.append(", height=").append(height);
        sb.append(", fee=").append(fee);
        sb.append(", additionalProperties=").append(additionalProperties);
        sb.append('}');
        return sb.toString();
    }
}