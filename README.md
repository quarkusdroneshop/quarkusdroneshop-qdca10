# quarkusdroneshop-qdca10

Quarkus ベースのドリンク製造マイクロサービス (QDCA10 モデル)。Kafka から注文チケットを受け取り、ドリンク製造のビジネスロジックを実行し、完了イベントを送信します。在庫切れ (86'd) の場合は `eighty-six` トピックにイベントを送信します。

## アーキテクチャ

```
quarkusdroneshop-counter
    │  qdca10-in (dev) / shop-asite.qdca10-in (prod) ──▶
    ▼
quarkusdroneshop-qdca10
    │
    ├──▶ orders-up        (製造完了通知 → counter)
    └──▶ eighty-six       (在庫切れ通知)
```

## Kafka トピック

| チャネル | dev トピック | prod トピック | 方向 |
|---|---|---|---|
| orders-in | `qdca10-in` | `shop-asite.qdca10-in` | 受信 |
| orders-up | `orders-up` | `orders-up` | 送信 |
| eighty-six | `eighty-six` | `eighty-six` | 送信 |

## ローカル開発

```shell
git clone https://github.com/quarkusdroneshop/quarkusdroneshop-support.git
cd quarkusdroneshop-support
docker compose up

cd ../quarkusdroneshop-qdca10
./mvnw quarkus:dev
```

## 環境変数 (本番)

| 変数名 | 説明 |
|---|---|
| `KAFKA_BOOTSTRAP_URLS` | Kafka ブローカー URL |

## パッケージング

```shell
# JVM モード
./mvnw package
java -jar target/quarkusdroneshop-qdca10-1.0-SNAPSHOT-runner.jar

# ネイティブビルド
./mvnw package -Pnative -Dquarkus.native.container-build=true
./target/quarkusdroneshop-qdca10-1.0-SNAPSHOT-runner

# Docker 実行
docker run -i --network="host" \
  -e KAFKA_BOOTSTRAP_URLS=localhost:9092 \
  quarkusdroneshop-qdca10/quarkus-shop-QDCA10:latest
```

## 参考

- [Quarkus](https://quarkus.io/)
- [quarkusdroneshop.github.io](https://quarkusdroneshop.github.io)
