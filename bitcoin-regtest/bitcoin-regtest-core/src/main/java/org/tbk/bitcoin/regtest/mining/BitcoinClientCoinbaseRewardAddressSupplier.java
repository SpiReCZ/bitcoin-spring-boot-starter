package org.tbk.bitcoin.regtest.mining;

import com.msgilligan.bitcoinj.rpc.BitcoinClient;
import org.bitcoinj.core.Address;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public final class BitcoinClientCoinbaseRewardAddressSupplier implements CoinbaseRewardAddressSupplier {

    private final BitcoinClient client;

    public BitcoinClientCoinbaseRewardAddressSupplier(BitcoinClient client) {
        this.client = requireNonNull(client);
    }

    @Override
    public Address get() {
        try {
            return client.getNewAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

