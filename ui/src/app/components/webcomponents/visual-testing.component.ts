import {Component, Inject, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {StepResultScreenshotComparision} from "../../models/step-result-screenshot-comparision.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {StepResultScreenshotComparisonService} from "../../services/step-result-screenshot-comparison.service";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {fabric} from "fabric";
import {Shape} from "../../models/shape.model";
import {Page} from "../../shared/models/page";
import {TestStepResult} from "../../models/test-step-result.model";
import {TestStepScreenshotService} from "../../services/test-step-screenshot.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-visual-testing',
  templateUrl: './visual-testing.component.html',
  styles: []
})
export class VisualTestingComponent extends BaseComponent implements OnInit {
  public screenshotComparisonId: Number;
  public screenshotComparison: StepResultScreenshotComparision;
  public filteredTestStepResult: Page<TestStepResult>;

  public baseImageCanvas: fabric.Canvas;
  public currentImageCanvas: fabric.Canvas;
  public diffRegions: fabric.Group = new fabric.Group([],{data : 'DiffRegionGroup', selectable: false});
  public canvasWidth: number;
  public canvasHeight: number;
  public scalingFactor: number;
  public showDifferences: boolean;
  public isCombinedView: boolean;
  public ignoreRegionStartPoint: any;
  public ignoreRegionEndPoint: any;
  public hasIgnoredRegions: boolean;
  public isIgnoreRegion: boolean;
  public beforeModifiedObject: any;
  public isShowConfirm: boolean;
  public visualDifferenceForAllSteps: boolean;
  isApproveBaseImage: any;
  public canUpdateIgnoreRegion: boolean;

  private screenshotComparison$: Subscription;
  private subscriptions: Subscription[] = [];
  private baseImageContainerWidth;
  private baseImageContainerHeight;

  constructor(
    @Inject(MAT_DIALOG_DATA) public options: { screenshotComparisonId: Number, filteredTestStepResult: Page<TestStepResult> },
    private matModal: MatDialog,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public testStepScreenshotService: TestStepScreenshotService,
    private screenshotComparisonService: StepResultScreenshotComparisonService) {
    super(authGuard, notificationsService, translate, toastrService)
    this.screenshotComparisonId = this.options.screenshotComparisonId;
    this.filteredTestStepResult = this.options.filteredTestStepResult;

  }

