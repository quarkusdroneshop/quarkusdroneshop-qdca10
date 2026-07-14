package io.quarkusdroneshop.qdca10;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * ArchUnit によるアーキテクチャ適合性テスト。
 * パッケージ構造:
 *   io.quarkusdroneshop.domain.*               - 共有ドメイン (Item / 値オブジェクト)
 *   io.quarkusdroneshop.qdca10.domain.*        - QDCA10 ドメイン層 (エンティティ / イベント / 例外)
 *   io.quarkusdroneshop.qdca10.infrastructure.*- インフラ層 (Kafka / シリアライズ)
 */
@AnalyzeClasses(
        packages = "io.quarkusdroneshop",
        importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    // =========================================================================
    // 1. 命名規則
    // =========================================================================

    @ArchTest
    static final ArchRule Deserializer命名規則 =
        classes()
            .that().implement("org.apache.kafka.common.serialization.Deserializer")
            .or().areAssignableTo(
                io.quarkus.kafka.client.serialization.ObjectMapperDeserializer.class)
            .should().haveSimpleNameEndingWith("Deserializer");

    @ArchTest
    static final ArchRule Serializer命名規則 =
        classes()
            .that().implement("org.apache.kafka.common.serialization.Serializer")
            .and().resideInAPackage("io.quarkusdroneshop..")
            .should().haveSimpleNameEndingWith("Serializer");

    @ArchTest
    static final ArchRule 例外クラスの命名規則 =
        classes()
            .that().areAssignableTo(Exception.class)
            .and().resideInAPackage("io.quarkusdroneshop..")
            .should().haveSimpleNameEndingWith("Exception");

    // =========================================================================
    // 2. パッケージ配置ルール
    // =========================================================================

    @ArchTest
    static final ArchRule Deserializerはinfrastructureに配置 =
        classes()
            .that().haveSimpleNameEndingWith("Deserializer")
            .should().resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule Serializerはinfrastructureに配置 =
        classes()
            .that().haveSimpleNameEndingWith("Serializer")
            .and().resideInAPackage("io.quarkusdroneshop..")
            .should().resideInAPackage("..infrastructure..");

    // =========================================================================
    // 3. レイヤー間依存ルール
    // =========================================================================

    @ArchTest
    static final ArchRule ドメイン層はInfrastructureに依存しない =
        noClasses()
            .that().resideInAPackage("io.quarkusdroneshop.qdca10.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("io.quarkusdroneshop.qdca10.infrastructure..");

    @ArchTest
    static final ArchRule 共有ドメイン層はQdca10Infrastructureに依存しない =
        noClasses()
            .that().resideInAPackage("io.quarkusdroneshop.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("io.quarkusdroneshop.qdca10.infrastructure..");

    @ArchTest
    static final ArchRule ドメインクラスはPublic =
        classes()
            .that().resideInAPackage("io.quarkusdroneshop.qdca10.domain")
            .and().areNotInterfaces()
            .and().areNotAnonymousClasses()
            .should().bePublic();

    @ArchTest
    static final ArchRule Infrastructureの依存範囲チェック =
        classes()
            .that().resideInAPackage("io.quarkusdroneshop.qdca10.infrastructure..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "io.quarkusdroneshop.qdca10.infrastructure..",
                "io.quarkusdroneshop.qdca10.domain..",
                "io.quarkusdroneshop.domain..",
                "java..",
                "javax..",
                "jakarta..",
                "io.quarkus..",
                "io.smallrye..",
                "org.eclipse.microprofile..",
                "org.apache.kafka..",
                "com.fasterxml..",
                "org.slf4j..",
                "org.jboss..");

    // =========================================================================
    // 4. 循環依存
    // =========================================================================

    @ArchTest
    static final ArchRule パッケージ間循環依存なし =
        slices()
            .matching("io.quarkusdroneshop.qdca10.(*)..")
            .should().beFreeOfCycles();
}
