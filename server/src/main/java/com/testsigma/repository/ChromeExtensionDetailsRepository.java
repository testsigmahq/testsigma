package com.testsigma.repository;

import com.testsigma.model.ChromeExtensionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ChromeExtensionDetailsRepository extends JpaRepository<ChromeExtensionDetails, Long> {

}