  ngOnInit(): void {
    this.getBaseImageContainerDimention();
    this.fetchScreenshot();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  getBaseImageContainerDimention(){
    const baseImageContainer = document.getElementById("base_image_container");
    this.baseImageContainerWidth = baseImageContainer?.offsetWidth;
    this.baseImageContainerHeight = baseImageContainer?.offsetHeight;
  }

  fetchScreenshot(): void {
    this.screenshotComparison$ = this.screenshotComparisonService.show(this.screenshotComparisonId).subscribe(
      (res: StepResultScreenshotComparision) => {
        this.screenshotComparison = res;
        this.hasIgnoredRegions = false;
        this._renderBaseImage();
        this._renderCurrentImage();
        this.hasIgnoredRegions = !!this.screenshotComparison?.testStepScreenshot?.ignoredCoordinates?.length;
      },
      error => console.error('Failed to get screenshotComparison: '+error)
    );
    this.subscriptions.push(this.screenshotComparison$);
  }

  _renderBaseImage(): void {
    this.canvasWidth = document.getElementById('base_image_container').clientWidth;
    this.canvasHeight = document.getElementById('base_image_container').clientHeight;
    if(this.baseImageCanvas){
      this.baseImageCanvas.clear();
    }else{
      this.baseImageCanvas = new fabric.Canvas('base_image_canvas', {
        hoverCursor: 'pointer',
        width: this.canvasWidth,
        height: this.canvasHeight
      });
    }
    let img = new Image();
    img.onload = () => this._onBaseImageLoad(img);
    img.src = this.screenshotComparison.testStepScreenshot.screenShotURL + '';
  }

  _onBaseImageLoad(img: HTMLImageElement): void {
    let oImg = new fabric.Image(img);
    const canvasDimention = this.calculateAspectRatioFit(oImg.width,oImg.height,this.baseImageContainerWidth,this.baseImageContainerHeight);
    this.canvasWidth = canvasDimention.width;
    this.canvasHeight = canvasDimention.height;
    oImg.scale(canvasDimention.ratio);
    oImg.set('selectable', false);
    this.baseImageCanvas.setWidth(this.canvasWidth);
    this.baseImageCanvas.setHeight(this.canvasHeight);
    this.baseImageCanvas.add(oImg);
    setTimeout(() => {
      this._drawIgnoreRegionsOnCanvas();
      this._drawDifferenceRegionsOnCanvas(true);
      this.baseImageCanvas.renderAll();
    },100);
  }

  _renderCurrentImage(): void {
    if(this.currentImageCanvas){
      this.currentImageCanvas.clear();
    }else{
      this.currentImageCanvas = new fabric.Canvas('current_image_canvas', {
        hoverCursor: 'pointer',
        width: this.canvasWidth,
        height: this.canvasHeight
      });
    }
    let img = new Image();
    img.onload = () => this._onCurrentImageLoad(img);
    img.src = this.screenshotComparison.screenShotURL + '';
  }

  calculateAspectRatioFit(srcWidth, srcHeight, maxWidth, maxHeight){
    const ratio = Math.min(maxWidth / srcWidth, maxHeight / srcHeight)
    return { width: srcWidth*ratio, height: srcHeight*ratio , ratio };
  }

  _onCurrentImageLoad(img: HTMLImageElement): void {
    let oImg = new fabric.Image(img);
    const canvasDimention = this.calculateAspectRatioFit(oImg.width,oImg.height,this.baseImageContainerWidth,this.baseImageContainerHeight);
    this.canvasWidth = canvasDimention.width;
    this.canvasHeight = canvasDimention.height;
    oImg.scale(canvasDimention.ratio);
    oImg.set('selectable', false);
    this.currentImageCanvas.setWidth(this.canvasWidth);
    this.currentImageCanvas.setHeight(this.canvasHeight);
    this.currentImageCanvas.add(oImg);
    setTimeout(() => {
      this._drawDifferenceRegionsOnCanvas(null,true);
      this.currentImageCanvas.renderAll();
    }, 250);
  }

  _drawDifferenceRegionsOnCanvas(addOnlyToBaseImage?: boolean, addOnlyToCurrentImage?: boolean): void {
    let scaleX = this?._coOrdinatesScaling()[0];
    let scaleY = this._coOrdinatesScaling()[1];
    this.screenshotComparison.diffCoordinates.forEach((shape, index) => {
      let rect = new fabric.Rect({
        left: <number>shape.x * scaleX,
        top: <number>shape.y * scaleY,
        height: <number>shape.h * scaleY,
        width: <number>shape.w * scaleX,
        fill: "rgba(255, 0, 0)",
        opacity: 0.4,
        selectable: false,
        data: 'diffRegion'+index,
        lockMovementX: true,
        lockMovementY: true,
        lockRotation: true,
        lockUniScaling: true,
        lockScalingX: true,
        lockScalingY: true,
        lockSkewingX: true,
        lockSkewingY: true,
        lockScalingFlip: true
      });
      if(addOnlyToBaseImage){
        this.baseImageCanvas.add(rect);
      } else if(addOnlyToCurrentImage){
        this.currentImageCanvas.add(rect);
      } else if(!addOnlyToBaseImage && !addOnlyToCurrentImage){
        this.baseImageCanvas.add(rect);
        this.currentImageCanvas.add(rect);
      }
      this.showDifferences = true;
    });
  }

  _removeDifferenceRegionsFromCanvas(): void {
    this.currentImageCanvas.getObjects().forEach(element => {
      if (element?.data?.includes('diffRegion')) {
        this.currentImageCanvas.remove(element);
      }
    });
    this.baseImageCanvas.getObjects().forEach(element => {
      if (element?.data?.includes('diffRegion')) {
        this.baseImageCanvas.remove(element);
      }
    });
  }

  toggleDifferences(): void {
    this.showDifferences = !this.showDifferences;
    if (this.showDifferences) {
      this._drawDifferenceRegionsOnCanvas();
    }
    else this._removeDifferenceRegionsFromCanvas();
  }

  highlightDifferences(): void {
    let scaleX = this._coOrdinatesScaling()[0];
    let scaleY = this._coOrdinatesScaling()[0];
    this.screenshotComparison.diffCoordinates.forEach((shape: Shape) => {
      this._animatedCircles(shape, scaleX, scaleY);
    })
  }

  _animatedCircles(shape: Shape, scaleX: number, scaleY: number) {
    let circle = new fabric.Circle({
      radius: 1,
      stroke: 'red',
      opacity: 0.5,
      fill: 'transparent',
      strokeWidth: 3,
      left: (<number>shape.x * scaleX) + ((<number>shape.w * scaleX) / 2),
      top: (<number>shape.y * scaleY) + (<number>shape.h * scaleY / 2)
    });
    this.baseImageCanvas.add(circle);
    this.currentImageCanvas.add(circle);
    this._addForwardAnimation(circle, shape, scaleX, scaleY);
  }

  _addForwardAnimation(circle: fabric.Circle, shape: Shape, scaleX: number, scaleY: number) {
    circle.animate({'radius': 24, left: (<number>shape.x * scaleX) - 7, top: (<number>shape.y * scaleY) - 7}, {
      duration: 1000,
      easing: fabric.util.ease['easeInSine'],
      onChange: () => {
        this.baseImageCanvas.renderAll();
        this.currentImageCanvas.renderAll();
      },
      onComplete: () => this._addBackwardAnimation(circle, shape, scaleX, scaleY)
    })
  }

  _addBackwardAnimation(circle: fabric.Circle, shape: Shape, scaleX: number, scaleY: number) {
    circle.animate({
      'radius': 0,
      left: (<number>shape.x * scaleX) + ((<number>shape.w * scaleX) / 2),
      top: (<number>shape.y * scaleY) + (<number>shape.h * scaleY / 2)
    }, {
      duration: 1000,
      easing: fabric.util.ease['easeInSine'],
      onChange: () => {
        this.baseImageCanvas.renderAll();
        this.currentImageCanvas.renderAll();
      },
      onComplete: () => {
        this.baseImageCanvas.remove(circle);
        this.currentImageCanvas.remove(circle);
      }
    })
  }

  _coOrdinatesScaling(): number[] {
    return [
      this.canvasWidth / <number>this?.screenshotComparison?.imageShape?.[1],
      this.canvasHeight / <number>this?.screenshotComparison?.imageShape?.[0],
    ]
  }

  _drawIgnoreRegionsOnCanvas(): void {
    if (this.screenshotComparison.testStepScreenshot.ignoredCoordinates
      && this.screenshotComparison.testStepScreenshot.ignoredCoordinates.length) {
      let scaleX = this._coOrdinatesScaling()[0];
      let scaleY = this._coOrdinatesScaling()[1];
      this.screenshotComparison.testStepScreenshot.ignoredCoordinates.forEach((shape: Shape,index) => {
        let rect = new fabric.Rect({
          left: <number>shape.x * scaleX,
          top: <number>shape.y * scaleY,
          height: <number>shape.h * scaleY,
          width: <number>shape.w * scaleX,
          fill: "rgb(255 204 156)",
          opacity: 0.8,
          data: 'ignoreRegion'+index
        });
        rect.set('selectable',false);
        this.baseImageCanvas.add(rect);
      });
    }
  }

  toggleIgnoreRegionAdditionListener(): void {
    this.isIgnoreRegion = !this.isIgnoreRegion
    if (this.isIgnoreRegion) {
      this._addIgnoreRegionAdditionListener();
    } else {
      this._removeIgnoreRegionAdditionListener();
    }
  }

  toggleIgnoreRegionUpdateListener(){
    this.canUpdateIgnoreRegion = !this.canUpdateIgnoreRegion;
    if (this.canUpdateIgnoreRegion) {
      this._addIgnoreRegionUpdateListener();
    } else {
      this._removeIgnoreRegionUpdateListener();
    }
  }

  private _addIgnoreRegionUpdateListener() {
    this.baseImageCanvas.forEachObject((element => {
      if( element?.data?.includes('ignoreRegion')) {
        element.set('lockMovementX', false);
        element.set('lockMovementY', false);
        element.set('lockRotation', false);
        element.set('lockUniScaling', false);
        element.set('lockScalingX', false);
        element.set('lockScalingY', false);
        element.set('lockSkewingX', false);
        element.set('lockSkewingY', false);
        element.set('lockScalingFlip', false);
        element.set('selectable', true);
      }
    }));
    this.baseImageCanvas.on('object:modified', (event) => {
      this.saveCurrentIgnoreRegion(event);
    });
  }

  private _removeIgnoreRegionUpdateListener() {
    this.baseImageCanvas.forEachObject((element => {
      if(element?.data?.indexOf('ignoreRegion')) {
        element.set('selectable',false);
      }
    }));
    this.baseImageCanvas.off('object:modified');
  }

  viewCombined(): void {
    this.isCombinedView = !this.isCombinedView;
  }

  _addIgnoreRegionAdditionListener(): void {
    this.baseImageCanvas.on('mouse:down', (event) => {
      this._setIgnoreRegionStartPoint(event);
    });
    this.baseImageCanvas.on('mouse:up', (event) => {
      this._setIgnoreRegionEndPoint(event);
      this._drawIgnoreRegion();
    });
  }

  _setIgnoreRegionStartPoint(event): void {
    let currentPosition = this.baseImageCanvas.getPointer(event);
    console.debug('start coords:', currentPosition);
    this.ignoreRegionStartPoint = {'x': currentPosition.x, 'y': currentPosition.y};
  }

  _setIgnoreRegionEndPoint(event): void {
    let currentPosition = this.baseImageCanvas.getPointer(event);
    console.debug('end coords:', currentPosition);
    this.ignoreRegionEndPoint = {'x': currentPosition.x, 'y': currentPosition.y};
  }

  _drawIgnoreRegion() {
    let rect = new fabric.Rect({
      left: <number>this.ignoreRegionStartPoint.x,
      top: <number>this.ignoreRegionStartPoint.y,
      height: <number>(this.ignoreRegionEndPoint.y - this.ignoreRegionStartPoint.y),
      width: <number>(this.ignoreRegionEndPoint.x - this.ignoreRegionStartPoint.x),
      fill: "rgb(255 204 156)",
      opacity: 0.8,
      data: 'newIgnoreRegion'
    });
    this.baseImageCanvas.add(rect);
    this.baseImageCanvas.setActiveObject(rect);
    this.isShowConfirm = true;
  }

  saveCurrentIgnoreRegion(updateExistingIgnoreRegionEvent) {
    this.hasIgnoredRegions = true;
    let scaleX = this._coOrdinatesScaling()[0];
    let scaleY = this._coOrdinatesScaling()[1];
    let currentlySelectedObject = this.baseImageCanvas.getActiveObject();
    let tempIgnoredRegionArray = [];
    if(updateExistingIgnoreRegionEvent){
      if(updateExistingIgnoreRegionEvent.transform.action == "drag") {
        this._updateCurrentIgnoreRegionPosition(updateExistingIgnoreRegionEvent, tempIgnoredRegionArray, currentlySelectedObject);
      } else if(updateExistingIgnoreRegionEvent.transform.action == "scale") {
        this._updateCurrentIgnoreRegionSize(updateExistingIgnoreRegionEvent, tempIgnoredRegionArray, currentlySelectedObject);
      }
    } else {
      tempIgnoredRegionArray = this.screenshotComparison.testStepScreenshot.ignoredCoordinates || [];
      this.baseImageCanvas.getObjects().filter(object => object['data'] == 'newIgnoreRegion').forEach(object =>{
        let selectedObject = object;
        let newIgnoreRegion = new Shape();
        newIgnoreRegion.x = selectedObject.left / scaleX;
        newIgnoreRegion.y = selectedObject.top / scaleY;
        newIgnoreRegion.w = selectedObject.width / scaleX;
        newIgnoreRegion.h = selectedObject.height / scaleY;
        tempIgnoredRegionArray.push(newIgnoreRegion);
      })
    }
    this.testStepScreenshotService.update(this.screenshotComparison.testStepScreenshot).subscribe(() => {
      this.translate.get("message.common.saved.success", {FieldName: 'Ignore region'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.isShowConfirm = false;
        this.toggleIgnoreRegionUpdateListener();
      })
    });
  }

  private _updateCurrentIgnoreRegionPosition(updateExistingIgnoreRegionEvent, tempIgnoredRegionArray: any[], currentlySelectedObject: any) {
    let scaleX = this._coOrdinatesScaling()[0];
    let scaleY = this._coOrdinatesScaling()[1];
    let updatedIgnoreRegion = new Shape();
    this.screenshotComparison.testStepScreenshot.ignoredCoordinates.forEach((ignoreRegion, index) => {
      if (ignoreRegion.x == updateExistingIgnoreRegionEvent.transform.original.left / scaleX
        && ignoreRegion.y == updateExistingIgnoreRegionEvent.transform.original.top / scaleY) {
        console.debug('Skipping from adding to temp ignore region array');
      } else {
        tempIgnoredRegionArray.push(ignoreRegion);
      }
    });
    updatedIgnoreRegion.x = currentlySelectedObject.left / scaleX;
    updatedIgnoreRegion.y = currentlySelectedObject.top / scaleY;
    updatedIgnoreRegion.w = currentlySelectedObject.width / scaleX;
    updatedIgnoreRegion.h = currentlySelectedObject.height / scaleY;
    tempIgnoredRegionArray.push(updatedIgnoreRegion);
  }

  private _updateCurrentIgnoreRegionSize(updateExistingIgnoreRegionEvent: any, tempIgnoredRegionArray: any[], currentlySelectedObject: any) {
    let scaleX = this._coOrdinatesScaling()[0];
    let scaleY = this._coOrdinatesScaling()[1];
    let updatedIgnoreRegion = new Shape();
    this.screenshotComparison.testStepScreenshot.ignoredCoordinates.forEach((ignoreRegion, index) => {
      if (ignoreRegion.x == updateExistingIgnoreRegionEvent.transform.original.left / scaleX
        && ignoreRegion.y == updateExistingIgnoreRegionEvent.transform.original.top / scaleY) {
        console.debug('Skipping from adding to temp ignore region array');
      } else {
        tempIgnoredRegionArray.push(ignoreRegion);
      }
    });
    updatedIgnoreRegion.x = currentlySelectedObject.left / scaleX;
    updatedIgnoreRegion.y = currentlySelectedObject.top / scaleY;
    updatedIgnoreRegion.w = (currentlySelectedObject.width / scaleX) * currentlySelectedObject.scaleX;
    updatedIgnoreRegion.h = (currentlySelectedObject.height / scaleY) * currentlySelectedObject.scaleY;
    tempIgnoredRegionArray.push(updatedIgnoreRegion);
  }

  //Currently clearing all the ignore regions.
  //TODO-renju Add an option to specifically select an already ignored region and remove it
  removeAllIgnoreRegions() {
    this.screenshotComparison.testStepScreenshot.ignoredCoordinates = [];
    this.testStepScreenshotService.update(this.screenshotComparison.testStepScreenshot).subscribe(() => {
      this.translate.get("message.common.deleted.success", {FieldName: 'Ignore region'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.baseImageCanvas.renderAll();
        //TODO Comment and check if this is required
        // this.isShowConfirm = false;
      })
    });
    this.hasIgnoredRegions = false;
    this._renderBaseImage();
    this._renderCurrentImage();
  }

  cancelCurrentIgnoreRegion() {
    let obj = this.baseImageCanvas.getActiveObject();
    console.debug(this.beforeModifiedObject);
    this.baseImageCanvas.remove(obj);
    if (this.beforeModifiedObject) {
      let rect = new fabric.Rect({
        left: <number>this.beforeModifiedObject.left,
        top: <number>this.beforeModifiedObject.top,
        height: <number>(this.beforeModifiedObject.height),
        width: <number>(this.beforeModifiedObject.width),
        fill: "rgb(255 204 156)",
        opacity: 0.8
      });
      this.baseImageCanvas.add(rect);
      this.beforeModifiedObject = undefined;
    }
    this.baseImageCanvas.renderAll();
    this.isShowConfirm = false;
  }

  _removeIgnoreRegionAdditionListener() {
    this.baseImageCanvas.off('mouse:down');
    this.baseImageCanvas.off('mouse:up');
    this.baseImageCanvas.off('object:modified');
    this.baseImageCanvas.off('selection:created');
  }

  onScreenComparisonSelectionChange(event) {
    this.screenshotComparisonId = event.id;
    this.filteredTestStepResult = event.stepResultList;
    this.screenshotComparisonService.show(this.screenshotComparisonId).subscribe((res: StepResultScreenshotComparision) => {
      this.screenshotComparison = res;
      this.hasIgnoredRegions = false;
      this._renderBaseImage();
      this._renderCurrentImage();
      this.hasIgnoredRegions = !!this.screenshotComparison?.testStepScreenshot?.ignoredCoordinates?.length;
    });
  }

  setAsBaseImage() {
    this.screenshotComparisonService.markAsBase(this.screenshotComparison.id).subscribe(() => {
      this.translate.get("message.common.saved.success", {FieldName: 'Mark as base image'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
      })
    }, error => {
      this.translate.get("message.common.saved.failure", {FieldName: 'Mark as base image'}).subscribe((res: string) => {
        this.showAPIError(error, res)
      })
    })
  }
}
