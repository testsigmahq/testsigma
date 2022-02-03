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
  public canvasWidth: number;
  public canvasHeight: number;
  public scalingFactor: number;
  public showDifferences: boolean;
  public isCombinedView: boolean;
  public ignoreRegionStartPoint: any;
  public ignoreRegionEndPoint: any;
  public isIgnoreRegion: boolean;
  public beforeModifiedObject: any;
  public isShowConfirm: boolean;
  isApproveBaseImage: any;

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
    this.fetchScreenshot();
  }

  fetchScreenshot(): void {
    this.screenshotComparisonService.show(this.screenshotComparisonId).subscribe((res: StepResultScreenshotComparision) => {
      this.screenshotComparison = res;
      this._renderBaseImage();
      this._renderCurrentImage();
    });
  }

  _renderBaseImage(): void {
    this.canvasWidth = document.getElementById('base_image_container').clientWidth;
    this.canvasHeight = document.getElementById('base_image_container').clientHeight;
    this.baseImageCanvas = new fabric.Canvas('base_image_canvas', {
      hoverCursor: 'pointer',
      width: this.canvasWidth,
      height: this.canvasHeight
    });
    let img = new Image();
    img.onload = () => this._onBaseImageLoad(img);
    img.src = this.screenshotComparison.testStepScreenshot.screenShotURL + '';
  }

  _onBaseImageLoad(img: HTMLImageElement): void {
    let oImg = new fabric.Image(img);
    this.scalingFactor = this.canvasWidth / oImg.width;
    this.canvasHeight = oImg.height * this.scalingFactor
    oImg.scale(this.scalingFactor);
    oImg.set('selectable', false);
    this.baseImageCanvas.setWidth(this.canvasWidth);
    this.baseImageCanvas.setHeight(this.canvasHeight);
    this.baseImageCanvas.clear();
    this.baseImageCanvas.add(oImg);
  }

  _renderCurrentImage(): void {
    this.currentImageCanvas = new fabric.Canvas('current_image_canvas', {
      hoverCursor: 'pointer',
      width: this.canvasWidth,
      height: this.canvasHeight
    });
    let img = new Image();
    img.onload = () => this._onCurrentImageLoad(img);
    img.src = this.screenshotComparison.screenShotURL + '';
  }

  _onCurrentImageLoad(img: HTMLImageElement): void {
    let oImg = new fabric.Image(img);
    this.scalingFactor = this.canvasWidth / oImg.width;
    this.canvasHeight = oImg.height * this.scalingFactor
    oImg.scale(this.scalingFactor);
    oImg.set('selectable', false);
    this.currentImageCanvas.setWidth(this.canvasWidth);
    this.currentImageCanvas.setHeight(this.canvasHeight);
    this.currentImageCanvas.clear();
    this.currentImageCanvas.add(oImg);
    setTimeout(() => {
      this._addDifferences();
      this._drawIgnoreCoOrdinates();
      this.showDifferences = true;
      this.baseImageCanvas.renderAll();
      this.currentImageCanvas.renderAll();
    }, 100);
  }

  _addDifferences(): void {
    let scaleX = this?._coOrdinatesScaling()[0];
    let scaleY = this._coOrdinatesScaling()[1];
    this.screenshotComparison.diffCoordinates.forEach((shape: Shape) => {
      let rect = new fabric.Rect({
        left: <number>shape.x * scaleX,
        top: <number>shape.y * scaleY,
        height: <number>shape.h * scaleY,
        width: <number>shape.w * scaleX,
        fill: "rgba(255, 0, 0)",
        opacity: 0.4,
        selectable: false
      });
      this.currentImageCanvas.add(rect);
      this.baseImageCanvas.add(rect);
    });
  }

  _removeDifferences(): void {
    this.currentImageCanvas.getObjects().forEach(elm => {
      if (elm instanceof fabric.Rect) {
        this.currentImageCanvas.remove(elm);
      }
    });
    this.baseImageCanvas.getObjects().forEach(elm => {
      if (elm instanceof fabric.Rect) {
        this.baseImageCanvas.remove(elm);
      }
    });
  }

  toggleDifferences(): void {
    this.showDifferences = !this.showDifferences;
    if (this.showDifferences)
      this._addDifferences();
    else
      this._removeDifferences();
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
      this.canvasWidth / <number>this?.screenshotComparison?.imageShape[1],
      this.canvasHeight / <number>this?.screenshotComparison?.imageShape[0],
    ]
  }

  _drawIgnoreCoOrdinates(): void {
    if (this.screenshotComparison.testStepScreenshot.ignoredCoordinates && this.screenshotComparison.testStepScreenshot.ignoredCoordinates.length) {
      let scaleX = this._coOrdinatesScaling()[0];
      let scaleY = this._coOrdinatesScaling()[1];
      this.screenshotComparison.testStepScreenshot.ignoredCoordinates.forEach((shape: Shape) => {
        let rect = new fabric.Rect({
          left: <number>shape.x * scaleX,
          top: <number>shape.y * scaleY,
          height: <number>shape.h * scaleY,
          width: <number>shape.w * scaleX,
          fill: "rgb(255 204 156)",
          opacity: 0.8,
          selectable: true
        });
        this.baseImageCanvas.add(rect);
      });
    }
  }

  addIgnoreCoOrdinates(): void {
    this.isIgnoreRegion = !this.isIgnoreRegion
    if (this.isIgnoreRegion) {
      this._addLister();
    } else {
      this._removeLister();
    }
  }

  viewCombined(): void {
    this.isCombinedView = !this.isCombinedView;
  }

  _addLister(): void {
    this.baseImageCanvas.on('mouse:down', (event) => {
      if (event.target['_element'])
        this._setStartPosition(event);
      else {
        this.beforeModifiedObject = {
          width: event.target.width, height: event.target.height, top: event.target.top, left: event.target.left
        };
        this.isShowConfirm = true;
        this.addIgnoreCoOrdinates();
      }
    });
    this.baseImageCanvas.on('mouse:up', (event) => {
      if (event.target['_element']) {
        this._setEndPosition(event);
        this._drawIgnoreRegion();
        this.addIgnoreCoOrdinates();
      }
    });
  }

  _setStartPosition(event): void {
    let currentPosition = this.baseImageCanvas.getPointer(event);
    console.log('start position:', currentPosition);
    this.ignoreRegionStartPoint = {'x': currentPosition.x, 'y': currentPosition.y};
  }

  _setEndPosition(event): void {
    let currentPosition = this.baseImageCanvas.getPointer(event);
    console.log('start position:', currentPosition);
    this.ignoreRegionEndPoint = {'x': currentPosition.x, 'y': currentPosition.y};
  }

  _drawIgnoreRegion() {
    let rect = new fabric.Rect({
      left: <number>this.ignoreRegionStartPoint.x,
      top: <number>this.ignoreRegionStartPoint.y,
      height: <number>(this.ignoreRegionEndPoint.y - this.ignoreRegionStartPoint.y),
      width: <number>(this.ignoreRegionEndPoint.x - this.ignoreRegionStartPoint.x),
      fill: "rgb(255 204 156)",
      opacity: 0.8
    });
    this.baseImageCanvas.add(rect);
    this.baseImageCanvas.setActiveObject(rect);
    this.isShowConfirm = true;
  }

  saveIgnoreRegion() {
    let scaleX = this._coOrdinatesScaling()[0];
    let scaleY = this._coOrdinatesScaling()[1];
    let obj = this.baseImageCanvas.getActiveObject();
    let ignoreCoOrdinate = new Shape();
    ignoreCoOrdinate.x = obj.left / scaleX;
    ignoreCoOrdinate.y = obj.top / scaleY;
    ignoreCoOrdinate.w = obj.width / scaleX;
    ignoreCoOrdinate.h = obj.height / scaleY;
    let ignored = this.screenshotComparison.testStepScreenshot.ignoredCoordinates || [];
    ignored.push(ignoreCoOrdinate);
    this.screenshotComparison.testStepScreenshot.ignoredCoordinates = ignored;

    this.testStepScreenshotService.update(this.screenshotComparison.testStepScreenshot).subscribe(() => {
      this.translate.get("message.common.saved.success", {FieldName: 'Ignore region'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.baseImageCanvas.renderAll();
        this.isShowConfirm = false;
      })
    })
  }

  removeIgnoreRegion() {
    this.baseImageCanvas.remove(this.baseImageCanvas.getActiveObject());
    this.screenshotComparisonService.update(this.screenshotComparison).subscribe(() => {
      this.translate.get("message.common.saved.success", {FieldName: 'Ignore region'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.baseImageCanvas.renderAll();
        this.isShowConfirm = false;
      })
    })
  }

  resetIgnoreRegion() {
    let obj = this.baseImageCanvas.getActiveObject();
    console.log(this.beforeModifiedObject);
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

  _removeLister() {
    this.baseImageCanvas.off('mouse:down');
    this.baseImageCanvas.off('mouse:up');
    this.baseImageCanvas.off('object:modified');
    this.baseImageCanvas.off('selection:created');
  }

  screenComparisonSelection(event) {
    let screenshotComparisonId = event.id;
    this.filteredTestStepResult = event.stepResultList;
    this.screenshotComparisonService.show(screenshotComparisonId).subscribe((res: StepResultScreenshotComparision) => {
      this.screenshotComparison = res;
      this._renderBaseImage();
      this._renderCurrentImage();
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
