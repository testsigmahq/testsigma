import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild
} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators, ValidatorFn, AbstractControl} from '@angular/forms';
import {TestData} from "../../models/test-data.model";
import {TestDataSet} from "../../models/test-data-set.model";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import { Router } from '@angular/router';


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
  public encryptedIndexes: Number[] = [];
  public dataWithOrder: string[] = [];
  @ViewChild('setNameScrollable') setNameScrollable: CdkVirtualScrollViewport;
  @ViewChild('dataScrollable') dataScrollable: CdkVirtualScrollViewport;
  @ViewChild('actionScrollable') actionScrollable: CdkVirtualScrollViewport;
  @ViewChild('parametersHeader') parametersHeader: ElementRef;

  public activeScrollingElement: ElementRef;
  public hideToolTip: Boolean;
  public isReadOnly: boolean;

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
    private router: Router) {
  }

  ngOnInit(): void {
    this.isReadOnly = this.router.url.endsWith("/sets");
    if (!this.testData.id) {
      this.testDataForm.addControl('parameterNames', this.formBuilder.array([]));
      this.testDataForm.addControl('dataSets', this.formBuilder.array([]));
      this.testDataForm.addControl('encryptedIndexes', new FormControl(this.encryptedIndexes, []));
      this.testDataForm.addControl('dataWithOrder', new FormControl(this.dataWithOrder, []));
      this.addParameter();
      this.addDataSet();
    } else {
      this.testDataForm.addControl('parameterNames', this.formBuilder.array([]));
      this.testDataForm.addControl('dataSets', this.formBuilder.array([]));
      this.populateParameters();
      this.populateDataSets();
      this.testDataForm.addControl('encryptedIndexes', new FormControl(this.encryptedIndexes, []));
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
      selected: [false, Validators.required],
      name: new FormControl('',[Validators.required, this.isNameDuplicate() ]),
      description: '',
      expectedToFail: false,
      data: dataControls
    })

    if (index || index == 0) {
      this.datasetControls().controls.splice(index+1, 0, dataGroup);
    } else {
      this.datasetControls().push(dataGroup);
    }
    this.datasetControls().controls = [...this.datasetControls().controls];
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

  removeDataSet(index: number): void {
    this.hideToolTip = true;
    setTimeout(() => {
      this.datasetControls().controls.splice(index, 1);
      this.datasetControls().controls = [...this.datasetControls().controls];
      this.testDataForm.setControl('dataSets', this.datasetControls());
      this.hideToolTip = false;
    }, 100);
  }

  encrypt(index: number): void {
    this.encryptedIndexes.push(index);
    this.testDataForm.patchValue({
      encryptedIndexes: this.encryptedIndexes
    })
  }

  decrypt(index: Number): void {
    this.encryptedIndexes.splice(this.encryptedIndexes.indexOf(index), 1);
    this.testDataForm.patchValue({
      encryptedIndexes: this.encryptedIndexes
    })
  }

  isParameterEncrypted(parameterIndex: number): boolean {
    return this.encryptedIndexes.indexOf(parameterIndex) > -1;
  }

  populateParameters(): void {
    if (this.testData.data.length > 0) {
      let index = 0;
      for (let key in this.testData.data[0].data) {
        this.dataWithOrder.push(key);
        this.parameterControls().push(new FormControl(key, [this.isParameterDuplicate()]));
        if (this.testData.passwords.indexOf(key) > -1)
          this.encryptedIndexes.push(index);
        ++index;
      }
    }
  }

  populateDataSets(): void {
    this.testData.data.forEach((dataSet: TestDataSet) => {
      let dataControls = this.formBuilder.array([]);
      for (let key in dataSet.data) {
        dataControls.push(new FormControl(dataSet.data[key], []))
      }
      let dataGroup = this.formBuilder.group({
        selected: [dataSet.selected, Validators.required],
        name: new FormControl(dataSet.name,[Validators.required, this.isNameDuplicate() ]),
        description: dataSet.description,
        expectedToFail: dataSet.expectedToFail,
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

}
