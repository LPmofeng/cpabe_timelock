package org.demo;

import org.demo.contract.Cpabe;
import org.demo.pojo.CT;
import org.demo.pojo.Keys;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CpabeMock {

    @Test
    public void cpabeDemoTest() throws Exception {
        Cpabe contract = new Cpabe();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        Keys keys = contract.setup(ctx);
        when(stub.getStringState("pub")).thenReturn(keys.puk_json);
        when(stub.getStringState("msk")).thenReturn(keys.msk_json);
        String attr = "baf,fim,foo";
        String policy = "foo,bar,fim,2of3,baf,1of2";
        String url = "https://www.baidu.com";
        String prv_json = contract.keygen(ctx, attr);
        when(stub.getStringState("prv")).thenReturn(prv_json);
        CT ct = contract.enc(ctx, policy, url);
        when(stub.getStringState("cph")).thenReturn(ct.cph_json);
        when(stub.getStringState("aes")).thenReturn(ct.aes_json);
        boolean dec = contract.dec(ctx);
        System.out.println(dec);
    }

}
