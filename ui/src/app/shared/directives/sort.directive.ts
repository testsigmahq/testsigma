import {Directive, ElementRef, Input, Renderer2, SimpleChanges} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Directive({
  selector: '[appSort]'
})
export class SortDirective {

  @Input()
  public appSort;
  private originalElement;

  constructor(
    private _renderer: Renderer2,
    private _elementRef: ElementRef
  ) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if(this.originalElement == undefined)
      this.originalElement = this._elementRef.nativeElement.innerHTML;
    if(changes.appSort!=undefined){
      if (this.appSort == true) {
        this._elementRef.nativeElement.innerHTML = this.originalElement + ("<span class='fa-down-sort'></span>");
      }else if(this.appSort == false){
        this._elementRef.nativeElement.innerHTML =  this.originalElement + ("<span class='fa-up-sort'></span>");
      } else {
        this._elementRef.nativeElement.innerHTML = this.originalElement + ("<span class='fa-down-sort opaque-50'></span>");
      }
    }
  }

}
