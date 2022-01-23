import {Component, OnInit, Input, ViewChild, ElementRef, HostListener} from '@angular/core';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
})
export class DescriptionComponent implements OnInit {
  @Input('formCtrlName') formCtrlName;
  @ViewChild('editor') editor: ElementRef;
  @HostListener('focusout')
  onblur(){
    this.formCtrlName.setValue(this.editor.nativeElement.innerHTML);
  }

  constructor() { }

  ngOnInit(): void { }

}
