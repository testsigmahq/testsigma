import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {fabric} from "fabric";
import {MobileElementRect} from "../../models/mobile-element-rect.model";
import {DevicesService} from "../../services/devices.service";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {MobileElement} from "../../models/mobile-element.model";
import {Position} from "../../models/position.model";
import {SearchElementsModalComponent} from "./search-elements-modal.component";
import {ScreenDimensions} from "../../models/screen-dimensions.model";
import {MobileRecordingComponent} from "./mobile-recording.component";
import {MatDialog} from "@angular/material/dialog";
import {Platform} from "../../../enums/platform.enum";
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {AgentService} from "../../services/agent.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {JsonPipe} from "@angular/common";
import {MirroringData} from "../../models/mirroring-data.model";
import {Element} from "../../../models/element.model";
import {ElementLocatorType} from "../../../enums/element-locator-type.enum";
import {SendKeysRequest} from 'app/agents/models/send-keys-request.model';
import {Image} from "fabric/fabric-impl";
import {MobileRecorderEventService} from "../../../services/mobile-recorder-event.service";

@Component({
  selector: 'app-mirroring-container',
  templateUrl: './mirroring-container.component.html',
  providers: [JsonPipe]
})
export class MirroringContainerComponent extends BaseComponent implements OnInit {
  @ViewChild('mirroringContainer') mirroringContainer: ElementRef;
  @Input() public dataSource!: any;
  @Output() public dataSourceChange = new EventEmitter<any>();
  @Input()public loading!: boolean;
  @Output() loadingChange = new EventEmitter<boolean>();
  @Input() public loadingActions: boolean;
  @Input() public currentPageSource: MobileElement;
  @Input() public isIosNative: boolean;
  @Input() public isEdit: boolean;
  @Input() public uiId: number;
  @Input() public data: MirroringData;
  @Input() public sessionId: String;
  @Input() public mirroring: boolean;
  @Input() public selectedElement: Element;
  @Input() public isPauseRecord: boolean;
  @Output() saveTapOnDeviceStep: EventEmitter<Position> = new EventEmitter<Position>();
  @Output() saveChangeOrientationStep: EventEmitter<Boolean> = new EventEmitter<Boolean>();
  @Output() saveNavigateBackStep: EventEmitter<void> = new EventEmitter<void>();
  @Output() saveTapStep: EventEmitter<MobileElement> = new EventEmitter<MobileElement>();
  @Output() saveEnterStep = new EventEmitter<SendKeysRequest>();
  @Output() saveClearStep = new EventEmitter<MobileElement>();
  @Output() updateRecordedElement: EventEmitter<void> = new EventEmitter<void>();
  private devicesService:  DevicesService; //CloudDevicesService
  public inspectedElement: MobileElementRect = null;
  public fabricCanvas: fabric.Canvas;
  public actionType: String;
  public screenScaledInitially: boolean;
  private platform: Platform;
  private orientationConflict: boolean;
  public viewType: string = 'NATIVE_APP';
  public canvasHeight: number;
  public canvasWidth: number;
  private screenWidth: number;
  private screenHeight: number;
  public screenOriginalHeight: number;
  public screenOriginalWidth: number;
  private deviceDimensions = {
    'portrait': {width: 252, height: 448},
    'landscape': {width: 448, height: 252},
  };
  private locatorTypes = {
    accessibility_id: {variableName: "accessibilityId"},
    id_value: {variableName: "id"},
    xpath: {variableName: "xpath"},
    class_name: {variableName: "type"},
    name: {variableName: "name"}
  };
  private currentImage: Image;
  private hoveredElement: MobileElementRect;
  private xpathOptimized: boolean = false;
  public optimisingXpath: boolean = false;
  get isNativeAppEnabled() {
    return this.viewType == 'NATIVE_APP';
  }
  get hasWebContexts() {
    return this.currentPageSource?.contextNames?.find((contextName: String) => contextName.indexOf("WEBVIEW") > -1);
  }

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private agentService: AgentService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private JsonPipe: JsonPipe,
    private localDeviceService: DevicesService,
    public mobileRecorderEventService: MobileRecorderEventService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  //private userService: UserService,
  //private cloudDeviceService: CloudDevicesService

