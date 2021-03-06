/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.mssql;

import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static io.r2dbc.mssql.MssqlConnectionFactoryProvider.MSSQL_DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MssqlConnectionFactoryProvider}.
 *
 * @author Mark Paluch
 */
final class MssqlConnectionFactoryProviderTest {

    private final MssqlConnectionFactoryProvider provider = new MssqlConnectionFactoryProvider();

    @Test
    void doesNotSupportWithWrongDriver() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, "test-driver")
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(PORT, -1)
            .option(USER, "test-user")
            .build())).isFalse();
    }

    @Test
    void doesNotSupportWithoutDriver() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(PORT, -1)
            .option(USER, "test-user")
            .build())).isFalse();
    }

    @Test
    void doesNotSupportWithoutHost() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, MSSQL_DRIVER)
            .option(PASSWORD, "test-password")
            .option(PORT, -1)
            .option(USER, "test-user")
            .build())).isFalse();
    }

    @Test
    void doesNotSupportWithoutPassword() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, MSSQL_DRIVER)
            .option(HOST, "test-host")
            .option(PORT, -1)
            .option(USER, "test-user")
            .build())).isFalse();
    }

    @Test
    void doesNotSupportWithoutUser() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, MSSQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(PORT, -1)
            .build())).isFalse();
    }

    @Test
    void supports() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, MSSQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .build())).isTrue();
    }

    @Test
    void shouldConfigureWithStaticCursoredExecutionPreference() {

        MssqlConnectionFactory factory = this.provider.create(ConnectionFactoryOptions.builder()
            .option(DRIVER, MSSQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .option(MssqlConnectionFactoryProvider.PREFER_CURSORED_EXECUTION, "true")
            .build());

        ConnectionOptions options = factory.getConnectionOptions();

        assertThat(options.prefersCursors("foo")).isTrue();
    }

    @Test
    void shouldConfigureWithStaticBooleanCursoredExecutionPreference() {

        MssqlConnectionFactory factory = this.provider.create(ConnectionFactoryOptions.builder()
            .option(DRIVER, MSSQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .option(MssqlConnectionFactoryProvider.PREFER_CURSORED_EXECUTION, true)
            .build());

        ConnectionOptions options = factory.getConnectionOptions();

        assertThat(options.prefersCursors("foo")).isTrue();
    }

    @Test
    void shouldConfigureWithClassCursoredExecutionPreference() {

        Predicate<String> preference = s -> s.equals("foo");

        MssqlConnectionFactory factory = this.provider.create(ConnectionFactoryOptions.builder()
            .option(DRIVER, MSSQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .option(MssqlConnectionFactoryProvider.PREFER_CURSORED_EXECUTION, MyPredicate.class.getName())
            .build());

        ConnectionOptions options = factory.getConnectionOptions();

        assertThat(options.prefersCursors("foo")).isTrue();
        assertThat(options.prefersCursors("bar")).isFalse();
    }

    @Test
    void returnsDriverIdentifier() {
        assertThat(this.provider.getDriver()).isEqualTo(MSSQL_DRIVER);
    }

    static class MyPredicate implements Predicate<String> {

        @Override
        public boolean test(String s) {
            return s.equals("foo");
        }
    }
}
