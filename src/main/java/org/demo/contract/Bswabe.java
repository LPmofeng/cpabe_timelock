package org.demo.contract;

import com.alibaba.fastjson.JSON;
import edu.princeton.cs.algs4.Out;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import org.demo.cpabe.*;
import org.demo.pojo.CT;
import org.demo.pojo.Keys;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Contract(name = "demo")
@Default
public class Bswabe implements ContractInterface {
    /**
     * 初始化配对参数
     *
     * @param lambda
     * @param pairingParametersFileName
     */
    public Pairing initPairingParameter(final Integer lambda, String pairingParametersFileName) {
        //Global Setup
//        int lambda = 512;
//        动态产生的方法非常简单，大概有如下步骤：指定椭圆曲线的种类、产生椭圆曲线参数、初始化Pairing。
//        Type A曲线需要两个参数：rBit是 Zp 中阶数 p 的比特长度；qBit是 G 中阶数的比特长度。
        File file = new File(pairingParametersFileName);
        if (file.exists()) {
            return PairingFactory.getPairing(pairingParametersFileName);
        } else {
            TypeACurveGenerator pg = new TypeACurveGenerator(160, lambda);
            PairingParameters typeAParams = pg.generate();
            //将参数写入文件a.properties中，我用了Princeton大学封装的文件输出库
            Out out = new Out(pairingParametersFileName);
            out.println(typeAParams);
            return PairingFactory.getPairing(typeAParams);
        }
    }

    /*
     * Generate a public key and corresponding master secret key.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Keys setup(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        byte[] pub_byte, msk_byte;
        BswabePub pub = new BswabePub();
        BswabeMsk msk = new BswabeMsk();

        Element alpha, beta_inv;
        Pairing pairing = initPairingParameter(512, pub.pairingParametersFileName);
        pub.g = pairing.getG1().newElement();
        pub.f = pairing.getG1().newElement();
        pub.h = pairing.getG1().newElement();
        pub.gp = pairing.getG2().newElement();
        pub.g_hat_alpha = pairing.getGT().newElement();
        alpha = pairing.getZr().newElement();
        msk.beta = pairing.getZr().newElement();
        msk.g_alpha = pairing.getG2().newElement();

        alpha.setToRandom();
        msk.beta.setToRandom();
        pub.g.setToRandom();
        pub.gp.setToRandom();

        msk.g_alpha = pub.gp.duplicate();
        msk.g_alpha.powZn(alpha);

        beta_inv = msk.beta.duplicate();
        beta_inv.invert();
        pub.f = pub.g.duplicate();
        pub.f.powZn(beta_inv);

        pub.h = pub.g.duplicate();
        pub.h.powZn(msk.beta);

        pub.g_hat_alpha = pairing.pairing(pub.g, msk.g_alpha);

        /* store BswabePub into mskfile */
        pub_byte = SerializeUtils.serializeBswabePub(pub);
        String pub_json = JSON.toJSONString(pub_byte);
        stub.putStringState("pub", pub_json);

        /* store BswabeMsk into mskfile */
        msk_byte = SerializeUtils.serializeBswabeMsk(msk);
        String msk_json = JSON.toJSONString(msk_byte);
        stub.putStringState("msk", msk_json);

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

    /*
     * Generate a private key with the given set of attributes.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String keygen(final Context ctx, String attr_str) throws NoSuchAlgorithmException {
        ChaincodeStub stub = ctx.getStub();
        BswabePub pub = SerializeUtils.unserializeBswabePub(readPub(ctx));
        BswabeMsk msk = SerializeUtils.unserializeBswabeMsk(pub, readMsk(ctx));
        BswabePrv prv = new BswabePrv();
        Element g_r, r, beta_inv;
        Pairing pairing;

        /* initialize */
        pairing = PairingFactory.getPairing(pub.pairingParametersFileName);
        prv.d = pairing.getG2().newElement();
        g_r = pairing.getG2().newElement();
        r = pairing.getZr().newElement();
        beta_inv = pairing.getZr().newElement();

        /* compute */
        r.setToRandom();
        g_r = pub.gp.duplicate();
        g_r.powZn(r);

