package org.demo.pojo;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class Keys {
    @Property
    public String puk_json;
    @Property
    public String msk_json;

    public Keys(String puk_json, String msk_json) {
        this.puk_json = puk_json;
        this.msk_json = msk_json;
    }

    @Override
    public String toString() {
        return "Keys{" +
                "puk_json='" + puk_json + '\'' +
                ", msk_json='" + msk_json + '\'' +
                '}';
    }
}