  ngOnInit(): void {
    this.uiId = this.data.uiId
    this.devicesService = this.data.testsigmaAgentEnabled ? this.localDeviceService : this.localDeviceService;//this.cloudDeviceService;
    this.platform = this.isIosNative? Platform.iOS : Platform.Android;
    if (this.data?.device) {
      this.screenWidth = this.data.device.screenWidth;
      this.screenHeight = this.data.device.screenHeight;
    }
  }

  public switchToMirroringMode() {
    this.mirroring = true;
    this.removeInspectionElements();
    // this.devicesService.deleteSession(this.sessionId).subscribe();
    // this.sessionId = null;
  }

  public switchToActionMode(actionType: String) {
    this.actionType = actionType;
    this.removeInspectionElements();
    this.clearSelection();
  }

  public removeInspectionElements() {
    this.fabricCanvas?.getObjects()?.forEach((element, index) => {
      if (index) {
        this.fabricCanvas.remove(element);
      }
    });
    this.inspectedElement = null;
  }

  public navigateBack() {
    if (this.data.recording) {
      this.devicesService.sessionNavigateBack(this.sessionId).subscribe(
        () => {
          if (this.data.recording) {
            if(this.data.isStepRecord) this.saveNavigateBackStep.emit()
            this.handleActionSuccess()
          }
        },
        (err) => this.handleActionFailure(err, "mobile_recorder.notification.navigate_back.failure", "Navigate back", "/#8--failed-to-navigate-back")
      );
    } else {
      this.devicesService.navigateBack(this.data.device).subscribe(
        () => { if (this.data.recording) this.handleActionSuccess()},
        (err) => this.handleActionFailure(err, "mobile_recorder.notification.navigate_back.failure", "Navigate back", "/#8--failed-to-navigate-back")
      );
    }
  }

  public goToHome() {
    this.beforeAction();
    this.devicesService.goToHome(this.sessionId).subscribe(
      () => this.handleActionSuccess(),
      (err) => this.handleActionFailure(err, "mobile_recorder.notification.go_to_home.failure", "Navigate to Home", "#11--failed-to-navigate-to-home")
    );
  }

  public renderCurrentScreenshot() {
    this.recorderDialog.renderCurrentScreenshot();
  }

  public changeOrientation() {
    this.beforeAction()
    this.devicesService.changeOrientation(this.sessionId).subscribe({
      next: () => {
        this.devicesService.getScreenDimensions(this.sessionId).subscribe((screenDimensions: ScreenDimensions) => {
          this.screenOriginalWidth = screenDimensions.screenWidth;
          this.screenOriginalHeight = screenDimensions.screenHeight;
        })
        let width = this.screenWidth;
        let height = this.screenHeight;
        this.screenHeight = width;
        this.screenWidth = height;
        this.mobileRecorderEventService.isLandscapeMode = !this.mobileRecorderEventService?.isLandscapeMode;
        if(this.data.isStepRecord) this.saveChangeOrientationStep.emit(this.mobileRecorderEventService?.isLandscapeMode);
        this.handleActionSuccess();
      },
      error: (err) =>
        this.handleActionFailure(err, "mobile_recorder.notification.change_orientation_failed", "Change Orientation", "#10--failed-to-change-orientation")
    });
  }

