1. git
   git add ./
   git commit -m "test"
   git push origin main 
2. 清空所有未使用的docker挂载信息（慎用）

   docker volume prune

3. 清理没有再被任何容器引用的networks

   docker network prune

   docker rmi -f $(docker images -qa)

4. 进入fabric-samples/test-network，创建Fabric网络，并创建通道

   cd /usr/local/dev/code/go/src/github.com/hyperledger/fabric-samples/test-network
   ./network.sh up createChannel -ca

5. 打包、安装、审批、提交
   ./network.sh deployCC -ccn demo -ccp ../cpabe_timelock -ccl java -ccep "OR('Org1MSP.member','Org2MSP.member')"
   # 官方java智能合约测试
   ./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-java -ccl java -ccep "OR('Org1MSP.member','Org2MSP.member')"

5.设置执行环境及配置文件路径

    # 使用以下命令将这些二进制文件添加到您的 CLI 路径：
    # 您还需要设置FABRIC_CFG_PATH指向存储库中的core.yaml文件
    export PATH=${PWD}/../bin:$PATH
    export FABRIC_CFG_PATH=$PWD/../config/

6.测试智能合约

    # Environment variables for Org1
    export CORE_PEER_TLS_ENABLED=true
    export CORE_PEER_LOCALMSPID="Org1MSP"
    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
    export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
    export CORE_PEER_ADDRESS=localhost:7051
    
    # Environment variables for Org2
    export CORE_PEER_TLS_ENABLED=true
    export CORE_PEER_LOCALMSPID="Org2MSP"
    export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
    export CORE_PEER_ADDRESS=localhost:9051

6.1初始化账本

    peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n basic --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"function":"InitLedger","Args":[]}'
     
    
    # 生成公共参数
    sh invoke.sh '{"function":"setup","Args":["keystore/cpabe/pub_key","keystore/cpabe/master_key"]}'
    # 查询GP
    peer chaincode query -C mychannel -n bmtac -c '{"Args":["getGP","GP"]}'

6.2实体注册(AA、User)

    # AANum=2 attNum=4 userId="user01"
    sh invoke.sh '{"function":"entityRegister","Args":["2","4"]}'
    # 查询AA公钥
    peer chaincode query -C mychannel -n bmtac -c '{"Args":["getApk","AA0"]}'
    # 查询属性所在AA
    peer chaincode query -C mychannel -n bmtac -c '{"Args":["getAidByAtt","A0"]}'
    # 查询AA管理属性集
    peer chaincode query -C mychannel -n bmtac -c '{"Args":["getAttsByAid","AA0"]}'
    # 查询user01的公钥
    peer chaincode query -C mychannel -n bmtac -c '{"Args":["getUpk","user01"]}'
    # 查询user01
    peer chaincode query -C mychannel -n bmtac -c '{"Args":["getUserkeys","user01"]}'

6.3加密

    # final int policyAttrs, final int ttAtts(需设置的属性时间令牌)
    sh invoke.sh '{"function":"timeTokenGen","Args":["2"]}'
    sh invoke.sh '{"function":"encrypt","Args":["2","qwer"]}'

6.4 令牌生成

    sh invoke.sh '{"function":"attTokenGen","Args":["user01","2","2"]}'

    sh invoke.sh '{"function":"DecTokenGen","Args":["user01"]}'

6.5 解密

    sh invoke.sh '{"function":"Decrypt","Args":["user01","qwer"]}'

查看链码日志

    docker logs -f  链码容器id >> dev01.text