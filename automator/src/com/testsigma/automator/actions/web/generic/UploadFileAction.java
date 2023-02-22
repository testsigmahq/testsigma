package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA;

public class UploadFileAction extends ElementAction {
  @Override
  protected void execute() throws Exception {
    findElement();
    String downloadURL = getTestDataPropertiesEntity(TEST_STEP_DATA_MAP_KEY_TEST_DATA).getTestDataValuePreSignedURL();
    String fileName = String.format("%s_%s", System.currentTimeMillis(), FilenameUtils.getName(new URL(downloadURL).getPath()));
    String tempDir = FileUtils.getTempDirectoryPath();
    String filePath = String.format("%s%s%s", tempDir.endsWith(File.separator)?tempDir.substring(0,tempDir.length()-1):tempDir, File.separator, fileName);
    FileUtils.copyURLToFile(new URL(downloadURL), new File(filePath), (60* 1000), (60 * 1000));
    getElement().sendKeys(filePath);
    setSuccessMessage("Successfully executed.");
  }
}
