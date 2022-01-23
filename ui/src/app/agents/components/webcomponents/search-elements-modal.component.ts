import {Component, EventEmitter, Inject, OnInit} from '@angular/core';
import {ElementLocatorType} from "../../../enums/element-locator-type.enum";
import {FormControl, Validators} from '@angular/forms';
import {DevicesService} from "../../services/devices.service";
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MobileElement} from "../../models/mobile-element.model";
import {collapse} from "../../../shared/animations/animations";
import {Platform} from "../../../enums/platform.enum";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-search-elements-modal',
  templateUrl: './search-elements-modal.component.html',
  animations: [collapse]
})
export class SearchElementsModalComponent extends BaseComponent implements OnInit {
  public availableLocatorTypes = [
    ElementLocatorType.accessibility_id,
    ElementLocatorType.id_value,
    ElementLocatorType.xpath,
    ElementLocatorType.class_name,
    ElementLocatorType.name
  ]
  public byValueControl = new FormControl('', [Validators.required]);
  public findByTypeControl = new FormControl(ElementLocatorType.xpath);
  public dataControl = new FormControl('', [Validators.required]);
  public byValue = '';
  public mobileElements: MobileElement[] = [];
  public idCopied: boolean;
  public selectedType = ElementLocatorType.xpath;
  public enteredData = "";
  public isFetching = false;
  public noElementsFound: boolean = false;
  public ids: any[]= [];
  public selectedElement: MobileElement;
  public onSelect = new EventEmitter();
  public onAction = new EventEmitter();
  public devicesService: DevicesService;
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private matDialog: MatDialogRef<SearchElementsModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{sessionId: any, platform: Platform, testsigmaAgentEnabled: boolean},
    private localDeviceService: DevicesService,
    //private cloudDeviceService: CloudDevicesService
    ) {
    super(authGuard, notificationsService, translate, toastrService);
    this.devicesService = this.data.testsigmaAgentEnabled? localDeviceService : localDeviceService;//cloudDeviceService;
  }

  ngOnInit(): void {
    if(this.data.platform == Platform.iOS){
      this.availableLocatorTypes.push(ElementLocatorType.name)
    }
  }

  searchElement() {
    this.isFetching = true;
    this.devicesService.findElements(this.data.sessionId, this.data.platform,this.selectedType, this.byValueControl.value).subscribe(
      res => {
        this.isFetching = false;
        this.mobileElements = res;
        this.noElementsFound = this.mobileElements.length == 0 ;
        if(!this.noElementsFound)
          this.highlightSelectedElement(this.mobileElements[0]);
        this.filterIds();
      }, exception => {
        this.isFetching = false;
        let msg;
        if(exception['error']['error'].includes("Unable to compile"))
          msg = this.translate.instant('search_elements.invalid_caused_search_failure')
        else
          msg = this.translate.instant('search_elements.session_expiry_caused_search_failure')
        this.showAPIError(NotificationType.Error, msg);
      }
    )
  }

  private filterIds() {
    this.ids = [];
    this.mobileElements.forEach(mobileElement => this.ids.push(mobileElement.id));
  }

  showCopiedTooltip() {
    this.idCopied = true;
    setTimeout(() => this.idCopied = false, 10000);
  }

  tap() {
    let selectedElement = this.selectedElement? this.selectedElement : this.mobileElements[0];
    this.onAction.emit({
      mobileElement: selectedElement,
      action: "tap",
      value: this.byValue,
      type: this.selectedType,
      index: this.getIndex(selectedElement)
    })
  }

  clear() {
    let selectedElement = this.selectedElement? this.selectedElement : this.mobileElements[0];
    this.onAction.emit({
      mobileElement: selectedElement,
      action:"clear",
      value: this.byValue,
      type: this.selectedType,
      index: this.getIndex(selectedElement)
    })
  }

  sendKeys() {
    let selectedElement = this.selectedElement? this.selectedElement : this.mobileElements[0];
    this.onAction.emit({
      mobileElement: selectedElement,
      keys: this.enteredData, action:"sendKeys",
      value: this.byValue,
      type: this.selectedType,
      index: this.getIndex(selectedElement)
    })
  }

  highlightSelectedElement(mobileElement: MobileElement) {
    if(this.selectedElement == mobileElement)
      return;
    this.selectedElement = mobileElement;
    this.onSelect.emit(mobileElement);
  }

  textEditable(): boolean {
    let type = this.selectedElement.type;
    return (!(type == 'android.widget.EditText'
      || type == 'android.widget.TextView'
      || type == 'XCUIElementTypeTextField'
      || type == 'XCUIElementTypeSecureTextField'
      || type == 'input'
      || type == null));
  }

  private getIndex(selectedElement) {
    let index;
    if(selectedElement.webViewName != null){
      let webViewElements = this.mobileElements.filter((mobileElement: MobileElement) => {
        return mobileElement.webViewName != null;
      })
      return webViewElements.indexOf(selectedElement);
    } else {
      index = this.mobileElements.indexOf(selectedElement);
    }
    return index;
  }
}
