package org.demo.pojo;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class CT {
    @Property
    public String cph_json;
    @Property
    public String aes_json;

    public CT(String cph_json, String aes_json) {
        this.cph_json = cph_json;
        this.aes_json = aes_json;
    }
}
