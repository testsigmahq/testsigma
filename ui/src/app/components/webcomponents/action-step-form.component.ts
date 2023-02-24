import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestStep} from "../../models/test-step.model";
import {FormControl, FormGroup} from '@angular/forms';
import {Page} from "../../shared/models/page";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {fromEvent} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {ActionElementSuggestionComponent} from "./action-element-suggestion.component";
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {TestStepService} from "../../services/test-step.service";
import {TestCase} from "../../models/test-case.model";
import {ActionTestDataFunctionSuggestionComponent} from "./action-test-data-function-suggestion.component";
import {TestDataType} from "../../enums/test-data-type.enum";
import {ActionTestDataParameterSuggestionComponent} from "./action-test-data-parameter-suggestion.component";
import {ActionTestDataEnvironmentSuggestionComponent} from "./action-test-data-environment-suggestion.component";
import {DefaultDataGeneratorService} from "../../services/default-data-generator.service";
import {TestStepType} from "../../enums/test-step-type.enum";
import {TestStepTestDataFunction} from "../../models/test-step-test-data-function.model";
import {DefaultDataGenerator} from "../../models/default-data-generator.model";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCaseService} from "../../services/test-case.service";
import {StepDetailsDataMap} from "../../models/step-details-data-map.model";
import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {AddonTestStepTestData} from "../../models/addon-test-step-test-data.model";
import {AddonElementData} from "../../models/addon-element-data.model";
import {Router} from "@angular/router";
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";
import {TestStepPriority} from "../../enums/test-step-priority.enum";
import {MobileRecorderEventService} from "../../services/mobile-recorder-event.service";
import {MobileStepRecorderComponent} from "../../agents/components/webcomponents/mobile-step-recorder.component";
import {ResultConstant} from "../../enums/result-constant.enum";
import {TestStepMoreActionFormComponent} from "./test-step-more-action-form.component";
import {AddonTestDataFunctionService} from "../../services/addon-default-data-generator.service";
import {AddonTestDataFunction} from "../../models/addon-test-data-function.model";
import {AddonTestDataFunctionParameter} from "../../models/addon-test-data-function-parameter.model";
import {StepActionType} from "../../enums/step-action-type.enum";
import {ActionTestDataRuntimeVariableSuggestionComponent} from './action-test-data-runtime-variable-suggestion.component';
import {extractStringByDelimiterByPos} from "../../utils/strings";
import {TestData} from "../../models/test-data.model";
import {TestDataService} from "../../services/test-data.service";
import {TestDataSetService} from "../../services/test-data-set.service";
import {TestDataMapValue} from "../../models/test-data-map-value.model";
import {ForLoopData} from "../../models/for-loop-data.model";


