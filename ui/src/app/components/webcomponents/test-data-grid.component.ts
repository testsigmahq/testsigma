import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef, EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators, ValidatorFn, AbstractControl} from '@angular/forms';
import {TestData} from "../../models/test-data.model";
import {TestDataSet} from "../../models/test-data-set.model";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import { Router } from '@angular/router';
import * as moment from 'moment';
import {MatDialog} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";


@Component({
  selector: 'app-test-data-grid',
  templateUrl: './test-data-grid.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {'class': 'd-flex ts-col-100 flex-wrap'}
})


export class TestDataGridComponent implements OnInit {
  @Input('testDataForm') testDataForm: FormGroup;
  @Input('testData') testData: TestData;
  @Input('formSubmitted') formSubmitted: Boolean;
  public dataWithOrder: string[] = [];
  public selectedParameters = [];
  public selectAll: Boolean = false;
  @ViewChild('setNameScrollable') setNameScrollable: CdkVirtualScrollViewport;
  @ViewChild('dataScrollable') dataScrollable: CdkVirtualScrollViewport;
  @ViewChild('actionScrollable') actionScrollable: CdkVirtualScrollViewport;
  @ViewChild('parametersHeader') parametersHeader: ElementRef;
  @Output('onDeleteSet') onDeleteSet = new EventEmitter<number>();

  public activeScrollingElement: ElementRef;
  public hideToolTip: Boolean;
  public isReadOnly: boolean;

  readonly MAX_TEST_DATA_SETS = 200;

  get duplicateSetNames(): Boolean{
    let duplicate = this.datasetControls().controls.find((item) => {
      return item['controls'].name.errors?.duplicate
    });
    return !!duplicate;
  }

  get duplicateParametersName(): Boolean{
    let duplicate = this.parameterControls().controls.find((item) => {
      return item.errors?.duplicate;
    });
    return !!duplicate;
  }

  constructor(
    private formBuilder: FormBuilder,
    private matModal: MatDialog,
    public translate: TranslateService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.isReadOnly = this.router.url.endsWith("/sets");
    if (!this.testData.id) {
      this.testDataForm.addControl('parameterNames', this.formBuilder.array([]));
      this.testDataForm.addControl('dataSets', this.formBuilder.array([]));
      this.testDataForm.addControl('dataWithOrder', new FormControl(this.dataWithOrder, []));
      this.addParameter();
      this.addDataSet();
    } else {
      this.testDataForm.addControl('parameterNames', this.formBuilder.array([]));
      this.testDataForm.addControl('dataSets', this.formBuilder.array([]));
      this.populateParameters();
      this.populateDataSets();
      this.testDataForm.addControl('dataWithOrder', new FormControl(this.dataWithOrder, []));
    }
    this.refreshControls();
  }

  onHeaderScroll(isHeaderScroll?:boolean) {
    if (!this.activeScrollingElement)
      this.activeScrollingElement = this.parametersHeader.nativeElement;
    if (this.activeScrollingElement == this.parametersHeader.nativeElement || isHeaderScroll) {
      const dataElement = this.dataScrollable.elementRef.nativeElement as HTMLElement;
      dataElement.scrollLeft = this.parametersHeader.nativeElement.scrollLeft;
    }
  }

  onSetNameScroll() {
    if (!this.activeScrollingElement)
      this.activeScrollingElement = this.setNameScrollable.elementRef;
    if (this.activeScrollingElement == this.setNameScrollable.elementRef) {
      const setElement = this.setNameScrollable.elementRef.nativeElement as HTMLElement;
      const dataElement = this.dataScrollable.elementRef.nativeElement as HTMLElement;
      dataElement.scrollTop = setElement.scrollTop;
    }
  }

  onDataSetScroll() {
    if (!this.activeScrollingElement)
      this.activeScrollingElement = this.dataScrollable.elementRef;
    if (this.activeScrollingElement == this.dataScrollable.elementRef) {
      const setElement = this.setNameScrollable.elementRef.nativeElement as HTMLElement;
      const dataElement = this.dataScrollable.elementRef.nativeElement as HTMLElement;
      setElement.scrollTop = dataElement.scrollTop;
      this.parametersHeader.nativeElement.scrollLeft = dataElement.scrollLeft;
    }
  }

  onHoverRow(index){
    let dataSetControls = this.onLeaveRow();
    dataSetControls.controls[index]['ishover'] = true;
  }

  onLeaveRow() {

    let dataSetControls = this.datasetControls();
    dataSetControls.controls.forEach(control => {
      control['ishover'] = false
    })
    return dataSetControls;
  }

  addParameter(): void {
    this.parameterControls().push(new FormControl('', [Validators.required, this.isParameterDuplicate()]));
    let dataSetControls = this.datasetControls();
    let count = dataSetControls.length;
    for (let index = 0; index < count; index++) {
      dataSetControls.controls[index]['controls']['data'].push(new FormControl('', []));
    }
    setTimeout(()=> {
      this.parametersHeader.nativeElement.children[this.parametersHeader.nativeElement.children.length-1].lastElementChild.children.item(1).focus();
      setTimeout(()=> {
        this.onHeaderScroll(true)
      }, 30);
    }, 30)
  }