  public _onImageLoad(img: HTMLImageElement) {
    let oImg = new fabric.Image(img);
    const mode = this.mobileRecorderEventService?.isLandscapeMode ? 'landscape' : 'portrait';
    this.canvasWidth = this.deviceDimensions[mode].width;
    this.canvasHeight = this.deviceDimensions[mode].height;
    if (this.mobileRecorderEventService?.isLandscapeMode == (oImg.width < oImg.height)) {
      this.orientationConflict = true;
      this.canvasHeight = this.canvasHeight * (oImg.height / oImg.width)
    }
    if (!this.isIosNative) {
      this.screenWidth = oImg.width;
      this.screenHeight = oImg.height;
    }
    let scaleXFactor = this.canvasWidth / oImg.width;
    let scaleYFactor = this.canvasHeight / oImg.height;
    let fullscreenScalingFactor = this.getFullScreenScalingFactor(oImg, scaleXFactor);
    oImg._set('scaleX', scaleXFactor * fullscreenScalingFactor);
    oImg._set('scaleY', scaleXFactor * fullscreenScalingFactor);
    oImg.set('selectable', false);
    if (this.mobileRecorderEventService?.isLandscapeMode && oImg.width < oImg.height) {
      // Pratheepv : Reason for not fully relaying on landscape value is some times image from browserstack is coming in portrait mode
      oImg.rotate(90);
      oImg.center();
      oImg._set('scaleX', (this.canvasHeight / oImg.width) * fullscreenScalingFactor);
      oImg._set('scaleY', (this.canvasWidth / oImg.height) * fullscreenScalingFactor);
      oImg.set('top', 0);
      oImg.set('left', this.canvasWidth * fullscreenScalingFactor);
    }
    this.canvasHeight *= fullscreenScalingFactor;
    this.canvasWidth *= fullscreenScalingFactor;
    this.fabricCanvas.setWidth(this.canvasWidth);
    this.fabricCanvas.setHeight(this.canvasHeight);
    this.fabricCanvas.clear();
    this.fabricCanvas.add(oImg);
    this.currentImage = oImg
    this.screenScaledInitially = true;
    this.addTapAndSwipeListeners();
    if (!this.mirroring && this.sessionId) {
      // TODO: Need to add debounce [Pratheepv]
      this.recorderDialog.getCurrentPageSource();
    }
    this.loading = false;
    this.loadingChange.emit(this.loading)
    if (this.isEdit && this.uiId) {
      this.inspectedElement = new MobileElementRect();
      this.inspectedElement.mobileElement = new MobileElement();
      this.inspectedElement.mobileElement.contentDesc = "";
      this.inspectedElement.mobileElement[this.locatorTypes[this.selectedElement?.locatorType]?.variableName]
        = this.uiId ? this.selectedElement?.locatorValue : "";
      this.selectElement(this.inspectedElement);
    }
  }

  public openSearchDialog() {
    let pageSourceElement = null;
    const dialogRef = this.dialog.open(SearchElementsModalComponent, {
      data: {
        sessionId: this.sessionId,
        platform: this.platform,
        testsigmaAgentEnabled: this.data.testsigmaAgentEnabled
      },
      panelClass: ['mat-dialog', 'rds-none'],
      width: '700px',
      disableClose: true
    });
    dialogRef.componentInstance.onSelect.subscribe((mobileElement) => {
      pageSourceElement = this.findElementByCoOrdinates(mobileElement.x1, mobileElement.x2, mobileElement.y1, mobileElement.y2, this.dataSource)
      if (pageSourceElement != undefined) {
        this.highlightOnSelection(pageSourceElement);
      } else {
        this.highlightOnSelection(mobileElement);
      }
    });
    dialogRef.componentInstance.onAction.subscribe((result) => {
      if (result?.action == "tap")
        this.searchAndTap(result.type, result.value, result.index, result.mobileElement);
      else if (result?.action == "sendKeys") {
        this.searchAndSendKeys(result.type, result.value, result.index, result.keys, result.mobileElement);
      } else if (result?.action == "clear") {
        this.searchAndClear(result.type, result.value, result.index, result.mobileElement);
      }
    });
  }

  public highlightOnSelection(mobileElement: MobileElement) {
    this.removeInspectionElements();
    this.clearSelection();
    this.drawElement(mobileElement, true);
    this.drawElements(this.currentPageSource);
  }

  private findElementByCoOrdinates(x1, x2, y1, y2, elements) {
    for (let element of elements) {
      if (element.x1 == x1 && element.x2 == x2 && element.y1 == y1 && element.y2 == y2)
        return element;
      else if (element.childElements) {
        let foundElement = this.findElementByCoOrdinates(x1, x2, y1, y2, element.childElements);
        if (foundElement)
          return foundElement;
      }
    }
  }

  public switchViewMode(viewType: string) {
    if (this.viewType == viewType) return;
    this.removeInspectionElements();
    this.clearSelection();
    this.viewType = viewType;
    this.fabricCanvas.renderAll();
    this.drawElements(this.currentPageSource);
    this.dataSource = [JSON.parse(this.JsonPipe.transform(this.currentPageSource))];
    this.dataSourceChange.emit(this.dataSource);
  }

