package org.veiculo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.veiculo.model.entity.Marca;
import org.veiculo.model.entity.Modelo;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    boolean existsByModelo(String modelo);

    boolean existsByModeloAndMarca(String modelo, Marca marca);

    List<Modelo> findByMarcaId(Long marcaId);

    /**
     * Verifica se existe um modelo similar (case-insensitive e com trim) para uma marca específica
     * Usa LOWER e TRIM para comparação flexível
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Modelo m " +
           "WHERE LOWER(TRIM(m.modelo)) = LOWER(TRIM(:modelo)) " +
           "AND m.marca = :marca")
    boolean existsModeloSimilarByMarca(@Param("modelo") String modelo, @Param("marca") Marca marca);

    /**
     * Busca um modelo similar (case-insensitive e com trim) para uma marca específica
     * Útil para evitar duplicatas com pequenas variações de escrita
     */
    @Query("SELECT m FROM Modelo m " +
           "WHERE LOWER(TRIM(m.modelo)) = LOWER(TRIM(:modelo)) " +
           "AND m.marca = :marca")
    Optional<Modelo> findModeloSimilarByMarca(@Param("modelo") String modelo, @Param("marca") Marca marca);
}


