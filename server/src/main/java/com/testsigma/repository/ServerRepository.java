package com.testsigma.repository;

import com.testsigma.model.Server;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ServerRepository extends BaseRepository<Server, Long> {
}
