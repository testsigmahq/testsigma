import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Page} from "../../shared/models/page";
import {PageObject} from "../../shared/models/page-object";
import {MultiSelectAutocomplete} from "../../shared/components/webcomponents/multiselect-autocomplete";
import {TestCaseService} from "../../services/test-case.service";
import {TestCase} from "../../models/test-case.model";

@Component({
  selector: 'app-test-case-multi-select',
  template: `
    <app-multiselect-autocomplete *ngIf="testCases"
                                  [items]="testCases"
                                  [value]="value"
                                  [selectedPlaceholder]="testSuitePlaceholderValues"
                                  (onSearch)="fetchTestCases($event)"
                                  (onValueChange)="setSelectedTestSuiteValues($event)">
    </app-multiselect-autocomplete>
  `,
  styles: [
  ]
})
export class TestCaseMultiSelectComponent implements OnInit {

  public testCases: Page<TestCase>;
  public testSuitePlaceholderValues: PageObject[];
  @Input('value') public value?: any;
  @Output('value') public valueEvent?: any =  new EventEmitter<any>();
  @Output('onValueChange') onValueChange = new EventEmitter<any>();
  @ViewChild(MultiSelectAutocomplete) private multiSelectAutocomplete!: MultiSelectAutocomplete;
  @Input("versionId") public versionId: number


  constructor(
    public testCaseService: TestCaseService
  ) { }

  ngOnInit(): void {
    this.fetchTestCases();
  }

  public fetchTestCases(term?) {
    let searchName = '';
    if (term) {
      searchName = ",name:*" + term + "*";
    }
    let query = "workspaceVersionId:"+this.versionId+ searchName;
    this.testCaseService.findAll(query).subscribe(res => {
      this.testCases = this.filterDublicateEntities(res);
    });
  }

  filterDublicateEntities(entitiePageObj){
    let testCaseIds = [];
    let testCases = [];
    if(entitiePageObj && entitiePageObj?.content){
      entitiePageObj?.content?.forEach((testCase)=>{
        if(!testCaseIds.includes(testCase?.id)){
          testCases.push(testCase);
          testCaseIds.push(testCase.id);
        }
      });
      entitiePageObj.content = testCases;
    }
    return entitiePageObj;
  }
  setSelectedTestSuiteValues(event: any) {
    if (this.testSuitePlaceholderValues?.length)
      this.value = [...this.value, ...this.testSuitePlaceholderValues?.map(option => option["id"])];
    else
      this.value = event;
    this.valueEvent.emit(this.value)
    this.onValueChange.emit(this.value)
  }

  public reset(){
    this.multiSelectAutocomplete.clearSelection();
  }

}
