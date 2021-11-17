package org.tbk.spring.testcontainer.electrumx.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.tbk.spring.testcontainer.bitcoind.config.BitcoindContainerAutoConfiguration;
import org.tbk.spring.testcontainer.electrumx.ElectrumxContainer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElectrumxContainerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void noBeansAreCreated() {
        this.contextRunner.withUserConfiguration(
                BitcoindContainerAutoConfiguration.class,
                ElectrumxContainerAutoConfiguration.class
        ).withPropertyValues(
                "org.tbk.spring.testcontainer.electrumx.enabled=false"
        ).run(context -> {
            assertThat(context.containsBean("electrumxContainer"), is(false));
            assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(ElectrumxContainer.class));
        });
    }

    @Test
    void beansAreCreated() {
        this.contextRunner.withUserConfiguration(
                BitcoindContainerAutoConfiguration.class,
                ElectrumxContainerAutoConfiguration.class
        ).withPropertyValues(
                "org.tbk.spring.testcontainer.bitcoind.enabled=true",
                "org.tbk.spring.testcontainer.bitcoind.rpcuser=myrpcuser",
                "org.tbk.spring.testcontainer.bitcoind.rpcpassword=correcthorsebatterystaple",
                // ----------------------------------------------
                "org.tbk.spring.testcontainer.electrumx.enabled=true",
                "org.tbk.spring.testcontainer.electrumx.rpcuser=myrpcuser",
                "org.tbk.spring.testcontainer.electrumx.rpcpass=correcthorsebatterystaple",
                "org.tbk.spring.testcontainer.electrumx.rpchost=localhost",
                "org.tbk.spring.testcontainer.electrumx.rpcport=18443"
        ).run(context -> {
            assertThat(context.containsBean("electrumxContainer"), is(true));
            assertThat(context.getBean(ElectrumxContainer.class), is(notNullValue()));
        });
    }

}
