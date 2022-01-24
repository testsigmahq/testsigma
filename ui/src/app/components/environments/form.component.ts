import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AbstractControl, FormControl, FormGroup, ValidatorFn, Validators, FormBuilder, FormArray} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {EnvironmentService} from "../../services/environment.service";
import {Environment} from "../../models/environment.model";

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  host: {'class': 'page-content-container'}
})
export class FormComponent extends BaseComponent implements OnInit {
  public environmentForm: FormGroup;
  public environment: Environment;
  public versionId: number;
  public formSubmitted = false;
  public encryptedNames: String[] = [];
  public codeMirrorOptions = {
    lineNumbers: true,
    lineWrapping: true,
    foldGutter: true,
    gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter', 'CodeMirror-lint-markers'],
    mode: {
      name: 'javascript', json: true
    }
  };
  @ViewChild('toggleTable') toggleTable: ElementRef

  public showJSON: Boolean = false;
  public saving: Boolean;
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private environmentService: EnvironmentService,
    private versionService: WorkspaceVersionService,
    private router: Router,
    private formBuilder: FormBuilder) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.environment = new Environment();
    this.environment.parameters = JSON.parse('{"":""}');
    this.environment.passwords = [];
    this.versionId = this.route.snapshot.parent.parent.params.versionId;
    this.route.snapshot.params = {...this.route.snapshot.params, ...{versionId: this.versionId}}
    this.pushToParent(this.route, this.route.snapshot.params);
    this.versionService.show(this.versionId).subscribe(res => {
      if (!this.route.snapshot.params.environmentId) {
        this.initiateForm();
      } else {
        this.fetchEnvironment(this.route.snapshot.params.environmentId);
      }
    })
  }

  private fetchEnvironment(requirementId: number): void {
    this.environmentService.show(requirementId).subscribe(res => {
      this.environment = res;
      this.environment.parametersJson = JSON.stringify(res.parameters, null, ' ');
      this.initiateForm();
    });
  }

  private initiateForm(): void {
    this.environmentForm = new FormGroup({
      name: new FormControl(this.environment.name, [Validators.required, Validators.minLength(4), Validators.maxLength(125)]),
      description: new FormControl(this.environment.description),
      parameters: this.formBuilder.array([]),
      paramsJson: new FormControl(this.environment.parametersJson,  this.validateJson())
    });
  }

  public parametersInvalid(): boolean{
    if(this.showJSON)
      this.parseJSON();
    else
      this.stringifyJSON()
    return Object.keys(this.environment.parameters).length == 0;
  }

  public create(): void {
    if(this.returnIfInvalid()) return;
    this.saving = true
    this.environmentService.create(this.environment).subscribe(
      (env: Environment) => {
        this.saving = false;
        this.translate.get('message.common.created.success', {FieldName: "Environment"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'environments', env.id, 'details'], { queryParams: { v: this.versionId }})
      },
      (err) => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: "Environment"})
          .subscribe(msg => this.showAPIError(err, msg))
      }
    );
  }

  public update(): void {
    if(this.returnIfInvalid()) return;
    this.saving = true;
    this.environmentService.update(this.environment).subscribe(
      (environment) => {
        this.saving = false;
        this.translate.get('message.common.update.success', {FieldName: "Environment"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'environments', environment.id, 'details'], { queryParams: { v: this.versionId }})
      },
      (err) => {
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: "Environment"})
          .subscribe(msg => this.showAPIError(err, msg))
      }
    );
  }

  private validateJson(): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      if (!this.environmentForm) return null;
      try {
        if (typeof this.environmentForm.controls.paramsJson.value == "string") {
          let JsonObject = JSON.parse(this.environmentForm.controls.paramsJson.value);
          for(let key of Object.keys(JsonObject))
            if (key.length < 1) return {invalidJson: true};
          if(Object.keys(JsonObject).length == 0) return { required: true };
        }
      } catch (e) { return { invalidJson: true } }
      return null;
    }
  }

  showTable() {
    this.showJSON = false;
    this.parseJSON();
  }

  parseJSON() {
    if (!this.environmentForm.controls.paramsJson || this.environmentForm.controls.paramsJson?.invalid)
      return;
    let json = JSON.parse(this.environment.parametersJson);
    let parsedJSON = JSON.parse("{}");
    for(let key in json){
      if(json.hasOwnProperty(key))
        if(key && key != '' && json[key] && json[key] != '')
          parsedJSON[key] = json[key];
    }
    this.environment.parameters = parsedJSON;
  }

  showJSONFormat() {
    this.showJSON = true;
    this.stringifyJSON();
  }

  stringifyJSON() {
    let json = JSON.parse("{}");
    this.environmentForm.getRawValue().parameters.forEach(parameter => {
      if(parameter.key && parameter.key != '' && parameter.value && parameter.value != '')
        json[parameter.key] = parameter.value;
    });
    this.environment.parameters = json;
    this.environment.parametersJson = JSON.stringify(json, null, 2);
  }

  returnIfInvalid(): Boolean {
    this.environment.description = this.environmentForm.controls.description.value;
    this.environment.passwords =this.environmentForm.controls.encryptedNames.value;
    this.formSubmitted = true;
    return (this.parametersInvalid() ||this.environmentForm.controls.name.invalid ||
            this.hasDuplicateParameters() || this.hasEmptyParameterValues() || this.hasEmptyParameterNames());
  }

  setEncryptedNames($event: String[]) {
    this.encryptedNames = $event;
  }

  hasDuplicateParameters(): boolean {
    let count = 0;
    let parameters = this.environmentForm.getRawValue().parameters;
    parameters.forEach(parameter => {
      if (parameter.key.trim().length || parameter.value.trim().length) {
        parameters.find(parameterItem => {
          if (parameterItem.key.trim() == parameter.key.trim()) {
            count++;
          }
        });
      }
    });
    return count > parameters.length;
  }

  hasEmptyParameters(): boolean {
    let empty =false;
    this.environmentForm.getRawValue().parameters.forEach(parameter => {
      if (parameter.value.trim().length && !parameter.key.trim().length) {
        empty = true;
      }
    });
    return empty
  }

  hasEmptyParameterValues(): boolean{
    let empty =false;
    this.environmentForm.getRawValue().parameters.forEach(parameter => {
      if (parameter.key.trim().length && !parameter.value.trim().length) {
        empty = true;
      }
    });
    return empty
  }

  hasEmptyParameterNames(): boolean{
    let empty =false;
    let parameters= this.environmentForm.getRawValue().parameters;
    parameters.forEach((parameter, index) => {
      if (!parameter.key.trim().length &&  index < parameters.length-1) {
        empty = true;
      }
    });
    return empty
  }
}
