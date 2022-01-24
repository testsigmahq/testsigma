import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {EnvironmentService} from "../../services/environment.service";
import {Environment} from "../../models/environment.model";
import {Page} from "../../shared/models/page";
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-environments-auto-complete',
  template: `
    <div
      class="w-100">
      <app-auto-complete
        [items]="environmentsList"
        [formGroup]="formGroup"
        [formCtrlName]="formControl"
        [value]="environment"
        [hasNone]="hasNone"
        (onSearch)="fetchEnvironments($event)"
        (onValueChange)="setEnvironment($event)"
      ></app-auto-complete>
      <label class="control-label" [translate]="'test_plan.environment.settings.environments'"></label>
    </div>
  `,
  styles: []
})
export class EnvironmentsAutoCompleteComponent implements OnInit {
  @Input('formGroup') formGroup: FormGroup;
  @Input('formCtrl') formControl: FormControl;
  @Input('value') environment?: Environment;
  @Input('environmentId') environmentId: number;
  @Input('hasNone') hasNone: Boolean;
  @Output('onValueChange') onValueChange = new EventEmitter<any>()
  public environmentsList: Page<Environment>;

  constructor(private environmentService: EnvironmentService) {
  }

  ngOnInit(): void {
    this.fetchEnvironments();
  }

  ngOnChanges(){
    this.onValueChange.emit(this.environment);
  }

  fetchEnvironments(term?: string) {
    let searchName = '';
    if (term) {
      searchName = ",name:*" + term + "*";
    }
    this.environmentService.findAll(searchName).subscribe(res => {
      this.environmentsList = res;
      if(this.environmentId) {
        this.setEnvironment(this.environmentsList?.content.find(environment => environment.id == this.environmentId))
      }
    })
  }

  setEnvironment(environment) {
    this.environment = null
    if(environment) {
      this.environment = environment;
    }
    this.onValueChange.emit(environment);

  }

}
