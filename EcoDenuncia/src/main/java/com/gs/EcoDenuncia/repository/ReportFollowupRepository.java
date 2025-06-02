package com.gs.EcoDenuncia.repository;

import com.gs.EcoDenuncia.model.ReportFollowup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportFollowupRepository extends JpaRepository<ReportFollowup, Long> {
    List<ReportFollowup> findByDenunciaId(Long denunciaId);
}
