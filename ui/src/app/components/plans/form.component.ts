import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {TestPlan} from "../../models/test-plan.model";
import {fade} from "../../shared/animations/animations";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlanType} from "../../enums/execution-type.enum";
import {TestPlanService} from "../../services/test-plan.service";
import {TestDeviceService} from "../../services/test-device.service";
import {TestSuiteService} from "../../services/test-suite.service";
import {TestDevice} from "../../models/test-device.model";
import {Pageable} from "../../shared/models/pageable";

@Component({
  animations: [fade],
  selector: 'app-form',
  templateUrl: './form.component.html',
  styles: [
  ]
})
export class FormComponent implements OnInit {
  public versionId: Number;
  public testPlanForm : FormGroup;
  public testPlan: TestPlan;
  public testPlanId: number;
  public version: WorkspaceVersion;
  public isFetchingComplete: boolean = false;
  public formSubmitted: boolean;

  constructor(
    private versionService: WorkspaceVersionService,
    private testPlanService: TestPlanService,
    private executionEnvironmentService: TestDeviceService,
    private testSuiteService: TestSuiteService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.testPlanId = this.route.snapshot.params.testPlanId;
    if(this.testPlanId) {
      this.testPlanService.find(this.testPlanId).subscribe(res => {
        this.testPlan = res;
        this.versionId = this.testPlan.workspaceVersionId;
        this.fetchVersion();
        this.fetchEnvironments();
      });
    } else {
      this.testPlan = new TestPlan();
      this.testPlan.testPlanLabType = TestPlanLabType.TestsigmaLab;
      this.testPlan.testPlanType = TestPlanType.CROSS_BROWSER;
      this.initForControls();
      this.versionId = this.route.snapshot.params.versionId;
      this.fetchVersion();
    }
  }

  fetchEnvironments() {
    this.executionEnvironmentService.findAll("testPlanId:"+this.testPlanId).subscribe(res => {
      this.testPlan.testDevices = res.content;
      this.fetchSuites();
    })
  }

  fetchSuites() {
    this.testPlan.testDevices.forEach((environment: TestDevice, index: number) => {
      this.testSuiteService.findAll("testPlanId:" + environment.testPlanId).subscribe(res => {
        let page = new Pageable();
        page.pageSize = 500;
        this.testSuiteService.findAll("testPlanId:" + environment.testPlanId, undefined, page).subscribe(res => {
          environment.testSuites = res.content;
          if (index == this.testPlan.testDevices.length - 1)
            this.initForControls();
        })
      })
    })
  }

  public noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }

  initForControls() {
    this.testPlanForm = this.formBuilder.group({
      name: new FormControl(this.testPlan.name, [Validators.required, Validators.minLength(4), Validators.maxLength(120),this.noWhitespaceValidator]),
      description: new FormControl(this.testPlan.description, []),
      testPlanLabType: new FormControl(this.testPlan.testPlanLabType, [Validators.required]),
      matchBrowserVersion: new FormControl(this.testPlan.matchBrowserVersion, []),
      testDevices: this.formBuilder.array([]),
      mailList: this.formBuilder.array([]),
      testPlanType: new FormControl(this.testPlan.testPlanType, [Validators.required])
    })

    setTimeout(()=> this.isFetchingComplete = true, 100)
  }

  fetchVersion() {
    this.versionService.show(this.versionId).subscribe(res => {
      this.version = res
      if(!this.testPlan.id)
        this.testPlan.testPlanType = this.version.workspace.isRest ? TestPlanType.DISTRIBUTED : this.testPlan.testPlanType;
    });
  }
}