  public drawElements(source: MobileElement) {
    let allElements = [];
    this.flattenElements(source, allElements);
    if (allElements.length > 0 && allElements[1].hasWebViewChild)
      source.hasWebViewChild = true;
    allElements.sort((a, b) => this.sortByArea(a, b)).reverse().forEach((element) => {
      if((this.isNativeAppEnabled && !element?.webViewName) || (!this.isNativeAppEnabled && element?.hasWebViewChild))
        this.drawElement(element);
    });
    this.fabricCanvas.renderAll();
  }

  private drawElement(element: MobileElement,toSelect?:boolean, toHighlight?: boolean) {
    if (element.x1 == null) {
      return;
    }

    let elementDimensions = this.boundToCanvasDimensions(element);

    let mobileElementRect = new MobileElementRect({
      left: elementDimensions.x,
      top: elementDimensions.y,
      height: elementDimensions.height,
      width: elementDimensions.width,
      fill: "#fff",
      opacity:   0,
    });

    mobileElementRect.set('selectable', false);
    mobileElementRect.set('mobileElement', element);
    this.fabricCanvas.add(mobileElementRect);
    if(Boolean(toSelect))
      this.selectElement(mobileElementRect, true);
    if(Boolean(toHighlight)) {
      this.highlightOnHover(mobileElementRect);
      this.hoveredElement = mobileElementRect;
    }
  }

  private boundToCanvasDimensions(element: MobileElement) {
    let scalingXFactor = this.canvasWidth / this.screenWidth;
    let scalingYFactor = this.orientationConflict ? this.canvasHeight / this.screenHeight : scalingXFactor;
    return {
      x: element.x1.valueOf() * scalingXFactor,
      y: element.y1.valueOf() * scalingYFactor,
      height: (element.y2.valueOf() - element.y1.valueOf()) * scalingYFactor,
      width: (element.x2.valueOf() - element.x1.valueOf()) * scalingXFactor
    }
  }

  private addHoverEventsToCanvas() {
    this.attachMouseOverEvent();
    this.attachMouseUpEvent();
    this.attachMouseOutEvent();
  }

  private attachMouseOverEvent() {
    this.fabricCanvas.on('mouse:over', (options: any) => {
      let mobileElementRect = <MobileElementRect>options.target;
      this.highlightOnHover(mobileElementRect);
    });
  }

  private highlightOnHover(mobileElementRect:MobileElementRect,){
    if (mobileElementRect) {
      if (mobileElementRect._originalElement) {
        return;
      }
      mobileElementRect.set('fill', '#f5fb1d');
      mobileElementRect.set('opacity', 0.2);
      this.fabricCanvas.renderAll();
    }
  }

  public mouseInFromAppSource(element:MobileElement){
    if (element.x1 == null || element.uuid == this.inspectedElement?.mobileElement?.uuid) return;
    if(this.hoveredElement && this.inspectedElement?.mobileElement?.uuid != this.hoveredElement.mobileElement?.uuid)
      this.fabricCanvas.remove(this.hoveredElement);
    this.drawElement(element, false, true);
  }

  public mouseOutFromAppSource(){
    if(this.hoveredElement && this.inspectedElement?.mobileElement?.uuid != this.hoveredElement.mobileElement?.uuid)
      this.fabricCanvas.remove(this.hoveredElement);
  }

  private attachMouseUpEvent() {
    this.fabricCanvas.on('mouse:up', (options) => {
      this.selectElement(<MobileElementRect>options.target);
    });
  }

  private attachMouseOutEvent() {
    this.fabricCanvas.on('mouse:out', (options: any) => {
      let mobileElementRect = options.target;
      if (mobileElementRect) {
        if (mobileElementRect._originalElement) {
          return;
        }
        if (mobileElementRect.get('elementSelected')) {
          mobileElementRect.set('opacity', 0.2);
          mobileElementRect.set('fill', '#6467f7');
        } else {
          mobileElementRect.set('opacity', 0);
          mobileElementRect.set('fill', '#ffffff');
        }
        this.fabricCanvas.renderAll();
      }
    });
  }

  private sortByArea(element1: MobileElement, element2: MobileElement) {
    let element1Dimensions = this.boundToCanvasDimensions(element1);
    let element2Dimensions = this.boundToCanvasDimensions(element2);
    let element1Area = element1Dimensions.width * element1Dimensions.height;
    let element2Area = element2Dimensions.width * element2Dimensions.height;
    if (element1Area == element2Area) {
      return element2.depth.valueOf() - element1.depth.valueOf();
    }
    return element1Area - element2Area;
  };

