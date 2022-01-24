import {Component, Input, OnInit} from '@angular/core';
import {ChromeRecorderDependentElement} from '../../models/chrome-recorder-dependent-element.model';

@Component({
  selector: 'app-elements-metadata',
  templateUrl: './element-metadata.component.html',
})
export class ElementMetadataComponent implements OnInit {
  @Input('name') name: String;
  @Input('parent') parent: ChromeRecorderDependentElement;
  @Input('pos') pos: number;
  constructor() {}
  ngOnInit() {
  }

}
