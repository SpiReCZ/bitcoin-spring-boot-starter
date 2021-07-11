package org.tbk.bitcoin.example.payreq.bitcoin.block;

import org.bitcoinj.core.Sha256Hash;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotNull;

final class BitcoinBlockSpecifications {

    private BitcoinBlockSpecifications() {
        throw new UnsupportedOperationException();
    }

    static @NotNull Specification<BitcoinBlock> byHash(@NotNull Sha256Hash hash) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hash"), hash);
    }
}
