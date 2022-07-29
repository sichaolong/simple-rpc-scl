package henu.soft.scl.rpc.compress;

import henu.soft.scl.extension.SPI;

/**
 * @author sichaolong
 * @date 2022/7/29 10:16
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
