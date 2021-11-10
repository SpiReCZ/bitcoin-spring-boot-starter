# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Breaking
- build: exclude protobuf files from jar file

### Changed
- build: build javadoc and sources jar only for publication by default
- upgrade: update netlayer from v0.6.8 to v0.7.2

### Removed
- module: sunset incubator module 'tbk-btcabuse-client'
- module: sunset incubator module 'spring-mqtt'
- module: sunset incubator module 'bitcoin-mqtt'
  
## [0.2.0] - 2021-09-26
### Breaking
- refactor: throw IllegalArgumentException instead of NPE on @NonNull violations
- adapt class names in lnd client module (remove 'JsonRpc' string in names)
- rename lnd info/health actuator property from 'lndJsonRpc' to 'lndApi'
- remove class `LndRpcClientFactory`

### Added
- spring-lnurl: Publish `LnurlAuthWalletActionEvent` on successfully authorized wallet requests
- lnd: create beans for all available LightningJ APIs

### Changed
- spring-lnurl: Improved customization support for `LnurlAuthConfigurer`
- upgrade: update jmolecules from v2021.0.2 to v2021.1.0
- upgrade: update consensusj from v0.5.8 to v0.5.9
- upgrade: update xchange from v5.0.7 to v5.0.11

## [0.1.0] - 2021-08-14
### Added
- Initial release

[Unreleased]: https://github.com/theborakompanioni/bitcoin-spring-boot-starter/compare/0.2.0...HEAD
[0.2.0]: https://github.com/theborakompanioni/bitcoin-spring-boot-starter/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/theborakompanioni/bitcoin-spring-boot-starter/releases/tag/0.1.0

