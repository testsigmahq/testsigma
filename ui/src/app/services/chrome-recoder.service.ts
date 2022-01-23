import {ElementFormComponent} from "../components/webcomponents/element-form.component";
import {ElementsListComponent} from "../components/elements/list.component";
import {TestCaseStepsListComponent} from "../components/webcomponents/test-case-steps-list.component";
import {Page} from "../shared/models/page";
import {TestStep} from "../models/test-step.model";
import {TestCase} from "../models/test-case.model";
import {WorkspaceVersion} from "../models/workspace-version.model";
import {NaturalTextActions} from "../models/natural-text-actions.model";
import {TestStepService} from "./test-step.service";
import {TranslateService} from "@ngx-translate/core";
import {NaturalTextActionsService} from "./natural-text-actions.service";
import {Injectable} from "@angular/core";
import {TestDataType} from "../enums/test-data-type.enum";
import {WorkspaceType} from "../enums/workspace-type.enum";
import {Element} from "../models/element.model";
import {ElementMetaData} from "../models/element-meta-data.model";
import {ElementElementDetails} from "../models/element-locator-details.model";

@Injectable({
  providedIn: 'root'
})
export class ChromeRecorderService {
  public static MESSAGE_START_RECORDING = "ts_1";
  public static MESSAGE_STOP_RECORDING = "ts_2";
  public static MESSAGE_SEND_RECORDING = "ts_4";
  public static MESSAGE_SEND_RESPONSE = "ts_5";
  public static MESSAGE_GET_UI_IDENTIFIER = "ts_11";
  public static INSTALLED_PING_MESSAGE = "ts_14";
  public static INSTALLED_PONG_MESSAGE = "ts_15";
  public static MESSAGE_STOP_SPY = "ts_16";
  public static MESSAGE_ELEMENT_TYPE = "ts_17";
  public static MESSAGE_TYPE_STOPPED_RECORDING = "ts_22";
  public static MESSAGE_TYPE_GET_NLP_STEPS_LIST = "ts_31";
  public static MESSAGE_TYPE_DELETE_STEP_BY_ID = "ts_49";
  public static MESSAGE_SAVE_UI_IDENTIFIER_LIST = "ts_42";
  public static POST_INSTALLED_PONG_MESSAGE = "ts_50";
  public static TS_WEB = "testsigma_web";
  public isInstalled: Boolean = false;
  public uiIdentifierCallBack;
  public uiIdentifierCallBackContext: ElementFormComponent;
  public multiUIIdentifierCallBack;
  public multiUIIdentifierCallBackContext: ElementsListComponent;
  public stepListCallBack;
  public stepListCallBackContext: TestCaseStepsListComponent;
  public isChrome: Boolean = false;
  public isStepRecording: Boolean = false;
  public messageEvent: MessageEvent;
  public recorderStepList: Page<TestStep>;
  public recorderTestCase: TestCase;
  public recorderVersion: WorkspaceVersion;
  public recorderTemplates: Page<NaturalTextActions>;
  public navigateTemplate = [1044, 94, 10116, 10001];

  constructor(
    private nlpTemplateService: NaturalTextActionsService,
    private testStepService: TestStepService,
    public translate: TranslateService) {
    console.log('initializing ChromeRecorderService');
    this.registerListenerAllWindowMessage();
    this.pingRecorder();
  }

  public isChromeBrowser() {
    let isChrome = navigator.userAgent.toLowerCase().indexOf("chrome");
    let isEdge = navigator.userAgent.toLowerCase().indexOf("edge");
    this.isChrome = isChrome > -1 && isEdge == -1;
  }

  public registerListenerAllWindowMessage() {

    window.addEventListener('message', (event) => {
      if (event.source !== window)
        return;
      console.log('pingRecorder:', event)
      this.messageEvent = event;
      if (this.isInstalledEvent) {
        this.isInstalled = true;
      } else if (this.isStoppedRecorder) {
        this.isStepRecording = false;
        if(!event.data.isUiIdentifierRecorder)
          this.stepListCallBack.apply(this.stepListCallBackContext, [])
        if(this.multiUIIdentifierCallBack)
          this.multiUIIdentifierCallBack.apply(this.multiUIIdentifierCallBackContext, []);
      } else if (this.isAddUiIdentifier) {
        this.addUIIdentifierListener()
      }

    });
  }

  get isInstalledEvent(): boolean {
    return this.messageEvent?.data?.type === ChromeRecorderService.INSTALLED_PONG_MESSAGE ||
      this.messageEvent?.data?.type === ChromeRecorderService.POST_INSTALLED_PONG_MESSAGE
  }

  get isStoppedRecorder(): boolean {
    return this.messageEvent?.data?.type === ChromeRecorderService.MESSAGE_TYPE_STOPPED_RECORDING ||
      this.messageEvent?.data?.type === ChromeRecorderService.MESSAGE_STOP_RECORDING
  }

