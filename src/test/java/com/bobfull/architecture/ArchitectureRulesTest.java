package com.bobfull.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

class ArchitectureRulesTest {

    private static final JavaClasses BOBFULL_CLASSES = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.bobfull");

    @Test
    void Controller는_Repository를_직접_참조하지_않는다() {
        noClasses()
                .that().resideInAnyPackage("..controller..")
                .should().dependOnClassesThat().resideInAnyPackage("..repository..")
                .check(BOBFULL_CLASSES);
    }

    @Test
    void 공통_Outbox_엔진은_기능별_Outbox_Handler_구현을_참조하지_않는다() {
        noClasses()
                .that().resideInAPackage("com.bobfull.infrastructure.outbox..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.bobfull.chat.outbox..",
                        "com.bobfull.reservation.outbox.."
                )
                .check(BOBFULL_CLASSES);
    }
}
