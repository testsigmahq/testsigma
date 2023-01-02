package com.testsigma.controller.recorder;

import com.testsigma.dto.TestCaseDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestCaseMapper;
import com.testsigma.mapper.recorder.TestDataMapper;
import com.testsigma.model.*;
import com.testsigma.service.*;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.TestCaseSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(value = "/os_recorder/test_cases")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestCasesRecorderController {

    private final TestCaseService testCaseService;
    private final TestStepService testStepService;
    private final NaturalTextActionsService templateService;
    private final TestCaseMapper testCaseMapper;
    private final TagService tagService;
    private final AttachmentService attachmentService;
    private final TestDataMapper testDataMapper;

    @RequestMapping(method = RequestMethod.GET)
    public Page<com.testsigma.model.recorder.TestCaseDTO> index(TestCaseSpecificationsBuilder builder,
                                                                @PageableDefault(value = 25, page = 0) Pageable pageable) {
        log.debug("GET /os_recorder/test_cases");

        Long workspaceVersionId = null;
        Boolean isStepGroup = null;
        TestCaseStatus status = null;
        for (SearchCriteria param : builder.params) {
            if (param.getKey().equals("applicationVersionId") || param.getKey().equals("workspaceVersionId")) {
                workspaceVersionId = Long.parseLong(param.getValue().toString());
            }
            else if (param.getKey().equals("isTestComponent")) {
                isStepGroup = Boolean.parseBoolean(param.getValue().toString());
            }
            else if (param.getKey().equals("status")) {
                String statusInString = param.getValue().toString();
                switch (statusInString) {
                    case "READY":
                        status = TestCaseStatus.READY;
                        break;
                    case "DRAFT":
                        status = TestCaseStatus.DRAFT;
                        break;
                    case "IN_REVIEW":
                        status = TestCaseStatus.IN_REVIEW;
                        break;
                    case "OBSOLETE":
                        status = TestCaseStatus.OBSOLETE;
                        break;
                    case "REWORK":
                        status = TestCaseStatus.REWORK;
                        break;
                }
            }
        }

        Page<TestCase> testCases = testCaseService.findAllByWorkspaceVersionIdAndIsStepGroupAndStatus(workspaceVersionId, isStepGroup, status, pageable);
        List<TestCaseDTO> testCaseDTOS = testCaseMapper.mapDTOs(testCases.getContent());
        List<com.testsigma.model.recorder.TestCaseDTO> results = testDataMapper.mapTestCaseDTOs(testCaseDTOS);
        return new PageImpl<>(results, pageable, testCases.getTotalElements());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public com.testsigma.model.recorder.TestCaseDTO show(@PathVariable("id") Long id) throws TestsigmaException {
        TestCase testCase = testCaseService.find(id);
        TestCaseDTO testCaseDTO = testCaseMapper.mapTo(testCase);
        testCaseDTO.setTags(tagService.list(TagType.TEST_CASE, id));
        testCaseDTO.setFiles(attachmentService.findAllByEntityIdAndEntity(id,
                TestCase.class.getName(), PageRequest.of(0, 10)));
        return testDataMapper.mapTestCaseDTO(testCaseDTO);
    }

    @GetMapping(value = "/validateUrls/{id}")
    public @ResponseBody
    ArrayList<String> findAllEmptyElementsByTestCaseId(@PathVariable(value = "id") Long id,
                                                       @RequestParam(value = "currentUrl", required = false) String currentUrl) throws Exception {
        List<TestStep> testSteps = testStepService.findAllByTestCaseIdAndNaturalTextActionIds(
                id,
                templateService.findByDisplayName("navigateTo")
                        .stream().map(NaturalTextActions::getId).map(Long::intValue).collect(Collectors.toList())
        );
        ArrayList<String> invalidUrlList = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();
        if (!StringUtils.isEmpty(currentUrl)) {
            if (invalidUrl(currentUrl)) invalidUrlList.add(currentUrl);
            return invalidUrlList;
        }
        for (TestStep testStep : testSteps) {
            if (testStep.getRecorderDataMap() != null && testStep.getRecorderDataMap().getTestData() != null) {
                for(String key : testStep.getRecorderDataMap().getTestData().keySet()) {
                    if(!testStep.getRecorderDataMap().getTestData().get(key).getType().equals("raw"))
                        continue;
                    String url = testStep.getRecorderDataMap().getTestData().get(key).getValue();
                    urls.add(url);
                    if ((url.indexOf("http://localhost") > -1)
                            || (url.indexOf("https://localhost") > -1)
                            || invalidUrl(url)) {
                        invalidUrlList.add(url);
                    }
                }
            }
        }
        return invalidUrlList;
    }

    private boolean invalidUrl(String url) {
        HttpURLConnection huc = null;
        try {
            huc = (HttpURLConnection) new URL(url).openConnection();
            huc.setRequestMethod("HEAD");
            huc.getResponseCode();
            return false;
        } catch (Exception ignore) {
            try {
                if (huc != null) {
                    huc.disconnect();
                }
            } catch (Exception ignored) {
            }
            return true;
        }
    }
}
