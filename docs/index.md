# QDCA10 マイクロサービス

## 概要

QDCA10 はドローンショップの **ドローンA 製造マイクロサービス** です。

- Counter から Kafka 経由で製造指示を受信
- ドローンAの製造ビジネスロジックを実行
- 製造完了後、Counter へ完了通知を送信

**フレームワーク**: Quarkus  
**デプロイ先クラスター**: b-cluster

---

## アーキテクチャ

```
Counter（a-cluster）
        │
        ▼ Kafka: qdca10-in（MirrorMaker2 経由）
┌────────────────┐
│    QDCA10      │ ── 製造ビジネスロジック実行
│                │
│                │──► Kafka: orders-up（製造完了通知）
└────────────────┘
                        │
                        ▼ MirrorMaker2
                Counter（a-cluster）
```

### Kafka トピック一覧

| トピック | 方向 | 説明 |
|---------|------|------|
| `qdca10-in` | 受信 | Counter からの製造指示 |
| `orders-up` | 送信 | 製造完了通知 |

### メッセージ形式（OrderTicket）

```json
{
  "orderId": "uuid",
  "item": "DRONE_A",
  "preparedBy": "QDCA10"
}
```

---

## ローカル開発

### 前提条件

- Java 17+
- Docker / Docker Compose

### 1. インフラ起動

```shell
git clone https://github.com/quarkusdroneshop/quarkusdroneshop-support.git
cd quarkusdroneshop-support
docker compose up -d
```

### 2. アプリケーション起動

```shell
git clone https://github.com/quarkusdroneshop/quarkusdroneshop-qdca10.git
cd quarkusdroneshop-qdca10
./mvnw clean compile quarkus:dev
```

### 3. テストメッセージ送信

```shell
# qdca10-in トピックへ直接送信
kafka-console-producer --broker-list localhost:9092 --topic qdca10-in
> {"orderId":"test-001","item":"DRONE_A","quantity":1}
```

### 環境変数

| 変数名 | デフォルト | 説明 |
|--------|-----------|------|
| `KAFKA_BOOTSTRAP_URLS` | `localhost:9092` | Kafka ブートストラップアドレス |

---

## 本番デプロイ（Tekton Pipeline）

### パイプライン概要

```
fetch-repository → semgrep-scan → maven-run → push-oc-apps
```

### 手動実行

```shell
tkn pipeline start build-and-push-quarkusdroneshop-qdca10 \
  -n quarkusdroneshop-cicd \
  --use-param-defaults
```

---

## テスト

```shell
# ユニットテスト(ArchUnit含む)
./mvnw test

# 統合テスト（Jacoco含む / Kafka コンシューマー動作確認）
./mvnw verify

# チェックスタイル
./mvnw checkstyle:check

# PMD
./mvnw pmd:pmd

# SpotBugs
./mvnw spotbugs:spotbugs

# semgrep
semgrep scan --config p/default --json > target/semgrep-results.json

# secret scan
gitleaks detect --source . --report-format json --report-path target/gitleaks-report.json --exit-code 1

# 脆弱性テスト
trivy fs --scanners vuln,secret,misconfig,license --exit-code=1 --ignorefile ./.trivyignore.yaml ./ > target/trivy.txt

# セキュリティテスト
mvn quarkus:dev > quarkus.log 2>&1 & QUARKUS_PID=$!; sleep 10; wapiti -u http://localhost:8080 -f json -o ./target/wapiti.json; kill $QUARKUS_PID

# テストレポートの作成
./mvnw exec:exec@generate-report
```

---

## 注意事項

- **製造時間シミュレーション**: 製造ビジネスロジックには一定の待機時間が含まれます（実際の製造時間をシミュレーション）。
- **クラスター間 Kafka**: `qdca10-in` は a-cluster の Kafka から MirrorMaker2 でミラーリング。完了通知 `orders-up` も MirrorMaker2 で a-cluster へ転送されます。
- **QDCA10Pro との違い**: QDCA10 はドローンAを、QDCA10Pro はドローンBを製造します。処理ロジックは同様ですが、製造する製品が異なります。