  public switchToInspectMode() {
    this.mirroring = false;
    if (this.actionType) {
      this.actionType = null;
      this.renderCurrentScreenshot();
      this.renderCurrentScreenshot();
    }
    if (!this.data.recording)
      this.recorderDialog.startSession();
  }

  private addTapAndSwipeListeners() {
    let _this = this;
    this.fabricCanvas.getObjects().forEach((object: fabric.Object) => {
      let mouseDownPosition;
      object.on('mousedown', function (options: fabric.IEvent) {
        mouseDownPosition = this.getLocalPointer(options.e);
      });
      object.on('mouseup', function (options: fabric.IEvent) {
        let mouseUpPosition = this.getLocalPointer(options.e);
        if ((mouseDownPosition && mouseUpPosition) && (mouseDownPosition.x !== mouseUpPosition.x || mouseDownPosition.y !== mouseUpPosition.y)) {
          _this.swipeOnDevice([
            _this.convertToMobilePosition(mouseDownPosition),
            _this.convertToMobilePosition(mouseUpPosition)
          ]);
        } else {
          _this.tapOnDevice(_this.convertToMobilePosition(mouseUpPosition));
        }
      });
    });

  }

  private swipeOnDevice(tapPoints: Position[]) {
    if (this.data.recording) {
      this.devicesService.sessionSwipe(this.sessionId, tapPoints).subscribe({
        next: () => this.handleActionSuccess(),
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.swipe.failure", "Swipe", "/#7--failed-to-swipe")
      });
    } else {
      this.devicesService.deviceSwipe(this.data.device, tapPoints).subscribe({
        next: () => this.handleActionSuccess(),
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.swipe.failure", "Swipe", "/#7--failed-to-swipe")
      });
    }
  };

  private tapOnDevice(tapPoint: Position) {
    if (this.data.recording) {
      if (tapPoint.x > this.screenOriginalWidth || tapPoint.y > this.screenOriginalHeight) {
        this.showNotification(NotificationType.Error, this.translate.instant("mobile_recorder.notification.tap.out_of_bound.failure", {
          screenWidth: this.screenOriginalWidth,
          screenHeight: this.screenOriginalHeight
        }));
        this.renderCurrentScreenshot();
        this.renderCurrentScreenshot();
        return;
      }
      this.devicesService.sessionTap(this.sessionId, tapPoint).subscribe({
        next: () => {
          if (this.data.recording) {
            if(this.data.isStepRecord) this.saveTapOnDeviceStep.emit(tapPoint);
            this.handleActionSuccess();
          }
        },
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.tap.failure", "Tap","/#6--failed-to-tap-on-the-element")
      });
    } else {
      this.devicesService.deviceTap(this.data.device, tapPoint).subscribe({
        next: () => {
          if (this.data.recording) this.handleActionSuccess();
        },
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.tap.failure", "Tap","/#6--failed-to-tap-on-the-element")
      });
    }
  }

  public clearSelection(){
    if (this.inspectedElement) {
      this.inspectedElement.set('fill', '#fff');
      this.inspectedElement.set('opacity', 0);
      this.inspectedElement.set('elementSelected', false);
      this.fabricCanvas.renderAll();
    }
    this.inspectedElement = null;
    this.recorderDialog.element = null
  }

  private convertToMobilePosition(position: any) {
    let positionWidth = this.screenWidth;
    let positionHeight = this.screenHeight;
    let xPosition = position.x * (positionWidth / this.canvasWidth);
    let yPosition = position.y * (positionHeight / this.canvasHeight);
    if (this.mobileRecorderEventService?.isLandscapeMode && !this.orientationConflict) {
      xPosition = position.x * (positionHeight / this.canvasHeight);
      yPosition = position.y * (positionWidth / this.canvasWidth);
    }
    let mobilePosition: Position = new Position();
    mobilePosition.x = xPosition;
    mobilePosition.y = yPosition;
    if (this.mobileRecorderEventService?.isLandscapeMode && this.orientationConflict) {
      mobilePosition.x = yPosition;
      mobilePosition.y = xPosition;
    }
    if(this.orientationConflict ) {
      mobilePosition.x = <number>mobilePosition.x - this.screenWidth;
      if(mobilePosition.x < 0)
        mobilePosition.x = <number>mobilePosition.x*-1;
    }
    return mobilePosition;
  }

