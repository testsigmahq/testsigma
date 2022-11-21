import {Component, HostListener, OnInit} from '@angular/core';
import {TestDataService} from "../../services/test-data.service";
import {TestDataSetService} from "../../services/test-data-set.service";
import {TestData} from "../../models/test-data.model";
import {ActivatedRoute, NavigationCancel, NavigationStart, Router} from '@angular/router';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from "../../shared/components/base.component";
import {MatDialog} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {TestDataSet} from "../../models/test-data-set.model";
import {ToastrService} from "ngx-toastr";
import {Observable} from "rxjs";

@Component({
  selector: 'app-test-data-edit',
  templateUrl: './edit.component.html',
})
export class EditComponent extends BaseComponent implements OnInit {
  public testData: TestData;
  public versionId: number;
  private testDataId: number;
  public testDataForm: FormGroup;
  public saving = false;
  public formSubmitted = false;
  public deletedSetIds: number[] = [];
  private blockRoute: boolean = false;
  public  oldTestDataSet : TestDataSet;
  @HostListener('window:beforeunload')
  canDeactivate(): Observable<boolean> | boolean {
    return !Boolean(this.blockRoute);
  }


  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testDataService: TestDataService,
    private testDataSetService: TestDataSetService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private formBuilder: FormBuilder) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.pushToParent(this.route, this.route.snapshot.params);
    this.testDataId = this.route.snapshot.params.testDataId;
    this.fetchTestData();
  }

  private fetchTestData() {
    this.testDataService.show(this.testDataId).subscribe(testData => {
      if(testData.isMigrated) {
        this.testDataSetService.findAll("testDataProfileId:" + this.testDataId, 'position').subscribe(res => {
          testData.data = res.content;
          this.setTestData(testData);
        })
      } else {
        this.setTestData(testData);
      }
    });
  }

  private setTestData(testData){
    this.testData = testData;
    this.oldTestDataSet = testData.data[0];
    this.versionId = testData.versionId;
    this.testDataForm = this.formBuilder.group({
      name: new FormControl(this.testData.name, [Validators.required, Validators.minLength(4), Validators.maxLength(250)])
    });
  }

  onDeleteSet(id: number){
    this.deletedSetIds.push(id);
  }

  update() {
    this.formSubmitted = true;
    if(this.testDataForm.invalid) return;
    this.saving = true;
    var rawValue = this.testDataForm.getRawValue();
    this.testData = new TestData().deserializeRawValue(rawValue);
    this.testData.setRenamedValues(rawValue, this.oldTestDataSet);
    this.testData.id = <number>this.testDataId;
    this.testData.versionId = this.versionId;
    if(rawValue?.parameterNames?.length) {
      let parameterNames = [];
      rawValue?.parameterNames?.forEach(name => {
        parameterNames.push(name.trim());
      });
      rawValue.parameterNames = parameterNames;
    }
    this.testData.columns = rawValue?.parameterNames;
    this.testData['testDataName'] = this.testData.name;
    this.deletedSetIds = this.deletedSetIds.filter((item, index) => this.deletedSetIds.indexOf(item) === index);
    if(this.deletedSetIds.length >0){
      this.testDataSetService.bulkDestroy(this.deletedSetIds).subscribe(
        ()=>{
          this.deletedSetIds = [];
          this.processUpdate();
        },
        (err)=>{
          this.saving = false;
          this.translate.get('message.common.update.failure', {FieldName: 'Test Data Profile'}).subscribe(
            msg => this.showAPIError(err, msg, 'Test Data Profile' ))
        }
      );
    }else{
      this.processUpdate();
    }
  }

  processUpdate(){
    this.testDataService.update(this.testData.id, this.testData).subscribe(
      res => {
        this.saving = false;
        this.translate.get('message.common.update.success', {FieldName: 'Test Data Profile'}).subscribe(
          res => this.showNotification(NotificationType.Success, res));
        this.blockRoute = false;
        this.router.navigate(['/td', 'data', res.id]);
      },
      err => {
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: 'Test Data Profile'}).subscribe(
          msg => this.showAPIError(err, msg, 'Test Data Profile' ))
      });
  }


}
