package com.testsigma.repository;

import com.testsigma.model.DefaultDataGeneratorFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DefaultDataGeneratorFilesRepository extends JpaRepository<DefaultDataGeneratorFile, Long> {
}

