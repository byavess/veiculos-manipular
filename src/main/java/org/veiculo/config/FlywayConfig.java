package org.veiculo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class FlywayConfig {

    private final DataSource dataSource;

    /**
     * Bean customizado que força a limpeza do banco SEMPRE antes das migrações
     * Ativado apenas quando: flyway.force-clean=true
     */
    @Bean
    @ConditionalOnProperty(prefix = "flyway", name = "force-clean", havingValue = "true")
    public FlywayMigrationInitializer flywayMigrationInitializer() {
        log.warn("========================================");
        log.warn("⚠️  ATENÇÃO: LIMPEZA FORÇADA DO BANCO ⚠️");
        log.warn("========================================");
        log.warn("Todas as tabelas serão DELETADAS!");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .cleanDisabled(false)  // Permite a execução do clean
                .load();

        // FORÇA A LIMPEZA DO BANCO
        log.warn("Executando CLEAN no banco de dados...");
        flyway.clean();
        log.info("✓ Banco limpo com sucesso!");

        // EXECUTA AS MIGRAÇÕES
        log.info("Executando migrações...");
        flyway.migrate();
        log.info("✓ Migrações aplicadas com sucesso!");

        log.warn("========================================");

        return new FlywayMigrationInitializer(flyway);
    }
}

