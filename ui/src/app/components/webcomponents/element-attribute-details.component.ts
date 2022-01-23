import {Component, Input, OnInit} from '@angular/core';
import {Element} from '../../models/element.model';
import {ElementService} from '../../shared/services/element.service';

@Component({
  selector: 'app-elements-details',
  templateUrl: './element-attribute-details.component.html',
})
export class ElementAttributeDetailsComponent implements OnInit {
  @Input('element') element: Element;
  constructor(
    public elementService: ElementService) {
  }

  ngOnInit() {
    this.fetchElement();
  }

  fetchElement(){
    this.elementService.show(this.element.id).subscribe(res => {
      this.element = res;
    });
  }
  getOperator(operator){
    if (operator === '=')
      return 'Equals';
    if (operator === 'starts-with')
      return 'StartsWith';
    if (operator === 'contains')
      return 'Contains';
  }
}