        prv.d = msk.g_alpha.duplicate();
        prv.d.mul(g_r);
        beta_inv = msk.beta.duplicate();
        beta_inv.invert();
        prv.d.powZn(beta_inv);
        String[] attrs = attr_str.split(",");
        int i, len = attrs.length;
        prv.comps = new ArrayList<>();
        for (i = 0; i < len; i++) {
            BswabePrvComp comp = new BswabePrvComp();
            Element h_rp;
            Element rp;

            comp.attr = attrs[i];

            comp.d = pairing.getG2().newElement();
            comp.dp = pairing.getG1().newElement();
            h_rp = pairing.getG2().newElement();
            rp = pairing.getZr().newElement();

            elementFromString(h_rp, comp.attr);
            rp.setToRandom();

            h_rp.powZn(rp);

            comp.d = g_r.duplicate();
            comp.d.mul(h_rp);
            comp.dp = pub.g.duplicate();
            comp.dp.powZn(rp);

            prv.comps.add(comp);
        }
        byte[] prv_byte = SerializeUtils.serializeBswabePrv(prv);
        String prv_json = JSON.toJSONString(prv_byte);
        stub.putStringState("prv", prv_json);
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

    /*
     * Pick a random group element and encrypt it under the specified access
     * policy. The resulting ciphertext is returned and the Element given as an
     * argument (which need not be initialized) is set to the random group
     * element.
     *
     * After using this function, it is normal to extract the random data in m
     * using the pbc functions element_length_in_bytes and element_to_bytes and
     * use it as a key for hybrid encryption.
     *
     * The policy is specified as a simple string which encodes a postorder
     * traversal of threshold tree defining the access policy. As an example,
     *
     * "foo bar fim 2of3 baf 1of2"
     *
     * specifies a policy with two threshold gates and four leaves. It is not
     * possible to specify an attribute with whitespace in it (although "_" is
     * allowed).
     *
     * Numerical attributes and any other fancy stuff are not supported.
     *
     * Returns null if an error occured, in which case a description can be
     * retrieved by calling bswabe_error().
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public CT enc(final Context ctx, String policy, String message) throws Exception {
        ChaincodeStub stub = ctx.getStub();
        BswabePub pub = SerializeUtils.unserializeBswabePub(readPub(ctx));
        BswabeCphKey keyCph = new BswabeCphKey();
        BswabeCph cph = new BswabeCph();
        Element s, m;

        /* initialize */
        Pairing pairing = PairingFactory.getPairing(pub.pairingParametersFileName);
        s = pairing.getZr().newElement();
        m = pairing.getGT().newElement();
        cph.cs = pairing.getGT().newElement();
        cph.c = pairing.getG1().newElement();
        cph.p = parsePolicyPostfix(policy);

        /* compute */
        m.setToRandom();
        s.setToRandom();
        cph.cs = pub.g_hat_alpha.duplicate();
        cph.cs.powZn(s); /* num_exps++; */
        cph.cs.mul(m); /* num_muls++; */

        cph.c = pub.h.duplicate();
        cph.c.powZn(s); /* num_exps++; */

        fillPolicy(cph.p, pub, s);

        keyCph.cph = cph;
        keyCph.key = m;

        byte[] cphBuf;
        byte[] aesBuf;
        byte[] plt;
        cphBuf = SerializeUtils.bswabeCphSerialize(cph);
        String cph_json = JSON.toJSONString(cphBuf);
        stub.putStringState("cph", cph_json);
        /* read file to encrypted */
        plt = message.getBytes(StandardCharsets.UTF_8);
        aesBuf = AESCoder.encrypt(m.toBytes(), plt);
        String aes_json = JSON.toJSONString(aesBuf);
        stub.putStringState("aes", aes_json);
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
    /*
     * Decrypt the specified ciphertext using the given private key, filling in
     * the provided element m (which need not be initialized) with the result.
     *
     * Returns true if decryption succeeded, false if this key does not satisfy
     * the policy of the ciphertext (in which case m is unaltered).
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public byte[] dec(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        BswabePub pub = SerializeUtils.unserializeBswabePub(readPub(ctx));
        BswabePrv prv = SerializeUtils.unserializeBswabePrv(pub, readPrv(ctx));
        BswabeCph cph = SerializeUtils.bswabeCphUnserialize(pub, readCph(ctx));

        BswabeElementBoolean beb = new BswabeElementBoolean();

        checkSatisfy(cph.p, prv);
        if (!cph.p.satisfiable) {
            System.err
                    .println("cannot decrypt, attributes in key do not satisfy policy");
            beb.e = null;
            beb.b = false;
            return null;
        }

        pickSatisfyMinLeaves(cph.p, prv);
        Pairing pairing = PairingFactory.getPairing(pub.pairingParametersFileName);
        Element fx = pairing.getGT().newOneElement();
        Element lx = pairing.getZr().newOneElement();
        decNodeFlatten(fx, lx, cph.p, prv, pub);

        // 这时的t = A
        Element m = cph.cs.duplicate();
        m.mul(fx); /* num_muls++; */

