package org.demo.pojo;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class CT {
    @Property()
    public String cph_json;
    @Property()
    public String aes_json;
    @Property()
    public Long enc_time;

    public CT(String cph_json, String aes_json, Long enc_time) {
        this.cph_json = cph_json;
        this.aes_json = aes_json;
        this.enc_time = enc_time;
    }

    public String getCph_json() {
        return cph_json;
    }

    public String getAes_json() {
        return aes_json;
    }

    public Long getEnc_time() {
        return enc_time;
    }

    @Override
    public String toString() {
        return "CT{" +
                "cph_json='" + cph_json + '\'' +
                ", aes_json='" + aes_json + '\'' +
                ", enc_time=" + enc_time +
                '}';
    }
}
