**从第3步开始，如果fabric网络启动错误，先关闭网络后执行第1，2步**

**0. git**
```sh
git add .
git commit -m "test"
git push origin main
```
**1. 清空所有未使用的docker挂载信息（慎用）**

```sh
docker volume prune
```

**2. 清理没有再被任何容器引用的networks**

```sh
docker network prune
```

**3. 进入fabric-samples/test-network，创建Fabric网络，并创建通道**

```sh
cd /usr/local/dev/code/go/src/github.com/hyperledger/fabric-samples/test-network

./network.sh up createChannel -ca
```

**4. 打包、安装、审批、提交**

```sh
./network.sh deployCC -ccn demo -ccp ../cpabe_timelock -ccl java -ccep "OR('Org1MSP.member','Org2MSP.member')"
```

**5.设置执行环境及配置文件路径**

```sh
# 使用以下命令将这些二进制文件添加到您的 CLI 路径：
# 您还需要设置FABRIC_CFG_PATH指向存储库中的core.yaml文件
export PATH=${PWD}/../bin:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
```

**6.测试智能合约**

```sh
# Environment variables for Org1
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051
```

**6.1初始化账本**

```sh
# cpabe
sh invoke.sh '{"function":"setup","Args":[]}'
sh invoke.sh '{"function":"keygen","Args":["baf,fim,foo"]}'
sh invoke.sh '{"function":"enc","Args":["foo,bar,fim,2of3,baf,1of2","www.baidu.com"]}'
sh invoke.sh '{"function":"dec","Args":[]}'

# sm
sh invoke.sh '{"function":"voteEnc","Args":["A","1"]}'
sh invoke.sh '{"function":"voteDec","Args":[]}'
```
**查看链码日志**

```sh
docker logs -f  链码容器id >> dev01.text
```