  get isAddUiIdentifier(): boolean {
    return this.messageEvent?.data?.type === ChromeRecorderService.MESSAGE_ELEMENT_TYPE;
  }

  get isAddMultipleUIIdentifiers(): boolean {
    return this.messageEvent?.data?.type === ChromeRecorderService.MESSAGE_SAVE_UI_IDENTIFIER_LIST;
  }

  get isGetNlpList(): boolean {
    return this.messageEvent?.data?.type === ChromeRecorderService.MESSAGE_TYPE_GET_NLP_STEPS_LIST;
  }

  get isRecordedDetails(): boolean {
    return this.messageEvent?.data?.type === ChromeRecorderService.MESSAGE_SEND_RECORDING;
  }

  get isStepDelete(): boolean {
    return this.messageEvent?.data?.type === ChromeRecorderService.MESSAGE_TYPE_DELETE_STEP_BY_ID;
  }

  public pingRecorder() {
    window.postMessage({
      from: ChromeRecorderService.TS_WEB,
      type: ChromeRecorderService.INSTALLED_PING_MESSAGE,
      content: "Get installed status"
    }, "*");
  };

  public addUIIdentifierListener() {
    let chromeRecorderElement = this.processingUiIdentifier(this.messageEvent.data.ele.data);
    if (this.uiIdentifierCallBack)
      this.uiIdentifierCallBack.apply(this.uiIdentifierCallBackContext, [chromeRecorderElement]);
  };

  public processingUiIdentifier(currentUiIdentifier) {
    let uiIdentifier = new Element().deserialize(currentUiIdentifier);
    uiIdentifier.metadata = new ElementMetaData().deserialize(currentUiIdentifier.metaData);
    currentUiIdentifier.elementDetails.data = currentUiIdentifier.elementDetails.attributes;
    uiIdentifier.elementDetails = new ElementElementDetails().deserialize(currentUiIdentifier.elementDetails);
    return uiIdentifier
  }

  public postGetUIIdentifierMessage(isMultipleElementCapture?: boolean) {
    window.postMessage({
      from: ChromeRecorderService.TS_WEB,
      type: ChromeRecorderService.MESSAGE_GET_UI_IDENTIFIER,
      content: "Get ui identifier",
      version_id: this.recorderVersion?.id,
      element_capture: isMultipleElementCapture,
      serverUrl: window.location.origin
    }, "*");
  };

  public sendResponseUIIdentifier(response) {
    window.postMessage({
      from: ChromeRecorderService.TS_WEB,
      type: ChromeRecorderService.MESSAGE_SEND_RESPONSE,
      response_data: response,
      content: "Send Response"
    }, "*");
  };

  public stopSpying(isStep?: boolean) {
    if(isStep){
      this.stepListCallBack.apply(this.stepListCallBackContext, [])
    }
    window.postMessage({
      from: ChromeRecorderService.TS_WEB,
      type: ChromeRecorderService.MESSAGE_STOP_SPY,
      content: "Stop Spying"
    }, "*");
  };

  public startRecording(excludeListMap?) {
    window.postMessage({
      from: ChromeRecorderService.TS_WEB,
      type: ChromeRecorderService.MESSAGE_START_RECORDING,
      content: "Start Recording",
      baseUrl: this.recorderTestCase.startUrl,
      test_case_id: this.recorderTestCase.id,
      version_id: this.recorderTestCase.workspaceVersionId | this.recorderVersion?.id,
      project_id: this.recorderVersion.workspace.id,
      applicationType: this.recorderVersion.workspace.workspaceType,
      exclude_map: excludeListMap,
      serverUrl: window.location.origin
    }, "*");
  };


  public getStepList() {
    this.fetchSteps();
  }

  public fetchNLPTemplates() {
    let workspaceType: WorkspaceType = this.recorderVersion.workspace.workspaceType;
    this.nlpTemplateService.findAll("workspaceType:" + workspaceType).subscribe(res => this.recorderTemplates = res);
  }

  public fetchSteps() {
    let query = "testCaseId:" + this.recorderTestCase.id;
    return this.testStepService.findAll(query, 'position').subscribe(res => {

      let cloudUrlExpression = /(?:^|\s)((http[s]?:\/\/)?(?:localhost|[\w-]+(?:\.[\w-]+)+)(:\d+)?(\/\S*)?)|(file:\/\/\/.+(?:\.[\w-]+))/;
      res.content.forEach((testStep) => {
        if (this.navigateTemplate.includes(<number>testStep?.naturalTextActionId) && testStep.testDataType == TestDataType.raw && cloudUrlExpression.test(<string>testStep.testDataValue))
          this.recorderTestCase.startUrl = testStep.testDataValue;
      });

      if (!res?.content?.length) {
        this.recorderTestCase.startUrl = undefined;
      }

      this.isStepRecording = true;
      this.startRecording()
    });
  }
}
