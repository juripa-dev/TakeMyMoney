package com.juripa.takemymoney.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class LockEth extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b5061026d806100206000396000f3fe6080604052600436106100345760003560e01c806337bdc99b1461003957806370a082311461005b578063b6b55f25146100a3575b600080fd5b34801561004557600080fd5b506100596100543660046101f8565b6100b6565b005b34801561006757600080fd5b506100916100763660046101c8565b6001600160a01b031660009081526020819052604090205490565b60405190815260200160405180910390f35b6100596100b13660046101f8565b61015d565b336000908152602081905260409020548181101561011b5760405162461bcd60e51b815260206004820152601e60248201527f72656c6561736520616d6f756e7420657863656564732062616c616e6365000060448201526064015b60405180910390fd5b3360008181526020819052604080822085850390555184156108fc0291859190818181858888f19350505050158015610158573d6000803e3d6000fd5b505050565b8034146101a15760405162461bcd60e51b815260206004820152601260248201527173686f756c6420657175616c2076616c756560701b6044820152606401610112565b33600090815260208190526040812080548392906101c0908490610211565b909155505050565b6000602082840312156101da57600080fd5b81356001600160a01b03811681146101f157600080fd5b9392505050565b60006020828403121561020a57600080fd5b5035919050565b6000821982111561023257634e487b7160e01b600052601160045260246000fd5b50019056fea2646970667358221220ecdf5a0100e34e3de5c1b423a9d79011e28c922eec576a2578d0e27ef26025fb64736f6c63430008070033";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_DEPOSIT = "deposit";

    public static final String FUNC_RELEASE = "release";

    @Deprecated
    protected LockEth(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected LockEth(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected LockEth(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected LockEth(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String account) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> deposit(BigInteger amount) {
        final Function function = new Function(
                FUNC_DEPOSIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, amount);
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(BigInteger amount) {
        final Function function = new Function(
                FUNC_RELEASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static LockEth load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new LockEth(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static LockEth load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new LockEth(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static LockEth load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new LockEth(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static LockEth load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new LockEth(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<LockEth> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(LockEth.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<LockEth> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(LockEth.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<LockEth> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LockEth.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<LockEth> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LockEth.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
