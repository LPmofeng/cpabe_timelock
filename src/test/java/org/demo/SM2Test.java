package org.demo;

import org.demo.sm.SM2EncDecUtils;
import org.demo.sm.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SM2Test {

    @Test
    public void sm2Test() throws IOException {
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
}
