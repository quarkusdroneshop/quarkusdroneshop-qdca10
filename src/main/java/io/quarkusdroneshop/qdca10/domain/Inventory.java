package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.EnumMap;
import java.util.Map;

@ApplicationScoped
public class Inventory {

    // drone-component-stock データプロダクト (dataproduct-component-stock-quantity) から
    // 受信した item ごとの直近の実在庫数 (ランダム値ではなく inventory ドメイン由来の実データ)。
    private final Map<Item, Integer> rawBaseline = new EnumMap<>(Item.class);

    // 注文消費の判定・減算に使う実際のプール。QDC_A101 と QDC_A102 は同一ドローンの
    // バリエーションのため、従来通り QDC_A101 のプールへ合算する。
    private final Map<Item, Integer> stock = new EnumMap<>(Item.class);

    Logger LOGGER = LoggerFactory.getLogger(Inventory.class.getName());

    /*
        QDC_A101 and QDC_A102 are simply tracked as QDC_A101
     */
    private static Item toLogicalKey(final Item item) {
        return item == Item.QDC_A102 ? Item.QDC_A101 : item;
    }

    // drone-component-stock からの補充完了通知を受信するたびに呼ばれる。
    // upsert (その item の最新の絶対数) を受け取るため、直近の値でそのまま置き換える
    // (ローカルでの消費分は補充シグナルの到着で洗い替えられる。以前の乱数版
    // restock() も同様に「上書き」だったため、挙動としては踏襲している)。
    // データプロダクトから一度もイベントを受信していない item は在庫 0 (=売り切れ)
    // として扱う (ランダムな初期値を捏造しない)。
    public synchronized void applyStockUpdate(final Item item, final int quantity) {
        LOGGER.debug("applying stock update from drone-component-stock: {} -> {}", item, quantity);
        rawBaseline.put(item, quantity);

        Item logicalKey = toLogicalKey(item);
        if (logicalKey == Item.QDC_A101) {
            int total = rawBaseline.getOrDefault(Item.QDC_A101, 0) + rawBaseline.getOrDefault(Item.QDC_A102, 0);
            stock.put(Item.QDC_A101, total);
        } else {
            stock.put(logicalKey, rawBaseline.getOrDefault(logicalKey, 0));
        }
    }

    public synchronized boolean decrementItem(final Item item) {

        LOGGER.debug("decrementing {}", item);

        Item key = toLogicalKey(item);
        Integer itemCount = stock.get(key);
        LOGGER.debug("current inventory for {} is {}", key, itemCount);

        if (itemCount == null || itemCount <= 0) return false;

        itemCount--;
        stock.put(key, itemCount);
        LOGGER.debug("updated inventory for {} is {}", key, stock.get(key));
        return true;
    }

    public synchronized Map<Item, Integer> getStock() {
        return new EnumMap<>(stock);
    }

    public synchronized Integer getTotalDrone() {
        return stock.getOrDefault(Item.QDC_A101, 0);
    }
}
