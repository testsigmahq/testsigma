import {Component, Inject, OnInit} from "@angular/core";
import {BaseComponent} from "app/shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {ReRunType} from "../../../enums/re-run-type.enum";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TestPlanResult} from "../../../models/test-plan-result.model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TestPlanResultService} from "../../../services/test-plan-result.service";
import {Router} from "@angular/router";
import {TestCaseResultService} from "../../../services/test-case-result.service";

@Component({
  selector: 're-run-popup',
  template: `
    <div class="theme-overlay-container">
      <div class="theme-overlay-header">
        <div
          class="theme-overlay-title"
          [translate]="'Re-run Configuration'">
        </div>
        <button
          class="theme-overlay-close"
          type="button"
          [matTooltip]="'hint.message.common.close' | translate"
          mat-dialog-close>
        </button>
      </div>
      <div class="theme-overlay-content">
        <mat-error *ngIf="this.error">{{this.error}} </mat-error>
        <form class="ts-form">
            <mat-radio-group [formControl]="this.reRunFormGroup?.controls['parentRunType']" (change)="onChange()" class="pb-15">
              <mat-radio-button class="pt-20" [value] = "reRunTypes.ALL_TESTS">All Test Cases</mat-radio-button><br>
              <mat-radio-button class="pt-20" [value] = "reRunTypes.ONLY_FAILED_TESTS">All Failed Test Cases</mat-radio-button>
              <div *ngIf="isDataDriven">
                <div *ngIf="showChildOptions" class="pl-20 pt-10">
                  <label class="details-title" style="color: #777C85;">DATA DRIVEN</label><br>
                  <mat-radio-group [formControl]="this.reRunFormGroup?.controls['childRunType']" class="pb-50">
                    <mat-radio-button class="pt-12" [value] = "reRunTypes.ALL_ITERATIONS">All Iterations</mat-radio-button><br>
                    <mat-radio-button class="pt-12" [value] = "reRunTypes.ONLY_FAILED_ITERATIONS">Failed Iterations</mat-radio-button>
                  </mat-radio-group>
                </div>
                <mat-radio-button class="pt-20" [value] = "reRunTypes.ONLY_FAILED_ITERATIONS_IN_FAILED_TESTS">
                  Only Failed Iteration in Failed Test Cases</mat-radio-button>
              </div>
            </mat-radio-group>
        </form>
      </div>
      <div class="theme-overlay-footer">
        <button class="theme-btn-clear-default my-6" [textContent]="'btn.common.cancel' | translate" mat-dialog-close></button>
        <button class="theme-btn-primary" (click)="reRun()" [disabled]="this.reRunFormGroup?.invalid || this.executionRequested">
          <span [textContent]="'Start' | translate"></span>
        </button>
      </div>
    </div>
  `
})

export class ReRunPopupComponent extends BaseComponent implements OnInit {
  public reRunTypes = ReRunType;
  public reRunFormGroup: FormGroup;
  public parentRunType: ReRunType;
  public childRunType: ReRunType;
  public error : String;
  public isDataDriven: boolean;
  public executionRequested: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data:{parentExecutionResult: TestPlanResult},
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public executionResultService: TestPlanResultService,
    private router: Router,
    private dialogRef: MatDialogRef<ReRunPopupComponent>,
    private testCaseResultService : TestCaseResultService){
    super(authGuard, notificationsService, translate)
  }

  ngOnInit() {
    this.checkDataDriven();
  }

  onChange(){
    if(this.isDataDriven && this.parentReRunType == ReRunType.ONLY_FAILED_TESTS)
      this.dialogRef.updateSize( '450px', '350px');
    else
      this.dialogRef.updateSize( '450px', '250px');
  }

  reRun() {
    this.executionRequested = true;
    let executionResult = new TestPlanResult();
    let lastRunId = this.data.parentExecutionResult.lastRun?.id;
    if(lastRunId)
      executionResult.reRunParentId = lastRunId;
    else
      executionResult.reRunParentId = this.data.parentExecutionResult.id;
    executionResult.testPlanId = this.data.parentExecutionResult.testPlanId;
    executionResult.isReRun = true;
    executionResult.reRunType = this.reRunType;
    this.executionResultService.create(executionResult).subscribe((result: TestPlanResult) => {
      this.translate.get("re_run.initiate.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.executionRequested = false;
        this.router.navigate(['/td', 'runs', result.id]);
      })
      this.close();
    }, error => {
      this.error = "Problem while starting execution";
      this.executionRequested = false;
    })
  }


  checkDataDriven(){
    let query = "iteration:null,testPlanResultId:" + this.data.parentExecutionResult.lastRun.id + ",isStepGroup:" + false;
    this.testCaseResultService.findAll("testPlanResultId:"+this.data.parentExecutionResult.lastRun.id+",iteration:null", "id,desc").subscribe(testCasteResults=>{
      testCasteResults.content.forEach(testCaseResult=>{
        if(testCaseResult.isDataDriven && !testCaseResult.isPassed && !this.isDataDriven && !testCaseResult.isStepGroup){
          this.isDataDriven = true;
        }
      })
      this.addReRunFormControls();
    })
  }

  addReRunFormControls(){
    this.parentRunType = ReRunType.ALL_TESTS;
    this.childRunType = ReRunType.ALL_ITERATIONS;
    this.reRunFormGroup = new FormGroup({
      parentRunType: new FormControl(this.parentRunType, Validators.required),
      childRunType: new FormControl(this.childRunType, [this.requiredIfValidator(() => this.isDataDriven &&
        this.reRunType == ReRunType.ONLY_FAILED_TESTS)])
    });
  }

  get showChildOptions(){
    return this.isDataDriven && this.parentReRunType == ReRunType.ONLY_FAILED_TESTS;
  }

  close(){
    this.dialogRef.close(this.reRunType);
  }

  get reRunType(){
    if(this.parentReRunType == ReRunType.ONLY_FAILED_TESTS && this.isDataDriven){
      return this.reRunFormGroup?.controls['childRunType']?.value;
    }
    return this.parentReRunType;
  }

  get parentReRunType(){
    return this.reRunFormGroup?.controls['parentRunType']?.value;
  }

}
