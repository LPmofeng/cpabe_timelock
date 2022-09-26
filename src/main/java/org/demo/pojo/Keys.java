package org.demo.pojo;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class Keys {
    // cpabe
    @Property()
    public String puk_json;
    @Property()
    public String msk_json;

    // sm
    @Property()
    public String sm_puk;
    @Property()
    public String sm_pri;

    public Keys(String puk_json, String msk_json, String sm_puk, String sm_pri) {
        this.puk_json = puk_json;
        this.msk_json = msk_json;
        this.sm_puk = sm_puk;
        this.sm_pri = sm_pri;
    }

    public String getPuk_json() {
        return puk_json;
    }

    public String getMsk_json() {
        return msk_json;
    }

    public String getSm_puk() {
        return sm_puk;
    }

    public String getSm_pri() {
        return sm_pri;
    }

    @Override
    public String toString() {
        return "Keys{" +
                "puk_json='" + puk_json + '\'' +
                ", msk_json='" + msk_json + '\'' +
                ", sm_puk='" + sm_puk + '\'' +
                ", sm_pri='" + sm_pri + '\'' +
                '}';
    }
}
