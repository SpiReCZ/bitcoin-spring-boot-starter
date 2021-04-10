package org.tbk.spring.testcontainer.bitcoind.config;

import com.msgilligan.bitcoinj.rpc.BitcoinClient;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tbk.bitcoin.zeromq.client.MessagePublishService;
import org.testcontainers.shaded.org.apache.commons.lang.math.RandomUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class BitcoinContainerWithZeroMqClientTest {

    @SpringBootApplication
    public static class BitcoinContainerClientTestApplication {

        public static void main(String[] args) {
            new SpringApplicationBuilder()
                    .sources(BitcoinContainerClientTestApplication.class)
                    .bannerMode(Banner.Mode.OFF)
                    .web(WebApplicationType.NONE)
                    .run(args);
        }
    }

    @Autowired
    private BitcoinClient bitcoinClient;

    @Autowired
    private MessagePublishService<Block> bitcoinBlockPublishService;

    @Test
    public void testPubzmqrawblock() throws InterruptedException, ExecutionException, TimeoutException {
        Duration timeout = Duration.ofSeconds(10);
        int amountOfBlockToGenerate = Math.max(1, RandomUtils.nextInt(10));
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<List<Block>> blockFuture = executorService.submit(() -> Flux.from(bitcoinBlockPublishService)
                .bufferTimeout(amountOfBlockToGenerate, Duration.ofSeconds(1))
                .blockFirst(timeout));

        Future<List<Sha256Hash>> generateBlockFuture = executorService.submit(() -> {
            try {
                Address newAddress = bitcoinClient.getNewAddress();
                List<Sha256Hash> sha256Hashes = bitcoinClient.generateToAddress(amountOfBlockToGenerate, newAddress);
                log.info("mined blocks {}", sha256Hashes);
                return sha256Hashes;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        List<Sha256Hash> generatedBlockHashes = generateBlockFuture.get(timeout.toSeconds(), TimeUnit.SECONDS);

        List<Block> receivedBlocks = blockFuture.get(timeout.toSeconds(), TimeUnit.SECONDS);

        executorService.shutdown();
        executorService.shutdownNow();

        assertThat(generatedBlockHashes, hasSize(greaterThanOrEqualTo(1)));
        assertThat(receivedBlocks, hasSize(generatedBlockHashes.size()));

        List<Sha256Hash> receivedBlockHashes = receivedBlocks.stream()
                .map(Block::getHash)
                .collect(Collectors.toUnmodifiableList());

        // check that all blocks are received - block might not be received in order
        assertThat("all generated blocks are received via zeromq", receivedBlockHashes, containsInAnyOrder(generatedBlockHashes.toArray()));
    }
}