  private getFullScreenScalingFactor( oImg, scaleXFactor): number {
    if (!(this.mobileRecorderEventService?.isLandscapeMode && oImg.width < oImg.height)) {
      this.canvasHeight = oImg.height * scaleXFactor;
    }
    let initialScalingFactor = (this.mirroringContainer.nativeElement.clientWidth - 73) / this.deviceDimensions.portrait.width;
    if (this.canvasFitsLayout(initialScalingFactor)) {
      return initialScalingFactor;
    } else if (this.canvasHeightExceedsLayout(initialScalingFactor) && !this.canvasWidthExceedsLayout(initialScalingFactor)) {
      return this.fitHeightToLayout();
    } else if (this.canvasWidthExceedsLayout(initialScalingFactor) && !this.canvasHeightExceedsLayout(initialScalingFactor)) {
      return this.fitWidthToLayout();
    } else if (this.canvasWidthExceedsLayout(initialScalingFactor) && this.canvasHeightExceedsLayout(initialScalingFactor)) {
      if (this.canvasFitsLayout(this.fitHeightToLayout())) {
        return this.fitHeightToLayout();
      } else if (this.canvasFitsLayout(this.fitWidthToLayout())) {
        return this.fitWidthToLayout();
      } else {
        console.log("not scaled")
        return 1;
      }
    }
  }

  private canvasWidthExceedsLayout = (initialScalingFactor) =>
    (this.canvasWidth * initialScalingFactor) > (this.mirroringContainer.nativeElement.clientWidth - (73 - (this.mobileRecorderEventService?.isLandscapeMode ? 42 : 0)));

  public canvasHeightExceedsLayout = (initialScalingFactor) =>
    (this.canvasHeight * initialScalingFactor) > (this.mirroringContainer.nativeElement.clientHeight - (30 + (this.mobileRecorderEventService?.isLandscapeMode ? 72 : 0)));

  private canvasFitsLayout = (initialScalingFactor) =>
    !this.canvasWidthExceedsLayout(initialScalingFactor) && !this.canvasHeightExceedsLayout(initialScalingFactor);

  private fitWidthToLayout = () =>
    (this.mirroringContainer.nativeElement.clientWidth - (73 - (this.mobileRecorderEventService?.isLandscapeMode ? 42 : 0))) / this.canvasWidth;

  public fitHeightToLayout = () =>
    (this.mirroringContainer?.nativeElement.clientHeight - (30 + (this.mobileRecorderEventService?.isLandscapeMode ? 72 : 0))) / this.canvasHeight;

  public createCanvas(screenDimensions?: ScreenDimensions) {
    if(Boolean(screenDimensions)){
      this.screenWidth = screenDimensions.screenWidth;
      this.screenHeight = screenDimensions.screenHeight;
      this.screenOriginalWidth = screenDimensions.screenWidth;
      this.screenOriginalHeight = screenDimensions.screenHeight;
    }
    this.fabricCanvas = new fabric.Canvas('mobile_mirroring_canvas', {
      hoverCursor: 'pointer',
      width: this.deviceDimensions.portrait.width,
      height: this.deviceDimensions.portrait.height
    });
    this.addHoverEventsToCanvas();
  }

  private searchAndTap(locatorType: ElementLocatorType, byValue: string, index: number, mobileElement: MobileElement) {
    this.beforeAction();
    this.devicesService.searchAndTapElement(this.sessionId, this.platform, locatorType, byValue, index, mobileElement.webViewName)
      .subscribe({
        next: () => {
          if(this.data.isStepRecord) this.saveTapStep.emit(mobileElement);
          this.handleActionSuccess();
          this.removeInspectionElements();
          this.clearSelection();
        },
        error: (error) => {
          this.handleActionFailure(error, "mobile_recorder.notification.tap.failure");
          this.inspectedElement = null;
        }
      });
  }

