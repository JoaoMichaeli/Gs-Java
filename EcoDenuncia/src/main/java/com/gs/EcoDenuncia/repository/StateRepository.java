package com.gs.EcoDenuncia.repository;

import com.gs.EcoDenuncia.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long>, JpaSpecificationExecutor<State> {
}
