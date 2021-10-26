package io.meshswap.core.service;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import io.meshswap.core.dto.AtomicSwapServiceResult;
import io.meshswap.core.dto.InitiateResult;
import io.meshswap.core.dto.RedeemResult;
import io.meshswap.core.util.FeeUtils;
import io.meshswap.core.util.SwapUtils;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class NativeSwapService implements AtomicSwapService {
    private Random random = new Random();
    private final int secretSize = 32;
    private NetworkParameters networkParameters = RegTestParams.get();
    // redeemAtomicSwapSigScriptSize is the worst case (largest) serialize size
    // of a transaction input script to redeem the atomic swap contract.  This
    // does not include final push for the contract itself.
    //
    //   - OP_DATA_73
    //   - 72 bytes DER signature + 1 byte sighash
    //   - OP_DATA_33
    //   - 33 bytes serialized compressed pubkey
    //   - OP_DATA_32
    //   - 32 bytes secret
    //   - OP_TRUE
    private int redeemAtomicSwapSigScriptSize = 1 + 73 + 1 + 33 + 1 + 32 + 1;

    // refundAtomicSwapSigScriptSize is the worst case (largest) serialize size
    // of a transaction input script that refunds a P2SH atomic swap output.
    // This does not include final push for the contract itself.
    //
    //   - OP_DATA_73
    //   - 72 bytes DER signature + 1 byte sighash
    //   - OP_DATA_33
    //   - 33 bytes serialized compressed pubkey
    //   - OP_FALSE
    private int refundAtomicSwapSigScriptSize = 1 + 73 + 1 + 33 + 1;

    @Autowired
    @Qualifier("initiator")
    BitcoinRPCService initiatorBitcoinRPCService;

    @Autowired
    @Qualifier("participant")
    BitcoinRPCService participantBitcoinRPCService;

    public Script generateLockScript(String initiatorAddress, String participantAddress, long lockTime, HashCode secretHash) {

        Address refundAddr = Address.fromString(networkParameters, initiatorAddress);
        Address redeemAddr = Address.fromString(networkParameters, participantAddress);
        ScriptBuilder builder = new ScriptBuilder();

        builder.op(ScriptOpCodes.OP_IF);
        {
            builder.op(ScriptOpCodes.OP_SIZE);
            builder.number(secretSize);
            builder.op(ScriptOpCodes.OP_EQUALVERIFY);
            builder.op(ScriptOpCodes.OP_SHA256);
            builder.data(secretHash.asBytes());
            builder.op(ScriptOpCodes.OP_EQUALVERIFY);
            builder.op(ScriptOpCodes.OP_DUP);
            builder.op(ScriptOpCodes.OP_HASH160);
            builder.data(redeemAddr.getHash());
        }
        builder.op(ScriptOpCodes.OP_ELSE);
        {
            builder.number(lockTime);
            builder.op(ScriptOpCodes.OP_CHECKLOCKTIMEVERIFY);
            builder.op(ScriptOpCodes.OP_DROP);
            builder.op(ScriptOpCodes.OP_DUP);
            builder.op(ScriptOpCodes.OP_HASH160);
            builder.data(refundAddr.getHash());
        }
        builder.op(ScriptOpCodes.OP_ENDIF);
        builder.op(ScriptOpCodes.OP_EQUALVERIFY);
        builder.op(ScriptOpCodes.OP_CHECKSIG);
        return builder.build();
    }

    private Transaction buildRefundTx(byte[] contract, Transaction contractTx, BigDecimal feePerCb, BigDecimal minFeePerKb) {
        return null;
    }

    public InitiateResult cmdInitiate(String initiatorAddress, String participantAddressStr, BigDecimal amount) {
        BitcoinRPCService bitcoinRPCService = initiatorBitcoinRPCService;
        InitiateResult result = new InitiateResult();
        Address participantAddress = LegacyAddress.fromString(networkParameters, participantAddressStr);
        byte[] secret = new byte[32]; //Longs.toByteArray(;);
        secret = Hex.decode("15315fd7c4d0a0a7c41a734a7589fed110251421839fb1539a777a3d02d202c7");
        //random.nextBytes(secret);
        ECKey key;
        log.info("Secret {}",Hex.toHexString(secret));
        HashCode secretHash = Hashing.sha256().hashBytes(secret);
        log.info("Hash of secret {}",Hex.toHexString(secretHash.asBytes()));
        Instant now = Instant.now();
        long lockTime =  Instant.now().plus(48, ChronoUnit.HOURS).getEpochSecond();
        lockTime = 1634891979;
        Script contract = generateLockScript(initiatorAddress, participantAddressStr, lockTime, secretHash);

        log.info("Contract: {}",Hex.toHexString(contract.getProgram()));
        byte[] scriptHash = Utils.sha256hash160(contract.getProgram());

        LegacyAddress contractP2SH = LegacyAddress.fromScriptHash(networkParameters, scriptHash);
        log.info("contract P2SH {}",contractP2SH.toString());
        Script contractP2SHPkScript = ScriptBuilder.createP2SHOutputScript(contractP2SH.getHash());

        Transaction unsignedContract = new Transaction(networkParameters);
        unsignedContract.addOutput(new TransactionOutput(networkParameters,unsignedContract,Coin.valueOf(amount.intValue()), contractP2SHPkScript.getProgram()));
        BitcoinRPCService.FundRawTransactionResult fundRawTransactionResult = bitcoinRPCService.fundRawTransaction(Hex.toHexString(unsignedContract.bitcoinSerialize()));
        if (StringUtils.hasLength(fundRawTransactionResult.hex)) {
            Map opResult = bitcoinRPCService.signRawTransaction(fundRawTransactionResult.hex);
            if (opResult.containsKey("hex") && (Boolean) opResult.get("complete")) {
                Transaction signedTransaction = new Transaction(networkParameters, Hex.decode((String) opResult.get("hex")));
                log.info("signed init tx [{}] {}", signedTransaction.getTxId(), opResult.get("hex"));
                //bitcoinRPCService.publishTransaction((String) opResult.get("hex"));
                result.contractHex = (String) opResult.get("hex");
                result.secretHash = secretHash.toString();
                result.secret = Hex.toHexString(secret);
                result.contractTx = signedTransaction.getTxId().toString();
                cmdRedeem(Hex.toHexString(contract.getProgram()),Hex.toHexString(signedTransaction.bitcoinSerialize()),Hex.toHexString(secret));
            }
        } else {
            log.error("Could nod fund transaction!");
        }

        return result;
    }

    private boolean checkChunks(Script script) {
        if (script.getChunks().size() != 20) {
            return false;
        }
        boolean result = script.getChunks().get(0).equalsOpCode(ScriptOpCodes.OP_IF) &&
                         script.getChunks().get(12).equalsOpCode(ScriptOpCodes.OP_CHECKLOCKTIMEVERIFY) &&
                         script.getChunks().get(1).equalsOpCode(ScriptOpCodes.OP_SIZE);
        return result;
    }


    public RedeemResult cmdRedeem(String contractHex, String contractTxHex, String secret) {
        BitcoinRPCService bitcoinRPCService = participantBitcoinRPCService;
        byte[] contract = Hex.decode(contractHex);
        Script script = new Script(Hex.decode(contractHex));
        RedeemResult result = new RedeemResult();
        if (!checkChunks(script)) {
            return result;
        }
        Transaction tx = new Transaction(networkParameters, Hex.decode(contractTxHex));
        byte[] secretHash = script.getChunks().get(5).data;
        log.info("extracted secret hash {}",Hex.toHexString(secretHash));
        byte[] recipientHash160 = script.getChunks().get(9).data;
        byte[] refundHash160 = script.getChunks().get(16).data;
        BigInteger secretSize = Utils.decodeMPI(Utils.reverseBytes(script.getChunks().get(2).data), false);
        BigInteger lockTime = Utils.decodeMPI(Utils.reverseBytes(script.getChunks().get(11).data), false);
        Address recipientAddress = LegacyAddress.fromPubKeyHash(networkParameters, recipientHash160);
        byte[] contractHash = Utils.sha256hash160(contract);
        LegacyAddress contractP2SH = LegacyAddress.fromScriptHash(networkParameters, contractHash);
        Script contractP2SHPkScript = ScriptBuilder.createP2SHOutputScript(contractP2SH.getHash());
        int contractOut = -1;
        int outIndex = 0;
        for (TransactionOutput output : tx.getOutputs()) {
            Script outScript = output.getScriptPubKey();
            if (Arrays.equals(outScript.getProgram(), contractP2SHPkScript.getProgram())) {
                contractOut = outIndex;
                break;
            }
            outIndex++;
        }
        if (contractOut == -1) {
            return (RedeemResult) AtomicSwapServiceResult.error(2);
        }

        String addrStr = bitcoinRPCService.getRawChangeAddress();

        Script outScript = ScriptBuilder.createP2PKHOutputScript(recipientHash160);
        Sha256Hash contractTxHash = tx.getTxId();
        Transaction redeemTx = new Transaction(networkParameters);
        redeemTx.addInput(
                new TransactionInput(networkParameters, redeemTx, new byte[0],
                        new TransactionOutPoint(networkParameters, contractOut , contractTxHash )));
        redeemTx.addOutput(Coin.ZERO, outScript);
        int redeemSize = estimateRedeemSerializeSize(contract,redeemTx.getOutputs());
        BitcoinRPCService.FeePerKbResult feePerKb = bitcoinRPCService.getFeePerKb();
        Coin fee = FeeUtils.feeForSerializeSize(feePerKb.useFee,redeemSize);
        fee = Coin.valueOf(400);
        redeemTx.getOutputs().get(0).setValue(tx.getOutput(contractOut).getValue().subtract(fee) );
        //check for isDust
        String privKeyStr = bitcoinRPCService.getPrivateKey(addrStr);
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameters, privKeyStr);
        ECKey key = dumpedPrivateKey.getKey();
        ECKey.ECDSASignature redeemSig = calcSignatureHash(redeemTx, 0, contract, recipientAddress, key);
        Script redeemSigScript = createRedeemP2SHContract(contract, redeemSig.encodeToDER(), key.getPubKey(), Hex.decode(secret));
        redeemTx.getInput(0).setScriptSig(redeemSigScript);
        Sha256Hash redeemTxHash = redeemTx.getTxId();
        Coin redeemFeePerKb = fee.div(redeemTx.bitcoinSerialize().length);
        redeemFeePerKb = redeemFeePerKb.div(100000);
       // log.info("redeem tx {}",redeemTx.)
        log.info("redeem tx [{}] {}", redeemTx.getTxId(), Hex.toHexString(redeemTx.bitcoinSerialize()));
        return result;
    }

    protected ECKey.ECDSASignature calcSignatureHash(Transaction redeemTx, int outIdx, byte[] contract, Address recipientAddress, ECKey key) {
        Script contractScript = new Script(contract);
        redeemTx.getInput(0).setScriptSig(contractScript);
        byte[] serializedTx = redeemTx.bitcoinSerialize();
        Sha256Hash first = Sha256Hash.of(serializedTx);
        Sha256Hash second = Sha256Hash.of(first.getBytes());
        return key.sign(second);
    }

    protected Script createRedeemP2SHContract(byte[] contract, byte[] signature, byte[] pubKey, byte[] secret) {
        ScriptBuilder builder = new ScriptBuilder();
        builder.data(signature);
        builder.data(pubKey);
        builder.data(secret);
        builder.number(1);
        builder.data(contract);
        return builder.build();
    }

    public String createP2SHAddress(String initiator, String participant) {
        ECKey key;
/*
        String base58PrivateKeyString = "KyyJ5vVWjZck5nsAgDWvoN1u7Q8qp5FzE8WiCq97MbnRgdLesqJZ";
        if (base58PrivateKeyString.length() == 51 || base58PrivateKeyString.length() == 52) {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, base58PrivateKeyString);
            key = dumpedPrivateKey.getKey();
        } else {
            BigInteger privKey = Base58.decodeToBigInteger(base58PrivateKeyString);
            key = ECKey.fromPrivate(privKey);
        }
*/
        Script script = generateLockScript(initiator,participant,48,HashCode.fromString("123"));
        byte[] scriptHash = Utils.sha256hash160(script.getProgram());

        LegacyAddress legacyAddress = LegacyAddress.fromScriptHash(networkParameters, scriptHash);
        log.info("Legacy address {}",legacyAddress.toString());
/*
        String pubKeyStr = key.getPublicKeyAsHex();
        System.out.println("Public key is: " + pubKeyStr + "\n");

        List<ECKey> eckeyAList = new ArrayList<>();
        eckeyAList.add(key);
        Script redeemScript = ScriptBuilder.createRedeemScript(1, eckeyAList);
        Script script = ScriptBuilder.createP2SHOutputScript(redeemScript);
        byte[] scriptHash = ScriptPattern.extractHashFromP2SH(script);
        LegacyAddress legacyAddress = LegacyAddress.fromScriptHash(params, scriptHash);
        System.out.println("P2S address from the WIF pivate key is: " + legacyAddress.toString()); //3Az5wdibtPRGac41aGtyqzT1ejtobvb6qW
 */
        return legacyAddress.toString();

    }
    // inputSize returns the size of the transaction input needed to include a
    // signature script with size sigScriptSize.  It is calculated as:
    //
    //   - 32 bytes previous tx
    //   - 4 bytes output index
    //   - Compact int encoding sigScriptSize
    //   - sigScriptSize bytes signature script
    //   - 4 bytes sequence
    private int inputSize(int sigScriptSize) {
        return 32 + 4 + VarInt.sizeOf((long)sigScriptSize) + sigScriptSize + 4;
    }

    private int sumOutputSerializeSizes(List<TransactionOutput> txOuts) {
        return txOuts.stream().mapToInt(item -> item.bitcoinSerialize().length).sum();
    }

    private int estimateRedeemSerializeSize(byte[] contract, List<TransactionOutput> txOuts) {
        int contractPushSize = contract.length;
        return 12 + VarInt.sizeOf(1) + VarInt.sizeOf((long)txOuts.size()) +
                inputSize(redeemAtomicSwapSigScriptSize+contractPushSize) +
                sumOutputSerializeSizes(txOuts);
    }
}
