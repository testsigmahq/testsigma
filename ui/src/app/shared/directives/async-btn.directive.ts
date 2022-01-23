import {Directive, ElementRef, HostListener, Input, Renderer2, SimpleChanges} from '@angular/core';
import {Subscription} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
@Directive({
  selector: '[appAsyncBtn]'
})
export class AsyncBtnDirective {
  private subscription: Subscription;
  private buttonString: string;
  private isClicked: boolean = false;

  @Input('isLoading') isLoading;
  @Input('message') message;

  constructor(
    private _renderer: Renderer2,
    public translate: TranslateService,
    private _elementRef: ElementRef
  ) {
    console.log(this._elementRef)
  }

  @HostListener('click')
  onClick() {
    this.buttonString = this._elementRef?.nativeElement?.textContent;
    this.isClicked = true;
    if(this.isLoading) {
      this.disable();
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if(this.isClicked)
    this.subscribe();
  }

  disable() {
    this._elementRef.nativeElement.textContent = this.translate.instant(this.message ? this.message : 'btn.common.please_wait');
    this._renderer.setAttribute(
      this._elementRef.nativeElement,
      'disabled',
      'true'
    );
  }

  enable() {
    this._elementRef.nativeElement.textContent = this.buttonString;
    this._renderer.removeAttribute(
      this._elementRef.nativeElement,
      'disabled'
    );
    this.isClicked = false;
  }

  subscribe() {
    if(this.isClicked && !this.isLoading){
      this.enable()
    } else {
      this.disable()
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
