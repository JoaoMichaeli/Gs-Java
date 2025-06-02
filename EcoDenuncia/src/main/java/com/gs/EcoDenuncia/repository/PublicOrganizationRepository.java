package com.gs.EcoDenuncia.repository;

import com.gs.EcoDenuncia.model.PublicOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicOrganizationRepository extends JpaRepository<PublicOrganization, Long>, JpaSpecificationExecutor<PublicOrganization> {
}