  private searchAndSendKeys(locatorType: ElementLocatorType, byValue: string, index: number, keys: string, mobileElement: MobileElement) {
    this.beforeAction(true);
    this.devicesService.searchAndSendKeys(this.sessionId, this.platform, locatorType, byValue, index, keys, mobileElement.webViewName)
      .subscribe({
        next: () => {
          if(this.data.isStepRecord) {
            let sendKeysRequest = new SendKeysRequest();
            sendKeysRequest.mobileElement = mobileElement;
            sendKeysRequest.keys = keys;
            this.saveEnterStep.emit(sendKeysRequest);
          }
          this.handleActionSuccess()
        },
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.send_keys.failure", "Enter", "/#9--failed-to-enter-data-into-the-element")
      });
  }

  private searchAndClear(locatorType: ElementLocatorType, byValue: string, index: number, mobileElement: MobileElement) {
    this.beforeAction();
    this.devicesService.searchAndClearElement(this.sessionId, this.platform, locatorType, byValue, index, mobileElement.webViewName)
      .subscribe({
        next: () => {
          if(this.data.isStepRecord) this.saveClearStep.emit(mobileElement);
          this.handleActionSuccess()
        },
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.clear.failure","Clear", "/#5--failed-to-clear-the-elements-text")
      });
  }

  private markHasWebViewFlag(element: MobileElement) {
    element.hasWebViewChild = true;
    if (element.parent)
      this.markHasWebViewFlag(element.parent)
  }

  private selectElement(mobileElementRect: MobileElementRect, nonCanvasSelection?: boolean) {
    if (mobileElementRect) {
      if (mobileElementRect._originalElement) {
        return;
      }
      if (this.inspectedElement) {
        this.inspectedElement.set('fill', '#fff');
        this.inspectedElement.set('opacity', 0);
        this.inspectedElement.set('elementSelected', false);
      }
      mobileElementRect.set('opacity', 0.2);
      mobileElementRect.set('fill', Boolean(nonCanvasSelection)? '#6467f7' :'#5bfb1d');
      mobileElementRect.set('elementSelected', true);
      this.inspectedElement = mobileElementRect;
      this.fabricCanvas.renderAll();
    }
    if (!this.isPauseRecord) {
      if (!Boolean(this.data.isStepRecord))
        this.recorderDialog.initElement();
      else
        this.updateRecordedElement.emit()
      this.optimiseXpath();
    }
  }

  private optimiseXpath(){
    if(!this.xpathOptimized){
      this.optimisingXpath = true;
      this.devicesService.findUniqueXpath(this.sessionId, this.platform, this.inspectedElement.mobileElement).subscribe(res =>{
        if(res != ''){
          this.xpathOptimized = true;
          this.inspectedElement.mobileElement.xpath = res;
          if(!Boolean(this.data.isStepRecord))
            this.recorderDialog.initElement();
          else
            this.updateRecordedElement.emit()
        }
        this.optimisingXpath = false;
      })
    } else {
      this.xpathOptimized = false;
      this.optimisingXpath = false;
    }
  }

  private flattenElements(parentElement: MobileElement, allElements: MobileElement[]) {
    let _this = this;
    if (this.isNativeAppEnabled && parentElement.webViewName)
      return
    else if (!this.isNativeAppEnabled && parentElement.webViewName)
      this.markHasWebViewFlag(parentElement)
    if (parentElement.x1 != null) {
      allElements.push(parentElement);
    }

    if (Array.isArray(parentElement.childElements)) {
      parentElement.childElements.forEach(function (childElement) {
        childElement.parent = parentElement;
        _this.flattenElements(childElement, allElements);
        childElement.parent = null;
      });
    }
  }

  private beforeAction(sendKeys?: boolean) {
    let recorderDialog: MobileRecordingComponent = this.recorderDialog;
    if (Boolean(sendKeys)) {
      recorderDialog.loadingActions = true;
      recorderDialog.viewType = "NATIVE_APP";
    } else
      recorderDialog.beforeAction();
  }

  private handleActionFailure(error, messageKey: string, actionType?: string, guideLink?:string) {
    this.recorderDialog.handleActionFailure(error, messageKey, actionType, guideLink);
  }

  private handleActionSuccess() {
    this.recorderDialog.handleActionSuccess();
  }

  private get recorderDialog():MobileRecordingComponent{
    return this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
  }
}
