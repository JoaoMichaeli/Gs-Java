package com.gs.EcoDenuncia.repository;

import com.gs.EcoDenuncia.model.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Long>, JpaSpecificationExecutor<Neighborhood> {
}
