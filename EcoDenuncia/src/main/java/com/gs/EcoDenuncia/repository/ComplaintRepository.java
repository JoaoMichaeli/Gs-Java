package com.gs.EcoDenuncia.repository;

import com.gs.EcoDenuncia.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUsuarioId(Long usuarioId);
}
