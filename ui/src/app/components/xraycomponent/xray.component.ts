import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {IntegrationsService} from "../../shared/services/integrations.service";
import {EntityExternalMappingService} from "../../services/entity-external-mapping.service";
import {Integrations} from "../../shared/models/integrations.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {EntityType} from "../../enums/entity-type.enum";
import {TestCaseResult} from "../../models/test-case-result.model";
import {Integration} from "../../shared/enums/integration.enum";

@Component({
  selector: 'app-xray-input',
  templateUrl: './xray.component.html',
  styles: []
})

export class XrayComponent implements OnInit{

  @Input('entityId') entityId: number;
  @Input('entityType') entityType: EntityType;
  @Input('entityExternalMapping') entityExternalMapping: EntityExternalMapping;
  @Input('display') display : boolean = false;
  @Input('testCaseResult') testCaseResult : TestCaseResult;
  @Output('createXrayLink') createXrayLink: EventEmitter<EntityExternalMapping> = new EventEmitter<EntityExternalMapping>();
  @Output('rePushInitialized') rePushInitialized : EventEmitter<void> = new EventEmitter<void>();
  @Output('rePushFailed') rePushFailed : EventEmitter<void> = new EventEmitter<void>();

  public xrayExternalApplication: Integrations;
  public xrayFormGroup: FormGroup;
  public entityName: string;
  public urlLink: string = "";
  public testCaseResultLink: string = "";
  public isSubmitted : Boolean = false;
  public isEdit : Boolean = false;

  constructor(
    public externalApplicationConfigService: IntegrationsService,
    public entityExternalMappingService : EntityExternalMappingService) {
  }

  ngOnInit() {
    this.process();
  }

  ngOnChanges(){
    this.process();
  }

  process(){
    if(this.entityExternalMapping == null || this.display || this.entityExternalMapping?.entityId != this.entityId) {
      this.entityExternalMapping = null;
      this.fetchXrayApplicationConfig();
    }
    else{
      this.urlLink = this.xrayExternalApplication.url.toString() + "/browse/" + this.entityExternalMapping?.externalId;
    }
    this.closeForm();
    this.toggle();
  }

  toggle(){
    switch (this.entityType){
      case EntityType.TEST_CASE:
        this.entityName = "Test";
        break;
      case EntityType.TEST_SUITE:
        this.entityName = "Test Set";
        break;
      case EntityType.TEST_PLAN:
        this.entityName = "Test Plan";
        break;
      default:
        this.entityName = "Test"
    }
  }

  fetchXrayApplicationConfig(){
    this.externalApplicationConfigService.findAll().subscribe(
      res => {
        if(res.length > 0){
          this.xrayExternalApplication = res.find(app => app.isXray);
          this.fetchExternalMappings()
        }
      }
    );
  }

  fetchExternalMappings(){
    this.entityExternalMappingService.findAll("integration:"+Integration.XrayCloud+",entityId:"+this.entityId).subscribe(res =>{
      if(res?.content?.length > 0){
        this.entityExternalMapping = res.content[0];
        if(this.runResult)
          this.prepareDataForTestResult();
        else if(this.entityExternalMapping.externalId)
          this.urlLink = this.xrayExternalApplication.url.toString() + "/browse/" + this.entityExternalMapping?.externalId;
        else
          this.urlLink = null;
      }else{
        this.urlLink = null;
        this.testCaseResultLink = null;
        this.entityExternalMapping = new EntityExternalMapping();
        this.addXrayFormControls();
      }
    })
  }

  editXrayId(){
    this.isEdit = true;
    this.addXrayFormControls();
  }


  addXrayFormControls(){
    this.entityExternalMapping.entityType = this.entityType
    this.entityExternalMapping.applicationId = this.xrayExternalApplication?.id;
    this.entityExternalMapping.entityId = this.entityId;
    this.xrayFormGroup = new FormGroup({
      id: new FormControl(this.entityExternalMapping?.id, []),
      externalId: new FormControl(this.entityExternalMapping.externalId, [Validators.required, Validators.maxLength(30)]),
      entityType: new FormControl(this.entityExternalMapping.entityType, []),
      applicationId: new FormControl(this.entityExternalMapping.applicationId, []),
      entityId : new FormControl(this.entityExternalMapping.entityId, []),
    })
  }

  linkXrayId() {
    this.isSubmitted = true
    if (this.xrayFormGroup.valid) {
      let mapping = new EntityExternalMapping().deserialize(this.xrayFormGroup.getRawValue());
      this.createXrayLink.emit(mapping);
    }
  }

  closeForm(){
    this.isEdit = false;
    this.isSubmitted = false;
    this.xrayFormGroup = null;
  }

  get suiteResult(){
    return this.entityType == EntityType.TEST_SUITE_RESULT;
  }

  get runResult(){
    return this.entityType == EntityType.RUN_RESULT;
  }

  get isPushFailed(){
    return this.entityExternalMapping?.pushFailed;
  }

  pushResults(event){
    event.stopImmediatePropagation();
    event.preventDefault();
    this.entityExternalMapping.entityType = EntityType.TEST_SUITE_RESULT;
    this.entityExternalMappingService.pushToXray(this.entityExternalMapping).subscribe(
      res=>{
        this.entityExternalMapping = res;
        this.rePushInitialized.emit();
      },
      error=>{
        this.rePushFailed.emit()
      })
    return false;
  }

  get isLoading(){
    return this.entityExternalMapping && this.entityExternalMapping.id &&
      this.entityExternalMapping.pushFailed==null
      && this.entityExternalMapping.externalId==null;
  }

  prepareDataForTestResult() {
    this.entityExternalMappingService.findAll("integration:"+Integration.XrayCloud+",entityId:" + this.testCaseResult?.testCaseId).subscribe(caseEntity => {
        if(caseEntity.content.length > 0 && this.entityExternalMapping.externalId) {
          this.populateTestResultLink(this.xrayExternalApplication.url.toString(),
            this.entityExternalMapping.externalId.toString(), caseEntity.content[0].externalId.toString());
        }else{
          this.testCaseResultLink = null;
        }
      }
    )
  }

  populateTestResultLink(url: string, xrayExecutionId: string, xrayTestId: string){
    this.testCaseResultLink = `${url}/plugins/servlet/ac/com.xpandit.plugins.xray/execution-page?ac.testExecIssueKey=${xrayExecutionId}&ac.testIssueKey=${xrayTestId}`;
  }

  openLink(event, urlLink){
    window.open(urlLink, '_blank');
    event.stopImmediatePropagation();
    event.preventDefault();
    return false;
  }

  deleteXrayId(){
    this.entityExternalMappingService.destroy(this.entityExternalMapping).subscribe(res=>{
      this.entityExternalMapping.id = null;
      this.entityExternalMapping.externalId = null;
      this.addXrayFormControls();
    })
  }
}