@Component({
  selector: 'app-action-step-form',
  templateUrl: './action-step-form.component.html',
  styles: [],
  host: {
    '(document:click)': 'onDocumentClick($event)',
  },
})
export class ActionStepFormComponent extends BaseComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  @Input('testStep') public testStep: TestStep;
  @Input('testSteps') testSteps: Page<TestStep>;
  @Input('testCase') testCase: TestCase;
  @Input('indentation') indentation: number;
  @Input('testStepsLength') testStepsLength: number;
  @Output('onCancel') onCancel = new EventEmitter<void>();
  @Output('onSave') onSave = new EventEmitter<TestStep>();
  @Input('stepForm') actionForm: FormGroup;
  @Input('templates') templates: Page<NaturalTextActions>;
  @Input('addonTemplates') addonTemplates?: Page<AddonNaturalTextAction>;
  @Input('selectedTemplate') currentTemplate: NaturalTextActions;
  @Input('testCaseResultId') testCaseResultId: number;
  @Input('isDryRun') isDryRun: boolean;
  @Output('onSuggestion') public onSuggestion = new EventEmitter<any>();
  @Optional() @Input('conditionTypeChange') conditionTypeChange: TestStepConditionType;

  @ViewChild('searchInput') searchInput: ElementRef;
  @ViewChild('replacer') replacer: ElementRef;
  @ViewChild('actionsDropDownContainer') actionsDropDownContainer: ElementRef;
  @ViewChild('displayNamesContainer') displayNamesContainer: ElementRef;
  @ViewChild('dataTypesContainer') dataTypesContainer: ElementRef;
  public currentFocusedIndex: number;
  public filteredTemplates: NaturalTextActions[];
  public filteredAddonTemplates: AddonNaturalTextAction[];
  public animatedPlaceholder: string;
  public formSubmitted: boolean;
  public isFetching: Boolean = false;
  public showTemplates: Boolean = false;
  public showDataTypes: Boolean = false;
  public showHelps: Boolean = false;
  public showActions: Boolean = false;
  public saving: boolean;
  public displayNames: any;
  public argumentList: any;
  public currentDataTypeIndex: number;
  public skipFocus: boolean;
  public selectedTestDataProfile: TestData;
  private elementSuggestion: MatDialogRef<ActionElementSuggestionComponent>;
  private StepMsg = ["Navigate to https://www.google.com/",
    "Enter admin in the userName field",
    "Enter 12345 in the password field",
    "Click on LoginButton",
    "Verify that the current page displays text Welcome"];
  private stepMsgMobile = ["Launch App",
    "Tap on Login button",
    "Enter userName@gmail.com in the Enter Email field",
    "Enter password123 in the Password field",
    "Tap on next login",
    "Verify that the current page displays text This email address is not registered on WordPress.com"];
  private placeholders: any[];
  private stepCreateArticles = {
    "WebApplication": "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/overview/",
    "MobileWeb": "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/overview/",
    "AndroidNative": "https://testsigma.com/docs/test-cases/create-steps-recorder/android-apps/",
    "IOSNative": "https://testsigma.com/docs/test-cases/create-steps-recorder/ios-apps/overview/",
    "Rest": "https://testsigma.com/tutorials/getting-started/automate-rest-apis/"
  }
  public stepArticleUrl = "";
  public navigateTemplate = [1044, 94, 10116, 10001]
  private testDataFunctionSuggestion: MatDialogRef<ActionTestDataFunctionSuggestionComponent>;
  private dataProfileSuggestion: MatDialogRef<ActionTestDataParameterSuggestionComponent>;
  private environmentSuggestion: MatDialogRef<ActionTestDataEnvironmentSuggestionComponent>;
  public localUrlVerifying: boolean;
  public localUrlValid: number = -1;
  public currentTestDataType: TestDataType;
  public currentTestDataFunction: DefaultDataGenerator;
  public isValidAttribute: Boolean;
  public isValidElement: Boolean;
  public isValidTestData: Boolean;
  public isCurrentDataTypeRaw: boolean = false;
  private currentStepDataMap: StepDetailsDataMap;
  private lastActionNavigateUrl: string;
  public currentAddonTemplate: AddonNaturalTextAction;
  public currentDataItemIndex: number;
  public currentAddonAllowedValues = [];
  @Input() stepRecorderView?: boolean;
  private eventEmitterAlreadySubscribed: Boolean = false;
  private oldStepData: TestStep;
  isAttachTestDataEvent: boolean = false;
  private runtimeSuggestion: MatDialogRef<ActionTestDataRuntimeVariableSuggestionComponent>;
  public selectedElementName : String;
  public listDataItem:string[] | TestData[];
  public listParameterItem:string[];
  public isFetchingListData: boolean = false;
  public isParameter: boolean = false;

  get mobileStepRecorder(): MobileStepRecorderComponent {
    return this.matModal.openDialogs.find(dialog => dialog.componentInstance instanceof MobileStepRecorderComponent).componentInstance;
  }

  get isTestDataRunTimeParameterType(){
    return this.currentTestDataType && this.currentTestDataType == TestDataType.runtime;
  }

  get isTestDataRandomParameterType(){
    return this.currentTestDataType && this.currentTestDataType == TestDataType.random;
  }

  public currentTestDataFunctionParameters: AddonTestDataFunctionParameter[];
  public currentAddonTDF: AddonTestDataFunction;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testStepService: TestStepService,
    private testDataFunctionService: DefaultDataGeneratorService,
    private testCaseService: TestCaseService,
    private addonTestDataFunctionService: AddonTestDataFunctionService,
    private matModal: MatDialog,
    private router: Router,
    private _eref: ElementRef,
    private mobileRecorderEventService: MobileRecorderEventService,
    private testDataService: TestDataService,
    private testDataSetService: TestDataSetService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get dataTypes() {
    return Object.keys(TestDataType);
  }

  get isEdit() {
    return this.testStep?.id;
  }

  ngOnInit(): void {
    this.fetchSteps();
    this.filter();
    this.filterAddonAction()
    this.placeholders = [...this.StepMsg];
    if (this.version.workspace.isMobile) {
      this.placeholders = [...this.stepMsgMobile]
    }
    if (this.testStep?.id) {
      this.oldStepData = new TestStep().deserialize(this.testStep);
      this.oldStepData.dataMap.testData = this.testStep.dataMap.testData;
      this.testStep.dataMap.conditionIf = Object.assign([], JSON.parse(JSON.stringify(this.testStep.dataMap.conditionIf)));
    }
    this.actionForm.addControl('action', new FormControl(this.testStep.action, []));
    this.testStep.type = TestStepType.ACTION_TEXT;
    this.isFetching = true;
    this.attachContentEditableDivKeyEvent();
    this.stepArticleUrl = this.stepCreateArticles[this.version.workspace.workspaceType];
    this.resetValidation();
    this.subscribeMobileRecorderEvents();
    this.mobileRecorderEventService.returnData.subscribe(res => {
      this.mobileRecorderEventService.setEmptyAction();
      if(res.type == 'element' && this.mobileRecorderEventService.currentlyTargetElement) {
        let name = typeof res.data == "string" ? res.data : res.data.name;
        this.assignElement(name, this.mobileRecorderEventService.currentlyTargetElement);
      }else if(res.type == TestDataType.global) {
        this.environmentAfterClose(res.data)
      } else if(res.type == TestDataType.parameter) {
        this.dataProfileAfterClose(res.data);
      } else if(res.type == TestDataType.function) {
        this.customFunctionAfterClose(res.data)
      } else if(res.type == TestDataType.runtime) {
        this.runtimeVariableAfterClose(res.data);
      }
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    this.clearSelection();
    if (changes['currentTemplate'] && !changes['currentTemplate']?.firstChange) {
      if (!this.testStep.isConditionalType)
      this.currentTemplate = changes['currentTemplate']['currentValue'];
    }
    this.setTemplate(this.currentTemplate);
    this.showTemplates = false;
    if(changes['conditionTypeChange']?.currentValue && !changes['conditionTypeChange']?.firstChange) {
      this.showActions = true;
      this.showTemplates = true;
      this.currentFocusedIndex = 0;
    }
  }

  getAddonTemplateAllowedValues(reference?) {
    this.currentAddonAllowedValues = undefined;
    this.currentAddonTemplate?.parameters.forEach(parameter => {
      if (parameter?.reference == reference) {
        this.currentAddonAllowedValues = parameter?.allowedValues?.length ? parameter?.allowedValues : undefined;
      }
    })
  }

  private resetValidation() {
    this.isValidAttribute = true;
    this.isValidTestData = true;
    this.isValidElement = true;
  }

  showACTIONDropdown() {
    if (this.skipFocus)
      return;
    this.showTemplates = true;
    this.currentFocusedIndex = 0;
  }

  showDataDropdown() {
    this.showDataTypes = true;
    this.currentDataTypeIndex = 0;
    this.showTemplates = false;
  }

  clearSelection(isClear?) {
    this.currentTemplate = undefined;
    if (this.replacer) {
      this.replacer.nativeElement.innerHTML = "";
      setTimeout(() => {
          if (!this.testStep?.id)
            this.replacer.nativeElement.focus()
        }
        ,
        100
      )
      ;
    }
    this.filteredTemplates = this.filter();
    this.filteredAddonTemplates = this.filterAddonAction();
    if (this.testStep?.id) {
      if (this.testStep.naturalTextActionId) {
        this.testStep.template = this.filteredTemplates.find(item => item.id == this.testStep.naturalTextActionId);
        delete this.testStep.addonTemplate;
      }
      if (this.testStep.addonActionId) {
        this.testStep.addonTemplate = this.filteredAddonTemplates.find(item => item.id === this.testStep.addonActionId);
        delete this.testStep.template;
      }
      //this.testStep.dataMap = new StepDetailsDataMap().deserialize(this.currentStepDataMap ? this.currentStepDataMap : this.testStep.dataMap);
    }
    this.showDataTypes = false;
    this.currentDataTypeIndex = 0;
    this.localUrlValid = -1;
    this.resetCFArguments();
    this.resetValidation();
    this.currentTestDataFunctionParameters = null;
    this.currentAddonTDF = null;
    this.mobileRecorderEventService.setEmptyAction();
    this.selectedElementName = undefined;
  }

  attachContentEditableDivKeyEvent() {
    if (this.replacer && this.replacer.nativeElement) {
      this.showHelps = true;
      if (this.testStep.id) {
        this.assignEditTemplate();
        this.showDataTypes = false;
      } else {
        if (this.testStep.isConditionalIf || this.testStep.isConditionalElseIf || this.testStep.isConditionalWhileLoop) {
          this.replacer.nativeElement.focus();
        }
      }
      fromEvent(this.replacer.nativeElement, 'click')
        .pipe(tap((event) => {
          this.showDataTypes = false;
          this.replacer.nativeElement.contentEditable = true;
          if (this.testDataPlaceholder().length) {
            this.testDataPlaceholder().forEach(item => {
              item?.removeAttribute("contentEditable")
            })
          }
          if (this.elementPlaceholder().length) {
            this.elementPlaceholder().forEach(item => {
              item?.removeAttribute("contentEditable")
            })
          }
          this.attributePlaceholder()?.removeAttribute("contentEditable");
          this.replacer.nativeElement.focus();
          this.resetValidation();
          console.log(event);
        })).subscribe();
      fromEvent(this.replacer.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(300),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            // Assuming the user is entering key in testData
            if (this.testDataPlaceholder().length) {
              let isEditTestData = false
              this.testDataPlaceholder().forEach(item => {
                if (item?.contentEditable != 'inherit' && item?.contentEditable)
                  isEditTestData = true;
              })
              if (isEditTestData)
                return;
            }
            console.log(event);
            if (["ArrowDown", "ArrowUp"].includes(event.key))
              return;
            if ("Backspace" == event.key &&  this.replacer.nativeElement.innerHTML.length === 0)
              delete this.currentTemplate;
            if ("Enter" == event.key) {
              if (this.showTemplates && this.filteredTemplate?.length > 0)
                this.selectTemplate();
              else if (this.currentTemplate?.id)
                this.validateTestData();
            }
            let htmlGrammar = this.replacer.nativeElement.innerHTML;
            htmlGrammar = htmlGrammar.replace(/<br>/g, "");
            if (htmlGrammar) {
              htmlGrammar = htmlGrammar.replace(/<span class="element+(.*?)>(.*?)<\/span>/g, "Element")
                .replace(/<span class="test_data+(.*?)>(.*?)<\/span>/g, "test data")
                .replace(/<span class="selected_list+(.*?)>(.*?)<\/span>/g, this.currentTemplate?.extractTestDataString)
                .replace(/<span class="attribute+(.*?)>(.*?)<\/span>/g, "attribute")
                .replace(/&nbsp;/g, "");
              this.filter(htmlGrammar);
              this.filterAddonAction(htmlGrammar)
            } else {
              this.filteredTemplates = this.filter();
              this.filteredAddonTemplates = this.filterAddonAction();
            }
          })
        )
        .subscribe();
      fromEvent(this.replacer.nativeElement, 'paste').pipe(tap((event) => {
        const text = (event['originalEvent'] || event).clipboardData.getData('text/plain');
        window.document.execCommand('insertText', false, text);
        event['stopPropagation']();
        event['preventDefault']();
        return false;
      })).subscribe()
      this.swapPlaceholder();
      this.preFillingSteps();
    } else
      setTimeout(() => {
        this.attachContentEditableDivKeyEvent();
      }, 100);
  }

  get actionTextLength() {
    return this.replacer?.nativeElement?.textContent?.length;
  }

  private preFillingSteps() {
    if (!this.testStep?.id && !this.testStepsLength) {
      let templateSearchText = "Navigate to";
      if (this.version.workspace.isMobileNative) {
        templateSearchText = "Launch App";
      }
      this.filter(templateSearchText);
      this.currentFocusedIndex = 0;
      this.selectTemplate();
      this.testDataPlaceholderText();
    }
  }

  private testDataPlaceholderText() {

    if (this.testDataPlaceholder()?.length) {
      this.replacer.nativeElement.contentEditable = false;
      let currentDataItemIndex = this.currentDataItemIndex || 0;
      this.testDataPlaceholder()[currentDataItemIndex].contentEditable = true;
      this.testDataPlaceholder()[currentDataItemIndex].innerHTML = "";
      this.testDataPlaceholder()[currentDataItemIndex].classList.add('placeholder-test-animate');
      this.testDataPlaceholder()[currentDataItemIndex].setAttribute("data-test-data-type", this.currentTestDataType);
      this.testDataPlaceholder()[currentDataItemIndex].style.minWidth = "100px";
      //this.testDataPlaceholder()?.focus();
      this.showDataTypes = false;
    } else {
      setTimeout(() => {
        this.testDataPlaceholderText()
      })
    }
  }

  filter(searchText?: string) {
    this.filteredTemplates = [];
    if (searchText && searchText.length) {
      this.templates.content.forEach(template => {
        if (template.searchableGrammar.toLowerCase().includes(searchText.toLowerCase()))
          this.filteredTemplates.push(template)
      })
      this.filteredTemplates.sort((a: NaturalTextActions, b: NaturalTextActions) => {
        return a.naturalText.toLowerCase().indexOf(searchText) - b.naturalText.toLowerCase().indexOf(searchText)
      })
    } else {
      this.filteredTemplates = this.templates.content;
      this.filteredTemplates.sort((a: NaturalTextActions, b: NaturalTextActions) => {
        return a.displayOrder - b.displayOrder;
      })
    }
    //this.filteredTemplates = this.filteredTemplates;
    this.currentFocusedIndex = 0;
    return this.filteredTemplates;
  }

  filterAddonAction(searchText?: string) {
    if (this.addonTemplates?.content?.length) {
      this.filteredAddonTemplates = [];
      if (searchText && searchText.length) {
        this.addonTemplates.content.forEach(template => {
          if (template.searchableGrammar.toLowerCase().includes(searchText.toLowerCase()))
            this.filteredAddonTemplates.push(template)
        })
      } else {
        this.filteredAddonTemplates = this.addonTemplates.content;
      }

      return this.filteredAddonTemplates;
    } else {
      return [];
    }
  }

  initNewTestStep(position: number, testCaseId): TestStep {
    let testStep = this.testStep;
    testStep.position = position;
    testStep.testCaseId = testCaseId;
    testStep.waitTime = 30;
    testStep.priority = TestStepPriority.MAJOR;
    testStep.type = TestStepType.ACTION_TEXT;
    if (this.version.workspace.isRest)
      testStep.type = TestStepType.REST_STEP;
    return testStep
  }

  addWhileConditionStep(step: TestStep) {
    this.testStep.conditionType = TestStepConditionType.LOOP_WHILE;
    this.testStep.priority = TestStepPriority.MINOR;
    this.testStep.parentId = step.id;
    this.testStep.parentStep = step;
    this.testStep.siblingStep = step;
    this.testStep.position = step.position + 1;
    this.testStep.stepDisplayNumber = step.stepDisplayNumber + ".1";
    step.siblingStep = this.testStep;
    step.isAfter = true;
    // return afterStep;
  }

  public fetchSteps() {
    let query = "testCaseId:" + this.testCase.id;
    this.testStepService.findAll(query, 'position').subscribe(res => {
      this.testSteps = res;
    })
  }


  private createWhileStep() {
    let whileStep = new TestStep();
    whileStep.conditionIf = [];
    whileStep.testCaseId = this.testCase.id;
    whileStep.position = this.testStep.position;
    if (this.testStep.parentId && !this.testStep?.getParentLoopId(this.testStep?.parentStep)) {
      whileStep.parentId = this.testStep.parentId;
    }
    whileStep.waitTime = 30;
    whileStep.priority = TestStepPriority.MINOR;
    whileStep.type = TestStepType.WHILE_LOOP;
    delete whileStep.conditionType;
    whileStep.visualEnabled = null;
    whileStep.ignoreStepResult = whileStep.ignoreStepResult === undefined ? true : whileStep.ignoreStepResult;
    this.testStepService.create(whileStep).subscribe(res => {
      this.testStep.parentId = res.id
      this.testStep.position = res.position + 1;
      this.testStep.parentStep = res;
      this.testStep.siblingStep = res;
      this.save(true);
    }, error => {
      this.translate.get('message.common.created.failure', {FieldName: 'Test Step'}).subscribe((res) => {
        this.showAPIError(error, res);
      })
    })
  }

  save(isSkip?: boolean) {
    if (this.testStep.isConditionalWhileLoop && !isSkip) {
      this.createWhileStep();
    } else {
      this.formSubmitted = true;
      if (this.validation()) {
        this.saving = true;
        this.testStepService.create(this.testStep).subscribe(step => this.afterSave(step), e => this.handleSaveFailure(e));
      }
    }
  }

  validateTestData() {
   if ((this.currentAddonTemplate || this.currentTemplate)&& this.navigateTemplate.includes(this.currentTemplate?.id))
    {
      this.navigateUrlValidation();
      setTimeout(()=> {
        this.save();
      },1000);
    }
   else this.save();
  }

  update(naturalTextActionId, addonActionId) {
    this.formSubmitted = true;
    if (this.validation()) {
      this.saving = true;
      if ((addonActionId && !this.testStep.naturalTextActionId) || (naturalTextActionId && this.testStep.addonActionId)) {
        this.testStep.naturalTextActionId = undefined;
      } else if ((naturalTextActionId && !this.testStep.addonActionId) || (addonActionId && this.testStep.naturalTextActionId)) {
        this.testStep.addonActionId = undefined;
      }
      this.testStepService.update(this.testStep).subscribe((step) => this.afterSave(step), e => this.handleSaveFailure(e, true))
    }
  }

  cancel() {
    if (this.testStep.id) {
      this.assignOldData();
    }
    this.clearSelection();
    this.isAttachTestDataEvent = false;
    this.showActions = false;
    this.showTemplates = false;
    this.replacer.nativeElement.blur();
    this.onCancel.emit();
  }

  assignOldData() {
    if (this.testStep.addonActionId) {
      this.testStep.addonTestData = this.oldStepData.addonTestData;
      this.testStep.addonElements = this.oldStepData.addonElements;
      this.testStep.addonTDF = this.oldStepData.addonTDF;
    } else if(this.testStep.naturalTextActionId) {
      this.testStep.dataMap.testData = this.oldStepData.dataMap.testData;
      this.testStep.attribute = this.oldStepData.attribute;
      this.testStep.element = this.oldStepData.element;
    }
  }

  addonValidation() {
    let dataMap = {};
    if (this.elementPlaceholder().length) {
      this.elementPlaceholder().forEach(item => {
        let reference = item.dataset?.reference;
        let value = item?.textContent?.replace(/&nbsp;/g, "").trim();
        if (value.length) {
          dataMap[reference] = value
        } else {
          this.isValidElement = false
        }
      })
    }
    if (this.testDataPlaceholder().length) {
      this.testDataPlaceholder().forEach(item => {
        let reference = item.dataset?.reference;
        let testDataType = TestDataType[item.dataset?.testDataType || 'raw'];
        let value = item?.textContent?.replace(/&nbsp;/g, "").trim();
        ['\@|', '\!|', '\~|', '\$|', '\*|', '\&|', '|'].some(type => {
          value = value.replace(type, '').replace(/&nbsp;/g, "");
        });
        if (value.length) {
          if (this.testStep?.addonTestData) {
            const testDataFunctionId = this.testStep?.addonTestData[reference]?.testDataFunctionId;
            const isAddonFn = this.testStep?.addonTestData[reference]?.isAddonFn;
            const args = this.testStep?.addonTestData[reference]?.testDataFunctionArguments;
            dataMap[reference] = {
              value: value,
              type: testDataType,
              testDataFunctionArguments: item.dataset?.testDataFunctionArguments ? JSON.parse(item.dataset?.testDataFunctionArguments) : args,
              testDataFunctionId: testDataType === TestDataType.function ? item.dataset?.testDataFunctionId ? item.dataset?.testDataFunctionId : testDataFunctionId : undefined,
              isAddonFn: item.dataset?.isAddonFn ? item.dataset?.isAddonFn : isAddonFn
            }
          } else {
            dataMap[reference] = {
              value: value,
              type: testDataType,
              testDataFunctionArguments: item.dataset?.testDataFunctionArguments ? JSON.parse(item.dataset?.testDataFunctionArguments) : undefined,
              testDataFunctionId: testDataType === TestDataType.function && item.dataset?.testDataFunctionId ? item.dataset?.testDataFunctionId : undefined,
              isAddonFn: item.dataset?.isAddonFn ? item.dataset?.isAddonFn : undefined
            }
          }
        } else {
          this.isValidTestData = false
        }
      })
    }
    let action = this.replacer.nativeElement.innerHTML;
    let testData = new Map<string, AddonTestStepTestData>();
    let elements = new Map<string, AddonElementData>();
    if (this.testStep?.addonTemplate?.parameters) {
      action = this.currentAddonTemplate.naturalText
      this.testStep.addonTemplate.parameters.forEach(parameter => {
        if (parameter.isTestData && dataMap[parameter.reference]) {
          //action = action.replace(parameter.reference, dataMap[parameter.reference]['value']);
          let data = new AddonTestStepTestData();
          data.value = dataMap[parameter.reference]['value'];
          data.type = dataMap[parameter.reference]['type'];
          data.testDataFunctionArguments = dataMap[parameter.reference]['testDataFunctionArguments'];
          data.isAddonFn = dataMap[parameter.reference]['isAddonFn'];
          data.testDataFunctionId = dataMap[parameter.reference]['testDataFunctionId'];
          testData[parameter.reference] = data;
        } else if (parameter.isElement) {
          //action = action.replace(parameter.reference, dataMap[parameter.reference]);
          let element = new AddonElementData();
          element.name = dataMap[parameter.reference];
          elements[parameter.reference] = element;
        }
      })
    }

    if (action && action.length && this.isValidElement && this.isValidTestData && this.isValidAttribute) {
      this.testStep.action = action;
      this.testStep.addonActionId = this.currentAddonTemplate.id;
      //this.testStep.addonNaturalTextActionData = new AddonNaturalTextActionDataModel();
      this.testStep.addonTestData = testData;
      this.testStep.addonElements = elements;
      this.testStep.type = TestStepType.ACTION_TEXT;
      this.testStep.addonTemplate = this.currentAddonTemplate;
      this.testStep.deserializeCommonProperties(this.actionForm.getRawValue());
      delete this.testStep.template;
      delete this.testStep.naturalTextActionId;
      delete this.testStep.testDataFunctionId;
      delete this.testStep.testDataFunctionArgs;
      delete this.testStep.dataMap.testData;
      delete this.testStep.element;
      delete this.testStep.dataMap.attribute;
      return true
    } else {
      return false;
    }
  }

  validation() {
    if (this.currentAddonTemplate) {
      return this.addonValidation();
    }
    let elementValue = this.elementPlaceholder()[0]?.textContent?.replace(/&nbsp;/g, "");
    let attributeValue = this.attributePlaceholder()?.textContent?.replace(/&nbsp;/g, "");
    let action = this.replacer.nativeElement.innerHTML.replace(/<span class="element+(.*?)>(.*?)<\/span>/g, elementValue)
      .replace(/<span class="attribute+(.*?)>(.*?)<\/span>/g, attributeValue)
      .replace(/&nbsp;/g, "");
    if (this.elementPlaceholder().length) {
      this.elementPlaceholder().forEach(item => {
        if (!item.textContent?.replace(/&nbsp;/g, "").trim()?.length) {
          this.isValidElement = false;
        }
      })
    }

    this.testDataValue(action, this.testStep, this.testDataPlaceholder());

    //this.isValidElement = this.elementPlaceholder() ? elementValue?.trim()?.length : true;
    this.isValidAttribute = this.attributePlaceholder() ? attributeValue?.trim().length : true;
    if (action && action.length && this.isValidElement && this.isValidTestData && this.isValidAttribute) {
      this.testStep.action = action;
      this.testStep.naturalTextActionId = this.currentTemplate?.id;
      this.testStep.type = TestStepType.ACTION_TEXT;
      this.testStep.template = this.currentTemplate;
      if (this.elementPlaceholder().length) {
        this.testStep.element = elementValue
      }
      if (attributeValue) {
        this.testStep.attribute = attributeValue
      }

      this.testStep.deserializeCommonProperties(this.actionForm.getRawValue());
      return true;
    } else {
      return false;
    }
  }

  testDataValue(action, testStep, testDataList): void {
    if(testStep?.getAllTestData?.length || testDataList?.length) {
      // for loop condition
        let t = this.rrr(testDataList);
        if(t && this.testStep.isForLoop) {
          this.fff(t)
        } else if(!this.testStep.isForLoop) {
          this.testStep.dataMap.testData = t;
        }
    }
  }

  rrr(testDataList): Map<string, any> {
    let testData = new Map<string, TestDataMapValue>();
    testDataList.forEach(item => {
      let reference = item?.['dataset']?.reference;
      let testDataType = TestDataType[item?.['dataset']?.testDataType || 'raw'];
      let value = item?.textContent?.replace(/&nbsp;/g, "").trim();
      let typeValue = false;
      ['\@|', '\!|', '\~|', '\$|', '\*|', '\%|', '\&|'].some(type => {
        if (value.startsWith(type)) {
          typeValue = true;
          value = value.replace(type, '').replace(/&nbsp;/g, "");
          value = value.replace('|', '').replace(/&nbsp;/g, "");
        }
      });
      if (value.length) {
        let setValue = new TestDataMapValue();
        setValue.type = typeValue? testDataType : TestDataType.raw;
        setValue['value'] = value;
        setValue['testDataFunction'] = new TestStepTestDataFunction();
        setValue['addonTDF'] = new AddonTestStepTestData();
        //Test-Data-profile added
        if(reference == 'test-data-profile') {
          testData['test-data-profile-id'] = this.getTestDataProfileIdValue();
        }
        testData[reference] = setValue
      } else {
        this.isValidTestData = false
      }
    })
    return testData;
  }

  fff(testDataValue): void {
      if (this.selectedTestDataProfile?.id || this.testStep?.forLoopData?.testDataProfileId) {
        let forLoopTestDataData = this.testStep?.forLoopData?.testDataProfileData;
        if(this.testStep.isTestDataLeftSetName || this.testStep?.isTestDataRightSetName) {
          if((testDataValue["left-data"]?.value == 'start-set-name' || testDataValue["left-data"]?.value == 'start')
            && testDataValue["left-data"]?.type == TestDataType.raw ) {
            testDataValue["left-data"].value = "-1";
          }
          if((testDataValue["right-data"]?.value == 'end-set-name' || testDataValue["right-data"]?.value == 'end')
            && testDataValue["right-data"]?.type == TestDataType.raw) {
            testDataValue["right-data"].value = "-1";
          }
        }
        if(this.testStep.isTestDataRightParameter || this.testStep.isTestDataLeftParameter) {
          if(testDataValue["left-data"]?.value == 'parameter' && testDataValue["left-data"]?.type == TestDataType.raw) {
            //testDataValue["left-data"].value = this.selectedTestDataSetName
          }
          if(testDataValue["right-data"]?.value == 'parameter' && testDataValue["right-data"]?.type == TestDataType.raw) {
            //testDataValue["right-data"].value = this.selectedTestDataSetName
          }
        }
        this.testStep.forLoopData = this.setForLoopData(testDataValue, this.testStep);
        this.testStep.forLoopData.testDataProfileData = forLoopTestDataData;

      } else {
        this.isValidTestData = true;
      }

  }

  setForLoopData(data, step: TestStep) {
    data['test-data-profile']['value'] = this.selectedTestDataProfile?.id || step.forLoopData?.testDataProfileId;
    return new ForLoopData().deserializeRawValue(data, step);

  }

  selectTemplate() {

    let template = this.filteredTemplate[this.currentFocusedIndex];
    if (template instanceof NaturalTextActions) {
      //this.selectedTemplate = undefined;
      if (this.testStep.id) {
        this.resetDataMap();
      }
      this.currentTemplate = template;
      this.testStep.template = template;
      setTimeout(() => {
        this.resetCFArguments();
        this.showTemplates = false;
        if (template instanceof NaturalTextActions)
          this.setTemplate(template);
      }, 100);
    } else {
      this.selectAddonTemplate(template)
    }
  }

  setTemplate(template: NaturalTextActions) {
    if (template) {
      delete this.currentAddonTemplate;
      this.resetDataMap();
      this.replacer.nativeElement.innerHTML = template.htmlGrammar;
      this.currentTemplate = template;
      this.attachActionTemplatePlaceholderEvents();
    }
  }

  setTestDataTemplate(template) {
    console.log(template)
    Object.keys(template.data.testData).forEach((item)=>{
      console.log(item, this.replacer.nativeElement.innerHTML, this.testDataPlaceholder(), this.testDataPlaceholder()[0].classList);
    })
  }

  selectAddonTemplate(template) {
    delete this.currentTemplate;
    //this.selectedTemplate = undefined;
    if (this.testStep.id) {
      //this.resetDataMap();
    }
    this.testStep.addonTemplate = template;
    setTimeout(() => {
      //this.resetCFArguments();
      this.showTemplates = false;
      this.setAddonTemplate(template);
    }, 100);
  }

  setAddonTemplate(template: AddonNaturalTextAction) {
    if (template) {
      //this.resetDataMap();
      this.currentAddonTemplate = template;
      this.replacer.nativeElement.innerHTML = template.htmlGrammar;
      this.attachActionTemplatePlaceholderEvents();
    }
  }

  setLastChildFlex() {
    let childNodes = this.replacer.nativeElement.childNodes
    if (!(childNodes[childNodes.length - 1]?.nodeType == Node.TEXT_NODE)) {
      let child = this.replacer.nativeElement.querySelectorAll('div.actiontext span');
      child[child.length - 1]?.classList.add('action-flex-auto')
    }
  }

  attachActionTemplatePlaceholderEvents() {
    setTimeout(() => {
      this.attachElementEvent();
      this.attachTestDataEvent();
      this.isAttachTestDataEvent = true;
      this.attachAttributeEvent();
      //this.appropriatePopup();
      this.setCursorAtTestData();
      this.setLastChildFlex();
    }, 300);
  }

  elementPlaceholder() {
    return this.replacer.nativeElement.querySelectorAll('div.actiontext span.element');
  }

  attachElementEvent() {
    if (this.elementPlaceholder()?.length) {
      this.elementPlaceholder().forEach(item => {
        item.addEventListener('click', (event) => {
          this.showTemplates = false;
          this.resetValidation();
          this.openElementsPopup(event?.target);
          event.stopPropagation();
          this.showTemplates = false;
        });
        item.addEventListener('paste', (event) => {
          event.preventDefault();
          event.stopPropagation();
          event.stopImmediatePropagation();
          const text = (event.originalEvent || event).clipboardData.getData('text/plain');
          window.document.execCommand('insertText', false, text);
        })
      })
    }
  }

  testDataPlaceholder() {
    let testDataList = this.replacer?.nativeElement?.querySelectorAll('div.actiontext span.test_data')?.length ? this.replacer?.nativeElement?.querySelectorAll('div.actiontext span.test_data') : [];
    let selectableList = this.replacer?.nativeElement?.querySelectorAll('div.actiontext span.selected_list')?.length ? this.replacer?.nativeElement?.querySelectorAll('div.actiontext span.selected_list') : [];
    return [...testDataList, ...selectableList];
  }

  getTestDataProfileIdValue() {
    const setValue = new TestDataMapValue();
    setValue.type = TestDataType.raw;
    setValue['value'] = this.selectedTestDataProfile?.id?.toString();
    return setValue;
  }
  mapTestDataType(type: String) {
    switch(type) {
      case "raw":
        return TestDataType.raw;
      case "parameter":
        return TestDataType.parameter;
      case "runtime":
        return TestDataType.runtime;
      case "global":
        return TestDataType.global;
      case "random":
        return TestDataType.random;
      case "function":
        return TestDataType.function;
    }
    return null;
  }

  attachTestDataEvent() {
    if (this.testDataPlaceholder()?.length) {

      this.setTestDataTemplate(this.currentTemplate)
      // this.testStep?.dataMap?.testData?.forEach(function(value,key) {
          this.currentTestDataType =  this.currentTestDataType || TestDataType.raw;
          console.log('attaching test data events');
          this.currentAddonAllowedValues = undefined
          this.testDataPlaceholder().forEach((item, index) => {
            item.addEventListener('click', (event) => {
              this.isCurrentDataTypeRaw = false;
              console.log('test data click event triggered');
              this.getAddonTemplateAllowedValues(item.dataset?.reference);
              this.getTestdataProfile(item.dataset?.reference);
              this.getParameter(item.dataset?.reference);
              this.currentDataTypeIndex = 0;
              item.contentEditable = true;
              this.currentDataItemIndex = index;
              this.replacer.nativeElement.contentEditable = false;
              this.resetValidation();
              if (this.removeHtmlTags(item?.textContent).trim().length)
              if (!this.removeHtmlTags(item?.textContent).trim().length)
                this.showDataDropdown();
              else
                this.showTemplates = false;
              event.stopPropagation();
              event.stopImmediatePropagation();
              return false;
            });
            item.addEventListener('keydown', (event) => {
              // if(this.isAutoCompleteValues(item?.dataset?.reference) && !["ArrowLeft", "ArrowRight", "Enter", "ArrowUp", "ArrowDown"].includes(event.key)) {
              //   if(event.key == "Backspace" && !this.showDataTypes) {
              //     this.showDataTypes = true;
              //     this.setSuggestionData(item);
              //   }
              //   return this.stopEvent(event);
              // }
              if (event.key == "Enter" && !this.showDataTypes) {
                return this.stopEvent(event);
              }
              this.getAddonTemplateAllowedValues(item.dataset?.reference);
              let value = item?.textContent;
              let testDataType = ['@|', '!|', '~|', '$|', '*|'].some(type => item?.textContent.includes(type))
              if (["Escape", "Tab"].includes(event.key))
                this.showDataTypes = false;
              if (!this.currentTemplate?.allowedValues || this.currentTemplate?.allowedValues.size! <= 0) {
                if (event.key == "ArrowUp" && this.currentDataTypeIndex != 0)
                  --this.currentDataTypeIndex;
                if (event.key == "ArrowDown" && this.currentDataTypeIndex < this.dataTypes.length - 1)
                  ++this.currentDataTypeIndex;
                if (event.key == "Enter") {
                  this.currentDataItemIndex = index;
                  this.selectTestDataType(TestDataType[this.dataTypes[this.currentDataTypeIndex]]);
                  setTimeout(() => {
                    item.innerHTML = this.removeHtmlTags(item?.textContent);
                  }, 100)
                }
              }
              if (value?.trim()?.length && testDataType &&
                (value?.trim()?.match(/\|/g) || []).length == 1 &&
                !(event.key == "Backspace" || event.key == "ArrowLeft" || event.key == "ArrowRight")) {
                this.selectDataType(value)
              }
              this.localUrlValid = -1;
              this.localUrlVerifying = false;
              this.urlPatternError = false;
              event.stopPropagation();
              event.stopImmediatePropagation();
              return false;
            });

            item.addEventListener('keyup', (event) => {
              console.log('test data keyup event triggered');
              // if(this.isAutoCompleteValues(item?.dataset?.reference) && !["ArrowLeft", "ArrowRight", "Enter", "ArrowUp", "ArrowDown"].includes(event.key)) {
              //   if(event.key == "Backspace" && !this.showDataTypes) {
              //     this.showDataTypes = true;
              //     this.setSuggestionData(item);
              //   }
              //   return this.stopEvent(event);
              // }
              if (event.key == "Enter" && !this.showDataTypes) {
                this.validateTestData();
                return this.stopEvent(event);
              }
              this.getAddonTemplateAllowedValues(item.dataset?.reference);
              this.urlPatternError = false;
              let testDataType = ['@|', '!|', '~|', '$|', '*|'].some(type => item?.textContent.includes(type))
              if (event.key == "Backspace") {
                this.selectDataType(item?.textContent, true)
              }
              if ((!testDataType && this.removeHtmlTags(item?.textContent).trim().length) || (!(["Escape", "Tab", "Backspace", "ArrowLeft", "ArrowRight", "Enter", "ArrowUp", "ArrowDown", "Shift", "Control", "Meta", "Alt"].includes(event.key)) && item?.textContent)) {
                this.showDataTypes = false;
              } else if (!this.removeHtmlTags(item?.textContent).trim().length) {
                this.showDataTypes = true;
              }
              if (event.key == "Backspace" && !this.removeHtmlTags(item?.textContent).trim().length) {
                this.showDataDropdown();
              }
              event.stopPropagation();
              event.stopImmediatePropagation();
              return false;
            })

            // this.testDataPlaceholder().addEventListener('mouseleave', (event) => {
            //   if(this.testDataPlaceholder()?.textContent.length) {
            //     let testDataType = ['@|', '!|', '~|', '$|', '*|', '%|'].some(type => this.testDataPlaceholder()?.textContent.includes(type))
            //     if (this.currentTemplate && this.navigateTemplate.includes(this.currentTemplate.id) && !testDataType) {
            //       fromEvent(this.replacer.nativeElement, 'mouseleave')
            //         .pipe(tap((event) => {
            //           if(this.testDataPlaceholder()?.innerHTML?.length && this.currentTestDataType == TestDataType.raw)
            //           this.testDataPlaceholder().blur()
            //         })).subscribe()
            //
            //       this.navigateUrlValidation();
            //     }
            //   }
            //   event.stopPropagation();
            //   event.stopImmediatePropagation();
            // })

            item.addEventListener('paste', (event) => {
              event.preventDefault();
              event.stopPropagation();
              event.stopImmediatePropagation();
              const text = (event.originalEvent || event).clipboardData.getData('text/plain');
              window.document.execCommand('insertText', false, text);
            })
            item.addEventListener('dblclick', (event) => {
              event.preventDefault();
              event.stopPropagation();
              event.stopImmediatePropagation();
              this.currentDataItemIndex = index;
              this.selectTestDataPlaceholder();
            })
          })

    }
  }
  public isAutoCompleteValues(reference?) {
    return ('test-data-profile' == reference && this.testStep.isTestdataProfile) ||
      ('left-data' == reference && this.testStep.isTestDataLeftSetName) ||
      ('right-data' == reference && this.testStep.isTestDataRightSetName) ||
      ('left-data' == reference && this.testStep.isTestDataLeftParameter) ||
      ('right-data' == reference && this.testStep.isTestDataRightParameter) ||
      this.currentTemplate?.allowedValues?.[reference]?.length
  }

  public stopEvent(event) {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation()
    return false;
  }
  selectDataType(value, isSkipSelect?: boolean) {
    let dataType = TestDataType.raw;
    if (value?.trim()?.match(/@\|(.?)\||@\|(.+?)\|/g)) {
      dataType = TestDataType.parameter;
    } else if (value?.trim()?.match(/!\|(.?)\||!\|(.+?)\|/g)) {
      dataType = TestDataType.function
    } else if (value?.trim()?.match(/~\|(.?)\||~\|(.+?)\|/g)) {
      dataType = TestDataType.random;
    } else if (value?.trim()?.match(/\$\|(.?)\||\$\|(.+?)\|/g)) {
      dataType = TestDataType.runtime;
    }
    this.selectTestDataType(dataType, false, isSkipSelect)
  }

  removeHtmlTags(value) {
    return value.replace(/<br>/g, "").replace(/<div>/g, "").replace(/<\/div>/g, "")
  }

  scrollUpTemplateFocus() {
    if (this.currentFocusedIndex)
      --this.currentFocusedIndex;
    let target = <HTMLElement>document.querySelector(".h-active");
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  scrollDownTemplateFocus() {
    if (this.currentFocusedIndex >= this.filteredTemplates.length - 1)
      return;
    ++this.currentFocusedIndex;
    let target = <HTMLElement>document.querySelector(".h-active");
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }


  attributePlaceholder() {
    return this.replacer.nativeElement.querySelector('div.actiontext span.attribute');
  }

  attachAttributeEvent() {
    if (this.attributePlaceholder()) {
      this.attributePlaceholder().addEventListener('click', (event) => {
        this.attributePlaceholder().contentEditable = true;
        this.replacer.nativeElement.contentEditable = false;
        this.showDataTypes = false;
        event.stopPropagation();
        event.stopImmediatePropagation();
        return false;
      });
      this.attributePlaceholder().addEventListener('keydown', (event) => {
        if (event.key == "Enter") {
          setTimeout(() => {
            this.attributePlaceholder().innerHTML = this.removeHtmlTags(this.attributePlaceholder().textContent);
          }, 100)

        }
        event.stopPropagation();
        event.stopImmediatePropagation();
        return false;
      });

      this.attributePlaceholder().addEventListener('paste', (event) => {
        event.preventDefault();
        event.stopPropagation();
        event.stopImmediatePropagation();
        const text = (event.originalEvent || event).clipboardData.getData('text/plain');
        window.document.execCommand('insertText', false, text);
      })
    }
  }

  inputType(displayName) {
    let type = 'text';
    if (displayName == 'int') {
      type = 'number'
    }
    return type;
  }

  selectTestDataType(type, isFromHtml?: boolean, isSkipSelect?: boolean) {
    this.resetCFArguments();
    this.resetTestData();
    this.currentTestDataFunction = undefined;
    this.currentTestDataType = type;
    this.showDataTypes = false;
    this.isCurrentDataTypeRaw = false;
    switch (type) {
      case TestDataType.parameter:
        this.assignDataValue("@| |")
        if (!this.testCase.isStepGroup || this.testStep.getParentLoopDataId(this.testStep, this.testCase))
          this.openSuggestionDataProfile()
        else if (this.testCase.isStepGroup)
          this.assignDataValue("@| |")
        break;
      case TestDataType.function:
        this.assignDataValue("!| |")
        this.openSuggestionTestDataFunction()
        break;
      case TestDataType.global:
        this.assignDataValue("*| |")
        this.openSuggestionEnvironment()
        break;
      case TestDataType.raw:
        if (isFromHtml)
          this.isCurrentDataTypeRaw = true
        this.assignDataValue("")
        if (!isSkipSelect)
          this.selectTestDataPlaceholder();
        break;
      case TestDataType.runtime: {
        this.assignDataValue("$| <span class='test_data_place'></span> |");
        this.openSuggestionRuntimeVariable();
        break;
      }
      case TestDataType.random:
        this.assignDataValue("~|<span class='test_data_place'></span>|");
        break;
    }
  }

  selectAllowedValues(allowedValue, isFromHtml?: boolean, isSkipSelect?: boolean) {
    this.resetCFArguments();
    this.resetTestData();
    this.currentTestDataType = TestDataType.raw;
    this.assignDataValue(this.getDataTypeString(TestDataType.raw, allowedValue));
  }

  setCursorAtTestData() {
    this.skipFocus = true;
    let element = this.testDataPlaceholder()[this.currentDataItemIndex || 0];
    if (!element) {
      this.setCursorAtAttribute();
      this.skipFocus = false;
      return;
    }
    this.selectNodeAndFocus(element);
    this.showDataTypes = false;
    setTimeout(() => this.showDataTypes = true, 300);
  }

  setCursorAtAttribute() {
    this.skipFocus = true;
    let element = this.attributePlaceholder();
    if (!element) {
      this.skipFocus = false;
      return;
    }
    this.selectNodeAndFocus(element);
  }

  selectNodeAndFocus(node) {
    try {
      let range = document.createRange();
      range.selectNodeContents(node);
      let sel = window.getSelection();
      sel.removeAllRanges();
      range.setStart(node, 0);
      range.setEnd(node, 1);
      node.focus()
      range.collapse(false);
      sel.addRange(range);
      this.skipFocus = false;
      range.selectNodeContents(node);
      node.click();
      node.focus();
    } catch (e) {
      if (node.className.indexOf("test_data") > -1) {
        node.focus();
      }
      this.skipFocus = false;
    }
  }

  setTempPosition() {
    let element = this.replacer.nativeElement.querySelector('div.actiontext span span.test_data_place');
    if(!element) {
      return false;
    }
    if (element) {
      this.replacer.nativeElement.contentEditable = false;
      element.contentEditable = true;
      this.setMeddlePosition(element);
    }
  }

  setMeddlePosition(element) {
    let range = document.createRange();
    range.selectNodeContents(element);
    let sel = window.getSelection();
    sel.removeAllRanges();
    range.setEnd(element, 0);
    element.focus()
    range.collapse(true);
    sel.addRange(range);
  }

  selectTestDataPlaceholder() {
    let range = document.createRange();
    range.selectNodeContents(this.testDataPlaceholder()[this.currentDataItemIndex]);
    let sel = window.getSelection();
    sel.removeAllRanges();
    sel.addRange(range);
    this.testDataPlaceholder()[this.currentDataItemIndex].contentEditable = true;
    this.replacer.nativeElement.contentEditable = false;
    this.testDataPlaceholder()[this.currentDataItemIndex].focus();
  }

  private assignEditTemplate() {
    let firstDataFunction = 0;
    let argumentsList;
    if(this.testStep?.addonTDF?.value) {
      Object.keys(this.testStep?.addonTDF?.value)?.forEach((testdataName, index) => {
        if(index == 0 && this.testStep.addonTDF?.value?.[testdataName]?.type == TestDataType.function){
          firstDataFunction = this.testStep.addonTDF?.value?.[testdataName]?.testDataFunctionId;
          argumentsList = this.testStep.addonTDF?.value?.[testdataName]?.testDataFunctionArguments;
        }
      })
    }
    if(!!this.testStep?.dataMap?.testData?.size) {
      this.testDataPlaceholder().forEach((item,index) => {
        this.testStep.dataMap.testData.forEach((testData: TestDataMapValue) => {
          if (testData?.type == TestDataType.function || firstDataFunction) {
            if (testData['testDataFunction'].id)
              this.showTestDataCF(testData['testDataFunction'].id);
            else if (testData['kibbutzTDF']['testDataFunctionId'] || firstDataFunction) {
              if(testData['kibbutzTDF']['isKibbutzFn']){
                this.showAddonTDF(firstDataFunction ? firstDataFunction : testData['kibbutzTDF']['testDataFunctionId'], argumentsList);
              }
              else{
                this.showTestDataCF(firstDataFunction ? firstDataFunction : testData['kibbutzTDF']['testDataFunctionId']);
              }
              this.editSerialize();
            }
          }
        })

      })

    } else {
      this.editSerialize();
    }
  }

  showAddonTDF(id: number, argumentsList) {
    this.addonTestDataFunctionService.show(id).subscribe(res => {
      this.currentAddonTDF = res;
      this.assignTDFData(res, argumentsList);
    })
  }

  private editSerialize() {
    if (this.testStep.addonTemplate) {
      this.replacer.nativeElement.innerHTML = this.testStep?.parsedAddonStep;
      this.setAddonTemplate(this.testStep.addonTemplate);
      if (this.testStep.addonElements) {
        const elementPlaceHolders = this.elementPlaceholder();
        let index = 0;
        for (const key in this.testStep.addonElements) {
          this.assignElement(this.testStep.addonElements[key].name, elementPlaceHolders[index++]);
        }
      }
      if (this.testStep.addonTestData) {
        const testDataPlaceHolders = this.testDataPlaceholder();
        let index = 0;
        testDataPlaceHolders.forEach(item => {
          let reference = item.dataset?.reference;
          const dataName = this.getDataTypeString(this.testStep.addonTestData[reference].type, this.testStep.addonTestData[reference].value);
          if (dataName) {
            let item = testDataPlaceHolders[index++];
            if (item?.contentEditable) {
              item.innerHTML = dataName;
            }
            item.setAttribute("data-test-data-type", this.testStep.addonTestData[reference].type);
          }
        })
      }
    } else {
      if (this.testStep?.template?.htmlGrammar) {
        this.replacer.nativeElement.innerHTML = this.testStep?.template?.htmlGrammar;
        this.showTemplates = false;
        this.currentTemplate = this.testStep.template;
        if (this.testStep?.element)
          this.assignElement(this.testStep?.element, this.elementPlaceholder()[0]);
        this.testStep?.dataMap?.testData.forEach((dataMapValue: TestDataMapValue, key: string) => {
          if (!dataMapValue.value && !dataMapValue.type)
            this.assignDataValue(this.getDataTypeString(dataMapValue.type, dataMapValue.value));
        });
        if (this.attributePlaceholder())
          this.setStepTestData();
        if (this.testStep.isForLoop && this.attributePlaceholder()?.length) {
          this.setStepLoopData();
        }
        this.attachActionTemplatePlaceholderEvents();
      }
      else
        this.replacer.nativeElement.innerHTML = this.testStep?.action;
    }
  }

  private setStepLoopData() {
    this.attributePlaceholder().forEach((item, index) => {
      let reference = item.dataset?.reference;
      let testData = this.testStep.forLoopData.setValuesParsed(reference);
      item.setAttribute("data-test-data-type",testData?.['type']);
      if(testData?.['type'] == TestDataType.function) {
        let functionData = testData?.['function'];
        item = this.testDataFunctionDataAssign(item,functionData)
      }
      item.innerHTML = this.getDataTypeString(testData?.['type'], testData?.['value']);
    })
  }

  private setStepTestData(){
    this.testDataPlaceholder().forEach((item, index) => {
      let reference = item.dataset?.reference;
      let testData = this.testStep.testData?.[reference];
      item.setAttribute("data-test-data-type",testData.type);
      if(testData.type == TestDataType.function) {
        item = this.testDataFunctionDataAssign(item,testData)
      }
      item.innerHTML = this.getDataTypeString(testData?.type, testData?.['value']);
    })
  }

  private testDataFunctionDataAssign(item, functionData){
    if(functionData.addonTDF) {
      if(functionData.addonTDF.testDataFunctionArguments)
        item.setAttribute('data-test-data-function-arguments', JSON.stringify(functionData.addonTDF.testDataFunctionArguments));
      if(functionData.addonTDF.testDataFunctionId)
        item.setAttribute('data-test-data-function-id', functionData.addonTDF.testDataFunctionId);
      item.setAttribute('data-function-data', JSON.stringify(functionData.addonTDF));
      item.setAttribute('data-is-kibbutz-fn', true);
    } else if(functionData.testDataFunction){
      item.setAttribute('data-function-data', JSON.stringify(functionData.testDataFunction));
      if(functionData.testDataFunction.args)
        item.setAttribute('data-test-data-function-arguments', JSON.stringify(functionData.testDataFunction.args));
      if(functionData.testDataFunction.id)
        item.setAttribute('data-test-data-function-id', functionData.testDataFunction.id);
    }
    return item;
  }

  private showTestDataCF(id) {
    this.testDataFunctionService.show(id).subscribe(res => {
      this.currentTestDataFunction = res
      this.editSerialize();
      this.assignCFData(res?.arguments, this.testStep?.testDataFunctionArgs)
    })
  }

  swapPlaceholder() {
    this.animatedPlaceholder = this.animatedPlaceholder ? this.animatedPlaceholder : this.placeholders[0];
    setTimeout(() => {
      this.replacer.nativeElement.classList.add('placeholder-animate');
    }, 1500)
    setTimeout(() => {
      let placeholderChanged = false;
      this.placeholders.forEach((placeholder, index) => {
        if (this.animatedPlaceholder == placeholder && !placeholderChanged) {
          this.replacer.nativeElement.classList.remove('placeholder-animate');
          setTimeout(() => {
            this.animatedPlaceholder = this.placeholders[index + 1];
            placeholderChanged = true;
          }, 100)
        }
      })
      this.swapPlaceholder();
    }, 3000)
  }

  // private appropriatePopup() {
  //   if (this.elementPlaceholder()) {
  //     this.elementPlaceholder().click();
  //   } else if (this.testDataPlaceholder()) {
  //     this.testDataPlaceholder().click();
  //     this.selectTestDataPlaceholder();
  //   } else if(this.attributePlaceholder()) {
  //     this.attributePlaceholder().click();
  //   }
  // }

  private getCurrentStepElement(targetElement?){
    let name:String;
    if(this.selectedElementName){
      name = this.selectedElementName;
    }else {
      name = this.testStep?.element;
      if(!name && this.testStep?.addonElements && targetElement?.dataset?.reference){
        name = this.testStep.addonElements[targetElement.dataset.reference]?.name;
      }
    };
    return name !="element" ? name : undefined;
  }

  private getPreviousStepElement() {
    for (let i = this.testStep.position - 1; i >= 0; i--) {
      let elementName = this.testSteps.content?.[i]?.element;
      if (elementName && elementName != "element") {
        return elementName;
      }
    }
  }

  private openElementsPopup(targetElement?) {
    let isNewUI=this.version.workspace.isWeb || this.version.workspace.isMobileWeb
    let sendDetails = {
      versionId:this.version?.id,
      version: this.version,
      testCase: this.testCase,
      testCaseResultId: this.testCaseResultId,
      isDryRun: this.isDryRun,
      isStepRecordView: this.stepRecorderView,
      isNewUI:isNewUI,
      previousStepElementName:  this.getPreviousStepElement(),
      currentStepElementName: this.getCurrentStepElement(targetElement),
      doRefresh: true,
    };
    if(targetElement)
      this.mobileRecorderEventService.currentlyTargetElement = targetElement;
    if (this.stepRecorderView) {
      this.mobileRecorderEventService.suggestionContent.next(Object.assign(sendDetails, {
        content: 'suggestionElement'
      }));
      return
    }
    this.elementSuggestion = this.matModal.open(ActionElementSuggestionComponent, {
      height: "100vh",
      width: '540px',
      position: {top: '0', right: '0'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: sendDetails
    });
    let afterClose = (element) => {
      if (element) {
        let name = typeof element == "string" ? element : element.name;
        this.assignElement(name, targetElement);
        this.selectedElementName = name;
      }
    }
    this.elementSuggestion.afterClosed().subscribe(element => afterClose(element));
  }

  private assignElement(elementName, targetElement?) {
    this.showTemplates = false;
    if (elementName) {
      if (elementName && elementName.length) {
        targetElement.innerHTML = elementName;
        this.selectedElementName = elementName;
        this.testStep.dataMap.elementString = elementName;
      }
      if (this.testDataPlaceholder()?.length && !this.isEdit) {
        this.testDataPlaceholder()?.[this.currentDataItemIndex || 0]?.click();
        this.selectTestDataPlaceholder();
      }
    }
  }



  private assignDataValue(dataName, isReplacer?:boolean) {
    this.showDataTypes = false;
    const testDataPlaceHolders = this.testDataPlaceholder();
    for (let i = 0; i < testDataPlaceHolders.length; i++) {
      const content = testDataPlaceHolders[i].innerHTML;
      if ((testDataPlaceHolders[i]?.contentEditable||Boolean(isReplacer)) && ((testDataPlaceHolders[i].getAttribute("data-test-data-type") == null) ||
        ((testDataPlaceHolders[i].getAttribute("data-test-data-type") != null && (Boolean(isReplacer) ||content == '@| |' || content == '!| |' || content == '*| |'
          || content == "$| <span class='test_data_place'></span> |" || content == "~| <span class='test_data_place'></span> |" || content == "" || !this.removeHtmlTags(testDataPlaceHolders[i].textContent).trim().length))))) {
        testDataPlaceHolders[i].innerHTML = dataName;
        testDataPlaceHolders[i].setAttribute("data-test-data-type", this.currentTestDataType);
        break;
      }
    }
    if (dataName.startsWith('~|') || dataName.startsWith('$|')) {
      this.setTempPosition()
    }
  }

  private resetDataMap() {
    this.resetTestData();
    delete this.currentTestDataType;
    this.resetTestDataValues();
    delete this.testStep['attribute'];
    delete this.testStep['element'];
    delete this.testStep['addonElements'];
  }

  private resetTestData() {
    delete this.testStep['testDataVal'];
    delete this.testStep['testDataType'];
    delete this.testStep['testDataFunctionId'];
    delete this.testStep['testDataFunctionArgs'];
    delete this.testStep['addonTestData'];
    delete this.testStep['addonTDF'];
  }

  private resetCFArguments() {
    this.argumentList = undefined;
    this.displayNames = undefined;
  }

  private openSuggestionTestDataFunction() {
    let suggestionData = {
      versionId: this.version?.id,
    };
    if(this.sendSuggestionDetails(suggestionData, this.mobileRecorderEventService.suggestionCustomFunction)) {
      return;
    }
    this.testDataFunctionSuggestion = this.matModal.open(ActionTestDataFunctionSuggestionComponent, {
      height: "100vh",
      width: '30%',
      position: {top: '0', right: '0'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {version: this.version?.id}
    });
    this.testDataFunctionSuggestion.afterClosed().subscribe(data => this.customFunctionAfterClose(data));
  }


  addTDFControls(argumentList, isEdit?) {
    const arr = Object.keys(this.actionForm.controls);
    arr.forEach((con) => {
      if (!(con == 'action')) {
        this.actionForm.removeControl(con);
      }
    })
    if (argumentList.length) {
      argumentList.forEach(argument => {
        this.actionForm.addControl(argument.reference, new FormControl(
          this.testStep?.addonTestData ? this.testStep?.addonTestData[argument.reference] :
            this.testStep?.addonTestData ? this.testStep?.testDataFunctionArgs[argument.reference] : undefined));
      })
      this.setAddonDataSet(this.testDataPlaceholder?.[this.currentDataItemIndex | 0], this.currentAddonTDF.id, this.actionForm.getRawValue());
    } else {
      this.setAddonTestDataValues(this.currentAddonTDF.id)
    }
  }

  isEmptyObject(obj) {
    return obj && Object.keys(obj).length == 0;
  }

  private assignTDFData(data, isEdit?) {
    this.currentAddonTDF = data;
    this.currentTestDataFunctionParameters = data.parameters;
    this.assignDataValue(this.getDataTypeString(TestDataType.function, data.displayName))
    this.addTDFControls(data.parameters, isEdit);
  }


  addCFControls(argumentList, values?) {
    const arr = Object.keys(this.actionForm.controls);
    arr.forEach((con) => {
      if (!(con == 'action')) {
        this.actionForm.removeControl(con);
      }
    })
    let argumentKeys = Object.keys(argumentList);
    argumentKeys.forEach(argument => {
      this.actionForm.addControl(argument, new FormControl(values ? values[argument] : undefined));
    })
  }

  private assignCFData(dataArguments, values?) {
    this.addCFControls(dataArguments['display_names'], values);
    this.argumentList = dataArguments['arg_types'];
    this.displayNames = dataArguments['display_names'];
    if (!this.isEmptyObject(this.argumentList))
      this.setFocus();
    else
      this.setTestDataFunctionToDom();
  }

  setFocus() {
    if (this.displayNamesContainer?.nativeElement) {
      this.displayNamesContainer.nativeElement?.querySelector('div input.autofocus')?.focus();
    } else {
      setTimeout(() => {
        this.setFocus()
      }, 100)
    }
  }

  sendSuggestionDetails(sendDetails, contentName) {
    if (this.stepRecorderView) {
      this.mobileRecorderEventService.suggestionContent.next(Object.assign(sendDetails, {
        content: contentName
      }));
      return true;
    } else {
      return false
    }
  }

  private openSuggestionDataProfile() {
    let suggestionData = {
      dataProfileId: this.testStep.getParentLoopDataId(this.testStep, this.testCase),
      dataProfileIds : this.testStep.getAllParentLoopTDPIds(this.testStep,this.testCase,this.testSteps),
      versionId: this.version?.id,
      testCaseId: this.testCase?.id,
      stepRecorderView: Boolean(this.stepRecorderView),
    };
    console.log(suggestionData);
    if(this.sendSuggestionDetails(suggestionData, this.mobileRecorderEventService.suggestionDataProfile)) {
      return;
    }
    this.dataProfileSuggestion = this.matModal.open(ActionTestDataParameterSuggestionComponent, {
      height: "100vh",
      width: '35%',
      position: {top: '0', right: '0'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: suggestionData
    });
    this.dataProfileSuggestion.afterClosed().subscribe(data => this.dataProfileAfterClose(data));
  }

  private openSuggestionEnvironment() {
    let suggestionData = {
      versionId: this.version?.id,
      stepRecorderView: Boolean(this.stepRecorderView),
    };
    if(this.sendSuggestionDetails(suggestionData, this.mobileRecorderEventService.suggestionEnvironment)) {
      return;
    }
    this.environmentSuggestion = this.matModal.open(ActionTestDataEnvironmentSuggestionComponent, {
      height: "100vh",
      width: '30%',
      position: {top: '0', right: '0'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        versionId: this.version?.id,
        stepRecorderView: Boolean(this.stepRecorderView),
      }
    });
    this.environmentSuggestion.afterClosed().subscribe(data => this.environmentAfterClose(data));
  }

  private showTestDataPopup() {
    this.showDataTypes = true;
    this.testDataPlaceholder()[this.currentDataItemIndex].click();
    this.testDataPlaceholder()[this.currentDataItemIndex].focus();
  }

  private getDataTypeString = (type, name) => {
    let testDataString = name;
    switch (type) {
      case TestDataType.random:
        testDataString = "~|<span class='test_data_place'>" + testDataString + "</span>|";
        break;
      case TestDataType.runtime:
        testDataString = "$|<span class='test_data_place'>" + testDataString + "</span>|";
        break;
      case TestDataType.function:
        testDataString = "!|" + testDataString + "|";
        break;
      case TestDataType.parameter:
        testDataString = "@|" + testDataString + "|";
        break;
      case TestDataType.global:
        testDataString = "*|" + testDataString + "|";
        break;
    }
    return testDataString;
  };
  public urlPatternError: boolean = false;

  public navigateUrlValidation() {
    this.currentDataItemIndex = this.currentDataItemIndex || 0;
    if (
      this.lastActionNavigateUrl != this.testDataPlaceholder()[this.currentDataItemIndex]?.textContent &&
      this.currentTestDataType == TestDataType.raw &&
      this.testDataPlaceholder()[this.currentDataItemIndex]?.innerHTML?.length &&
      this.navigateTemplate.includes(this.currentTemplate?.id)) {
      this.lastActionNavigateUrl = this.testDataPlaceholder()[this.currentDataItemIndex]?.textContent;
      let cloudUrlExpression = /(?:^|\s)((https?:\/\/)?(?:localhost|[\w-]+(?:\.[\w-]+)+)(:\d+)?(\/\S*)?)/;
      let localUrlExpression = /^(http[s]?:\/\/)[a-zA-Z0-9.\-:\/]?/;

      if (cloudUrlExpression.test(this.testDataPlaceholder()[this.currentDataItemIndex]?.textContent)) {
        this.localUrlValid = -1;
        this.localUrlVerifying = true;
        this.testCaseService.validateNavigationUrls(this.testCase.id, this.testDataPlaceholder()[this.currentDataItemIndex]?.textContent).subscribe(res => {
          this.localUrlValid = res.length;
          this.localUrlVerifying = false;
        }, error => {
          this.translate.get('action.step.message.localUrl.fetching_failed', {URL: this.testDataPlaceholder()[this.currentDataItemIndex]?.textContent}).subscribe((res) => {
            this.showAPIError(error, res);
            this.localUrlValid = -1;
            this.localUrlVerifying = false;
          })
        })
      } else if (!localUrlExpression.test(this.testDataPlaceholder()[this.currentDataItemIndex]?.textContent)) {
        this.urlPatternError = true
      }
    }
  }

  public setAddonTestDataValues(customFunctionId) {
    this.testDataPlaceholder().forEach((item, index) => {
      if (this.currentDataItemIndex != index)
        return
      let formValue = this.actionForm.getRawValue();
      if (item.getAttribute("data-test-data-type") || item.getAttribute('data-test-data-function-id')) {
        item.setAttribute('data-test-data-function-id', customFunctionId)
        let args: JSON = formValue;
        let argsArray = new Map<String, String>();
        delete formValue.action
        for (let argsKey in args) {
          argsArray[argsKey] = args[argsKey];
        }
        item.setAttribute('data-test-data-function-arguments', JSON.stringify(argsArray));
        item.setAttribute('data-is-addon-fn', true);
      }
    });
    this.currentTestDataFunctionParameters = null;
    this.currentAddonTDF = null;
  }

  setAddonDataSet(item, customFunctionId, formValue) {
    item.setAttribute('data-test-data-function-id', customFunctionId)
    let args: JSON = formValue;
    let argsArray = new Map<String, String>();
    delete formValue.action
    for (let argsKey in args) {
      argsArray[argsKey] = args[argsKey];
    }
    item.setAttribute('data-test-data-function-arguments', JSON.stringify(argsArray));
    item.setAttribute('data-is-kibbutz-fn', true);
  }

  public setTestDataValues(customFunctionId) {
    this.testDataPlaceholder().forEach((item, index) => {
      if (this.currentDataItemIndex != index)
        return
      let formValue = this.actionForm.getRawValue();
      if (item.getAttribute("data-test-data-type") || item.getAttribute('data-test-data-function-id')) {
        item.setAttribute('data-test-data-function-id', customFunctionId)
        let args: JSON = this.getArguments(formValue);
        let argsArray = new Map<String, String>();
        delete formValue.action
        for (let argsKey in args) {
          argsArray[argsKey] = args[argsKey];
        }
        item.setAttribute('data-test-data-function-arguments', JSON.stringify(argsArray));
        item.setAttribute('data-is-addon-fn', false);
      }
    });
    this.currentTestDataFunctionParameters = null;
    this.currentAddonTDF = null;
    this.resetCFArguments();
  }

  public resetTestDataValues() {
    this.testDataPlaceholder().forEach((item) => {
      item.removeAttribute('data-is-addon-fn');
      item.removeAttribute('data-test-data-function-arguments');
      item.removeAttribute('data-test-data-type');
      item.removeAttribute('data-test-data-function-id');
    })
  }

  public setTestDataFunctionToDom() {
    this.setTestDataValues(this.currentTestDataFunction.id);
    this.resetCFArguments();
  }

  /*private setDataMapValues() {
    this.testStep.testDataType = this.currentTestDataType;
    if (this.currentTestDataType && this.currentTestDataType == TestDataType.function && this.currentTestDataFunction) {
      let formValue = this.actionForm.getRawValue();
      delete formValue.action
      let testDataFunction = new TestStepTestDataFunction();
      testDataFunction.id = this.currentTestDataFunction.id;
      testDataFunction.function = this.currentTestDataFunction.functionName;
      testDataFunction.class = this.currentTestDataFunction.className;
      testDataFunction.argsTypes = this.argumentList || {};
      testDataFunction.args = this.getArguments(formValue);
      testDataFunction.type = this.currentTestDataFunction.arguments['fun_type'];
      testDataFunction.package = this.currentTestDataFunction.classPackage;
      this.testStep.testDataFunctionId = this.currentTestDataFunction.id;
      this.testStep.testDataFunctionArgs = this.getArguments(formValue);
    } else if (this.currentTestDataType && this.currentTestDataType == TestDataType.function && this.currentAddonTDF) {
      let formValue = this.actionForm.getRawValue();
      delete formValue.action
      let testData = new AddonTestStepTestData();
      testData.testDataFunctionArguments = formValue;
      testData.testDataFunctionId = this.currentAddonTDF.id;
      testData.value = this.currentAddonTDF.displayName;
      testData.type = this.currentTestDataType;
      this.testStep.addonTDF = testData;
    }
  }*/


  getArguments(formValue): JSON {
    let returnObj = {};
    Object.keys(formValue).forEach(item => {
      if (item.includes('arg'))
        returnObj[item] = formValue[item]
    })
    return <JSON>returnObj;
  }

  get inValidParameter() {
    return !!!this.isValidTestData ? 'Test Data' : !!!this.isValidElement ? 'Element' : !!!this.isValidAttribute ? 'Attribute' : false;
  }

  get filteredTemplate() {
    this.filteredTemplates = this.filteredTemplates.filter(template => template.displayName !== 'breakLoop' && template.displayName !== 'continueLoop');
    let returnData = [...this.filteredTemplates, ...this.filteredAddonTemplates];
    if (this.testStep.conditionType === TestStepConditionType.CONDITION_IF ||
      this.testStep.conditionType === TestStepConditionType.CONDITION_ELSE_IF) {
      returnData = returnData.filter(template => template.stepActionType === StepActionType.IF_CONDITION);
    } else if (this.testStep.conditionType === TestStepConditionType.LOOP_WHILE) {
      returnData = returnData.filter(template => template.stepActionType === StepActionType.WHILE_LOOP);
    } else if(this.testStep.conditionType === TestStepConditionType.LOOP_FOR) {
      returnData = returnData.filter(template => template.stepActionType === StepActionType.FOR_LOOP);
    }
    else {
      returnData = returnData.filter(template => !(template.stepActionType === StepActionType.WHILE_LOOP ||
        template.stepActionType === StepActionType.IF_CONDITION));
    }
    return returnData;
  }

  onDocumentClick(event) {
    if (this._eref.nativeElement.contains(event.target))
      return;
    this.showTemplates = false;
    this.currentFocusedIndex = 0;
    this.showDataTypes = false;
    if ((!this.currentTestDataType || this.currentTestDataType == TestDataType.raw) && !this.isCurrentDataTypeRaw && this.testDataPlaceholder()?.length && !this.testDataPlaceholder()[this.currentDataItemIndex]?.innerHTML?.length) {
      this.testDataPlaceholder()[this.currentDataItemIndex || 0].innerHTML = "test data";
    }
  }

  subscribeMobileRecorderEvents() {
    if (this.stepRecorderView) {
      this.mobileRecorderEventService.getStepRecorderEmitter().subscribe(res => {
        this.populateAndSaveFromRecorder(res)
      });
    }
  }

  private populateAndSaveFromRecorder(testStep: TestStep) {
    if (this.eventEmitterAlreadySubscribed ||
      (this.mobileStepRecorder.stepList.editedStep && this.mobileStepRecorder.stepList.editedStep.id != this.testStep.parentId)) return;
    let currentStep: TestStep = new TestStep();
    Object.assign(currentStep, this.testStep);
    if (this.testStep.position == 0) {
      testStep.testCaseId = currentStep.testCaseId;
      testStep.position = testStep.position || currentStep.position;
      testStep.testCaseId = this.testCase.id;
      testStep.dataMap.conditionIf = this.actionForm.getRawValue()?.conditionIf ? this.actionForm.getRawValue()?.conditionIf : [ResultConstant.SUCCESS];
      this.saveFromRecorder(testStep);
    } else {
      testStep.dataMap.conditionIf = this.actionForm.getRawValue()?.conditionIf ? this.actionForm.getRawValue()?.conditionIf : [ResultConstant.SUCCESS];
      this.testStep = testStep;
      this.testStep.position = testStep.position || currentStep.position;
      this.testStep.testCaseId = this.testCase.id;
      this.saveFromRecorder(this.testStep)
    }
    this.eventEmitterAlreadySubscribed = true;
  }

  private saveFromRecorder(testStep: TestStep) {
    this.formSubmitted = true;
    this.saving = true; // TODO - step in the list , should be the assigned this value // this.testStep = Object.assign(this.testStep, step);//new TestStep().deserialize(step.serialize());
    this.testStepService.create(testStep).subscribe(step => this.afterSave(step), e => this.handleSaveFailure(e));
  }

  private afterSave(step: TestStep) {
    if (step.addonActionId) {
      step.addonTemplate = this.testStep.addonTemplate
    } else {
      step.template = this.testStep.template;
    }
    step.parentStep = this.testStep.parentStep;
    step.siblingStep = this.testStep.siblingStep;
    step.stepDisplayNumber = this.testStep.stepDisplayNumber;
    if (Boolean(this.stepRecorderView)) {
      this.matModal.openDialogs?.find(dialog => dialog.componentInstance instanceof TestStepMoreActionFormComponent)?.close();
    }
    if(step.isForLoop){
      step.forLoopData = this.testStep.forLoopData;
      step.forLoopData.testDataProfileData = this.selectedTestDataProfile;
    }
    this.actionForm.reset();
    this.onSave.emit(step);
    this.saving = false;
    this.addActionStepAfterSwitch();

  }

  addActionStepAfterSwitch() {
    setTimeout(() => {
      if (Boolean(this.stepRecorderView) && this.mobileStepRecorder.addActionStepAfterSwitch) {
        this.mobileStepRecorder.createStep(this.mobileStepRecorder.actionStep, true);
        this.mobileStepRecorder.addActionStepAfterSwitch = false;
      }
    }, 1000)
  }

  private handleSaveFailure(err, isUpdate?) {
    let msgKey = Boolean(isUpdate) ? 'message.common.update.failure' : 'message.common.created.failure';
    this.showAPIError(err, this.translate.instant(msgKey, {FieldName: 'Test Step'}));
    this.saving = false;
  }

  ngOnDestroy() {
    this.eventEmitterAlreadySubscribed = true;
  }


  private environmentAfterClose(data) {
    data = data || ' ';
    this.assignDataValue(this.getDataTypeString(TestDataType.global, data));
    if (data == ' ') {
      this.showTestDataPopup()
    }
  }

  private dataProfileAfterClose(data) {
    if (data) {
      this.showDataTypes = false;
      if( typeof(data)==="string" ){
        this.assignDataValue(this.getDataTypeString(TestDataType.parameter, data));
      }
      else if( typeof(data)==="object" ){
        this.testStep.testDataProfileStepId = data?.testDataProfileStepId;
        this.assignDataValue(this.getDataTypeString(TestDataType.parameter, data?.suggestion));
      }
    }
    else {
      this.currentTestDataType = TestDataType.raw;
      if(!this.testStep?.stepGroup) this.showTestDataPopup();
    }
  }

  private customFunctionAfterClose(data) {
    if (data) {
      if (data instanceof AddonTestDataFunction) {
        this.assignTDFData(data);
      } else {
        this.currentTestDataFunction = data;
        this.assignCFData(data.arguments);
        this.showDataTypes = false;
        this.assignDataValue(this.getDataTypeString(TestDataType.function, data.classDisplayName + " :: " + data.displayName))
      }
    } else if (!data) {
      delete this.currentTestDataType
      this.showTestDataPopup()
    }
  }

  private openSuggestionRuntimeVariable() {
    let suggestionData = {
      projectId: this.version?.workspaceId,
      version: this.version,
      stepRecorderView: Boolean(this.stepRecorderView),
      testCaseSteps: this.testSteps?.content || [],
      testCase: this.testCase
    };

    if(this.sendSuggestionDetails(suggestionData, this.mobileRecorderEventService.suggestionRuntimeVariable)) {
      return;
    }

    this.runtimeSuggestion = this.matModal.open(ActionTestDataRuntimeVariableSuggestionComponent, {
      height: "100vh",
      width: '50%',
      position: {top: '0', right: '0'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: suggestionData
    });

    this.runtimeSuggestion.afterClosed().subscribe(data => this.runtimeVariableAfterClose(data));
  }

  runtimeVariableAfterClose(data) {
    if (data) {
      this.assignDataValue(this.getDataTypeString(TestDataType.runtime, data), true);//, this.testDataPlaceholder(), false);
    } else {
      this.showTestDataPopup();
    }
  }
  public extractStringByKey(str:string){
    return extractStringByDelimiterByPos({str:str})
  }

  public getParameter(reference) {
    this.listParameterItem = undefined;
    this.isParameter = false;
    if (('left-data' == reference && this.testStep.isTestDataLeftParameter) || ('right-data' == reference && this.testStep.isTestDataRightParameter)) {
        this.isParameter = true;
        this.fetchTestDataSet();
        this.focusOnSearch();
    }
  }
  public getTestdataProfile(reference?) {
    this.listDataItem = undefined;
    this.isParameter = false;
    if ('test-data-profile' == reference && this.testStep.isTestdataProfile) {
      this.fetchTestDataProfile();
      this.focusOnSearch();
    }
  }
  public selectTestDataProfile(testDataProfileId?){
    this.testDataSetService.findAll("testDataProfileId:" + testDataProfileId, 'position').subscribe(value => {
      this.listDataItem = Object.keys(value?.content[0]?.data);
      this.isFetchingListData = false;
    }, error => {
      this.isFetchingListData = false;
    })
  }

  get isDefaultType() {
    return true;
    //return !this.listDataItem && (!this.currentAllowedValues?.length || (this.isSpotEditEnable &&
    //  !this.currentAllowedValues?.length)) && !this.currentKibbutzAllowedValues?.length;
  }

  get isAllowedValues() {
    return true;
    //return !this.listDataItem && !this.currentKibbutzAllowedValues?.length && (this.currentAllowedValues?.length || this.testStep?.kibbutzTemplate?.isConditionalIF || this.testStep?.kibbutzTemplate?.isConditionalWhileLoop)
  }

  get isKibbutzAllowedValues(){
    return false;
    //return !this.listDataItem && this.currentKibbutzTemplate && this.currentKibbutzAllowedValues;
  }

  // selectTestDataProfile(testdata) {
  //   this.resetTestData();
  //   this.resetCFArguments();
  //   this.currentTestDataType = TestDataType.raw;
  //   this.assignDataValue(testdata.name || testdata, this.testDataPlaceholder());
  //   this.showDataTypes = false;
  //   if(testdata instanceof TestData) {
  //     this.selectedTestDataProfile = testdata;
  //     if(this.testStep.isTestdataParameter)
  //       this.fetchTestDataSet();
  //   }
  // }

  fetchTestDataProfile(term?) {
    let searchName = '';
    if (term) {
      searchName = ",testDataName:*" + term + "*";
    }
    this.isFetchingListData = true;
    this.testDataService.findAll("versionId:" + this.version.id + searchName).subscribe(res => {
      this.listDataItem = res.content;
      if (this.testCase?.id && this.testCase?.testDataId) {
        if (this.listDataItem && !this.listDataItem?.find(req => req?.['id'] == this.testCase.testDataId)) {
          this.listDataItem.push(this.testCase.testData)
        }
      }
      this.isFetchingListData = false;
    }, error => {
      this.isFetchingListData = false;
    });
  }

  fetchTestDataSet(term?) {
    this.isFetchingListData = true;
    if(this.listDataItem && term?.length) {
      let returnData = [];
      this.listDataItem.forEach(data => {
        if(data.toLowerCase().includes(term)){
          returnData.push(data);
        }
      });
      this.listDataItem = returnData;
      this.isFetchingListData = false;
      return
    }
    this.testDataSetService.findAll("testDataProfileId:" + this.testCase?.testData?.id , 'position').subscribe(res => {
      this.listParameterItem = Object.keys(res?.content[0]?.data);
      this.isFetchingListData = false;
    }, error => {
      this.isFetchingListData = false;
    })
  }

  focusOnSearch() {
    this.attachDebounceEvent();
  }

  attachDebounceEvent() {
    if (this.searchInput && this.searchInput.nativeElement)
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            if (this.searchInput?.nativeElement?.value) {
              if(this.isParameter){
                this.fetchTestDataSet(this.searchInput.nativeElement.value)
              } else {
                this.fetchTestDataProfile(this.searchInput.nativeElement.value)
              }
            } else {
              if(this.isParameter){
                this.fetchTestDataSet()
              } else {
                this.fetchTestDataProfile()
              }
            }
          })
        )
        .subscribe();
    else
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
  }
}
