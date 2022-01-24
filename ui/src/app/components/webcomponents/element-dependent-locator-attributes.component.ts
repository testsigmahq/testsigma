import {Component, Input, OnInit} from '@angular/core';
import {ChromeRecorderDependentElement} from "../../models/chrome-recorder-dependent-element.model";

@Component({
  selector: 'app-element-dependent-element-attributes',
  templateUrl: './element-dependent-locator-attributes.component.html',
  styles: []
})
export class ElementDependentLocatorAttributesComponent implements OnInit {

  @Input('element') element: ChromeRecorderDependentElement;
  @Input('title') title: string;
  @Input('index') index: number;

  public expandedPanelIndex: number;
  public panelOpenState: Boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

}
