import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {BaseComponent} from "app/shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {ReRunType} from "../../../enums/re-run-type.enum";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 're-run-form',
  template: `
    <mat-radio-group [formControl]="this.reRunFormGroup.controls['parentRunType']" (change)="onChange()" class="pb-15">
      <mat-radio-button class="pl-30" [value] = "reRunTypes.NONE">None</mat-radio-button>
      <mat-radio-button class="pl-30" [value] = "reRunTypes.ALL_TESTS">All Test Cases</mat-radio-button>
      <mat-radio-button class="pl-30" [value] = "reRunTypes.ONLY_FAILED_TESTS">All Failed Test Cases</mat-radio-button>
      <div *ngIf="isDataDriven">
        <div *ngIf="showChildOptions" class="pl-50 pt-10">
          <label class="details-title" style="color: #777C85;">DATA DRIVEN</label><br>
          <mat-radio-group [formControl]="this.reRunFormGroup.controls['childRunType']" (change)="onChange()">
            <mat-radio-button class="pl-30" [value] = "reRunTypes.ALL_ITERATIONS">All Iterations</mat-radio-button>
            <mat-radio-button class="pl-30" [value] = "reRunTypes.ONLY_FAILED_ITERATIONS">Failed Iterations</mat-radio-button>
          </mat-radio-group>
        </div>
        <mat-radio-button class="pl-30" [value] = "reRunTypes.ONLY_FAILED_ITERATIONS_IN_FAILED_TESTS">
          Only Failed Iteration in Failed Test Cases</mat-radio-button>
      </div>
    </mat-radio-group>
  `
})

export class ReRunFormComponent extends BaseComponent implements OnInit {
  public reRunTypes = ReRunType;
  public reRunFormGroup: FormGroup;
  public parentRunType: ReRunType;
  public childRunType: ReRunType;
  public error : String;
  @Input() public defaultType: ReRunType;
  @Output() public onTypeChange  = new EventEmitter<ReRunType>();
  @Input() public isDataDriven: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService){
    super(authGuard, notificationsService, translate)
  }

  ngOnInit() {
    this.initializeForm();
  }

  initializeForm(){
    if(this.defaultType == null || this.defaultType == ReRunType.NONE) {
      this.parentRunType = ReRunType.NONE;
      this.childRunType = ReRunType.ALL_ITERATIONS;
    }
    else
    if(this.defaultType == ReRunType.ALL_TESTS || this.defaultType == ReRunType.ONLY_FAILED_ITERATIONS_IN_FAILED_TESTS){
      this.parentRunType = this.defaultType;
      this.childRunType = ReRunType.ALL_ITERATIONS;
    }
    else if(this.defaultType == ReRunType.ONLY_FAILED_TESTS){
      this.parentRunType = this.defaultType;
      this.childRunType = ReRunType.ALL_ITERATIONS
    } else {
      this.parentRunType = ReRunType.ONLY_FAILED_TESTS;
      this.childRunType = this.defaultType;
    }
    this.reRunFormGroup = new FormGroup({
      parentRunType: new FormControl(this.parentRunType, Validators.required),
      childRunType: new FormControl(this.childRunType, [this.requiredIfValidator(() => this.isDataDriven &&
        this.reRunType == ReRunType.ONLY_FAILED_TESTS)])
    });
  }

  onChange(){
    this.onTypeChange.emit(this.reRunType);
  }

  get showChildOptions(){
    return this.isDataDriven && this.parentReRunType == ReRunType.ONLY_FAILED_TESTS;
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
