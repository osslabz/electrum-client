Electrum Client
===============
![GitHub](https://img.shields.io/github/license/osslabz/electrum-client)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/osslabz/electrum-client/build-on-push.yml?branch=dev&label=build&logo=git)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/osslabz/electrum-client/release.yml?branch=dev&label=perform-release&logo=semanticrelease)
[![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/net/osslabz/electrum-client/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/net/osslabz/electrum-client/README.md)
[![Maven Central](https://img.shields.io/maven-central/v/net.osslabz/electrum-client?label=Maven%20Central)](https://search.maven.org/artifact/net.osslabz/electrum-client)

A basic Electrum Client for Java/JVM-based languages using JSON-RPC.

0.3.0 is from May 2026, the third release since March 2025. The single test class asserts nothing; it is a main method that hits a public
Electrum server and logs the replies, and the only consumer is a private project of mine.

Current release:

```xml
<dependency>
    <groupId>net.osslabz</groupId>
    <artifactId>electrum-client</artifactId>
    <version>0.3.0</version>
</dependency>
```