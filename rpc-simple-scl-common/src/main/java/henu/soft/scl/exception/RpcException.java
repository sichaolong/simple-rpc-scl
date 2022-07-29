package henu.soft.scl.exception;


import henu.soft.scl.enums.RpcErrorMessageEnum;

/**
 * name : RpcException.java
 * creator : sichaolong
 * date : 2022/7/28 18:15
 * descript : RPC运行期异常封装类
**/

public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
