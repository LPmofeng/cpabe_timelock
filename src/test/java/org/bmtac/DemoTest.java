package org.bmtac;


import org.bmtac.contract.AssetTransfer;
import org.bmtac.cpabe.Cpabe;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DemoTest {
    final static boolean DEBUG = true;

    static String dir = "keystore/cpabe";

    static String pubfile = dir + "/pub_key";
    static String mskfile = dir + "/master_key";
    static String prvfile = dir + "/prv_key";

    static String inputfile = dir + "/url.text";
    static String encfile = dir + "/url.text.cpabe";
    static String decfile = dir + "/url.text.new";

    static String url = "https://www.baidu.com";

    static String[] attr = { "baf1", "fim1", "foo" };
    static String policy = "foo bar fim 2of3 baf 1of2";

    static String student_attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
            + "sn:student1 cn:student2 uid:student3 userPassword:student2 "
            + "ou:idp o:computer mail:student2@sdu.edu.cn title:student";

    static String student_policy = "sn:student1 cn:student2 uid:student3 title:student 2of2 1of2 2of2";
    @Test
    public void demoTest() throws Exception {
        Date t1 = new Date();
        Cpabe contract = new Cpabe();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        Cpabe test = new Cpabe();
        println("//start to setup");
        test.setup(ctx,pubfile, mskfile);
        println("//end to setup");

        println("//start to keygen");
        test.keygen(pubfile, prvfile, mskfile, student_attr);
        println("//end to keygen");

        println("//start to enc");
        test.enc(pubfile, student_policy, inputfile, encfile);
        println("//end to enc");

        println("//start to dec");
        test.dec(pubfile, prvfile, encfile, decfile);
        println("//end to dec");
        Date t2 = new Date();
        System.out.println(t2.getTime() - t1.getTime());
    }


    private static void println(Object o) {
        if (DEBUG) {
            System.out.println(o);
        }
    }
}
