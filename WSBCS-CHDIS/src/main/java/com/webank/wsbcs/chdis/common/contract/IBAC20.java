package com.webank.wsbcs.chdis.common.contract;

import java.math.BigInteger;
import java.util.Arrays;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class IBAC20 extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"name\":\"balanceOf\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"conflictFields\":[],\"inputs\":[{\"name\":\"account\",\"type\":\"address\",\"internalType\":\"address\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"address\"}],\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}],\"selector\":[],\"methodSignatureAsString\":\"balanceOf(address)\"},{\"name\":\"totalSupply\",\"type\":\"function\",\"constant\":true,\"payable\":false,\"anonymous\":false,\"stateMutability\":\"view\",\"conflictFields\":[],\"inputs\":[],\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"internalType\":\"uint256\",\"indexed\":false,\"components\":[],\"dynamic\":false,\"typeAsString\":\"uint256\"}],\"selector\":[],\"methodSignatureAsString\":\"totalSupply()\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    protected IBAC20(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public BigInteger balanceOf(String account) throws ContractException {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger totalSupply() throws ContractException {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public static IBAC20 load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new IBAC20(contractAddress, client, credential);
    }

    public static IBAC20 deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(IBAC20.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }
}
