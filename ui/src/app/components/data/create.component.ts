import {Component, OnInit} from '@angular/core';
import {TestDataService} from "../../services/test-data.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from "../../shared/components/base.component";
import {TestData} from "../../models/test-data.model";
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-data-create',
  templateUrl: './create.component.html',
})
export class CreateComponent extends BaseComponent implements OnInit {
  public versionId: number;
  public testData: TestData = new TestData();
  public testDataForm: FormGroup;
  public saving = false;
  public formSubmitted = false;

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
    this.versionId = this.route.snapshot.params.versionId;
    this.testDataForm = this.formBuilder.group({
      name: new FormControl(this.testData.name, [Validators.required, Validators.minLength(4), Validators.maxLength(125)])
    });
  }

  create() {
    this.formSubmitted = true;
    if(this.testDataForm.invalid) return;
    this.saving = true;
    this.testData = new TestData().deserializeRawValue(this.testDataForm.getRawValue());
    this.testData.versionId = this.versionId;
    this.testDataService.create(this.testData).subscribe((res) => {
      this.translate.get('message.common.created.success', {FieldName: 'Test Data Profile'}).subscribe((res) => {
        this.showNotification(NotificationType.Success, res);
        this.saving = false;
      });
      this.router.navigate(['/td', 'data', res.id]);
    }, (ex)=> {
      this.translate.get('message.common.created.failure', {FieldName: 'Test Data Profiles'}).subscribe((res) => {
        this.showAPIError(ex, res,'Test Data Profile');
        this.saving = false;
      });
    });
  }

}
