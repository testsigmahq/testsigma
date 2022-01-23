import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RestStepEntity} from "../../models/rest-step-entity.model";
import {RestAuthorization} from "../../enums/rest-authorization.enum";
import {RestMethod} from "../../enums/rest-method.enum";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TestStepService} from "../../services/test-step.service";
import {BaseComponent} from "../../shared/components/base.component";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService} from "angular2-notifications";
import {TestDataType} from "../../enums/test-data-type.enum";

@Component({
  selector: 'app-rest-form-request-details',
  templateUrl: './rest-form-request-details.component.html',
  styles: []
})
export class RestFormRequestDetailsComponent extends BaseComponent implements OnInit {
  @Input('form') form: FormGroup;
  @Input('restStep') restStep: RestStepEntity;
  @Input('formSubmitted') formSubmitted: Boolean;
  @Output('apiResponse') apiResponse = new EventEmitter<any>();
  public response: any = null;
  public isJSONResponse: boolean = false;
  public isFetching: boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testStepService: TestStepService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.addFormControls();
  }

  get authorizations() {
    return Object.keys(RestAuthorization);
  }

  get methods() {
    return Object.keys(RestMethod);
  }

  fetchApiResponse() {
    this.isFetching= true;
    let restStep = new RestStepEntity().deserializeRawValue(this.form.getRawValue());
    this.testStepService.fetchApiResponse(restStep).subscribe(res => {
      if (res && res['headers']) {
        let headers = res['headers'];
        if (headers && headers.length) {
          headers.forEach(header => {
            if (header.name == 'Content-Type' && header.value.includes('application/json')) {
              this.isJSONResponse = true;
            }
          })
        }
        this.response = typeof res['contentStr'] == 'string' ? res['contentStr'] : JSON.stringify(JSON.parse(res['contentStr']), null, 2);
        this.isFetching = false
        this.apiResponse.emit({response:this.response, isJSONResponse: this.isJSONResponse});
      }
    }, () => {
      this.isFetching = false
    })
  }

  addFormControls() {
    this.form.addControl('url', new FormControl(this.restStep.url, [
      Validators.required, Validators.pattern('(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/? | @\|(.*?)\| | *\|(.*?)\| | \$\|(.*?)\|')
    ]));
    this.form.addControl('followRedirects', new FormControl(this.restStep.followRedirects, []));
    this.form.addControl(
      'method', new FormControl(this.restStep.method, []));
    this.form.addControl('authorizationType', new FormControl(this.restStep.authorizationType, []));
    this.form.addControl('payload', new FormControl(this.restStep.payload, []));
    if (this.restStep?.authorizationValue)
      this.form.addControl('authorizationValue', new FormGroup({
        username: new FormControl(this.restStep?.authorizationValue?.username, []),
        password: new FormControl(this.restStep?.authorizationValue?.password, []),
        token: new FormControl(this.restStep?.authorizationValue?.token, [])
      }));

  }

  get canShowPayload() {
    const requestMethod = this.form.get('method').value;
    return requestMethod == RestMethod.PATCH || requestMethod == RestMethod.PUT || requestMethod == RestMethod.POST;
  }

  get isBearerAuthorization() {
    const authorizationType = this.form.get('authorizationType').value;
    return authorizationType == RestAuthorization.BEARER;
  }

  get isBasicAuthorization() {
    const authorizationType = this.form.get('authorizationType').value;
    return authorizationType == RestAuthorization.BASIC;
  }

  isParameter() {
   let url:String = this.form.getRawValue().url;
   return (url?.match(/\*\|(.?)\||\*\|(.+?)\|/g))
   || (url?.match(/\$\|(.?)\||\$\|(.+?)\|/g))
   || (url?.match(/@\|(.?)\||@\|(.+?)\|/g));
  }
}
