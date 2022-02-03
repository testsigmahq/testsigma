import {Directive, ElementRef, Inject, Input, NgZone, Optional, ViewContainerRef} from '@angular/core';
import {MAT_TOOLTIP_DEFAULT_OPTIONS, MAT_TOOLTIP_SCROLL_STRATEGY, MatTooltip, MatTooltipDefaultOptions} from '@angular/material/tooltip';
import {AriaDescriber, FocusMonitor} from '@angular/cdk/a11y';
import {Directionality} from '@angular/cdk/bidi';
import {Overlay, ScrollDispatcher} from '@angular/cdk/overlay';
import {Platform} from '@angular/cdk/platform';


@Directive({
  selector: '[appTooltipOnEllipsis]'
})
export class TooltipOnEllipsisDirective  extends MatTooltip{
  @Input('appTooltipOnEllipsis') appTooltipOnEllipsis?;
  constructor(
    _overlay: Overlay,
    _elementRef: ElementRef,
    _scrollDispatcher: ScrollDispatcher,
    _viewContainerRef: ViewContainerRef,
    _ngZone: NgZone,
    _platform: Platform,
    _ariaDescriber: AriaDescriber,
    _focusMonitor: FocusMonitor,
    _document: Document,
    @Inject(MAT_TOOLTIP_SCROLL_STRATEGY) _scrollStrategy: any,
    @Optional() _dir: Directionality,
    @Optional() @Inject(MAT_TOOLTIP_DEFAULT_OPTIONS)
      _defaultOptions: MatTooltipDefaultOptions)
  {
    super(
      _overlay,
      _elementRef,
      _scrollDispatcher,
      _viewContainerRef,
      _ngZone,
      _platform,
      _ariaDescriber,
      _focusMonitor,
      _scrollStrategy,
      _dir,
      _defaultOptions,
      _document
    );
    _elementRef.nativeElement.onload
  if(_elementRef.nativeElement.offsetWidth < _elementRef.nativeElement.scrollWidth) {
    if(Boolean(this.appTooltipOnEllipsis))
      this.message= this.appTooltipOnEllipsis;
    else
      this.message= _elementRef.nativeElement.textContent;
    }
  }

  ngAfterContentInit(){
    if(this["_elementRef"].nativeElement.offsetWidth < this["_elementRef"].nativeElement.scrollWidth)
      this.message = this.appTooltipOnEllipsis?.length>0? this.appTooltipOnEllipsis: this["_elementRef"].nativeElement.textContent;
    else {
      this.message = "";
    }
  }

}