  addDataSet(index?: number): void {
    let dataControls = this.formBuilder.array([]);
    let length = this.parameterControls().length;
    for (let index = 0; index < length; index++) {
      dataControls.push(new FormControl('', []))
    }

    let dataGroup = this.formBuilder.group({
      id: null,
      testDataProfileId: null,
      selected: [false, Validators.required],
      name: new FormControl('',[Validators.required, this.isNameDuplicate() ]),
      description: '',
      expectedToFail: false,
      position : index ? index+1 : this.datasetControls().length,
      data: dataControls
    })

    if (index || index == 0) {
      this.datasetControls().controls.splice(index+1, 0, dataGroup);
      this.handleSelectedListReOrder(index+1);
    } else {
      this.datasetControls().push(dataGroup);
    }
    this.datasetControls().controls = [...this.datasetControls().controls];
    this.selectAll=false;
  }

  parameterControls(): FormArray {
    return this.testDataForm.get('parameterNames') as FormArray;
  }

  datasetControls(): FormArray {
    return this.testDataForm.get('dataSets') as FormArray;
  }

  removeParameter(index: number): void {
    this.parameterControls().removeAt(index);
    let dataSetControls = this.datasetControls();
    let count = dataSetControls.length;
    this.dataWithOrder[index] = null;
    for (let i = 0; i < count; i++) {
      dataSetControls.controls[i]['controls']['data'].removeAt(index);
    }
  }

  removeDataSet(index: number, id: number): void {
    this.hideToolTip = true;
    if(id != null){
      this.onDeleteSet.emit(id);
    }
    setTimeout(() => {
      this.datasetControls().controls.splice(index, 1);
      this.datasetControls().controls = [...this.datasetControls().controls];
      this.testDataForm.setControl('dataSets', this.datasetControls());
      if(this.selectedParameters.indexOf(index)>-1)
        this.selectedParameters.splice(this.selectedParameters.indexOf(index), 1);
      this.handleSelectedListReOrderForDelete(index);
      this.hideToolTip = false;
    }, 100);
  }


  populateParameters(): void {
    if (this.testData.data.length > 0) {
      let index = 0;
      for (let key in this.testData.data[0].data) {
        this.dataWithOrder.push(key);
        this.parameterControls().push(new FormControl(key, [this.isParameterDuplicate()]));
        ++index;
      }
    }
  }

  populateDataSets(): void {
    this.testData.data.forEach((dataSet: TestDataSet) => {
      let dataControls = this.formBuilder.array([]);
      if(this.testData?.columns?.length) {
        this.testData?.columns?.forEach(key => {
          dataControls.push(new FormControl(dataSet.data[key], []))
        });
      }
      else
        for(let key in dataSet?.data){
          dataControls.push(new FormControl(dataSet.data[key],[]))
        }
      let dataGroup = this.formBuilder.group({
        id: dataSet.id,
        testDataProfileId: dataSet.testDataProfileId,
        selected: [dataSet.selected, Validators.required],
        name: new FormControl(dataSet.name,[Validators.required, this.isNameDuplicate() ]),
        description: dataSet.description,
        expectedToFail: dataSet.expectedToFail,
        position: dataSet.position,
        data: dataControls
      })
      this.datasetControls().push(dataGroup);
    })
  }

  isNameDuplicate(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      let alreadyExistsInArray;
      alreadyExistsInArray = this.datasetControls().controls.find((item) => {
        if( item['controls'].name != control){
          return (item['controls'].name.value == control.value);
        }
      });
      if (alreadyExistsInArray && !control.errors?.required)
        return {duplicate: true};
      else
        return null;
    }
  }

  isParameterDuplicate(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      let alreadyExistsInArray;
      alreadyExistsInArray = this.parameterControls().controls.find((item) => {
        if( item!= control){
          return (item.value == control.value);
        }
      });
      if (alreadyExistsInArray && !control.errors?.required)
        return {duplicate: true};
      else
        return null;
    }
  }

  private refreshControls() {
    this.testDataForm.controls.parameterNames.valueChanges.subscribe(() => {
      this.testData.data = this.parameterControls().getRawValue();
      this.testDataForm.addControl('parameterNames', this.formBuilder.array([]));
      this.populateParameters();
    });
    this.testDataForm.controls.dataSets.valueChanges.subscribe(() => {
      this.testDataForm.addControl('dataSets', this.formBuilder.array([]));
    })
  }

  get shouldShowAddIcon() {
    let isOldTDP = moment(this.testData?.createdAt).unix() < this.insertLimitDate;
    return isOldTDP || this.datasetControls().length < this.MAX_TEST_DATA_SETS;
  }

  get insertLimitDate() {
    // November 10, 2022 12:00:00 AM GMT
    return 1668038400000;
  }

  handleSelectedListReOrder(newIndex : number){
    this.selectedParameters = this.selectedParameters.map(index => {
      if(index>=newIndex)
        return index+1;
      else
        return index;
    });
  }

  handleSelectedListReOrderForDelete(newIndex : number){
    this.selectedParameters = this.selectedParameters.map(index => {
      if(index>=newIndex)
        return index-1;
      else
        return index;
    });
  }

}
