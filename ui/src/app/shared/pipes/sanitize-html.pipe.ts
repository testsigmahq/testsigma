import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({ name: 'SanitizeHtml'})

export class SanitizeHtmlPipe implements PipeTransform  {

  constructor(private _sanitizer:DomSanitizer) { }

  transform(value:string):SafeHtml {
    return this._sanitizer.bypassSecurityTrustHtml(value);
  }
}
