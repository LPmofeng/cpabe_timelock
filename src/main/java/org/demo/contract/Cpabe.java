package org.demo.contract;

import com.alibaba.fastjson.JSON;
import it.unisa.dia.gas.jpbc.Element;
import org.demo.cpabe.*;
import org.demo.pojo.CT;
import org.demo.pojo.Keys;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

@Contract(name = "demo")
@Default
public class Cpabe implements ContractInterface {
    final Bswabe bswabe = new Bswabe();

    /**
     * @param
     * @author Junwei Wang(wakemecn@gmail.com)
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Keys setup(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        byte[] pub_byte, msk_byte;
        BswabePub pub = new BswabePub();
        BswabeMsk msk = new BswabeMsk();
        bswabe.setup(pub, msk);

        /* store BswabePub into mskfile */
        pub_byte = SerializeUtils.serializeBswabePub(pub);
        // System.out.println(Arrays.toString(pub_byte));
        String pub_json = JSON.toJSONString(pub_byte);
        stub.putStringState("pub", pub_json);
        // System.out.println(json);
        // byte[] bytes = JSON.parseObject(json, byte[].class);
        // System.out.println(Arrays.toString(bytes));
        // Common.spitFile(pubfile, pub_byte);

        /* store BswabeMsk into mskfile */
        msk_byte = SerializeUtils.serializeBswabeMsk(msk);
        String msk_json = JSON.toJSONString(msk_byte);
        stub.putStringState("msk", msk_json);
        // Common.spitFile(mskfile, msk_byte);
        return new Keys(pub_json, msk_json);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public byte[] readPub(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String pub_json = stub.getStringState("pub");
        byte[] pub_byte = JSON.parseObject(pub_json, byte[].class);
        return pub_byte;
        // return SerializeUtils.unserializeBswabePub(pub_byte);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public byte[] readMsk(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String msk_json = stub.getStringState("msk");
        byte[] msk_byte = JSON.parseObject(msk_json, byte[].class);
        return msk_byte;
        // return SerializeUtils.unserializeBswabeMsk(SerializeUtils.unserializeBswabePub(pub_byte), msk_byte);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String keygen(final Context ctx, String attr_str) throws NoSuchAlgorithmException {
        ChaincodeStub stub = ctx.getStub();
        BswabePub pub;
        BswabeMsk msk;
        byte[] prv_byte;
        /* get BswabePub from pubfile */
        // pub_byte = Common.suckFile(pubfile);
        // pub = SerializeUtils.unserializeBswabePub(pub_byte);
        /* get BswabeMsk from mskfile */
        // msk_byte = Common.suckFile(mskfile);
        // msk = SerializeUtils.unserializeBswabeMsk(pub, msk_byte);
        pub = SerializeUtils.unserializeBswabePub(readPub(ctx));
        msk = SerializeUtils.unserializeBswabeMsk(pub, readMsk(ctx));

        // String[] attr_arr = LangPolicy.parseAttribute(attr_str);
        String[] attr_arr = attr_str.split(",");
        BswabePrv prv = bswabe.keygen(pub, msk, attr_arr);

        /* store BswabePrv into prvfile */
        prv_byte = SerializeUtils.serializeBswabePrv(prv);
        String prv_json = JSON.toJSONString(prv_byte);
        stub.putStringState("prv", prv_json);
        // Common.spitFile(prvfile, prv_byte);
        return prv_json;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public byte[] readPrv(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String prv_json = stub.getStringState("prv");
        byte[] prv_byte = JSON.parseObject(prv_json, byte[].class);
        // return SerializeUtils.unserializeBswabePrv(readPub(ctx), prv_byte);
        return prv_byte;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public CT enc(final Context ctx, String policy, String message) throws Exception {
        ChaincodeStub stub = ctx.getStub();
        BswabePub pub;
        BswabeCph cph;
        BswabeCphKey keyCph;
        byte[] plt;
        byte[] cphBuf;
        byte[] aesBuf;
        Element m;

        /* get BswabePub from pubfile */
        // pub_byte = Common.suckFile(pubfile);
        // pub = SerializeUtils.unserializeBswabePub(pub_byte);
        pub = SerializeUtils.unserializeBswabePub(readPub(ctx));
        keyCph = bswabe.enc(pub, policy);

        cph = keyCph.cph;
        m = keyCph.key;
        System.err.println("m = " + m.toString());

        if (cph == null) {
            System.out.println("Error happed in enc");
            System.exit(0);
        }
        cphBuf = SerializeUtils.bswabeCphSerialize(cph);
        String cph_json = JSON.toJSONString(cphBuf);
        stub.putStringState("cph", cph_json);
        /* read file to encrypted */
        // plt = Common.suckFile(inputfile);
        plt = message.getBytes(StandardCharsets.UTF_8);
        aesBuf = AESCoder.encrypt(m.toBytes(), plt);
        String aes_json = JSON.toJSONString(aesBuf);
        stub.putStringState("aes", aes_json);
        // Common.writeCpabeFile(encfile, cphBuf, aesBuf);
        return new CT(cph_json, aes_json);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public byte[] readCph(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String cph_json = stub.getStringState("cph");
        byte[] cph_byte = JSON.parseObject(cph_json, byte[].class);
        return cph_byte;
        // return SerializeUtils.bswabeCphUnserialize(readPub(ctx), cph_byte);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public byte[] readAes(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String aes_json = stub.getStringState("aes");
        return JSON.parseObject(aes_json, byte[].class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean dec(final Context ctx) throws Exception {
        ChaincodeStub stub = ctx.getStub();
        byte[] aesBuf;
        byte[] plt;
        BswabeCph cph;
        BswabePrv prv;
        BswabePub pub;

        /* get BswabePub from pubfile */
        // pub_byte = Common.suckFile(pubfile);
        // pub = SerializeUtils.unserializeBswabePub(pub_byte);
        pub = SerializeUtils.unserializeBswabePub(readPub(ctx));
        /* read ciphertext */
        // tmp = Common.readCpabeFile(encfile);
        // aesBuf = tmp[0];
        // cphBuf = tmp[1];
        // cph = SerializeUtils.bswabeCphUnserialize(pub, cphBuf);
        cph = SerializeUtils.bswabeCphUnserialize(pub, readCph(ctx));
        /* get BswabePrv form prvfile */
        // prv_byte = Common.suckFile(prvfile);
        // prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);
        prv = SerializeUtils.unserializeBswabePrv(pub, readPrv(ctx));
        aesBuf = readAes(ctx);
        BswabeElementBoolean beb = bswabe.dec(pub, prv, cph);
        System.err.println("e = " + beb.e.toString());
        if (beb.b) {
            plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
            String s = new String(plt);
            System.out.println("dec meg: " + s);
            // Common.spitFile(decfile, plt);
        } else {
            System.exit(0);
        }
        return beb.b;
    }


}
