# Welcome to BetterShop ![build status](https://ci.husk.pro/BetterShop/badge)

This is a rewrite of the original [Shop by Conjurate](https://github.com/Nowaha/Shop-by-Conjurate) with aim of extensibility, cleanliness and performance in mind!

## ⚙️ Goals
- Simple to use
- Clean, fast code
- Near-full feature parity

The biggest issue with the previous plugin was the code was unmaintainable, making changes or debugging was unnecessarily difficult.

This is aimed to improve in all these aspects as well as provide greater functionality!

## 📥 Download

While the project is still in it's early days, bleeding edge downloads are [available here](https://ci.husk.pro/)

Alternatively you can build it yourself using [Maven](https://maven.apache.org/) with
``mvn clean package``

## 🔨 Setup

Setup is simple!
1. Install [Vault](https://www.spigotmc.org/resources/vault.34315/).
2. Move jar to plugins folder
3. Done

## 💻 Commands
| Command | Description | Permission |
| --------------- | ---------------- | ---------------- |
| \/shop *shopName* | Shows the main shop or if provided, the given shop name | N/A
| \/shop create *shopName* | Creates a shop with given name | shop.create
| \/shop delete *shopName* | Deletes a shop with given name | shop.delete
| \/shop edit *shopName* | Edits the shop with given name | shop.edit
| \/shop list | Lists all shops | shop.list
| \/shop help | Shows the command usages | N/A
| \/shop reload | Reloads the config | shop.reload

## 🧰 Tracking development

Development can be followed on the project page [here](https://github.com/Huskehhh/BetterShop/projects/1)
If you have a feature request that isn't there, please [create an issue](https://github.com/Huskehhh/BetterShop/issues)

## 🔧 BetterShop integration (Developers)

If you wish to develop a plugin that hooks into BetterShop, you can find the artifacts on my maven repository:

```xml
<repository>
    <id>husk</id>
    <url>https://maven.husk.pro/snapshots/</url>
</repository>
```

```xml
<dependency>
  <groupId>pro.husk</groupId>
  <artifactId>BetterShop</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## ⚠️ Issues
Please use the [issue tracker](https://github.com/Huskehhh/BetterShop/issues)