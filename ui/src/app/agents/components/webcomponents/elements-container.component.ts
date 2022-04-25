import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MobileElementRect} from "../../models/mobile-element-rect.model";
import {MobileElement} from "../../models/mobile-element.model";
import {AbstractControl, FormControl, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {StepSummaryComponent} from "../../../components/webcomponents/step-summary.component";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationModalComponent} from "../../../shared/components/webcomponents/confirmation-modal.component";
import {TranslateService} from '@ngx-translate/core';
import {MobileRecordingComponent} from "./mobile-recording.component";
import {Element} from "../../../models/element.model";
import {ElementLocatorType} from "../../../enums/element-locator-type.enum";
import {ElementService} from "../../../shared/services/element.service";
import {ElementScreenNameService} from "../../../shared/services/element-screen-name.service";
import {ElementScreenName} from "../../../models/element-screen-name.model";
import {ElementCreateType} from "../../../enums/element-create-type.enum";
import {Observable, of} from "rxjs";
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {ToastrService} from "ngx-toastr";
import {DuplicateLocatorWarningComponent} from "../../../components/webcomponents/duplicate-locator-warning.component";

@Component({
  selector: 'app-save-elements',
  templateUrl: './elements-container.component.html'
})
export class ElementsContainerComponent extends BaseComponent implements OnInit {
  @Input() uiId: number;
  @Input() isEdit: boolean;
  @Input() versionId: number
  @Input() inspectedElement: MobileElementRect;
  @Output() inspectedElementChange = new EventEmitter<MobileElementRect>();
  @Input() element!: Element;
  @Output() elementEventEmitter = new EventEmitter<Element>();
  @Input() elements!: Element[];
  @Output() elementsEventEmitter = new EventEmitter<Element[]>();
  @Input() elementForm!: FormGroup;
  @Output() elementFormEventEmitter = new EventEmitter<FormGroup>();
  @Input() selectElement!: Element;
  @Output() selectedElementEventEmitter = new EventEmitter<Element>();
  @Output() handleElementUpdateEventEmitter = new EventEmitter<{ err: any, isCreate: boolean }>();
  public fetchedElement: Element;
  public editedIndex: number;
  public formSubmitted: any;
  public returnResponse: Element;
  public availableLocatorTypes = [
    ElementLocatorType.accessibility_id,
    ElementLocatorType.id_value,
    ElementLocatorType.xpath,
    ElementLocatorType.class_name,
    ElementLocatorType.name
  ];
  public locatorTypes = {
    accessibility_id: {variableName: "accessibilityId"},
    id_value: {variableName: "id"},
    xpath: {variableName: "xpath"},
    class_name: {variableName: "type"},
    name: {variableName: "name"}
  };
  public screenNameOptions: Observable<Set<ElementScreenName>>;
  public screenNames: Set<ElementScreenName>;
  public isElementChanged: Boolean;
  private byPassDuplicateCheck: boolean;


  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private elementService: ElementService,
    private elementScreenNameService: ElementScreenNameService,
    private dialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
    this.screenNameOptions = new Observable<Set<ElementScreenName>>();
    this.screenNames = new Set<ElementScreenName>();
  }

  ngOnInit(): void {
    this.fetchScreenNames();
    if (Boolean(this.uiId)) {
      this.getElement();
    }
  }

  get creationDuplicateQuery(): string {
    let element = new Element();
    element.locatorValue = this.element.locatorValue;
    return this.byPassSpecialCharacters('workspaceVersionId:' + this.versionId + ',locatorType:' + this.element?.locatorType +
      ',locatorValue:' + this.element?.locatorValueWithSpecialCharacters +
      (this.element?.screenNameId ? ',screenNameId:' + this.element?.screenNameId : ''));
  }

  public setScreenName(screenName: any, query?, create?, update?, addToList?) {

    if (screenName?.id != null) {
      this.element.screenNameObj.name = screenName.name;
      this.element.screenNameId = screenName.id;
      this.elementForm.get('screen_name').setValue(this.element.screenNameObj.name);
    } else {
      this.createScreenName(screenName.name).subscribe(screenNameBean => {
        this.element.screenNameObj.name = screenNameBean.name;
        this.element.screenNameId = screenNameBean.id;
        this.elementForm.get('screen_name').setValue(this.element.screenNameObj.name);
        if (Boolean(query)) {
          query += ',screenNameId:' + this.element.screenNameId;
          this.fetchDuplicateLocators(query, create, update, addToList);
        }
      })
    }

  }

  private fetchScreenNames() {
    this.elementScreenNameService.findAll('workspaceVersionId:' + this.versionId).subscribe(res => {
      res.content.forEach(screenName => {
        this.screenNames.add(screenName);
      })
      this.screenNameOptions = of(this.screenNames)

    })
  }

  public filterData(target: any) {
    let name: String = target.value;
    this.element.screenNameObj.name = name;
    this.isElementChanged = true;
    if (!name.length) {
      this.element.screenNameId = null;
    }
    let arrSet: Set<ElementScreenName> = new Set<ElementScreenName>();
    this.elementScreenNameService.findAll('workspaceVersionId:' + this.versionId + (name?.length > 0 ? ",name~" + name : '')).subscribe(res => {
      res.content.forEach(screenName => {
        arrSet.add(screenName);
      })
      this.screenNameOptions = of(arrSet);
      if (res.empty) {
        this.element.screenNameId = null;
      }
    })
  }

  public getElement() {
    this.elementService.show(this.uiId).subscribe(
      (res: Element) => {
        this.selectElement = res;
        this.selectedElementEventEmitter.emit(res);
        this.fetchedElement = res;
        this.initElement();
      }
    )
  }

  public backToListView() {
    this.element = null;
  }

  public editElement(index: number) {
    this.element = Object.assign({}, this.elements[index]);
    this.editedIndex = index;
  }

  public removeFromList(index: number) {
    const dialogRef = this.dialog.open(ConfirmationModalComponent, {
      width: '450px',
      data: {
        description: this.translate.instant("element.delete.confirmation.message")
      },
      panelClass: ['matDialog', 'delete-confirm']
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.elements.splice(index, 1);
    });
  }

  public saveElements() {
    this.elements.forEach((element: Element, $index) => {
      if (!element.saved && !element.errors) {
        this.formSubmitted = true;
        if (element.name.match('^[a-zA-Z0-9_\\\- ]+$')) {
          element.saving = true;
          this.createScreenName(element.screenNameObj.name.toString()).subscribe((screenName) => {
            element.screenNameId = screenName.id;
            this.elementService.create(element).subscribe(() => {
              element.saved = true;
              element.saving = false;
              element.errors = null;
            }, (err) => {
              element.errors = err;
              element.saving = false;
            });
          })
        } else {
          this.elements[$index].errors = ["specialCharacter"];
        }
      }
    });
  }

  public initElement() {
    this.setTimer(1800);
    this.element = new Element();
    this.element.mobileElementRect = Object.assign(new MobileElementRect(), this.inspectedElement);
    this.element.mobileElementRect
      .mobileElement = this.inspectedElement ? this.inspectedElement.mobileElement : new MobileElement();
    this.element.workspaceVersionId = this.versionId;

    if ((!this.isEdit) || (this.isEdit && this.element != this.fetchedElement)) {
      this.element.locatorType = (this.element.mobileElementRect.mobileElement.accessibilityId) ?
        ElementLocatorType.accessibility_id : (this.element.mobileElementRect.mobileElement.id ?
          ElementLocatorType.id_value : ElementLocatorType.xpath);
    } else {
      this.element.locatorType = this.selectElement.locatorType;
    }

    this.element.locatorValue = this.element.mobileElementRect
      .mobileElement[this.locatorTypes[this.element.locatorType].variableName];

    this.element.createdType = ElementCreateType.MOBILE_INSPECTOR;

    if (this.isEdit && (this.selectElement != null && this.selectElement != undefined)) {
      let screenName: ElementScreenName = new ElementScreenName();
      screenName.name = this.selectElement?.screenNameObj.name;
      screenName.workspaceVersionId = this.element.workspaceVersionId;
      this.element.screenNameObj = screenName;
      this.element.screenNameObj = screenName;
      this.element.screenNameId = screenName.id;
      this.element.name = this.selectElement?.name;
      if (!this.element.locatorValue) {
        this.element.locatorType = ElementLocatorType[this.selectElement?.locatorType];
        this.element.locatorValue = this.selectElement?.locatorValue;
      }
    }

    this.elementForm = new FormGroup({
      name: new FormControl(this.element.name, [Validators.required, Validators.minLength(4), Validators.maxLength(250),
        Validators.pattern('[a-zA-Z0-9_\\\- ]+$'), this.isNameDuplicate(), this.noWhitespaceValidator]),
      screen_name: new FormControl(this.element.screenNameObj?.name, [Validators.required,
        Validators.minLength(4)]),
      definition: new FormControl(this.element.locatorValue, [Validators.required]),
      locatorType: new FormControl(this.element.locatorType, [Validators.required])
    });
    this.formSubmitted = false;
  }

  private isNameDuplicate(): ValidatorFn {
    return (c: AbstractControl): { [key: string]: boolean } | null => {
      let alreadyExistsInArray;
      if (this.uiId) {
        alreadyExistsInArray = false;
      } else {
        alreadyExistsInArray = this.elements.find((item, $index) => {
          let editedId = ($index == this.editedIndex);
          return (item.name == this.elementForm?.get('name').value && !editedId);
        });
      }
      return alreadyExistsInArray ? {duplicate: true} : null;
    }
  }

  private handleSave(element: Element, isCreate: boolean) {
    this.returnResponse = element;
    if (!isCreate) {
      let summaryPopupOpen: StepSummaryComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof StepSummaryComponent)?.componentInstance;
      if (summaryPopupOpen) summaryPopupOpen.updateElement(element.name);
    }
    this.handleElementUpdateEventEmitter.emit({err: false, isCreate: isCreate});
  }

  private handleError(err: any, isCreate: boolean) {
    this.handleElementUpdateEventEmitter.emit({err: err, isCreate: isCreate});
  }

  private setTimer(number) {
    let recorderDialog: MobileRecordingComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
    recorderDialog.setTimer(number)
  }

  ngOnDestroy() {
    this.elementEventEmitter.emit(this.element);
    this.elementsEventEmitter.emit(this.elements);
    this.elementFormEventEmitter.emit(this.elementForm);
  }

  private addToListAfterDuplicateValidation() {
    if (this.editedIndex == -1) {
      this.elements.push(this.element);
    } else {
      this.element.errors = null;
      this.elements[this.editedIndex] = Object.assign(new Element(), this.element);
      this.editedIndex = -1;
    }
    this.element = null;
    this.formSubmitted = false;
  }

  private updateElementAfterDuplicateValidation() {
    this.elementService.update(this.uiId, this.element).subscribe(
      (res) => this.handleSave(res, false), (err) => this.handleError(err, false))
  }

  private createElementAfterDuplicateValidation() {
    this.elementService.create(this.element).subscribe(
      (res) => this.handleSave(res, true), (err) => this.handleError(err, true));
  }

  private openDuplicateLocatorWarning(res: Element[]) {
    let elementType = this.translate.instant('element.locator_type.' + this.element.locatorType);
    let description = this.uiId ?
      this.translate.instant('element.update_confirmation_with_screen_name.description',
        {
          elementType: elementType, definition: this.element.locatorValue,
          screenName: this.element.screenNameObj.name
        }) :
      this.translate.instant('element.create_confirmation_with_screen_name.description',
        {
          elementType: elementType, definition: this.element.locatorValue,
          screenName: this.element.screenNameObj.name
        });
    const dialogRef = this.dialog.open(DuplicateLocatorWarningComponent, {
      width: '568px',
      height: 'auto',
      data: {
        description: description,
        elements: res,
        isRecorder: false,
        isUpdate: Boolean(this.uiId)
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe(result => {
        if (result) {
          if (this.uiId)
            this.updateElementAfterDuplicateValidation()
          else {
            (Boolean(this.isEdit)) ? this.createElementAfterDuplicateValidation() : this.addToListAfterDuplicateValidation()
          }
        }
      });
  }

  public createElement(byPassDuplicateCheck?: boolean) {
    this.byPassDuplicateCheck = Boolean(byPassDuplicateCheck);
    this.validateAndSave(true);
  }

  public updateElement(byPassDuplicateCheck?: boolean) {
    this.byPassDuplicateCheck = Boolean(byPassDuplicateCheck);
    this.validateAndSave(false, true);
  }

  public addToList(byPassDuplicateCheck?: boolean) {
    this.byPassDuplicateCheck = Boolean(byPassDuplicateCheck);
    this.validateAndSave(false, false, true);
  }

  private validateAndSave(create, update ?:boolean , addToList ?:boolean) {
    let query = this.creationDuplicateQuery + (update ? ',id!' + this.uiId : '');
    this.formSubmitted = true;
    if (this.elementForm.invalid) return;
    let screenNameBean: ElementScreenName = new ElementScreenName();
    screenNameBean.name = this.elementForm.get('screen_name').value;
    screenNameBean.workspaceVersionId = this.element.workspaceVersionId;
    if (Boolean(this.element.screenNameId) || addToList)
      this.fetchDuplicateLocators(query, create, update, addToList);
    else
      this.setScreenName(screenNameBean, query, create, update, addToList);
  }

  private fetchDuplicateLocators(query, create, update, addToList) {
    if(this.byPassDuplicateCheck){
      this.save(create, update, addToList);
      return;
    }
    this.elementService.findAll(query).subscribe(res => {
      if (res?.content?.length) {
        this.openDuplicateLocatorWarning(res.content);
      } else {
        this.save(create, update, addToList);      }
    });
  }

  public createScreenName(screenName: string): Observable<ElementScreenName> {
    let screenNameBean: ElementScreenName = new ElementScreenName();
    screenNameBean.name = screenName;
    screenNameBean.workspaceVersionId = this.versionId;
    return this.elementScreenNameService.create(screenNameBean);
  }

  private save(create, update, addToList){
    if(create){
      this.createElementAfterDuplicateValidation();
    } else if(update){
      this.createElementAfterDuplicateValidation();
    } else if(addToList){
      this.addToListAfterDuplicateValidation();
    }
  }
}
