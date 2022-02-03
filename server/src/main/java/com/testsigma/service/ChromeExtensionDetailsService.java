package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.ChromeExtensionDetails;
import com.testsigma.repository.ChromeExtensionDetailsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class ChromeExtensionDetailsService {

  private final ChromeExtensionDetailsRepository repository;

  public ChromeExtensionDetails findOne(Long id) throws ResourceNotFoundException, SQLException {
    Optional<ChromeExtensionDetails> chromeExtensionDetail = repository.findById(id);
    return chromeExtensionDetail.orElseThrow(() -> new ResourceNotFoundException("Missing:" + id));
  }

  public ChromeExtensionDetails save(@NonNull ChromeExtensionDetails chromeExtensionDetails) throws SQLException, ResourceNotFoundException {
    chromeExtensionDetails = repository.save(chromeExtensionDetails);
    return chromeExtensionDetails;
  }
}

