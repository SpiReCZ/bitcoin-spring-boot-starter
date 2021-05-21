package org.tbk.bitcoin.regtest.mining;

import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;

import java.time.Duration;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Slf4j
public final class ScheduledRegtestMiner extends AbstractScheduledService implements RegtestMiner {
    private static final Duration DEFAULT_DELAY = Duration.ofSeconds(60);
    private static final Scheduler DEFAULT_SCHEDULER = Scheduler.newFixedDelaySchedule(Duration.ZERO, DEFAULT_DELAY);

    private final RegtestMiner delegate;
    private final Scheduler scheduler;

    public ScheduledRegtestMiner(RegtestMiner delegate) {
        this(delegate, DEFAULT_SCHEDULER);
    }

    public ScheduledRegtestMiner(RegtestMiner delegate, Scheduler scheduler) {
        this.delegate = requireNonNull(delegate);
        this.scheduler = requireNonNull(scheduler);
    }

    @Override
    protected void runOneIteration() {
        this.mineBlocks(1);
    }

    @Override
    protected Scheduler scheduler() {
        return this.scheduler;
    }

    @Override
    public List<Sha256Hash> mineBlocks(int count) {
        return delegate.mineBlocks(count);
    }

    @Override
    public List<Sha256Hash> mineBlocks(int count, CoinbaseRewardAddressSupplier addressSupplier) {
        return delegate.mineBlocks(count, addressSupplier);
    }

}

