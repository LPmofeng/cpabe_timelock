package org.bmtac;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import org.bmtac.contract.AssetTransfer;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Test;


/**
 * 1. 验证某些行为(方法)
 * 一旦mock对象被创建了，mock对象会记住所有的交互。
 * 如果验证时，和使用中操作的不同，或者增加的对象不是一个，或者增加方法都会验证失败
 * 2. 做测试桩 (Stub)
 * 测试桩可以理解为对mock对象进行的一些预期操作，mock函数默认返回的是null。
 */
public class AssetTransferTest {
    @Test
    public void invokeBMTACTest() {
        AssetTransfer contract = new AssetTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        double avgTime;
        int sumTime = 0;
        int exeCount = 20;

        // GlobalSetup
        // GlobalParam GP = contract.GlobalSetup(ctx, 512, "a.properties");
        // Pairing pairing = PairingFactory.getPairing(GP.getPairingParametersFileName());
        // when(stub.getStringState("GP")).thenReturn(JSON.toJSONString(GP));

        // entityRegister
        // int AANum = 1;
        // List<String> aids = GenUtils.genAidArry(AANum);
        // Map<String, Authority> authorityMap = new HashMap<>();

        // int attNum = 2;
        // List<String> atts = GenUtils.genAttArry(attNum);
        // Map<String, String> attributeMap = new HashMap<>();

        // int policyAttrs = 2;
        // String policy = AccessPolicyUtils.getPolicyStr(policyAttrs);

        // int ttAtts = 2;
        // List<String> tt_atts = GenUtils.genAttArry(ttAtts);
        //
        // String userId = "user01";
        // String KeyStr = "qwer";
        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //
        // for (int k = 0; k < exeCount; k++) {
        //     将属性集存到账本中
            // int count = 0;
            // for (String attribute : atts) {
            //     if (count >= AANum) {
            //         count = 0;
            //     }
            //     attributeMap.put(attribute, aids.get(count));
            //     when(stub.getStringState(attribute)).thenReturn(aids.get(count));
            //     count++;
            // }
            // for (String aid : aids) {
            //     Authority authority = contract.AASetup(ctx, aid);
            //     authorityMap.put(aid, authority);
            //     when(stub.getStringState(aid + "_pk")).thenReturn(JSON.toJSONString(authority.getAuthPublicKey()));
            //     when(stub.getStringState(aid + "_sk")).thenReturn(JSON.toJSONString(authority.getAuthSecretKey()));
            // }
            //
            // encryption
            // Date begin = new Date(System.currentTimeMillis() - 500000);
            // Date end = new Date(System.currentTimeMillis() + 500000);
            // String strBegin = df.format(begin);
            // String strEnd = df.format(end);

            // for (String tt_att : tt_atts) {
            //     TimeToken token = contract.TimeTokenGen(ctx, strBegin, strEnd, tt_att);
            //     when(stub.getStringState("tt_" + tt_att)).thenReturn(JSON.toJSONString(token));
            // }
            // when(stub.getStringState("tt_atts")).thenReturn(JSON.toJSONString(tt_atts));
            //
            // Date t1 = new Date();
            // 加密
            // Ciphertext ciphertext = contract.Encrypt(ctx, policy, KeyStr);
            // when(stub.getStringState("KEY")).thenReturn(JSON.toJSONString(GP.hashToGT(pairing, KeyStr.getBytes()).getImmutable().toBytes()));
            // when(stub.getStringState("CT")).thenReturn(JSON.toJSONString(ciphertext));
            // Map<String, byte[]> c2 = ciphertext.getC2();
            // for (Map.Entry<String, byte[]> entry : c2.entrySet()) {
            //     when(stub.getStringState(entry.getKey() + "_c2")).thenReturn(JSON.toJSONString(entry.getValue()));
            // }
            // Date t2 = new Date();
            //
            // 测试token时，用户是全属性
            // Userkeys userkeys = contract.UserSetup(ctx, userId);
            // when(stub.getStringState(userId + "_pk")).thenReturn(JSON.toJSONString(userkeys.getUpk()));
            // List<String> userAttrs = contract.issueAttrByPolicy(policy);
            // List<UserAuthorityKey> userAuthorityKeys = new ArrayList<>();

            // for (String curAid : aids) {

                // 用户属性属于当前AA中的属性
                // List<String> userAttrsWithAid = new ArrayList<>();
                // 为了测试极限时间，平常时，用户的属性就是访问策略的全部属性，
                // 但是为了测试AttrTokenGen算法时，使用的是全属性,也就是测试每一个AA生成属性令牌时的时间
                // for (String att : userAttrs) {
                //     String aid = contract.getAidByAtt(ctx, att);
                //     if (aid.equals(curAid)) {
                //         userAttrsWithAid.add(att);
                //     }
                // }
                // 如果当前AA中有用户的属性时，才会对其生成常规密钥。
                // 生成UserAuthorityKey类型的数据，当所在AA中不存在用户的属性时，不产生UserAttributeKey
                // if (userAttrsWithAid.isEmpty()) {
                //     continue;
                // }
                // UserAuthorityKey uAK = contract.AttrTokenGen(ctx, curAid, userAttrsWithAid, userId);
                // userAuthorityKeys.add(uAK);
            // }

            // Userkeys newUserkeys = new Userkeys(userId, userkeys.getUsk(), userkeys.getUpk(),
            //         JSON.toJSONString(userAuthorityKeys), JSON.toJSONString(userAttrs));
            // when(stub.getStringState(userId)).thenReturn(JSON.toJSONString(newUserkeys));
            //
            //DecTokenGen
            // DKToken dkToken = contract.DecTokenGen(ctx, userId);
            // when(stub.getStringState(userId + "_dt")).thenReturn(JSON.toJSONString(dkToken));
            //Decrypt

            // contract.Decrypt(ctx, userId, KeyStr);
            //

            // int runTime = (int) (t2.getTime() - t1.getTime());
            // sumTime += runTime;
        // }
        // avgTime = sumTime / (float) exeCount;
        // System.out.println("avg time = " + avgTime);
    }
}

