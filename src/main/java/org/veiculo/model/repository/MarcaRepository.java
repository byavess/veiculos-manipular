package org.veiculo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.veiculo.model.entity.Marca;

import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    boolean existsByNome(String nome);

    Optional<Marca> findByNome(String nome);
}

