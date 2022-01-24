import {Component, OnInit} from '@angular/core';
import {TestDataService} from "../../services/test-data.service";
import {TestData} from "../../models/test-data.model";
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from "../../shared/components/base.component";
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {TestDataSet} from "../../models/test-data-set.model";
import {ToastrService} from "ngx-toastr";

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
  public  oldTestDataSet : TestDataSet;


  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testDataService: TestDataService,
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.pushToParent(this.route, this.route.snapshot.params);
    this.testDataId = this.route.snapshot.params.testDataId;
    this.fetchTestData();
  }

  private fetchTestData() {
    this.testDataService.show(this.testDataId).subscribe(res => {
      this.testData = res;
      this.oldTestDataSet =this.testData.data[0];
      this.versionId = this.testData.versionId;
      this.testDataForm = this.formBuilder.group({
        name: new FormControl(this.testData.name, [Validators.required, Validators.minLength(4), Validators.maxLength(125)])
      });
    });
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
    this.testData['testDataName'] = this.testData.name;
    this.testDataService.update(this.testData.id, this.testData).subscribe(
        res => {
        this.saving = false;
        this.translate.get('message.common.update.success', {FieldName: 'Test Data Profile'}).subscribe(
            res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'data', res.id]);
      },
        err => {
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: 'Test Data Profile'}).subscribe(
            msg => this.showAPIError(err, msg ))
      });
  }


}