        fx = pairing.pairing(cph.c, prv.d);
        fx.invert();
        m.mul(fx); /* num_muls++; */

        beb.e = m;
        beb.b = true;

        return m.toBytes();
    }

    private void decNodeFlatten(Element fx, Element lx, BswabePolicy p,
                                BswabePrv prv, BswabePub pub) {
        if (p.children == null || p.children.length == 0) {
            decLeafFlatten(fx, lx, p, prv, pub);
        } else {
            decInternalFlatten(fx, lx, p, prv, pub);
        }
    }

    private void decLeafFlatten(Element fx, Element lx, BswabePolicy p,
                                BswabePrv prv, BswabePub pub) {
        BswabePrvComp c;
        Element s, t;

        c = prv.comps.get(p.attri);
        Pairing pairing = PairingFactory.getPairing(pub.pairingParametersFileName);
        s = pairing.pairing(p.c, c.d); /* num_pairings++; */
        t = pairing.pairing(p.cp, c.dp); /* num_pairings++; */
        t.invert();
        s.mul(t); /* num_muls++; */

        s.powZn(lx); /* num_exps++; */

        fx.mul(s); /* num_muls++; */
    }

    private void decInternalFlatten(Element fx, Element lx,
                                    BswabePolicy p, BswabePrv prv, BswabePub pub) {
        int i;
        Element t, lxCopy;
        Pairing pairing = PairingFactory.getPairing(pub.pairingParametersFileName);
        t = pairing.getZr().newOneElement();

        for (i = 0; i < p.satl.size(); i++) {
            lagrangeCoef(t, p.satl, p.satl.get(i));
            // lx的值复制给lxcopy
            lxCopy = lx.duplicate();
            lxCopy.mul(t);
            decNodeFlatten(fx, lxCopy, p.children[p.satl.get(i) - 1], prv, pub);
        }
    }

    /**
     * 这里求的是，拉格朗日插值公式的基函数的值
     */
    private void lagrangeCoef(Element _t, ArrayList<Integer> s, int i) {
        Element t;

        t = _t.duplicate();
        _t.setToOne();
        for (int j : s) {
            if (j == i) {
                continue;
            }
            t.set(-j);
            _t.mul(t); /* num_muls++; */
            t.set(i - j);
            t.invert();
            _t.mul(t); /* num_muls++; */
        }
    }

    private void pickSatisfyMinLeaves(BswabePolicy p, BswabePrv prv) {
        int i, k, l, c_i;
        int len;
        ArrayList<Integer> c = new ArrayList<Integer>();

        if (p.children == null || p.children.length == 0) {
            p.min_leaves = 1;
        } else {
            len = p.children.length;
            for (i = 0; i < len; i++) {
                if (p.children[i].satisfiable) {
                    pickSatisfyMinLeaves(p.children[i], prv);
                }
            }

            for (i = 0; i < len; i++) {
                c.add(i);
            }

            Collections.sort(c, new IntegerComparator(p));

            p.satl = new ArrayList<Integer>();
            p.min_leaves = 0;
            l = 0;

            for (i = 0; i < len && l < p.k; i++) {
                c_i = c.get(i); /* c[i] */
                if (p.children[c_i].satisfiable) {
                    l++;
                    p.min_leaves += p.children[c_i].min_leaves;
                    k = c_i + 1;
                    p.satl.add(k);
                }
            }
        }
    }

    private void checkSatisfy(BswabePolicy p, BswabePrv prv) {
        int i, l;
        String prvAttr;

        p.satisfiable = false;
        if (p.children == null || p.children.length == 0) {
            for (i = 0; i < prv.comps.size(); i++) {
                prvAttr = prv.comps.get(i).attr;
                // System.out.println("prvAtt:" + prvAttr);
                // System.out.println("p.attr" + p.attr);
                if (prvAttr.compareTo(p.attr) == 0) {
                    // System.out.println("=staisfy=");
                    p.satisfiable = true;
                    p.attri = i;
                    break;
                }
            }
        } else {
            for (i = 0; i < p.children.length; i++) {
                checkSatisfy(p.children[i], prv);
            }

            l = 0;
            for (i = 0; i < p.children.length; i++) {
                if (p.children[i].satisfiable) {
                    l++;
                }
            }

            if (l >= p.k) {
                p.satisfiable = true;
            }
        }
    }

    private void fillPolicy(BswabePolicy p, BswabePub pub, Element s)
            throws NoSuchAlgorithmException {
        int i;
        Element r, t, h;
        Pairing pairing = PairingFactory.getPairing(pub.pairingParametersFileName);
        r = pairing.getZr().newElement();
        t = pairing.getZr().newElement();
        h = pairing.getG2().newElement();

        p.q = randPoly(p.k - 1, s);

        if (p.children == null || p.children.length == 0) {
            p.c = pairing.getG1().newElement();
            p.cp = pairing.getG2().newElement();

            elementFromString(h, p.attr);
            p.c = pub.g.duplicate();

            p.c.powZn(p.q.coef[0]);
            p.cp = h.duplicate();
            p.cp.powZn(p.q.coef[0]);
        } else {
            for (i = 0; i < p.children.length; i++) {
                r.set(i + 1);
                evalPoly(t, p.q, r);
                fillPolicy(p.children[i], pub, t);
            }
        }

    }

    // Element r, BswabePolynomial q, Element x
    private void evalPoly(Element _t, BswabePolynomial q, Element r) {
        int i;
        Element s, t;

        s = _t.duplicate();
        t = _t.duplicate();

        _t.setToZero();
        t.setToOne();

        for (i = 0; i < q.deg + 1; i++) {
            /* r += q->coef[i] * t */
            s = q.coef[i].duplicate();
            s.mul(t);
            _t.add(s);

            /* t *= r */
            t.mul(r);
        }

    }

    private BswabePolynomial randPoly(int deg, Element s) {
        int i;
        BswabePolynomial q = new BswabePolynomial();
        q.deg = deg;
        q.coef = new Element[deg + 1];

        for (i = 0; i < deg + 1; i++) {
            q.coef[i] = s.duplicate();
        }

        q.coef[0].set(s);

        for (i = 1; i < deg + 1; i++) {
            q.coef[i].setToRandom();
        }

        return q;
    }

    private BswabePolicy parsePolicyPostfix(String s) {
        String[] toks;
        String tok;
        ArrayList<BswabePolicy> stack = new ArrayList<BswabePolicy>();
        BswabePolicy root;

        toks = s.split(",");


        int toks_cnt = toks.length;
        for (int index = 0; index < toks_cnt; index++) {
            int i, k, n;

            tok = toks[index];
            if (!tok.contains("of")) {
                stack.add(baseNode(1, tok));
            } else {
                BswabePolicy node;

                /* parse kof n node */
                String[] k_n = tok.split("of");
                k = Integer.parseInt(k_n[0]);
                n = Integer.parseInt(k_n[1]);

                if (k < 1) {
                    System.out.println("error parsing " + s
                            + ": trivially satisfied operator " + tok);
                    return null;
                } else if (k > n) {
                    System.out.println("error parsing " + s
                            + ": unsatisfiable operator " + tok);
                    return null;
                } else if (n == 1) {
                    System.out.println("error parsing " + s
                            + ": indentity operator " + tok);
                    return null;
                } else if (n > stack.size()) {
                    System.out.println("error parsing " + s
                            + ": stack underflow at " + tok);
                    return null;
                }

                /* pop n things and fill in children */
                node = baseNode(k, null);
                node.children = new BswabePolicy[n];

                for (i = n - 1; i >= 0; i--) {
                    node.children[i] = stack.remove(stack.size() - 1);
                }

                /* push result */
                stack.add(node);
            }
        }

        if (stack.size() > 1) {
            System.out.println("error parsing " + s
                    + ": extra node left on the stack");
            return null;
        } else if (stack.size() < 1) {
            System.out.println("error parsing " + s + ": empty policy");
            return null;
        }

        root = stack.get(0);
        return root;
    }

    private BswabePolicy baseNode(int k, String s) {
        BswabePolicy p = new BswabePolicy();

        p.k = k;
        if (!(s == null)) {
            p.attr = s;
        } else {
            p.attr = null;
        }
        p.q = null;

        return p;
    }

    private void elementFromString(Element h, String s)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(s.getBytes());
        h.setFromHash(digest, 0, digest.length);
    }

    private class IntegerComparator implements Comparator<Integer> {
        BswabePolicy policy;

        public IntegerComparator(BswabePolicy p) {
            this.policy = p;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            int k, l;

            k = policy.children[o1].min_leaves;
            l = policy.children[o2].min_leaves;

            return k < l ? -1 : k == l ? 0 : 1;
        }
    }
}
