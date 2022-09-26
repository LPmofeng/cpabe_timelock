package org.demo;

import com.owlike.genson.Genson;
import org.demo.contract.Bswabe;
import org.demo.pojo.CT;
import org.demo.pojo.Keys;
import org.demo.sm.SM2EncDecUtils;
import org.demo.sm.Util;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CpabeMock {
    private final Genson genson = new Genson();
    @Test
    public void cpabeDemoTest() throws Exception {
        Bswabe contract = new Bswabe();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        // cpabe
        Keys keys = contract.setup(ctx);
        when(stub.getStringState("pub")).thenReturn(keys.puk_json);
        when(stub.getStringState("msk")).thenReturn(keys.msk_json);
        when(stub.getStringState("sm_pub")).thenReturn(keys.sm_puk);
        when(stub.getStringState("sm_pri")).thenReturn(keys.sm_pri);
        String attr = "baf,fim,foo";
        String policy = "foo,bar,fim,2of3,baf,1of2";
        String url = "https://www.baidu.com";
        String keygen = contract.keygen(ctx, attr);
        // System.out.println(keygen);
        int i = keygen.indexOf("=", keygen.indexOf("=") + 1);
        String prv_json = keygen.substring(i+1);
        // System.out.println(prv_json);
        when(stub.getStringState("prv")).thenReturn(prv_json);
        CT ct = contract.enc(ctx, policy, url);
        when(stub.getStringState("cph")).thenReturn(ct.cph_json);
        when(stub.getStringState("aes")).thenReturn(ct.aes_json);
        String dec = contract.dec(ctx);
        System.out.println("dec msg:" + dec);


        String voteEnc = contract.voteEnc(ctx, "A", 1);
        System.out.println(voteEnc);
        when(stub.getStringState("voteCount")).thenReturn("1");

        when(stub.getStringState("vote_ct1")).thenReturn(SM2EncDecUtils.encrypt(Util.hexToByte(keys.sm_puk), "1".getBytes()));
        String s = contract.voteDec(ctx);
        System.out.println(s);
    }

}
