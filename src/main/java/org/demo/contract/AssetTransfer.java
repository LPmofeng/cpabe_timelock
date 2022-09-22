package org.demo.contract;

import org.apache.log4j.Logger;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;

@Contract(name = "basic")
@Default
public class AssetTransfer implements ContractInterface {
    private final Logger log = Logger.getLogger(AssetTransfer.class);

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    /*@Transaction(intent = Transaction.TYPE.SUBMIT)
    public GlobalParam GlobalSetup(final Context ctx, final int lambda, final String pairingParametersFileName) {
        ChaincodeStub stub = ctx.getStub();
        Pairing pairing = GenUtils.InitPairingParameter(lambda, pairingParametersFileName);
        Element g = pairing.getG1().newRandomElement().getImmutable();
        Element a = pairing.getZr().newRandomElement().getImmutable();
        Element h = g.powZn(a).getImmutable();
        GlobalParam gp = new GlobalParam(g.toBytes(), a.toBytes(), h.toBytes(), pairingParametersFileName);
        // 由于Element类型无法直接json化，因此先转换成字节数组类型，最终上传至账本
        String json = JSON.toJSONString(gp);
        //log.info("GP: " + gp);
        stub.putStringState("GP", json);
        return gp;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public GlobalParam getGP(final Context ctx, final String GPID) {
        ChaincodeStub stub = ctx.getStub();
        String GPJSON = stub.getStringState(GPID);
        if (GPJSON == null || GPJSON.isEmpty()) {
            String errorMessage = String.format("gp Asset %s does not exist", GPID);
            log.info(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        return JSON.parseObject(GPJSON, GlobalParam.class);
    }*/
}
