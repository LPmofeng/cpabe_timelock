package org.demo;


import org.demo.cpabe.Cpabe;
import org.demo.sm.SM2EncDecUtils;
import org.demo.sm.Util;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Test;

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

    static String[] attr = {"baf1", "fim1", "foo"};
    static String policy = "foo bar fim 2of3 baf 1of2";

    static String student_attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
            + "sn:student1 cn:student2 uid:student3 userPassword:student2 "
            + "ou:idp o:computer mail:student2@sdu.edu.cn title:student";

    static String student_policy = "sn:student1 cn:student2 uid:student3 title:student 2of2 1of2 2of2";

    @Test
    public void demoTest() throws Exception {
        Date t1 = new Date();
        Cpabe test = new Cpabe();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        println("//start to setup");
        test.setup(pubfile, mskfile);
        println("//end to setup");

        println("//start to keygen");
        test.keygen(pubfile, prvfile, mskfile, student_attr);
        println("//end to keygen");

        println("//start to enc");
        test.enc(pubfile, student_policy, inputfile, encfile);
        println("//end to enc");

        println("//start to dec");
        boolean flag = test.dec(pubfile, prvfile, encfile, decfile);
        println("//end to dec");

        if (flag) {
            //输入明文
            String plainText = "1421345jhag";

            byte[] sourceData = plainText.getBytes();

            String prik = "0B1CE43098BC21B8E82B5C065EDB534CB86532B1900A49D49F3C53762D2997FA";
            String pubk = "04BB34D657EE7E8490E66EF577E6B3CEA28B739511E787FB4F71B7F38F241D87F18A5A93DF74E90FF94F4EB907F271A36B295B851F971DA5418F4915E2C1A23D6E";

            System.out.println("加密: ");
            String cipherText = SM2EncDecUtils.encrypt(Util.hexToByte(pubk), sourceData);

            //输出密文
            System.out.println(cipherText);
            System.out.println("解密: ");

            //解密后明文
            plainText = new String(SM2EncDecUtils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText)));
            System.out.println(plainText);

        }
        Date t2 = new Date();
        System.out.println(t2.getTime() - t1.getTime());
    }


    private static void println(Object o) {
        if (DEBUG) {
            System.out.println(o);
        }
    }
}
