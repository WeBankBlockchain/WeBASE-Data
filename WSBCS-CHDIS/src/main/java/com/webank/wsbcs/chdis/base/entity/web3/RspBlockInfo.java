package com.webank.wsbcs.chdis.base.entity.web3;

import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.utils.Numeric;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于统一v2 v3返回值，以v2为准
 * @author marsli
 * @date 2022/7/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RspBlockInfo {

  private String parentHash;
  private Long number;
  private String hash;
  private String logsBloom;
  private String transactionsRoot;
  private String receiptsRoot;
  private String stateRoot;
  private Integer sealer;
  private List<String> sealerList;
  private String gasUsed;
  private String timestamp;
  private List<Signature> signatureList;
  // v3
  private List<Long> consensusWeights;
  // onlyHeader必须false, onlyTxHash必须false，要返回全量的交易
  private List<JsonTransactionResponse> transactions;
  private Integer transCounts;

//  /**
//   * 兼容性字段，v2
//   */
//  @Deprecated
//  protected String dbHash;
//  @Deprecated
//  protected List<String> extraData;
//  @Deprecated
//  protected String gasLimit;


  public RspBlockInfo(org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block block) {
    this.parentHash = block.getParentHash();
    this.number = block.getNumber().longValue();
    this.hash = block.getHash();
    this.logsBloom = block.getLogsBloom();
    this.transactionsRoot = block.getTransactionsRoot();
    this.receiptsRoot = block.getReceiptsRoot();
    this.stateRoot = block.getStateRoot();
    this.sealer = Numeric.decodeQuantity(block.getSealer()).intValue();
    this.sealerList = block.getSealerList();
    this.gasUsed = block.getGasUsed();
//    this.timestamp = Numeric.decodeQuantity(block.getTimestamp()).longValue();
    this.timestamp = block.getTimestamp();
    this.signatureList = block.getSignatureList().stream().map(Signature::new).collect(Collectors.toList());
    this.consensusWeights = null;
    this.transactions = block.getTransactions().stream().map(tx->new JsonTransactionResponse((org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse)tx)).collect(Collectors.toList());
    this.transCounts = this.transactions.size();
  }


  public RspBlockInfo(org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock.Block block) {
    this.parentHash = block.getParentInfo().isEmpty() ? null : block.getParentInfo().get(0).getBlockHash(); // 区块并行遗留导致list
    this.number = block.getNumber();
    this.hash = block.getHash();
    this.logsBloom = block.getLogsBloom();
    this.transactionsRoot = block.getTransactionsRoot();
    this.receiptsRoot = block.getReceiptsRoot();
    this.stateRoot = block.getStateRoot();
    this.sealer = block.getSealer();
    this.sealerList = block.getSealerList();
    this.gasUsed = block.getGasUsed();
//    this.timestamp = BigInteger.valueOf(block.getTimestamp()).toString(16);
    this.timestamp = String.valueOf(block.getTimestamp());
    this.signatureList = block.getSignatureList().stream().map(Signature::new).collect(Collectors.toList());
    this.consensusWeights = block.getConsensusWeights();
    this.transactions = block.getTransactions().stream().map(tx->new JsonTransactionResponse((org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse)tx)).collect(Collectors.toList());
    this.transCounts = this.transactions.size();
  }


  public RspBlockInfo(BcosBlockHeader.BlockHeader block) {
    this.parentHash = block.getParentHash();
    this.number = block.getNumber().longValue();
    this.hash = block.getHash();
    this.logsBloom = block.getLogsBloom();
    this.transactionsRoot = block.getTransactionsRoot();
    this.receiptsRoot = block.getReceiptsRoot();
    this.stateRoot = block.getStateRoot();
    this.sealer = Numeric.decodeQuantity(block.getSealer()).intValue();
    this.sealerList = block.getSealerList();
    this.gasUsed = block.getGasUsed();
//    this.timestamp = Numeric.decodeQuantity(block.getTimestamp()).longValue(); // 使用hex，兼容以前的
    this.timestamp = block.getTimestamp();
    this.signatureList = block.getSignatureList().stream().map(Signature::new).collect(Collectors.toList());
  }


  public RspBlockInfo(org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlockHeader.BlockHeader block) {
    this.parentHash = block.getParentInfo().isEmpty() ? null : block.getParentInfo().get(0).getBlockHash(); // 区块并行遗留导致list
    this.number = block.getNumber();
    this.hash = block.getHash();
    this.logsBloom = block.getLogsBloom();
    this.transactionsRoot = block.getTransactionsRoot();
    this.receiptsRoot = block.getReceiptsRoot();
    this.stateRoot = block.getStateRoot();
    this.sealer = block.getSealer();
    this.sealerList = block.getSealerList();
    this.gasUsed = block.getGasUsed();
    // 兼容旧的hex格式
//    this.timestamp = BigInteger.valueOf(block.getTimestamp()).toString(16);
    this.timestamp = String.valueOf(block.getTimestamp());
    this.signatureList = block.getSignatureList().stream().map(Signature::new).collect(Collectors.toList());
    this.consensusWeights = block.getConsensusWeights();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Signature {
    private int index;
    private String signature;

    public Signature(org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlockHeader.Signature signature) {
      this.index = signature.getIndex();
      this.signature = signature.getSignature();
    }


    public Signature(BcosBlockHeader.Signature signature) {
      this.index = Numeric.decodeQuantity(signature.getIndex()).intValue();
      this.signature = signature.getSignature();
    }

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class JsonTransactionResponse {

    private String chainId;
    private String groupId;
    private SignatureResponse signature;
    private String hash;
    private String nonce;
    private Long blockLimit;
    private String to;
    private String from;
    private String input;
    /**
     * appInfo存储
     */
    private String extraData;
    private String abi; // v3有
    /**
     * 兼容性字段，只在v2存在
     */
//    @Deprecated
//    protected String blockHash;
//    @Deprecated
//    protected Long blockNumber;
//    @Deprecated
//    protected String transactionIndex;
//    @Deprecated
//    protected String value;
//    @Deprecated
//    protected String gasPrice;
//    private List<MerkleProofUnit> transactionProof; // v3有

    // v2
    public JsonTransactionResponse(org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse transaction) {
      this.chainId = Numeric.decodeQuantity(transaction.getChainId()).toString(10);
      this.groupId = Numeric.decodeQuantity(transaction.getGroupId()).toString(10);
      this.signature = new SignatureResponse(transaction.getSignature());
      this.hash = transaction.getHash();
      this.nonce = transaction.getNonce();
      this.blockLimit = Numeric.decodeQuantity(transaction.getBlockLimit()).longValue();
      this.to = transaction.getTo();
      this.from = transaction.getFrom();
      this.input = transaction.getInput();
      this.extraData = transaction.getExtraData();
    }

    // v3
    public JsonTransactionResponse(org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse transaction) {
      this.chainId = transaction.getChainID();
      this.groupId = transaction.getGroupID();
      this.signature = new SignatureResponse(transaction.getSignature());
      this.hash = transaction.getHash();
      this.nonce = transaction.getNonce();
      this.blockLimit = transaction.getBlockLimit();
      this.to = transaction.getTo();
      this.from = transaction.getFrom();
      this.input = transaction.getInput();
      this.extraData = transaction.getExtraData();
      this.abi = transaction.getAbi();
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SignatureResponse {
    private String r;
    private String s;
    private String v;
    private String signature;

    // v3
    public SignatureResponse(String signature) {
      this.signature = signature;
      this.r = "";
      this.s = "";
      this.v = "";
    }

    // v2
    public SignatureResponse(org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse.SignatureResponse signatureResponse) {
      this.r = signatureResponse.getR();
      this.s = signatureResponse.getS();
      this.v = signatureResponse.getV();
      this.signature = signatureResponse.getSignature();
    }
  }

}
